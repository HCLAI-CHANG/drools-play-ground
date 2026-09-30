package com.example.hclai.drools_play_ground.service;

import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieModule;
import org.kie.api.builder.Message;
import org.kie.api.event.rule.AfterMatchFiredEvent;
import org.kie.api.event.rule.DefaultAgendaEventListener;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.internal.io.ResourceFactory;
import org.springframework.stereotype.Service;

import com.example.hclai.drools_play_ground.model.RuleResult;
import com.example.hclai.drools_play_ground.model.UserData;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class RuleService {

    private final KieContainer kieContainer;

    public RuleResult fireRule(UserData data) {

        KieSession kieSession = kieContainer.newKieSession("rulesSession");

        RuleResult result = new RuleResult();
        result.setResult("false");
        result.setLevel("normal");
        result.setApproval("staff");

        try {

            kieSession.getKieBase().getKiePackages().forEach(kpkg ->
                kpkg.getRules().forEach(rule -> log.info("LOADED RULE: package={}, name={}", kpkg.getName(), rule.getName()) )
            );

            kieSession.addEventListener(new DefaultAgendaEventListener() {

                @Override
                public void matchCreated(org.kie.api.event.rule.MatchCreatedEvent event) {
                    log.info("MATCH CREATED: {}", event.getMatch().getRule().getName());
                }

                @Override
                public void matchCancelled(org.kie.api.event.rule.MatchCancelledEvent event) {
                    log.info("MATCH CANCELLED: {}", event.getMatch().getRule().getName());
                }

                @Override
                public void afterMatchFired(AfterMatchFiredEvent event) {
                    log.info("FIRED: {}", event.getMatch().getRule().getName());
                }

            });

            kieSession.insert(data);
            kieSession.insert(result);

            int count = kieSession.fireAllRules();
            log.info("{} rules implementd", count);

        } finally {
            kieSession.dispose();
        }

        return result;

    }

    public RuleResult fireExcelRule(UserData data) {

        KieServices kieServices = KieServices.Factory.get();
        Resource resource = ResourceFactory.newClassPathResource("rules/eproduct1/excelrule.xls");
        resource.setResourceType(ResourceType.DTABLE);

        KieFileSystem kieFileSystem = kieServices.newKieFileSystem();
        kieFileSystem.write(resource);

        KieBuilder kieBuilder = kieServices.newKieBuilder(kieFileSystem);
        kieBuilder.buildAll();

        if (kieBuilder.getResults().hasMessages(Message.Level.ERROR)) {
            log.error("Decision table compile error: {}", kieBuilder.getResults().getMessages());
            throw new RuntimeException("Decision table compile error: " + kieBuilder.getResults().getMessages());
        }

        KieModule kieModule = kieBuilder.getKieModule();
        KieSession kieSession = kieServices.newKieContainer(kieModule.getReleaseId()).newKieSession();

        RuleResult result = new RuleResult();
        result.setResult("false");
        result.setLevel("normal");
        result.setApproval("staff");

        try {

            kieSession.getKieBase().getKiePackages().forEach(kpkg ->
                kpkg.getRules().forEach(rule -> log.info("LOADED RULE: package={}, name={}", kpkg.getName(), rule.getName()) )
            );

            kieSession.addEventListener(new DefaultAgendaEventListener() {

                @Override
                public void matchCreated(org.kie.api.event.rule.MatchCreatedEvent event) {
                    log.info("MATCH CREATED: {}", event.getMatch().getRule().getName());
                }

                @Override
                public void matchCancelled(org.kie.api.event.rule.MatchCancelledEvent event) {
                    log.info("MATCH CANCELLED: {}", event.getMatch().getRule().getName());
                }

                @Override
                public void afterMatchFired(AfterMatchFiredEvent event) {
                    log.info("FIRED: {}", event.getMatch().getRule().getName());
                }

            });

            kieSession.insert(data);
            kieSession.insert(result);

            int count = kieSession.fireAllRules();
            log.info("{} rules implementd", count);

        } finally {
            kieSession.dispose();
        }

        return result;

    }

}
