package com.alatauBot.entity.standart;

import lombok.Data;

@Data
public class User {
    private Integer     id;
    private Long        chatId;
    private String      phone;
    private String      fullName;
    private String      userName;
}
