package com.twinsickle.aem.utils.resource.helper;

import com.day.cq.commons.jcr.JcrConstants;
import com.twinsickle.aem.utils.jcr.JcrNodeBuilder;
import com.twinsickle.aem.utils.resource.AdapterUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.jcr.resource.api.JcrResourceConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.nodetype.NodeType;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ResourceHelper {
    private static final Logger LOG = LoggerFactory.getLogger(ResourceHelper.class);

    public static Resource getOrCreateResource(ResourceResolver resourceResolver, String path, String type){
        Resource resource = resourceResolver.getResource(path);
        if(resource == null){
            Resource parentResource = getOrCreateResource(resourceResolver,
                    StringUtils.substringBeforeLast(path, File.separator), type);

            if (parentResource != null) {
                Map<String, Object> properties = new HashMap<>();
                    properties.put(JcrConstants.JCR_PRIMARYTYPE, type);
                try {
                    resource = resourceResolver.create(parentResource,
                            StringUtils.substringAfterLast(path, File.separator), properties);
                    ResourceHelper.setMixin(resource, NodeType.MIX_CREATED);

                    resourceResolver.commit();
                } catch (PersistenceException e) {
                    LOG.error("Failed to create resource.", e);
                }
            }
        }
        return resource;
    }

    public static Resource getOrCreateResource(ResourceResolver resourceResolver, String path) {
        Resource resource = resourceResolver.getResource(path);
        if (resource == null) {
            Resource parentResource = getOrCreateResource(resourceResolver, StringUtils.substringBeforeLast(path, File.separator));

            if (parentResource != null) {
                Map<String, Object> properties = new HashMap<>();
                if (StringUtils.countMatches(path, File.separator) == 3) {
                    properties.put(JcrConstants.JCR_PRIMARYTYPE, JcrResourceConstants.NT_SLING_ORDERED_FOLDER);
                } else {
                    properties.put(JcrConstants.JCR_PRIMARYTYPE, JcrConstants.NT_UNSTRUCTURED);
                }
                try {
                    resource = resourceResolver.create(parentResource, StringUtils.substringAfterLast(path, File.separator), properties);
                    ResourceHelper.setMixin(resource, NodeType.MIX_CREATED);

                    resourceResolver.commit();
                } catch (PersistenceException e) {
                    LOG.error("Failed to create resource.", e);
                }
            }
        }

        return resource;
    }

    public static void setMixin(Resource resource, String mixinName) {
        AdapterUtil.adaptTo(resource, Node.class)
                .flatMap(JcrNodeBuilder::getNode)
                .ifPresent(jcrNode -> jcrNode.addMixin(mixinName));
    }
}
