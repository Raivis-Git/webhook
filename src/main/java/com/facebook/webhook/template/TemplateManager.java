package com.facebook.webhook.template;

import com.facebook.webhook.template.model.*;
import com.google.gson.*;
import com.google.gson.reflect.*;
import org.slf4j.*;
import org.springframework.core.io.*;
import org.springframework.stereotype.*;
import org.springframework.util.*;

import java.io.*;
import java.nio.charset.*;
import java.util.*;;

@Component
public class TemplateManager {

    Logger logger = LoggerFactory.getLogger(TemplateManager.class);

    List<MessageTemplate> messageTemplateList = null;

    public TemplateManager() {
    }

    public String loadJsonTemplate(String fileName) {
        try {
            Resource resource = new ClassPathResource("templates/" + fileName.trim() + ".json");
            return new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            logger.error("Failed to load template file: {}", fileName, e);
            throw new TemplateNotFoundException("Could not load template: " + fileName);
        }
    }

    public void updateMessageTemplate(String fileName) {
        String jsonContent = loadJsonTemplate(fileName);
        this.messageTemplateList = new Gson().fromJson(jsonContent, new TypeToken<List<MessageTemplate>>(){}.getType());
    }

    public List<MessageTemplate> getMessageTemplate(String fileName) {
        if (CollectionUtils.isEmpty(this.messageTemplateList))
            updateMessageTemplate(fileName);
        return this.messageTemplateList;
    }

}
