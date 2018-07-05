package com.twinsickle.aem.utils.resource.impl;


import com.twinsickle.aem.utils.resource.AutoClosingResourceResolverFactory;
import com.twinsickle.aem.utils.resource.ResourceResolverService;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

@Component(
        immediate = true,
        service = ResourceResolverService.class
)
public class ResourceResolverServiceImpl implements ResourceResolverService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ResourceResolverServiceImpl.class);

    @Reference
    private AutoClosingResourceResolverFactory factory;


    @Override
    public void ifPresent(ResourceResolverConsumer function, String serviceUser) {
        try (ResourceResolver resolver = factory.getResourceResolver(serviceUser)) {
            function.accept(resolver);
        } catch (LoginException le) {
            LOGGER.error("ResourceResolverServiceImpl#ifPresent - failed to login", le);
        } catch (Exception e){
            LOGGER.error("ResourceResolverServiceImpl#ifPresent - failed to close resource resolver", e);
        }
    }

    @Override
    public void ifPresent(String path, ResourceConsumer function, String serviceUser) {
        ifPresent(resolver ->function.accept(resolver.resolve(path)),serviceUser);
    }

    @Override
    public <R> Optional<R> map(ResourceResolverFunction<Optional<R>> function, String serviceUser) {
        try(ResourceResolver resolver = factory.getResourceResolver(serviceUser)){
            return function.apply(resolver);
        } catch (LoginException le){
            LOGGER.error("ResourceResolverServiceImpl#map - failed to login", le);
        } catch (Exception e){
            LOGGER.error("ResourceResolverServiceImpl#map - failed to close resource resolver", e);
        }
        return Optional.empty();
    }

    @Override
    public <R> Optional<R> map(String path, ResourceFunction<Optional<R>> function, String serviceUser) {
        return map(resolver -> function.apply(resolver.resolve(path)), serviceUser);
    }
}
