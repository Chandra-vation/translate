package com.translation.core.service;

import org.apache.sling.api.resource.Resource;

public interface TranslationService {
    String translateText(String sourceText, String sourceLang, String targetLang);
    void translatePage(Resource pageResource, String sourceLang, String targetLang);
}
