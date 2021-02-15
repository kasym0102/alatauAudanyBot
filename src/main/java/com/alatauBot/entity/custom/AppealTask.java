package com.alatauBot.entity.custom;

import lombok.Data;

import java.util.Date;

@Data
public class AppealTask {
    private Integer     id;
    private Integer     receptionTypeId;
    private Integer     fullName;
    private String      phone;
    private String      text;
    private Long        chatId;
    private Integer     appealTypeId;
    private Integer     appealTegId;
    private Date        dataBegin;
    private String      location;
    private Integer     idStatus;
    private String      photo;
    private String      video;
    private Integer     appraisalId;
    private Date        dataDeadline;
}