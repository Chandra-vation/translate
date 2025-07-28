package com.translation.core.servlets;


import com.translation.core.service.TranslationService;
import com.translation.core.service.impl.TranslationServiceImpl;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.Optional;


@Component(service = { Servlet.class },
        property = {
                "sling.servlet.methods=POST",
                "sling.servlet.paths=/bin/translatePage"
        })
public class TranslationServlet extends SlingAllMethodsServlet {

    private final transient Logger log = LoggerFactory.getLogger(TranslationServlet.class);


    @Reference
    private TranslationService translationService;

    @Override
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response)
            throws ServletException, IOException {
        try{
            String path = request.getParameter("path");
            String sourceLang = Optional.ofNullable(request.getParameter("sourceLang"))
                    .filter(s -> !s.isBlank())
                    .orElse("en");

            String targetLang = Optional.ofNullable(request.getParameter("targetLang"))
                    .filter(s -> !s.isBlank())
                    .orElse("fr");


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

            translationService.translatePage(pageResource, sourceLang, targetLang);
            response.getWriter().write("Translation triggered for: " + path);
        } catch (Exception e) {
            log.error("Exception caught in TranslationServlet:- ",e);
            response.getWriter().write("Servlet failed to execute, PLease check logs");
        }

    }
}
