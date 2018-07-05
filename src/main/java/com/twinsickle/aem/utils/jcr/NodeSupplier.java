package com.twinsickle.aem.utils.jcr;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

@FunctionalInterface
public interface NodeSupplier {
    Node apply() throws RepositoryException;
}
