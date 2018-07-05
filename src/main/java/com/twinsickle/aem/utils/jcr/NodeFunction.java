package com.twinsickle.aem.utils.jcr;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

@FunctionalInterface
public interface NodeFunction<T> {
    T apply(Node node) throws RepositoryException;
}
