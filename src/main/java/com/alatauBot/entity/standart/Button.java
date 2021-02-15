package com.alatauBot.entity.standart;

import com.alatauBot.entity.enums.Language;
import lombok.Data;

@Data
public class Button {
    private Integer     id;
    private String      name;
    private Integer     commandId;
    private String      url;
    private Language language;
    private boolean     requestContact;
    private Integer     messageId;
}
