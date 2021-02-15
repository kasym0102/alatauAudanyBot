package com.alatauBot.dao.impl;

import com.alatauBot.dao.AbstractDao;
import com.alatauBot.entity.enums.FileType;
import com.alatauBot.entity.enums.Language;
import com.alatauBot.entity.standart.Message;

import java.sql.ResultSet;
import java.sql.SQLException;

public class MessageDao extends AbstractDao<Message> {

    public Message          getMessage(long messageId) {
        sql = "SELECT * FROM MESSAGE WHERE ID = ? AND LANG_ID = ?";
        return getJdbcTemplate().queryForObject(sql, setParam(messageId, getLanguage().getId()), this::mapper);
    }

    public String           getMessageText(long id) {
        sql = "SELECT NAME FROM MESSAGE WHERE ID = ? AND LANG_ID = ?";
        return getJdbcTemplate().queryForObject(sql, setParam(id, getLanguage().getId()), String.class);
    }

    public String           getMessageText(long id, Language language) {
        sql = "SELECT NAME FROM MESSAGE WHERE ID = ? AND LANG_ID = ?";
        return getJdbcTemplate().queryForObject(sql, setParam(id, language.getId()), String.class);
    }

    public Message          getMessage(long messageId, Language language) {
        sql = "SELECT * FROM MESSAGE WHERE ID = ? AND LANG_ID = ?";
        return getJdbcTemplate().queryForObject(sql, setParam(messageId, language.getId()), this::mapper);
    }

    public void             update(Message message) {
        sql = "UPDATE MESSAGE SET NAME = ?, PHOTO = ?, FILE = ?, TYPE_FILE = ? WHERE ID = ? AND LANG_ID = ?";
        getJdbcTemplate().update(sql, message.getName(), message.getPhoto(), message.getFile(), message.getFileType() == null ? null : message.getFileType().name(),
                message.getId(), message.getLanguage().getId());
    }

    @Override
    protected Message   mapper(ResultSet rs, int index) throws SQLException {
        Message message = new Message();
        message.setId                   (rs.getInt                      (1));
        message.setName                 (rs.getString                   (2));
        message.setPhoto                (rs.getString                   (3));
        message.setKeyboardMarkUpId     (rs.getLong                     (4));
        message.setFile                 (rs.getString                   (5));
        message.setFileType             (rs.getString                   (6) != null ? FileType.valueOf(rs.getString(6)) : null);
        message.setLanguage             (Language.getById(rs.getInt     (7)));
        return message;
    }
}
