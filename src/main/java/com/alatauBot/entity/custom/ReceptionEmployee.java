package com.alatauBot.entity.custom;

import lombok.Data;

@Data
public class ReceptionEmployee {
    private Integer id;
    private Integer receptionId;
    private String  name;
    private Long    chatId;
}
