package com.twinsickle.aem.utils.resource;

import org.apache.sling.api.adapter.Adaptable;

import java.util.Optional;

public final class AdapterUtil {
    private AdapterUtil(){}


    public static <AdaptableType extends Adaptable, AdapterType> Optional<AdapterType> adaptTo(AdaptableType input, Class<AdapterType> cls){
        if(input == null || cls == null){
            return Optional.empty();
        }
        return Optional.ofNullable(input.adaptTo(cls));
    }
}
