package com.alatauBot.dao.impl;

import com.alatauBot.dao.AbstractDao;
import com.alatauBot.entity.standart.Admin;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class AdminDao extends AbstractDao<Admin> {

    public List<Admin>              getAll(){
        sql = "SELECT * FROM ADMIN";
        return getJdbcTemplate().query(sql, this::mapper);
    }

    public boolean                  isAdmin(long chatId) {
        sql = "SELECT count(*) FROM ADMIN WHERE USER_ID = ?";
        int count  = getJdbcTemplate().queryForObject(sql, setParam(chatId), Integer.class);
        if (count > 0) return true;
        return false;
    }

    public void                     insertAdmin(Admin admin){
        sql = "INSERT INTO ADMIN (USER_ID, COMMENT)VALUES(?, ?)";
        getJdbcTemplate().update(sql, setParam(admin.getUserId(), admin.getComment()));
    }

    public void                     delete(long chatId) {
        sql = "DELETE FROM ADMIN WHERE USER_ID = ?";
        getJdbcTemplate().update(sql, chatId);
    }

    @Override
    protected Admin     mapper(ResultSet rs, int index) throws SQLException {
        Admin admin = new Admin();
        admin.setId(rs.getInt(1));
        admin.setUserId(rs.getLong(2));
        admin.setComment(rs.getString(3));
        return admin;
    }
}
