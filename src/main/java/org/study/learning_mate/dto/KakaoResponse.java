package org.study.learning_mate.dto;

import java.util.Map;

public class KakaoResponse implements OAuth2Response{
    private final Map<String , Object> attributes;

    public KakaoResponse(Map<String , Object> attributes) {
        this.attributes = (Map<String, Object>) attributes.get("response");
    }

    @Override
    public String getProviderId() {
        return attributes.get("id").toString();
    }

    @Override
    public String getEmail() {
        return attributes.get("email").toString();
    }

    @Override
    public String getName() {
        return attributes.get("name").toString();
    }

}