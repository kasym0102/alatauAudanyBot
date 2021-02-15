package com.alatauBot.dao.impl;

import com.alatauBot.dao.AbstractDao;

import java.sql.ResultSet;
import java.sql.SQLException;

public class MapsDao extends AbstractDao<String> {

    public String       getMapsPath(int id) {
        sql = "SELECT PATH FROM MAPS WHERE ID = ?";
        return getJdbcTemplate().queryForObject(sql, setParam(id), String.class);
    }

    @Override
    protected String    mapper(ResultSet rs, int index) throws SQLException {
        return rs.getString(1);
    }
}
