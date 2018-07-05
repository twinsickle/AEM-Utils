package com.twinsickle.aem.utils.servlet;

import org.apache.sling.api.SlingHttpServletResponse;

public interface ServletResponder {

    <T> void respondWithSuccess(SlingHttpServletResponse response, T payload);
    void respondWithError(SlingHttpServletResponse response, String errorMessage, int status);
}
