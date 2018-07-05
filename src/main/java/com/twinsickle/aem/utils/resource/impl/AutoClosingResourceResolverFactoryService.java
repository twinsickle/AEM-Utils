package com.twinsickle.aem.utils.resource.impl;




import com.twinsickle.aem.utils.resource.AutoClosingResourceResolver;
import com.twinsickle.aem.utils.resource.AutoClosingResourceResolverFactory;
import org.apache.sling.api.resource.*;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Component(
        immediate = true,
        service = AutoClosingResourceResolverFactory.class
)
public class AutoClosingResourceResolverFactoryService implements AutoClosingResourceResolverFactory {

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    @Override
    public ResourceResolver getResourceResolver(String serviceUser) throws LoginException {
        Map<String, Object> permissions = new HashMap<>();
        permissions.put(ResourceResolverFactory.SUBSERVICE, serviceUser);
        return getResourceResolver(permissions);
    }

    @Override
    public ResourceResolver getResourceResolver(Map<String, Object> permissions) throws LoginException {
        ResourceResolver resolver = resourceResolverFactory.getServiceResourceResolver(permissions);
        return new AutoClosingResourceResolverImpl(resolver);
    }

    private class AutoClosingResourceResolverImpl implements AutoClosingResourceResolver {

        private ResourceResolver resolver;

        private AutoClosingResourceResolverImpl(ResourceResolver resolver){
            this.resolver = resolver;
        }

        @Override
        public Resource resolve(HttpServletRequest httpServletRequest, String s) {
            return resolver.resolve(httpServletRequest, s);
        }

        @Override
        public Resource resolve(String s) {
            return resolver.resolve(s);
        }

        @Deprecated
        @Override
        public Resource resolve(HttpServletRequest httpServletRequest) {
            throw new UnsupportedOperationException("Method has been deprecated");
        }

        @Override
        public String map(String s) {
            return resolver.map(s);
        }

        @Override
        public String map(HttpServletRequest httpServletRequest, String s) {
            return resolver.map(httpServletRequest, s);
        }

        @Override
        public Resource getResource(String s) {
            return resolver.getResource(s);
        }

        @Override
        public Resource getResource(Resource resource, String s) {
            return resolver.getResource(resource, s);
        }

        @Override
        public String[] getSearchPath() {
            return resolver.getSearchPath();
        }

        @Override
        public Iterator<Resource> listChildren(Resource resource) {
            return resolver.listChildren(resource);
        }

        @Override
        public Resource getParent(Resource resource) {
            return resolver.getParent(resource);
        }

        @Override
        public Iterable<Resource> getChildren(Resource resource) {
            return resolver.getChildren(resource);
        }

        @Override
        public Iterator<Resource> findResources(String s, String s1) {
            return resolver.findResources(s, s1);
        }

        @Override
        public Iterator<Map<String, Object>> queryResources(String s, String s1) {
            return resolver.queryResources(s, s1);
        }

        @Override
        public boolean hasChildren(Resource resource) {
            return resolver.hasChildren(resource);
        }

        @Override
        public ResourceResolver clone(Map<String, Object> map) throws LoginException {
            return new AutoClosingResourceResolverImpl(resolver.clone(map));
        }

        @Override
        public boolean isLive() {
            return resolver.isLive();
        }

        @Override
        public void close() {
            resolver.close();
        }

        @Override
        public String getUserID() {
            return resolver.getUserID();
        }

        @Override
        public Iterator<String> getAttributeNames() {
            return resolver.getAttributeNames();
        }

        @Override
        public Object getAttribute(String s) {
            return resolver.getAttribute(s);
        }

        @Override
        public void delete(Resource resource) throws PersistenceException {
            resolver.delete(resource);
        }

        @Override
        public Resource create(Resource resource, String s, Map<String, Object> map) throws PersistenceException {
            return resolver.create(resource, s, map);
        }

        @Override
        public void revert() {
            resolver.revert();
        }

        @Override
        public void commit() throws PersistenceException {
            resolver.commit();
        }

        @Override
        public boolean hasChanges() {
            return resolver.hasChanges();
        }

        @Override
        public String getParentResourceType(Resource resource) {
            return resolver.getParentResourceType(resource);
        }

        @Override
        public String getParentResourceType(String s) {
            return resolver.getParentResourceType(s);
        }

        @Override
        public boolean isResourceType(Resource resource, String s) {
            return resolver.isResourceType(resource, s);
        }

        @Override
        public void refresh() {
            resolver.refresh();
        }

        @Override
        public Resource copy(String s, String s1) throws PersistenceException {
            return resolver.copy(s, s1);
        }

        @Override
        public Resource move(String s, String s1) throws PersistenceException {
            return resolver.move(s, s1);
        }

        @Override
        public <AdapterType> AdapterType adaptTo(Class<AdapterType> aClass) {
            return resolver.adaptTo(aClass);
        }
    }
}
