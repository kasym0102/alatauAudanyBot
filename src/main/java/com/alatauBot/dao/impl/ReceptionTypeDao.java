package com.alatauBot.dao.impl;

import com.alatauBot.dao.AbstractDao;
import com.alatauBot.entity.custom.ReceptionType;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class ReceptionTypeDao extends AbstractDao<ReceptionType> {

    public List<ReceptionType>          getAllType() {
        sql = "SELECT * FROM RECEPTION_TYPE WHERE LANG_ID = ? ORDER BY ID ASC";
        return getJdbcTemplate().query(sql, setParam(getLanguage().getId()), this::mapper);
    }

    public List<ReceptionType>          getAll(int receptionTypeId) {
        sql = "SELECT * FROM RECEPTION_TYPE WHERE RECEPTION_ID = ? AND LANG_ID = ? ORDER BY ID ASC";
        return getJdbcTemplate().query(sql, setParam(receptionTypeId, getLanguage().getId()),  this::mapper);
    }

    public List<ReceptionType>          getAllByAppealTypeId(int id) {
        sql = "SELECT * FROM RECEPTION_TYPE WHERE APPEAL_TYPE_ID = ? AND LANG_ID = ?";
        return getJdbcTemplate().query(sql, setParam(id, getLanguage().getId()),  this::mapper);
    }

    public List<ReceptionType>          getAllByReceptionTypeId(int id) {
        sql = "SELECT * FROM RECEPTION_TYPE WHERE RECEPTION_TYPE_ID = ? AND LANG_ID = ?";
        return getJdbcTemplate().query(sql, setParam(id, getLanguage().getId()),  this::mapper);
    }

    public List<ReceptionType> getAllKz(ReceptionType receptionType) {
        sql = "SELECT * FROM RECEPTION_TYPE WHERE LANG_ID = ? ORDER BY id DESC"; //ORDER BY ID DESC
        return getJdbcTemplate().query(sql, setParam(receptionType.getLanguageId()),  this::mapper);
    }

    public String                       getReceptionText(int id){
        sql = "SELECT NAME FROM RECEPTION_TYPE WHERE ID = ? AND LANG_ID = ?";
        return getJdbcTemplate().queryForObject(sql, setParam(id, getLanguage().getId()), String.class);
    }

    public int                         getChatIdById(int id){
        sql = "SELECT CHAT_ID FROM RECEPTION_TYPE WHERE ID = ? AND LANG_ID = ?";
        return getJdbcTemplate().queryForObject(sql, setParam(id, getLanguage().getId()), Integer.class);
    }


    public ReceptionType                getById(int receptionId) {
        sql = "SELECT * FROM RECEPTION_TYPE WHERE ID = ? AND LANG_ID = ?";
        return getJdbcTemplate().queryForObject(sql, setParam(receptionId, getLanguage().getId()), this::mapper);
    }

    public long getChatIdByReceptionTypeId(int receptionTypeId){
        sql = "SELECT CHAT_ID FROM RECEPTION_TYPE WHERE ID = ? AND LANG_ID = ?";
        return getJdbcTemplate().queryForObject(sql, setParam(receptionTypeId, getLanguage().getId()), Long.class);
    }

    public void                         delete(int receptionId) {
        sql = "DELETE FROM RECEPTION_TYPE WHERE ID = ?";
        getJdbcTemplate().update(sql, receptionId);
    }

    public void                         update(ReceptionType reception) {
        sql = "UPDATE RECEPTION_TYPE SET NAME = ? WHERE ID = ? AND LANG_ID = ?";
        getJdbcTemplate().update(sql, reception.getName(), reception.getId(), reception.getLanguageId());
    }

    @Override
    protected ReceptionType mapper(ResultSet rs, int index) throws SQLException {
        ReceptionType receptionType = new ReceptionType();
        receptionType.setId             (rs.getInt      (1));
        receptionType.setName           (rs.getString   (2));
        receptionType.setReceptionId    (rs.getInt      (3));
        receptionType.setAppealTypeId   (rs.getInt      (4));
        receptionType.setLanguageId     (rs.getInt      (5));
        receptionType.setChatId         (rs.getLong     (6));
        receptionType.setComment        (rs.getString   (7));
        return receptionType;
    }
}
