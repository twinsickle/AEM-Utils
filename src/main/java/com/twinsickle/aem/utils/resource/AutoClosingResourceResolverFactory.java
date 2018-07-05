package com.twinsickle.aem.utils.resource;

import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;

import java.util.Map;

public interface AutoClosingResourceResolverFactory {

    ResourceResolver getResourceResolver(String serviceUser) throws LoginException;

    ResourceResolver getResourceResolver(Map<String, Object> permissions) throws LoginException;
}
