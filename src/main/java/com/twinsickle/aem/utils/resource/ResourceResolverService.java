package com.twinsickle.aem.utils.resource;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

public interface ResourceResolverService {

    @FunctionalInterface
    interface ResourceResolverFunction<T> extends Function<ResourceResolver, T> {}

    @FunctionalInterface
    interface ResourceResolverConsumer extends Consumer<ResourceResolver> {}

    @FunctionalInterface
    interface ResourceFunction<T> extends Function<Resource, T> {}

    @FunctionalInterface
    interface ResourceConsumer extends Consumer<Resource> {}

    void ifPresent(ResourceResolverConsumer function, String serviceUser);

    void ifPresent(String path, ResourceConsumer function, String serviceUser);

    <R> Optional<R> map(ResourceResolverFunction<Optional<R>> function, String serviceUser);

    <R> Optional<R> map(String path, ResourceFunction<Optional<R>> function, String serviceUser);

}

