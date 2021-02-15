package com.alatauBot.entity.custom;

import lombok.Data;

@Data
public class SendMember  {
    private Integer     id;
    private Long        chatId;
    private Integer     receptionId;
    private Integer     registrationId;
}