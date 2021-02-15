package com.alatauBot.entity.custom;

import lombok.Data;

import java.util.Date;

@Data
public class AppealTaskRequestToRenewal {
    private Integer id;
    private String textCause;
    private Integer taskId;
    private Long chatId;
    private Date dateDeadline;
    private Date oldDateDeadline;

}

