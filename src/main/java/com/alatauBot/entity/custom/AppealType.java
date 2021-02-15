package com.alatauBot.entity.custom;

import lombok.Data;

@Data
public class AppealType {
    private Integer     id;
    private String      name;
    private Integer     receptionTypeId;
    private Integer     languageId;
}
