package com.alatauBot.entity.custom;

import com.alatauBot.entity.enums.Language;
import lombok.Data;

@Data
public class DepartmentsType {
    private Integer     id;
    private String      name;
    private Language language;
}
