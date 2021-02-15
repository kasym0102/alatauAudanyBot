package com.alatauBot.entity.custom;

import lombok.Data;

@Data
public class ReceptionType {
    private Integer     id;
    private String      name;
    private Integer     receptionId;
    private Integer     appealTypeId;
    private Integer     languageId;
    private Long        chatId;
    private String      comment;
}
