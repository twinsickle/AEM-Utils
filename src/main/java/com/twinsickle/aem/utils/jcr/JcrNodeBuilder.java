package com.twinsickle.aem.utils.jcr;

import com.day.cq.commons.jcr.JcrUtil;
import com.twinsickle.aem.utils.value.ValueFactory;
import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.commons.JcrUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.*;
import java.util.Collection;
import java.util.Optional;

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
                    .orElse(Boolean.FALSE);
        }

        @Override
        public Optional<Value> getValue(String name){
            return tryGet(node -> {
                Property property = node.getProperty(name);
                return property.getValue();
            });
        }

        @Override
        public void setValue(String name, Object value) {
            if(value == null || StringUtils.isEmpty(name)){
                LOG.warn("JcrNodeBuilder#setValue - parameters invalid");
                return;
            }
            trySet(node -> node.setProperty(name, ValueFactory.createValue(value)));
        }

        @Override
        public void setValue(String name, Collection<?> values) {
            if(values == null || StringUtils.isEmpty(name)){
                LOG.warn("JcrNodeBuilder#setValue - parameters invalid");
                return;
            }
            trySet(node -> node.setProperty(name, ValueFactory.createValueArray(values)));
        }

        @Override
        public void addMixin(String mixin){
            trySet(node -> {
                if(node.canAddMixin(mixin)){
                    node.addMixin(mixin);
                }
            });
        }

        private void trySet(NodeConsumer function){
            try {
                function.apply(node);
            } catch (RepositoryException re){
                LOG.warn("JcrNodeBuilder#trySet - failed to set property", re);
            }
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
