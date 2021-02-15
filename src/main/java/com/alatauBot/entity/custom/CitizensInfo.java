package com.alatauBot.entity.custom;

import lombok.Data;

import java.util.Date;

@Data
public class CitizensInfo {
    private Integer     id;
    private String      document;
    private Date        date;
    private String      time;
}
