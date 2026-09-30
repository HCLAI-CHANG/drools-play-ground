package com.example.hclai.drools_play_ground.enums;

public enum EmploymentTypeEnum {

    FULLTIME("fulltime", "正職"),
    PARTTIME("parttime", "兼職"),
    SELF_EMPLOYED("self employed", "自營"),
    UNKNOWN("unknown", "未知");

    private String type;
    private String desc;

    EmploymentTypeEnum(String type, String desc) {
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
