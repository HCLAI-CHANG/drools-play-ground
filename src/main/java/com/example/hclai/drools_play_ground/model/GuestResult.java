package com.example.hclai.drools_play_ground.model;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class GuestResult {

    private String name;

    private boolean result;

    private BigDecimal finalPrice;

    private boolean kickout;

}
