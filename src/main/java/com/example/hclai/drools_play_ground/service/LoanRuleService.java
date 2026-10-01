package com.example.hclai.drools_play_ground.service;

import java.math.BigDecimal;

import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieModule;
import org.kie.api.builder.Message;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.internal.io.ResourceFactory;
import org.springframework.stereotype.Service;

import com.example.hclai.drools_play_ground.enums.RiskLevelEnum;
import com.example.hclai.drools_play_ground.model.LoanApplication;
import com.example.hclai.drools_play_ground.model.LoanResult;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoanRuleService {

    private final KieContainer kieContainer;

    public LoanResult fireRule(LoanApplication application) {

        KieSession kieSession = kieContainer.newKieSession("loanRulesSession");

        LoanResult result = new LoanResult();
        result.setId(application.getId());
        result.setName(application.getName());
        result.setApproved(true);
        result.setApprovedAmount(BigDecimal.ZERO);
        result.setInterestRate(BigDecimal.ZERO);
        result.setRiskLevel(RiskLevelEnum.UNKNOWN);
        result.setRejectReason(null);

        try {

            kieSession.insert(application);
            kieSession.insert(result);

            kieSession.fireAllRules();

        } finally {
            kieSession.dispose();
        }

        return result;

    }

    public LoanResult fireExcelRule(LoanApplication application) {

        KieServices kieServices = KieServices.Factory.get();
        Resource resource = ResourceFactory.newClassPathResource("rules/loan/loanRule_fixed_logic_v4.xlsx");
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

        LoanResult result = new LoanResult();
        result.setId(application.getId());
        result.setName(application.getName());
        result.setApproved(true);
        result.setApprovedAmount(BigDecimal.ZERO);
        result.setInterestRate(BigDecimal.ZERO);
        result.setRiskLevel(RiskLevelEnum.UNKNOWN);
        result.setRejectReason(null);

        try {

            kieSession.insert(application);
            kieSession.insert(result);

            kieSession.fireAllRules();

        } finally {
            kieSession.dispose();
        }

        return result;

    }

}
