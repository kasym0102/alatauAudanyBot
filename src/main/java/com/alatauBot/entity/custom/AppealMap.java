package com.alatauBot.entity.custom;

import lombok.Data;

@Data
public class AppealMap {
    private Integer     id;
    private Integer     receptionTypeId;
    private Integer     appealTypeId;
    private Integer     appealTegId;
    private String      appealLocation;
}
