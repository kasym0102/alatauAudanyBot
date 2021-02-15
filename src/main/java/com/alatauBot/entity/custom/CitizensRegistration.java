package com.alatauBot.entity.custom;

import lombok.Data;

import java.util.Date;

@Data
public class CitizensRegistration {
    private Integer     id;
    private Long        chatId;
    private Integer     receptionId;
    private String      iin;
    private String      question;
    private String      status;
    private Date        date;
    private String        citizensDate;
    private String      citizensTime;
}
