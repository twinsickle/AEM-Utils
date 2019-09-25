package com.twinsickle.aem.utils.resource;

import com.twinsickle.aem.utils.resource.helper.ResourceHelper;
import org.apache.commons.lang.StringUtils;
import org.apache.jackrabbit.core.fs.FileSystem;
import org.apache.sling.api.resource.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.List;

public abstract class SavableSlingModel {
    private static final Logger LOG = LoggerFactory.getLogger(SavableSlingModel.class);

    private static final String LIST_NODE_NAME = "item";

    public void save(String path, ResourceResolver resolver) throws PersistenceException {
        Resource resource = ResourceHelper.getOrCreateResource(resolver, path);
        save(resource);
        resolver.commit();
    }

    private void save(Resource resource){
        AdapterUtil.adaptTo(resource, ModifiableValueMap.class)
                .ifPresent(map -> handleFields(map, resource));
    }

    private void handleFields(ValueMap map, Resource resource){
        Field[] fields = this.getClass().getDeclaredFields();
        for (Field field : fields) {
            handleField(field, map, resource);
        }
    }

    private void handleField(Field field, ValueMap map, Resource resource){
        field.setAccessible(true);
        String name = field.getName();
        try {
            Object value = field.get(this);
            if(value == null){
                return;
            }
            if(value instanceof SavableSlingModel) {
                String childPath = resource.getPath() + FileSystem.SEPARATOR_CHAR + name;
                Resource child = ResourceHelper.getOrCreateResource(resource.getResourceResolver(), childPath);
                ((SavableSlingModel) value).save(child);
            } else if (value instanceof List){
                List models = (List)value;
                if (!models.isEmpty() && models.get(0) instanceof SavableSlingModel){
                    ResourceResolver resolver = resource.getResourceResolver();
                    String childPath = resource.getPath() + FileSystem.SEPARATOR_CHAR + name;
                    ResourceHelper.getOrCreateResource(resolver, childPath);
                    for(int i = 0; i < ((List) value).size(); i++){
                        String itemPath = childPath + FileSystem.SEPARATOR_CHAR + LIST_NODE_NAME + i;
                        Resource item = ResourceHelper.getOrCreateResource(resolver, itemPath);
                        ((SavableSlingModel) models.get(i)).save(item);
                    }
                }
            } else if (hasStringValue(value) || hasArrayValue(value) || value.getClass().isPrimitive()) {
                map.put(name, value);
            }
        } catch (IllegalAccessException iae){
            LOG.error("Unable to access field {}", name, iae);
        }
    }

    private boolean hasStringValue(Object value){
        return value instanceof String && StringUtils.isNotEmpty(value.toString());
    }

    private boolean hasArrayValue(Object value){
        return value instanceof Object[];
    }
}
