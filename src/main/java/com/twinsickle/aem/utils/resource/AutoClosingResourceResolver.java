package com.twinsickle.aem.utils.resource;

import org.apache.sling.api.resource.ResourceResolver;

public interface AutoClosingResourceResolver extends ResourceResolver, AutoCloseable {}
