package com.example.hclai.drools_play_ground.model;

import java.math.BigDecimal;

import com.example.hclai.drools_play_ground.enums.RiskLevelEnum;

import lombok.Data;

@Data
public class LoanResult {

    private String id;

    private String name;

    private boolean approved;

    private BigDecimal approvedAmount;

    private BigDecimal interestRate;

    private RiskLevelEnum riskLevel;

    private String rejectReason;

}
