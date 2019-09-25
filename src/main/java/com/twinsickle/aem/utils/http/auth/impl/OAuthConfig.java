package com.twinsickle.aem.utils.http.auth.impl;

import com.twinsickle.aem.utils.http.auth.AuthConfig;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Component(service = AuthConfig.class)
public class OAuthConfig implements AuthConfig {

    private static final String CLIENT_ID = "client_id";
    private static final String CLIENT_SECRET = "client_secret";
    private static final String SCOPE = "scope";
    private static final String GRANT_TYPE = "grant_type";

    @ObjectClassDefinition(
            name = "OAuth Configuration"
    )
    /*package-private*/ @interface Config {
        @AttributeDefinition(
                name = "OAuth Token URL"
        )
        String orvis_oauth_url() default "";

        @AttributeDefinition(
                name = "OAuth Client ID"
        )
        String orvis_oauth_client_id() default "";

        @AttributeDefinition(
                name = "OAuth Client Secret"
        )
        String orvis_oath_client_secret() default "";

        @AttributeDefinition(
                name = "OAuth Scope"
        )
        String orvis_oauth_scope() default "";

        @AttributeDefinition(
                name = "OAuth Grant Type"
        )
        String orvis_oauth_grant_type() default "";
    }

    private String url;
    private String clientId;
    private String clientSecret;
    private String scope;
    private String grantType;

    @Activate
    protected void activate(Config config){
        this.url = config.orvis_oauth_url();
        this.clientId = config.orvis_oauth_client_id();
        this.clientSecret = config.orvis_oath_client_secret();
        this.scope = config.orvis_oauth_scope();
        this.grantType = config.orvis_oauth_grant_type();
    }


    @Override
    public String getUrl(){
        return url;
    }

    public String getClientId(){
        return clientId;
    }

    public String getClientSecret(){
        return clientSecret;
    }

    public String getScope(){
        return scope;
    }

    public String getGrantType(){
        return grantType;
    }

    @Override
    public HttpEntity buildRequest(){
        List<NameValuePair> nvps = new ArrayList<>();
        nvps.add(new BasicNameValuePair(CLIENT_ID, getClientId()));
        nvps.add(new BasicNameValuePair(CLIENT_SECRET, getClientSecret()));
        nvps.add(new BasicNameValuePair(SCOPE, getScope()));
        nvps.add(new BasicNameValuePair(GRANT_TYPE, getGrantType()));
        return new UrlEncodedFormEntity(nvps, StandardCharsets.UTF_8);
    }
}
