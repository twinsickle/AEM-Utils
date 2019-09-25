package com.twinsickle.aem.utils.http.auth;

import com.twinsickle.aem.utils.http.Http;
import com.twinsickle.aem.utils.http.HttpResult;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class Authorization {

    private static final Logger LOG = LoggerFactory.getLogger(Authorization.class);

    private AuthConfig config;

    public Authorization(AuthConfig config){
        this.config = config;
    }

    public Optional<Header> getAuthorizationHeader(){
        return getAuthorization(config.buildRequest())
                .map(this::buildHeader);
    }

    private OAuthHeader buildHeader(OAuthResponse response){
        return new OAuthHeader(response.getAccessToken());
    }

    private Optional<OAuthResponse> getAuthorization(HttpEntity request){
        Http<OAuthResponse> http = Http.of(OAuthResponse.class);
        return http.post(config.getUrl(), request)
                .filter(HttpResult::success)
                .flatMap(HttpResult::getEntity);
    }
}
