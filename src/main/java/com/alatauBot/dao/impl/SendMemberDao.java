package com.alatauBot.dao.impl;

import com.alatauBot.dao.AbstractDao;
import com.alatauBot.entity.custom.SendMember;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class SendMemberDao extends AbstractDao<SendMember> {
    public      void                insertOrUpdate(SendMember sendMember) {
        if (isRegistered(sendMember.getChatId())) {
            update(sendMember);
        } else {
            insert(sendMember);
        }
    }

    public      boolean             isRegistered(long chatId) {
        sql = "SELECT COUNT(*) FROM SEND_MEMBER WHERE CHAT_ID = ?";
        if (getJdbcTemplate().queryForObject(sql, setParam(chatId), Integer.class) != 0) return true;
        return false;
    }

    public      void                insert(SendMember sendMember) {
        sql = "INSERT INTO SEND_MEMBER(CHAT_ID, RECEPTION_ID, REGISTRATION_ID) VALUES ( ?,?,? )";
        getJdbcTemplate().update(sql, sendMember.getChatId(), sendMember.getReceptionId(), sendMember.getRegistrationId());
    }

    private     void                update(SendMember sendMember) {
        sql = "UPDATE SEND_MEMBER SET RECEPTION_ID = ?, REGISTRATION_ID = ? WHERE CHAT_ID = ?";
        getJdbcTemplate().update(sql, sendMember.getReceptionId(), sendMember.getRegistrationId(), sendMember.getChatId());
    }

    public      SendMember          getByChatId(long chatId) {
        sql = "SELECT * FROM SEND_MEMBER WHERE CHAT_ID = ?";
        return getJdbcTemplate().queryForObject(sql, setParam(chatId), this::mapper);
    }

    public      List<SendMember>    getAllByChatId(long chatId) {
        sql = "SELECT * FROM SEND_MEMBER WHERE CHAT_ID = ?";
        return getJdbcTemplate().query(sql, setParam(chatId), this::mapper);
    }

    @Override
    protected   SendMember  mapper(ResultSet rs, int index) throws SQLException {
        SendMember sendMember = new SendMember();
        sendMember.setId                (rs.getInt      (1));
        sendMember.setChatId            (rs.getLong     (2));
        sendMember.setReceptionId       (rs.getInt      (3));
        sendMember.setRegistrationId    (rs.getInt      (4));
        return sendMember;
    }
}
