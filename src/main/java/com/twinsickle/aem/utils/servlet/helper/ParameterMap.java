package com.twinsickle.aem.utils.servlet.helper;

import org.apache.commons.lang.StringUtils;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class ParameterMap {

    private Map<String, Object> parameters;

    @SuppressWarnings("unchecked")
    public ParameterMap(Map parameters) {
        this.parameters = parameters;
    }

    public String[] getArrayProperty(String name) {
        return getProperty(name).orElse(new String[0]);
    }

    public String getStringProperty(String name) {
        return getProperty(name).map(arr -> arr[0]).orElse(StringUtils.EMPTY);
    }

    private Optional<String[]> getProperty(String name){
        return Optional.ofNullable((String[])parameters.get(name));
    }

    public boolean isArray(String name) {
        String[] propertyArray = (String[])parameters.get(name);
        return propertyArray.length > 1;
    }

    public Set<String> getKeys() {
        return parameters.keySet();
    }
}
