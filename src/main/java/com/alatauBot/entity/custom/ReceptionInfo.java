package com.alatauBot.entity.custom;

import com.alatauBot.entity.enums.Language;
import lombok.Data;

@Data
public class ReceptionInfo {
    private Integer     id;
    private String      text;
    private Integer     receptionId;
    private String      photo;
    private Language language;
}
