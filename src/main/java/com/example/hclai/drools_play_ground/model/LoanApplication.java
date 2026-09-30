package com.example.hclai.drools_play_ground.model;

import java.math.BigDecimal;

import com.example.hclai.drools_play_ground.enums.EmploymentTypeEnum;

import lombok.Data;

@Data
public class LoanApplication {

    private String id;

    private String name;

    private int age;

    private BigDecimal monthlyIncome;

    private BigDecimal creditScore;

    private BigDecimal existingDebt;

    private BigDecimal requestAmount;

    private EmploymentTypeEnum employmentType;

    private boolean hasBadRecord;

}
