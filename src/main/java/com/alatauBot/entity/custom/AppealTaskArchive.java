package com.alatauBot.entity.custom;

import com.alatauBot.entity.enums.FileType;
import lombok.Data;

@Data
public class AppealTaskArchive {
    private Integer     id;
    private String      text;
    private String      file;
    private FileType    fileType;
    private Integer     taskId;

    public void setFile(String file, FileType fileType) {
        this.file       = file;
        this.fileType   = fileType;
    }
}
