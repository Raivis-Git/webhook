package com.facebook.webhook.template.model;

import com.fasterxml.jackson.annotation.*;

import java.util.*;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GenericTemplateElement {
    private String title;
    private String subtitle;
    private String imageUrl;
    private List<ButtonTemplate> buttons;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public List<ButtonTemplate> getButtons() {
        return buttons;
    }

    public void setButtons(List<ButtonTemplate> buttons) {
        this.buttons = buttons;
    }
}
