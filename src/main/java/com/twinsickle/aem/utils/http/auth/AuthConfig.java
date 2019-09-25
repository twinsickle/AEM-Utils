package com.twinsickle.aem.utils.http.auth;

import org.apache.http.HttpEntity;

public interface AuthConfig {
    String getUrl();
    HttpEntity buildRequest();
}
