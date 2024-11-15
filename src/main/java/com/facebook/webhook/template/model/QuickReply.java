package com.facebook.webhook.template.model;

import com.fasterxml.jackson.annotation.*;
import com.google.gson.annotations.*;

@JsonIgnoreProperties(ignoreUnknown = true)
public class QuickReply {
    @SerializedName("content_type")
    private String contentType;
    private String title;
    private String payload;
    @SerializedName("image_url")
    private String imageUrl;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }
}
