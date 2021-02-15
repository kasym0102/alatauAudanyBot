package com.alatauBot.dao.impl;

import com.alatauBot.dao.AbstractDao;
import com.alatauBot.entity.custom.ReceptionEmployee;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class ReceptionEmployeeDao extends AbstractDao<ReceptionEmployee> {

    public List<ReceptionEmployee>      getReceptionEmployeeById(int receptionId){
        sql = "SELECT * FROM RECEPTION_EMPLOYEE WHERE RECEPTION_ID = ?";
        return getJdbcTemplate().query(sql, setParam(receptionId), this::mapper);
    }

    public ReceptionEmployee            getByChatId(long chatId) {
        sql = "SELECT * FROM RECEPTION_EMPLOYEE WHERE CHAT_ID = ?";
        return getJdbcTemplate().queryForObject(sql, setParam(chatId), this::mapper);
    }

    public List<ReceptionEmployee>      getAllByChatId(long chatId) {
        sql = "SELECT * FROM RECEPTION_EMPLOYEE WHERE CHAT_ID = ?";
        return getJdbcTemplate().query(sql, setParam(chatId), this::mapper);
    }

    public void                         delete(int receptionEmpId){
        sql = "DELETE FROM RECEPTION_EMPLOYEE WHERE ID = ?";
        getJdbcTemplate().update(sql, setParam(receptionEmpId));
    }

    public void                         insert(ReceptionEmployee receptionEmployee){
        sql = "INSERT INTO RECEPTION_EMPLOYEE(RECEPTION_ID, NAME, CHAT_ID) VALUES(?, ?, ?)";
        getJdbcTemplate().update(sql, setParam(receptionEmployee.getReceptionId(), receptionEmployee.getName(), receptionEmployee.getChatId()));
    }

    public boolean                      isReceptionEmployee(long chatId) {
        sql         = "SELECT count(*) FROM RECEPTION_EMPLOYEE WHERE CHAT_ID = ?";
        int count   = getJdbcTemplate().queryForObject(sql, setParam(chatId), Integer.class);
        if (count > 0) return true;
        return false;
    }

    @Override
    protected ReceptionEmployee mapper(ResultSet rs, int index) throws SQLException {
        ReceptionEmployee receptionEmployee = new ReceptionEmployee();
        receptionEmployee.setId             (rs.getInt      (1));
        receptionEmployee.setReceptionId    (rs.getInt      (2));
        receptionEmployee.setName           (rs.getString   (3));
        receptionEmployee.setChatId         (rs.getLong     (4));
        return receptionEmployee;
    }
}
