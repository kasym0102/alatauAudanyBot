package com.alatauBot.entity.standart;

import com.alatauBot.entity.enums.FileType;
import com.alatauBot.entity.enums.Language;
import lombok.Data;

@Data
public class Message {
    private Integer     id;
    private String   name;
    private String   photo;
    private long     keyboardMarkUpId;
    private String   file;
    private FileType fileType;
    private Language language;

    public void setFile(String file, FileType fileType) {
        this.file       = file;
        this.fileType   = fileType;
    }
}
