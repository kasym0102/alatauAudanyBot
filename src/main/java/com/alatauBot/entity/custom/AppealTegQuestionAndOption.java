package com.alatauBot.entity.custom;

import lombok.Data;

@Data
public class AppealTegQuestionAndOption {
    private Integer     id;
    private Integer     appealTegId;
    private String      question;
    private String      answer;
    private Integer     langId;
}
