package com.example.hclai.drools_play_ground.enums;

public enum RiskLevelEnum {

    LOW("low", "低風險"),
    MEDIUM("medium", "中風險"),
    HIGH("high", "高風險"),
    UNKNOWN("unknown", "未知");

    private String type;
    private String desc;

    RiskLevelEnum(String type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public String getType() {
        return type;
    }

    public String getDesc() {
        return desc;
    }

}
