package com.alatauBot.entity.custom;

import lombok.Data;

@Data
public class AppealTeg {
    private Integer     id;
    private String      name;
//    private String      desc;
    private Integer     appealTypeId;
    private Integer     receptionTypeId;
    private Integer     lang_id;
}
