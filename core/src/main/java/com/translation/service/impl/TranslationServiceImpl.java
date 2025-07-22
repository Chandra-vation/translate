package com.translation.service.impl;

import java.io.FileInputStream;
import java.util.Collections;

import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.translate.v3beta1.LocationName;
import com.google.cloud.translate.v3beta1.TranslateTextRequest;
import com.google.cloud.translate.v3beta1.TranslateTextResponse;
import com.google.cloud.translate.v3beta1.TranslationServiceClient;
import com.google.cloud.translate.v3beta1.TranslationServiceSettings;
import com.translation.service.TranslationService;

@Component(service = TranslationService.class)
public class TranslationServiceImpl implements TranslationService {

    @Reference
    private ResourceResolverFactory resolverFactory;

    private String projectId = "your-gcp-project-id";
    private String credentialsPath = "/etc/secrets/gcp-credentials.json"; // path to your JSON

    @Override
    public String translateText(String sourceText, String sourceLang, String targetLang) {
        try {
            GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream(credentialsPath))
                    .createScoped(Collections.singletonList("https://www.googleapis.com/auth/cloud-platform"));

            TranslationServiceSettings settings = TranslationServiceSettings.newBuilder()
                    .setCredentialsProvider(() -> credentials)
                    .build();

            try (TranslationServiceClient client = TranslationServiceClient.create(settings)) {
                LocationName parent = LocationName.of(projectId, "global");

                TranslateTextRequest request = TranslateTextRequest.newBuilder()
                        .setParent(parent.toString())
                        .setMimeType("text/plain")
                        .setSourceLanguageCode(sourceLang)
                        .setTargetLanguageCode(targetLang)
                        .addContents(sourceText)
                        .build();

                TranslateTextResponse response = client.translateText(request);
                return response.getTranslations(0).getTranslatedText();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "[Translation Failed]";
        }
    }

 /*
    @Override
    public void translatePage(Resource pageResource, String sourceLang, String targetLang) {
        try (ResourceResolver resolver = resolverFactory.getServiceResourceResolver(
                Collections.singletonMap(ResourceResolverFactory.SUBSERVICE, "translationService"))) {

            PageManager pageManager = resolver.adaptTo(PageManager.class);
            Page sourcePage = pageResource.adaptTo(Page.class);
            if (sourcePage == null) return;

            // Create language copy path
            String targetPath = sourcePage.getPath().replace("/en/", "/" + targetLang + "/");
            Page targetPage = pageManager.copy(sourcePage, targetPath, null, true, true);

            // Translate basic properties like jcr:title
            Node targetNode = targetPage.adaptTo(Node.class);
            if (targetNode != null && targetNode.hasProperty("jcr:title")) {
                String originalTitle = targetNode.getProperty("jcr:title").getString();
                String translatedTitle = translateText(originalTitle, sourceLang, targetLang);
                targetNode.setProperty("jcr:title", translatedTitle);
            }

            // Translate text components
            Iterator<Resource> components = targetPage.getContentResource().listChildren();
            while (components.hasNext()) {
                Resource component = components.next();
                Node componentNode = component.adaptTo(Node.class);
                if (componentNode != null && componentNode.hasProperty("text")) {
                    String originalText = componentNode.getProperty("text").getString();
                    String translatedText = translateText(originalText, sourceLang, targetLang);
                    componentNode.setProperty("text", translatedText);
                }
            }

            resolver.commit();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
         */
}
