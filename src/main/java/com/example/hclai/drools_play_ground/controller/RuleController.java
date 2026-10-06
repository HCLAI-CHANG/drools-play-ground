package com.example.hclai.drools_play_ground.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.hclai.drools_play_ground.model.GuestData;
import com.example.hclai.drools_play_ground.model.GuestResult;
import com.example.hclai.drools_play_ground.model.LoanApplication;
import com.example.hclai.drools_play_ground.model.LoanResult;
import com.example.hclai.drools_play_ground.model.RuleResult;
import com.example.hclai.drools_play_ground.model.UserData;
import com.example.hclai.drools_play_ground.service.DiscountService;
import com.example.hclai.drools_play_ground.service.LoanRuleService;
import com.example.hclai.drools_play_ground.service.RuleService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class RuleController {

    private final RuleService ruleService;

    private final LoanRuleService loanRuleService;

    private final DiscountService discountService;

    @PostMapping("/discountRule")
    public GuestResult fireRule(@Valid @RequestBody GuestData data) {

        log.info("run fireRule, user: {}", data.getName());

        return discountService.fireRule(data);

    }

    @PostMapping("/xlsDiscountRule")
    public GuestResult fireXlsRule(@Valid @RequestBody GuestData data) {

        log.info("run fireRule, user: {}", data.getName());

        return discountService.fireExcelRule(data);

    }

    @PostMapping("/rule")
    public RuleResult fireRule(@Valid @RequestBody UserData data) {

        log.info("run fireRule, user: {}", data.getName());

        return ruleService.fireRule(data);

    }

    @PostMapping("/xlsRule")
    public RuleResult fireXlsRule(@Valid @RequestBody UserData data) {

        log.info("run fireXlsRule, user: {}", data.getName());

        return ruleService.fireExcelRule(data);

    }

    @PostMapping("/loanRule")
    public LoanResult fireLoanRule(@Valid @RequestBody LoanApplication application) {

        log.info("run fireLoanRule, id: {}, name: {}", application.getId(), application.getName());

        return loanRuleService.fireRule(application);

    }

    @PostMapping("/xlsLoanRule")
    public LoanResult fireXlsLoanRule(@Valid @RequestBody LoanApplication application) {

        log.info("run fireXlsLoanRule, id: {}, name: {}", application.getId(), application.getName());

        return loanRuleService.fireExcelRule(application);

    }

}
