package com.alatauBot.dao.impl;

import com.alatauBot.dao.AbstractDao;
import com.alatauBot.entity.enums.Language;
import com.alatauBot.entity.standart.Button;
import com.alatauBot.entity.standart.Keyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class KeyboardMarkUpDao extends AbstractDao<Keyboard> {

    private ButtonDao buttonDao;

    public                          KeyboardMarkUpDao() { buttonDao = factory.getButtonDao();}

    public ReplyKeyboard            select(long keyboardMarkUpId) {
        if (keyboardMarkUpId < 0) {
            ReplyKeyboardRemove keyboard = new ReplyKeyboardRemove();
            return keyboard;
        }
        if (keyboardMarkUpId == 0) {
            return null;
        }
        sql = "SELECT * FROM KEYBOARD WHERE ID = ?";
        return getKeyboard(getJdbcTemplate().queryForObject(sql, setParam(keyboardMarkUpId), this::mapper));
    }

    private ReplyKeyboard           getKeyboard(Keyboard keyboard) {
        String buttonIds = keyboard.getButtonIds();
        if (buttonIds == null) {
            return null;
        }
        String[] rows = buttonIds.split(";");
        if (keyboard.isInline()) {
            return getInlineKeyboard(rows);
        } else {
            return getReplyKeyboard(rows);
        }
    }

    private InlineKeyboardMarkup    getInlineKeyboard(String[] rowIds) {
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        for (String buttonIdsString : rowIds) {
            List<InlineKeyboardButton> row = new ArrayList<>();
            String[] buttonIds = buttonIdsString.split(",");
            for (String buttonId : buttonIds) {
                Button buttonFromDb = buttonDao.getButton(Integer.parseInt(buttonId));
                InlineKeyboardButton button = new InlineKeyboardButton();
                String buttonText = buttonFromDb.getName();
                button.setText(buttonText);
                String url = buttonFromDb.getUrl();
                if (url != null) {
                    button.setUrl(url);
                } else {
                    buttonText = buttonText.length() < 64 ? buttonText : buttonText.substring(0,64);
                    button.setCallbackData(buttonText);
                }
                row.add(button);
            }
            rows.add(row);
        }
        keyboard.setKeyboard(rows);
        return keyboard;
    }

    private ReplyKeyboard           getReplyKeyboard(String[] rows) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setResizeKeyboard(true);
        List<KeyboardRow> keyboardRowList = new ArrayList<>();
        boolean isRequestContact = false;
        for (String buttonIdsString : rows) {
            KeyboardRow keyboardRow = new KeyboardRow();
            String[] buttonIds = buttonIdsString.split(",");
            for (String buttonId : buttonIds) {
                Button buttonFromDb = buttonDao.getButton(Integer.parseInt(buttonId));
                KeyboardButton button = new KeyboardButton();
                String buttonText = buttonFromDb.getName();
                button.setText(buttonText);
                button.setRequestContact(buttonFromDb.isRequestContact());
                if (buttonFromDb.isRequestContact()) {
                    isRequestContact = true;
                }
                keyboardRow.add(button);
            }
            keyboardRowList.add(keyboardRow);
        }
        replyKeyboardMarkup.setKeyboard(keyboardRowList);
        replyKeyboardMarkup.setOneTimeKeyboard(isRequestContact);
        return replyKeyboardMarkup;
    }

    public ReplyKeyboard            selectForEdition(long keyboardMarkUpId, Language language) {
        if (keyboardMarkUpId < 0) {
            ReplyKeyboardRemove keyboard = new ReplyKeyboardRemove();
            return keyboard;
        }
        if (keyboardMarkUpId == 0) {
            return null;
        }
        sql = "SELECT * FROM KEYBOARD WHERE ID = ?";
        return getKeyboardForEdition(getJdbcTemplate().queryForObject(sql, setParam(keyboardMarkUpId), this::mapper), language);
    }

    private ReplyKeyboard           getKeyboardForEdition(Keyboard keyboard, Language language) {
        String buttonIds = keyboard.getButtonIds();
        if (buttonIds == null) {
            return null;
        }
        String[] rows = buttonIds.split(";");
        return getInlineKeyboardForEdition(rows, language);
    }

    private InlineKeyboardMarkup    getInlineKeyboardForEdition(String[] rowIds, Language language) {
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        for (String buttonIdsString : rowIds) {
            List<InlineKeyboardButton> row = new ArrayList<>();
            String[] buttonIds = buttonIdsString.split(",");
            for (String buttonId : buttonIds) {
                Button buttonFromDb = buttonDao.getButton(Integer.parseInt(buttonId), language);
                InlineKeyboardButton button = new InlineKeyboardButton();
                String buttonText = buttonFromDb.getName();
                button.setText(buttonText);
                String url = buttonFromDb.getUrl();
                if (url != null) {
                    button.setUrl(url);
                } else {
                    button.setCallbackData(buttonId);
                }
                row.add(button);
            }
            rows.add(row);
        }
        keyboard.setKeyboard(rows);
        return keyboard;
    }

    public boolean                  isInline(long keyboardMarkUpId) {
        sql = "SELECT INLINE FROM KEYBOARD WHERE ID = ?";
        return getJdbcTemplate().queryForObject(sql, setParam(keyboardMarkUpId), Boolean.class);
    }

    public List<Button>             getListForEdit(int keyId) {
        List<Button> list = new ArrayList<>();
        for (String x : Arrays.asList(getButtonString(keyId).split(";"))) {
            list.add(buttonDao.getButton(Integer.parseInt(x)));
        }
        return list;
    }

    public String                   getButtonString(int id) {
        sql = "SELECT BUTTON_IDS FROM KEYBOARD WHERE ID = ?";
        return getJdbcTemplate().queryForObject(sql, setParam(id), String.class);
    }

    @Override
    protected Keyboard              mapper(ResultSet rs, int index) throws SQLException {
        Keyboard keyboard = new Keyboard();
        keyboard.setId(rs.getInt(1));
        keyboard.setButtonIds(rs.getString(2));
        keyboard.setInline(rs.getBoolean(3));
        keyboard.setComment(rs.getString(4));
        return keyboard;
    }
}
