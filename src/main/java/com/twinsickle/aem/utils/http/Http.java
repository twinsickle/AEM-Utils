package com.twinsickle.aem.utils.http;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.twinsickle.aem.utils.http.auth.AuthConfig;
import com.twinsickle.aem.utils.http.auth.Authorization;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

public class Http<T> {
    private static final Logger LOG = LoggerFactory.getLogger(Http.class);

    public enum Type {
        XML,
        JSON
    }

    private final Class<T> tClass;

    private Http(Class<T> tClass){
        this.tClass = tClass;
    }

    public static <T> Http<T> of(Class<T> tClass){
        return new Http<>(tClass);
    }

    public Optional<HttpResult<T>> get(String uri){
        HttpGet get = new HttpGet(uri);
        return execute(get);
    }

    public Optional<HttpResult<T>> get(String uri, AuthConfig config){
        HttpGet get = new HttpGet(uri);
        Authorization auth = new Authorization(config);
        auth.getAuthorizationHeader()
                .ifPresent(get::setHeader);
        return execute(get);
    }

    public Optional<HttpResult<T>> post(String uri, Object obj, Type contentType){
        return buildEntity(obj, contentType)
                .flatMap(entity -> this.post(uri, entity));
    }

    public Optional<HttpResult<T>> post(String uri, Object obj, Type contentType, AuthConfig config){
        Authorization auth = new Authorization(config);
        return buildEntity(obj, contentType)
                .map(entity -> {
                    HttpPost post = new HttpPost(uri);
                    post.setEntity(entity);
                    auth.getAuthorizationHeader()
                            .ifPresent(post::setHeader);
                    return post;
                })
                .flatMap(this::execute);
    }

    public Optional<HttpResult<T>> post(String uri, HttpEntity request){
        HttpPost post = new HttpPost(uri);
        post.setEntity(request);
        return execute(post);
    }

    private Optional<HttpEntity> buildEntity(Object obj, Type contentType){
        try {
            if (contentType.equals(Type.XML)) {
                XmlMapper mapper = new XmlMapper();
                return Optional.of(new StringEntity(mapper.writeValueAsString(obj) ,ContentType.APPLICATION_XML));
            } else if (contentType.equals(Type.JSON)) {
                ObjectMapper mapper = new ObjectMapper();
                return Optional.of(new StringEntity(mapper.writeValueAsString(obj), ContentType.APPLICATION_JSON));
            }
        } catch (JsonProcessingException jme){
            LOG.error("Http#buildEntity - failed to convert object to requested format");
        }
        return Optional.empty();
    }

    private Optional<HttpResult<T>> execute(HttpUriRequest request){
        HttpClient client = HttpClientBuilder.create().build();
        try {
            return Optional.ofNullable(client.execute(request, this::handleResponse));
        } catch (ClientProtocolException cpe){
            LOG.error("Http#execute - exception creating request", cpe);
        } catch (IOException ioe){
            LOG.error("Http#execute - exception sending request", ioe);
        }
        return Optional.empty();
    }

    private HttpResult<T> handleResponse(HttpResponse response){
        StatusLine statusLine = response.getStatusLine();
        int statusCode = statusLine.getStatusCode();
        boolean success = HttpStatus.SC_OK <= statusCode
                && HttpStatus.SC_MULTIPLE_CHOICES > statusCode;
        LOG.debug("Response status: {}", statusCode);
        return new ConcreteHttpResult(success, getPayload(response.getEntity()));
    }

    private T getPayload(HttpEntity entity){
        ContentType contentType = ContentType.get(entity);
        if(contentType != null) {
            try {
                InputStream inputStream = entity.getContent();
                if (ContentType.APPLICATION_XML.toString().equalsIgnoreCase(contentType.toString())) {
                    XmlMapper mapper = new XmlMapper();
                    return mapper.readValue(inputStream, tClass);
                } else if (ContentType.APPLICATION_JSON.toString().equalsIgnoreCase(contentType.toString())) {
                    ObjectMapper mapper = new ObjectMapper();
                    return mapper.readValue(inputStream, tClass);
                }
            } catch (IOException ioe) {
                LOG.warn("Http#handleResponse - Failed to retrieve response entity", ioe);
            }
        }
        return null;
    }

    private class ConcreteHttpResult implements HttpResult<T> {

        private boolean success;
        private T entity;

        private ConcreteHttpResult(boolean success, T entity){
            this.success = success;
            this.entity = entity;
        }

        public boolean success(){
            return success;
        }

        public Optional<T> getEntity(){
            return Optional.ofNullable(this.entity);
        }

    }
}
