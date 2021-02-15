package com.alatauBot.dao.impl;

import com.alatauBot.dao.AbstractDao;
import com.alatauBot.entity.custom.CitizensRegistration;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

public class CitizensRegistrationDao extends AbstractDao<CitizensRegistration> {

    public void                         insert(CitizensRegistration registration) {
        sql = "INSERT INTO CITIZENS_REGISTRATION(CHAT_ID, RECEPTION_ID, IIN, QUESTION, STATUS, DATE, CITIZENS_DATE, CITIZENS_TIME) VALUES ( ?,?,?,?,?,?,?,? )";
        getJdbcTemplate().update(sql, registration.getChatId(), registration.getReceptionId(), registration.getIin(), registration.getQuestion(), registration.getStatus(), registration.getDate(), registration.getCitizensDate(), registration.getCitizensTime());
    }

    public List<CitizensRegistration>   getAll(int receptionId) {
        sql = "SELECT * FROM CITIZENS_REGISTRATION WHERE RECEPTION_ID = ? AND STATUS = 'Записан'";
        return getJdbcTemplate().query(sql, setParam(receptionId), this::mapper);
    }

    public List<CitizensRegistration>   getRegistrationsByTime(Date dateBegin, Date deadline, int receptionId) {
        sql = "SELECT * FROM CITIZENS_REGISTRATION WHERE DATE BETWEEN ? AND  ? AND RECEPTION_ID = ? ORDER BY ID";
        return getJdbcTemplate().query(sql, setParam(dateBegin, deadline, receptionId), this::mapper);
    }

    public List<CitizensRegistration>   getByReceptionId(int receptionId) {
        sql = "SELECT * FROM CITIZENS_REGISTRATION WHERE RECEPTION_ID = ? AND STATUS = 'Записан'";
        try {
            return getJdbcTemplate().query(sql, setParam(receptionId), this::mapper);
        } catch (Exception e) {
            if (e.getMessage().contains("Incorrect result size: expected 1, actual 0")) return null;
            throw e;
        }
    }

    public CitizensRegistration         getById(int id) {
        sql = "SELECT * FROM CITIZENS_REGISTRATION WHERE ID = ? AND STATUS = 'Записан'";
        return getJdbcTemplate().queryForObject(sql, setParam(id), this::mapper);
    }

    public CitizensRegistration         getOneById(int id) {
        sql = "SELECT * FROM CITIZENS_REGISTRATION WHERE ID = ? AND STATUS = 'Записан'";
        return getJdbcTemplate().queryForObject(sql, setParam(id), this::mapper);
    }

    public void                         update(CitizensRegistration registration) {
        sql = "UPDATE CITIZENS_REGISTRATION SET STATUS = ? WHERE ID = ?";
        getJdbcTemplate().update(sql, registration.getStatus(), registration.getId());
    }

    @Override
    protected CitizensRegistration      mapper(ResultSet rs, int index) throws SQLException {
        CitizensRegistration citizensRegistration = new CitizensRegistration();
        citizensRegistration.setId              (rs.getInt          (1));
        citizensRegistration.setChatId          (rs.getLong         (2));
        citizensRegistration.setReceptionId     (rs.getInt          (3));
        citizensRegistration.setIin             (rs.getString       (4));
        citizensRegistration.setQuestion        (rs.getString       (5));
        citizensRegistration.setStatus          (rs.getString       (6));
        citizensRegistration.setDate            (rs.getDate         (7));
        citizensRegistration.setCitizensDate    (rs.getString         (8));
        citizensRegistration.setCitizensTime    (rs.getString       (9));
        return citizensRegistration;
    }
}
