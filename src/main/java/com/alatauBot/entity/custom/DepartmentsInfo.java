package com.alatauBot.entity.custom;

import com.alatauBot.entity.enums.Language;
import lombok.Data;

@Data
public class DepartmentsInfo {
    private Integer     id;
    private String      text;
    private String      photo;
    private String      document;
    private Language language;
}
