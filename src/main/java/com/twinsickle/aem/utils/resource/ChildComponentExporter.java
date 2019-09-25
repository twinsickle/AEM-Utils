package com.twinsickle.aem.utils.resource;

import com.adobe.cq.export.json.ComponentExporter;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.TemplatedResource;
import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Via;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.factory.ModelFactory;

import javax.inject.Inject;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Model(adaptables = SlingHttpServletRequest.class)
public abstract class ChildComponentExporter implements ComponentExporter {

    private static final String PAGE_CONTENT_NODE = "/jcr:content";

    @Self
    private SlingHttpServletRequest request;

    @Self
    @Via("resource")
    private Resource resource;

    @Inject
    private ModelFactory modelFactory;

    @SuppressWarnings("unchecked")
    protected <T> T getModel(String name, T defaultObj) {
        T model =  (T)getModel(name, defaultObj.getClass());
        if (model == null) {
            return defaultObj;
        }
        return model;
    }

    private  <T> T getModel(String name, Class<T> tClass){
        Resource child = resource.getChild(name);
        if(child == null){
            return null;
        }
        return modelFactory.getModelFromWrappedRequest(request, child, tClass);
    }

    protected Page getResourcePage(PageManager pageManager){
        return pageManager.getPage(StringUtils.substringBefore(resource.getPath(), PAGE_CONTENT_NODE));
    }

    protected  <T extends ComponentExporter> Map<String, T> getChildModelMap(Page page, Class<T> tClass){
        if(page == null){
            return Collections.emptyMap();
        }

        Map<String, T> exportedChildren = new HashMap<>();

        page.listChildren().forEachRemaining(childPage -> {
            Resource contentResource = childPage.getContentResource();

            if (contentResource == null) {
                return;
            }

            TemplatedResource templatedResource = AdapterUtil.adaptTo(contentResource, TemplatedResource.class)
                    .orElse(null);

            if (templatedResource != null) {
                contentResource = templatedResource;
            }

            exportedChildren.put(childPage.getName(),
                    modelFactory.getModelFromWrappedRequest(request, contentResource, tClass));
        });

        return exportedChildren;
    }
}
