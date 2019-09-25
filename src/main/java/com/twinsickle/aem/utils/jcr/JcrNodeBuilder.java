package com.twinsickle.aem.utils.jcr;

import com.day.cq.commons.jcr.JcrUtil;
import com.twinsickle.aem.utils.value.ValueFactory;
import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.commons.JcrUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.*;
import java.util.*;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class JcrNodeBuilder {
    private static final Logger LOG = LoggerFactory.getLogger(JcrNodeBuilder.class);

    private static final String JCR_PROPERTY_START = "jcr:";

    public static Optional<JcrNode> getNode(Node node){
        if(node == null){
            return Optional.empty();
        }
        return Optional.of(new JcrNodeImpl(node));
    }

    private static String getValidName(String name){
        if(StringUtils.startsWith(name,JCR_PROPERTY_START)){
            return name;
        }
        return JcrUtil.createValidName(name);
    }

    public static Optional<JcrNode> getOrBuildNode(Node parent, String name, String type){
        return tryBuild(() -> JcrUtils.getOrAddNode(parent, getValidName(name), type))
                .flatMap(JcrNodeBuilder::getNode);
    }

    public static Optional<JcrNode> getOrBuildNode(String path, String type, Session session){
        return tryBuild(() -> JcrUtils.getOrCreateByPath(path, type, session))
                .flatMap(JcrNodeBuilder::getNode);
    }

    public static Optional<JcrNode> buildUniqueNode(Node parent, String name, String type, Session session){
        return tryBuild(() -> JcrUtil.createUniqueNode(parent, getValidName(name), type, session))
                .flatMap(JcrNodeBuilder::getNode);
    }

    public static Optional<JcrNode> buildUniqueNode(String path, String type, Session session){
        return tryBuild(() -> JcrUtil.createUniquePath(path, type, session))
                .flatMap(JcrNodeBuilder::getNode);
    }

    private static Optional<Node> tryBuild(NodeSupplier function){
        try {
            return Optional.ofNullable(function.apply());
        } catch (RepositoryException re){
            LOG.error("JcrNodeBuilder#tryBuild - failed to build node", re);
        }
        return Optional.empty();
    }

    private static class JcrNodeImpl implements JcrNode{
        private Node node;

        private JcrNodeImpl(Node node){
            this.node = node;
        }

        @Override
        public Optional<JcrNode> getOrCreateChild(String name, String type) {
            return getOrBuildNode(node, name, type);
        }

        @Override
        public Optional<JcrNode> createUniqueChild(String name, String type, Session session){
            return buildUniqueNode(name, type, session);
        }

        @Override
        public boolean hasValue(String name){
            return tryGet(node -> node.hasProperty(name))
                    .orElse(false);
        }

        @Override
        public Optional<Value> getValue(String name){
            return tryGet(node -> {
                Property property = node.getProperty(name);
                if(property.isMultiple()){
                    return null;
                }
                return property.getValue();
            });
        }

        @Override
        public List<Value> getValues(String name){
            return tryGet(node -> {
                Property property = node.getProperty(name);
                if(property.isMultiple()) {
                    Value[] values = property.getValues();
                    return Arrays.asList(values);
                }
                return new LinkedList<Value>();
            }).orElse(Collections.emptyList());
        }

        @Override
        public boolean setValue(String name, Object value) {
            if(value == null || StringUtils.isEmpty(name)){
                LOG.warn("JcrNodeBuilder#setValue - parameters invalid");
                return false;
            }
            return trySet(node -> node.setProperty(name, ValueFactory.createValue(value)));
        }

        @Override
        public boolean setValue(String name, Collection<?> values) {
            if(values == null || StringUtils.isEmpty(name)){
                LOG.warn("JcrNodeBuilder#setValue - parameters invalid");
                return false;
            }
            return trySet(node -> node.setProperty(name, ValueFactory.createValueArray(values)));
        }

        @Override
        public boolean addValue(String name, Object value){
            if(value == null || StringUtils.isEmpty(name)){
                LOG.warn("JcrNodeBuilder#setValue - parameters invalid");
                return false;
            }
            List<Value> values = new LinkedList<>();
            if(hasValue(name)) {
                tryGet(node -> {
                    Property property = node.getProperty(name);
                    try {
                        if (property.isMultiple()) {
                            return Arrays.asList(property.getValues());
                        }
                        return Collections.singletonList(property.getValue());
                    } finally {
                        property.remove();
                    }
                }).ifPresent(values::addAll);
            }
            return trySet(node -> {
                values.add(ValueFactory.createValue(value));
                node.setProperty(name, values.toArray(new Value[0]));
            });
        }

        @Override
        public boolean deleteValue(String name){
            if(StringUtils.isEmpty(name)){
                LOG.warn("JcrNodeBuilder#setValue - parameters invalid");
                return false;
            }
            if(hasValue(name)){
                return trySet(node -> node.getProperty(name).remove());
            }
            return false;
        }

        @Override
        public boolean addMixin(String mixin){
            return trySet(node -> {
                if(node.canAddMixin(mixin)){
                    node.addMixin(mixin);
                }
            });
        }

        @Override
        public Stream<JcrNode> getChildren(){
            return tryGet(JcrUtils::getChildNodes)
                    .map(itr -> StreamSupport.stream(itr.spliterator(), false))
                    .map(stream -> stream.map(JcrNodeBuilder::getNode)
                            .map(Optional::get))
                    .orElse(Stream.empty());
        }

        @Override
        public Optional<String> getPath(){
            return tryGet(Node::getPath);
        }

        @Override
        public Optional<String> getName(){
            return tryGet(Node::getName);
        }

        private boolean trySet(NodeConsumer function){
            try {
                function.apply(node);
                return true;
            } catch (RepositoryException re){
                LOG.warn("JcrNodeBuilder#trySet - failed to set property", re);
            }
            return false;
        }

        private <T> Optional<T> tryGet(NodeFunction<T> function){
            try {
                return Optional.ofNullable(function.apply(node));
            } catch (RepositoryException re){
                LOG.warn("JcrNodeBuilder#tryGet - unable to access property", re);
            }
            return Optional.empty();
        }
    }
}
