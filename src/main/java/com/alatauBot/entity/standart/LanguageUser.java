package com.alatauBot.entity.standart;

import com.alatauBot.entity.enums.Language;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LanguageUser {
    private Long        chatId;
    private Language language;
}
