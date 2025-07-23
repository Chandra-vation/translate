package com.translation.servlets;


import com.translation.service.TranslationService;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;

@Component(service = Servlet.class,
        property = {
                "sling.servlet.methods=POST",
                "sling.servlet.paths=/bin/translatePage"
        })
public class TranslationServlet extends SlingAllMethodsServlet {

    @Reference
    private TranslationService translationService;

    @Override
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response)
            throws ServletException, IOException {

        String path = request.getParameter("path");
        String sourceLang = request.getParameter("sourceLang"); // e.g. "en"
        String targetLang = request.getParameter("targetLang"); // e.g. "fr"

        if (path == null || sourceLang == null || targetLang == null) {
            response.setStatus(400);
            response.getWriter().write("Missing parameters");
            return;
        }

        ResourceResolver resolver = request.getResourceResolver();
        Resource pageResource = resolver.getResource(path);
        if (pageResource == null) {
            response.setStatus(404);
            response.getWriter().write("Page not found: " + path);
            return;
        }

        translationService.translateText("Hello World!!!", sourceLang, targetLang);
        response.getWriter().write("Translation triggered for: " + path);
    }
}
