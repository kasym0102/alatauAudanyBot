package com.alatauBot.dao.impl;

import com.alatauBot.dao.AbstractDao;
import com.alatauBot.entity.custom.AppealEmployee;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class AppealEmployeeDao extends AbstractDao<AppealEmployee> {

    public List<AppealEmployee>     getAll(){
        sql = "SELECT * FROM APPEAL_EMPLOYEE";
        return getJdbcTemplate().query(sql, this::mapper);
    }

    public List<AppealEmployee>     getAllByTegId(int tegId){
        sql = "SELECT * FROM APPEAL_EMPLOYEE WHERE APPEAL_TEG_ID = ?";
        return getJdbcTemplate().query(sql, setParam(tegId), this::mapper);
    }

    public int getChatIdByTegId(int tegId){
        sql = "SELECT CHAT_ID FROM APPEAL_EMPLOYEE WHERE APPEAL_TEG_ID = ?";
        return getJdbcTemplate().queryForObject(sql, setParam(tegId), Integer.class);
    }

    public List<AppealEmployee> getAllByChatId(long chatId){
            sql = "SELECT * FROM APPEAL_EMPLOYEE WHERE CHAT_ID = ? ORDER BY APPEAL_TEG_ID ASC";
        return getJdbcTemplate().query(sql, setParam(chatId), this::mapper);
    }

    public AppealEmployee getOneByTegId(int tegId){
        sql = "SELECT * FROM APPEAL_EMPLOYEE WHERE APPEAL_TEG_ID = ?";
        return getJdbcTemplate().queryForObject(sql, setParam(tegId), this::mapper);
    }

    public AppealEmployee getOneByChatId(long id){
        sql = "SELECT * FROM APPEAL_EMPLOYEE WHERE CHAT_ID = ?";
        return getJdbcTemplate().queryForObject(sql, setParam(id), this::mapper);
    }

    public boolean isEmployeeCategory(long chatId) {
        sql = "SELECT count(*) FROM APPEAL_EMPLOYEE WHERE CHAT_ID = ?";
        int count  = getJdbcTemplate().queryForObject(sql, setParam(chatId), Integer.class);
        if (count > 0) return true;
        return false;
    }

    public List<AppealEmployee>     getAllByReceptionTypeId(int id){
        sql = "SELECT * FROM APPEAL_EMPLOYEE WHERE RECEPTION_TYPE_ID = ?";
        return getJdbcTemplate().query(sql,setParam(id) ,this::mapper);
    }

    public List<AppealEmployee>     getAllById(int id){
        sql = "SELECT * FROM APPEAL_EMPLOYEE WHERE ID = ?";
        return getJdbcTemplate().query(sql, setParam(id), this::mapper);
    }

    public void                     delete(int id){
        sql = "DELETE FROM APPEAL_EMPLOYEE WHERE ID = ?";
        getJdbcTemplate().update(sql, id);
    }

    public List<AppealEmployee>     getOneById(int id){
        sql = "SELECT * FROM APPEAL_EMPLOYEE WHERE APPEAL_TEG_ID = ?";
        return getJdbcTemplate().query(sql, setParam(id), this::mapper);
    }

    public int                      insert(AppealEmployee appealEmployee) {
        sql = "INSERT INTO APPEAL_EMPLOYEE(NAME, CHAT_ID, APPEAL_TEG_ID) VALUES(?, ?, ?)";
        return (int) getDBUtils().updateForKeyId(sql, setParam(appealEmployee.getName(), appealEmployee.getChatId(), appealEmployee.getTegId()));
    }

    @Override
    protected AppealEmployee mapper(ResultSet rs, int index) throws SQLException {
        AppealEmployee appealEmployee = new AppealEmployee();
        appealEmployee.setId                (rs.getInt      (1));
        appealEmployee.setName              (rs.getString   (2));
        appealEmployee.setChatId            (rs.getLong     (3));
        appealEmployee.setTegId             (rs.getInt      (4));
        return appealEmployee;
    }
}
