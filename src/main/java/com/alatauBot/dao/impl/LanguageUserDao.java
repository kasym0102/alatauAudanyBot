package com.alatauBot.dao.impl;

import com.alatauBot.dao.AbstractDao;
import com.alatauBot.entity.enums.Language;
import com.alatauBot.entity.standart.LanguageUser;

import java.sql.ResultSet;
import java.sql.SQLException;

public class LanguageUserDao extends AbstractDao<LanguageUser> {

    public void             insertOrUpdate(LanguageUser languageUser) {
        if (isRegistered(languageUser.getChatId())) {
            update(languageUser);
        } else {
            insert(languageUser);
        }
    }

    private void            insert(LanguageUser languageUser) {
        sql = "INSERT INTO LANG_USER (CHAT_ID, LANG_ID) VALUES (?,?)";
        getJdbcTemplate().update(sql, languageUser.getChatId(), languageUser.getLanguage().getId());
    }

    private void            update(LanguageUser languageUser) {
        sql = "UPDATE LANG_USER SET LANG_ID = ? WHERE CHAT_ID = ?";
        getJdbcTemplate().update(sql, languageUser.getLanguage().getId(), languageUser.getChatId());
    }

    public LanguageUser     getByChatId(long chatId) {
        sql = "SELECT * FROM LANG_USER WHERE CHAT_ID = ?";
        LanguageUser languageUser = null;
        try {
            languageUser = getJdbcTemplate().queryForObject(sql, setParam(chatId), this::mapper);
        } catch (Exception e) {
        }
        return languageUser;
    }

    public boolean          isRegistered(long chatId) {
        sql = "SELECT COUNT(*) FROM LANG_USER WHERE CHAT_ID = ?";
        if (getJdbcTemplate().queryForObject(sql, setParam(chatId), Integer.class) != 0) {
            return true;
        }
        return false;
    }

    @Override
    protected LanguageUser  mapper(ResultSet rs, int index) throws SQLException {
        LanguageUser languageUser = new LanguageUser();
        languageUser.setChatId      (rs.getLong                     (1));
        languageUser.setLanguage    (Language.getById(rs.getInt     (2)));
        return languageUser;
    }
}
