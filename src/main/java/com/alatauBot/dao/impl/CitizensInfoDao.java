package com.alatauBot.dao.impl;

import com.alatauBot.dao.AbstractDao;
import com.alatauBot.entity.custom.CitizensInfo;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CitizensInfoDao extends AbstractDao<CitizensInfo> {
    public CitizensInfo     get() {
        sql = "SELECT * FROM CITIZENS_INFO WHERE ID = ?";
        return getJdbcTemplate().queryForObject(sql, setParam(100), this::mapper);
    }

    public CitizensInfo     getById(int id) {
        sql = "SELECT * FROM CITIZENS_INFO WHERE ID = ?";
        return getJdbcTemplate().queryForObject(sql, setParam(id), this::mapper);
    }

    public void             update(CitizensInfo citizenInfo) {
        sql = "UPDATE CITIZENS_INFO SET DATE = ?, TIME = ? WHERE ID = ?";
        getJdbcTemplate().update(sql, citizenInfo.getDate(), citizenInfo.getTime(), citizenInfo.getId());
    }

    @Override
    protected CitizensInfo  mapper(ResultSet rs, int index) throws SQLException {
        CitizensInfo citizensInfo = new CitizensInfo();
        citizensInfo.setId          (rs.getInt      (1));
        citizensInfo.setDocument    (rs.getString   (2));
        citizensInfo.setDate        (rs.getDate     (3));
        citizensInfo.setTime        (rs.getString   (4));
        return citizensInfo;
    }
}
