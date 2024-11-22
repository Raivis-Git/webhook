package com.facebook.webhook.template;

import java.util.function.*;

public class TemplateNotFoundException extends RuntimeException implements Supplier<TemplateNotFoundException> {
    public TemplateNotFoundException(String message) {
        super(message);
    }

    @Override
    public TemplateNotFoundException get() throws TemplateNotFoundException {
        throw new TemplateNotFoundException(getMessage());
    }
}
