package com.example.hclai.drools_play_ground.service;

import java.math.BigDecimal;

import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
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

    public LoanResult fireXlsxRule(LoanApplication application) {

        KieSession kieSession = kieContainer.newKieSession("xlsxLoanRulesSession");

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
