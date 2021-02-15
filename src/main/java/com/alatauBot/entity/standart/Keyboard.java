package com.alatauBot.entity.standart;

import lombok.Data;

@Data
public class Keyboard {
    private Integer     id;
    private String      buttonIds;
    private boolean     inline;
    private String      comment;
}
