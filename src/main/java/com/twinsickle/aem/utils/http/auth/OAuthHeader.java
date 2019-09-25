package com.twinsickle.aem.utils.http.auth;


import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.ParseException;

public class OAuthHeader implements Header {

    private static final String AUTHORIZATION_HEADER_PARAM = "Authorization";
    private static final String BEARER_TOKEN_PARAM = "Bearer ";

    private String token;

    public OAuthHeader(String token){
        this.token = token;
    }

    @Override
    public HeaderElement[] getElements() throws ParseException {
        return new HeaderElement[0];
    }

    @Override
    public String getName() {
        return AUTHORIZATION_HEADER_PARAM;
    }

    @Override
    public String getValue() {
        return BEARER_TOKEN_PARAM + token;
    }
}
