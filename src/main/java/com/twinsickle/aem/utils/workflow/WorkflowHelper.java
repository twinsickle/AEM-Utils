package com.twinsickle.aem.utils.workflow;

import com.day.cq.workflow.exec.WorkItem;
import com.day.cq.workflow.exec.WorkflowData;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.jcr.resource.api.JcrResourceConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Session;
import java.util.Collections;
import java.util.Optional;

public final class WorkflowHelper {
    private static final Logger LOG = LoggerFactory.getLogger(WorkflowHelper.class);

    private static final String TYPE_JCR_PATH = "JCR_PATH";

    private WorkflowHelper(){}

    public static Optional<String> getResourcePath(WorkItem workItem){
        WorkflowData workflowData = workItem.getWorkflowData();
        if(isPathWorkflow(workflowData)){
            return Optional.ofNullable(workflowData.getPayload().toString());
        }
        return Optional.empty();
    }

    private static boolean isPathWorkflow(WorkflowData workflowData){
        String type = workflowData.getPayloadType();
        return TYPE_JCR_PATH.equals(type);
    }

    public static Optional<ResourceResolver> getResourceResolver(Session session, ResourceResolverFactory resolverFactory){
        try {
            return Optional.of(resolverFactory.getResourceResolver(
                    Collections.singletonMap(JcrResourceConstants.AUTHENTICATION_INFO_SESSION, session)));
        } catch (LoginException le){
            LOG.error("URLExporterProcess#getResourceResolver - authentication error retrieving resource resolver", le);
        }
        return Optional.empty();
    }
}
