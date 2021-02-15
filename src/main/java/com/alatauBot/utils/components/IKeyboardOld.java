package com.alatauBot.utils.components;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

public class IKeyboardOld {

    private int[]                                  buttonCounts = null;
    private List<List<InlineKeyboardButton>>       inlineList;
    private List<List<List<InlineKeyboardButton>>> inlineTables;

    public void     next(int... buttonCounts) {
        this.buttonCounts = buttonCounts;
        setRows();
    }

    private void    setRows() {
        if (inlineTables == null) {
            inlineTables    = new ArrayList<>();
            inlineList      = new ArrayList<>();
        } else {
            if (inlineList.size() == 0) {
                throw new RuntimeException("You have not added any inline buttons");
            } else {
                inlineTables.add(inlineList);
                inlineList = new ArrayList<>();
            }
        }
    }
}
