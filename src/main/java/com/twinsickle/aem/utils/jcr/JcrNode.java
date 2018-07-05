package com.twinsickle.aem.utils.jcr;

import javax.jcr.Session;
import javax.jcr.Value;
import java.util.Collection;
import java.util.Optional;

public interface JcrNode {

    Optional<JcrNode> getOrCreateChild(String name, String type);
    Optional<JcrNode> createUniqueChild(String name, String type, Session session);
    void setValue(String name, Object value);
    void setValue(String name, Collection<?> values);
    boolean hasValue(String name);
    Optional<Value> getValue(String name);
    void addMixin(String mixin);
}
