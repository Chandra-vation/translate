package com.translation.workflow;

import com.translation.service.TranslationService;
import com.day.cq.workflow.exec.WorkItem;
import com.day.cq.workflow.exec.WorkflowProcess;
import com.day.cq.workflow.metadata.MetaDataMap;
import com.day.cq.workflow.WorkflowSession;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.util.Collections;

@Component(service = WorkflowProcess.class,
        property = {"process.label=Translation Trigger Step"})
public class TranslationWorkflowProcess implements WorkflowProcess {

    @Reference
    private ResourceResolverFactory resolverFactory;

    @Reference
    private TranslationService translationService;

    @Override
    public void execute(WorkItem workItem, WorkflowSession workflowSession, MetaDataMap args) {
        String payloadPath = workItem.getWorkflowData().getPayload().toString();
        String sourceLang = args.get("sourceLang", "en");
        String targetLang = args.get("targetLang", "fr");

        try (ResourceResolver resolver = resolverFactory.getServiceResourceResolver(
                Collections.singletonMap(ResourceResolverFactory.SUBSERVICE, "translationService"))) {

            Resource pageResource = resolver.getResource(payloadPath);
            if (pageResource != null) {
                translationService.translateText("pageResource", sourceLang, targetLang);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
