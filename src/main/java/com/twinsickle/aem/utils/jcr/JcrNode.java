package com.twinsickle.aem.utils.jcr;

import javax.jcr.Session;
import javax.jcr.Value;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public interface JcrNode {

    Optional<JcrNode> getOrCreateChild(String name, String type);
    Optional<JcrNode> createUniqueChild(String name, String type, Session session);
    boolean setValue(String name, Object value);
    boolean setValue(String name, Collection<?> values);
    boolean hasValue(String name);
    Optional<Value> getValue(String name);
    List<Value> getValues(String name);
    boolean addValue(String name, Object value);
    boolean addMixin(String mixin);
    Stream<JcrNode> getChildren();
    boolean deleteValue(String name);
    Optional<String> getPath();
    Optional<String> getName();
}
