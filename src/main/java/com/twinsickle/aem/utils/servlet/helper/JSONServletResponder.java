package com.twinsickle.aem.utils.servlet.helper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.twinsickle.aem.utils.servlet.ServletResponder;
import org.apache.http.entity.ContentType;
import org.apache.sling.api.SlingHttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.PrintWriter;

public class JSONServletResponder implements ServletResponder {
    private static final Logger LOG = LoggerFactory.getLogger(JSONServletResponder.class);

    private static final boolean SUCCESS = true;
    private static final boolean ERROR = false;

    @Override
    public <T> void respondWithSuccess(SlingHttpServletResponse response, T payload){
        Response<T> success = new Response<>(payload, SUCCESS);
        respond(success, response);
    }

    @Override
    public void respondWithError(SlingHttpServletResponse response, String errorMessage, int status){
        Response<String> error = new Response<>(errorMessage, ERROR);
        response.setStatus(status);
        respond(error, response);
    }

    private void respond(Response response, SlingHttpServletResponse servletResponse){
        servletResponse.setContentType(ContentType.APPLICATION_JSON.toString());
        try(PrintWriter writer = servletResponse.getWriter()) {
            ObjectMapper mapper = new ObjectMapper();
            writer.write(mapper.writeValueAsString(response));
            writer.flush();
        } catch (IOException ioe){
            LOG.error("JSONServletResponder#respond - failed to respond", ioe);
            servletResponse.setStatus(SlingHttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private class Response<T> {
        private boolean success;
        private T data;
        private Response(T data, boolean success){
            this.data = data;
            this.success = success;
        }

        public boolean isSuccess() {
            return success;
        }

        public T getData() {
            return data;
        }
    }
}
