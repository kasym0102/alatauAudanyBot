package com.alatauBot.entity.custom;

import lombok.Data;

import java.util.Date;

@Data
public class ReminderTask {

    private Integer     id;
    private String      text;
    private Date        dateBegin;
    private Date        dateEnd;
}
