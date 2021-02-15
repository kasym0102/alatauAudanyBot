package com.alatauBot.dao.impl;

import com.alatauBot.dao.AbstractDao;
import com.alatauBot.entity.custom.DepartmentsInfo;
import com.alatauBot.entity.enums.Language;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class DepartmentsInfoDao extends AbstractDao<DepartmentsInfo> {

    public List<DepartmentsInfo>            getAllById(int id){
        sql = "SELECT * FROM DEPARTMENTS_INFO WHERE ID = ? AND LANG_ID = ?";
        return getJdbcTemplate().query(sql, setParam(id, getLanguage().getId()), this::mapper);
    }

    public DepartmentsInfo                  getOneById(int id){
        sql = "SELECT * FROM DEPARTMENTS_INFO WHERE ID = ? AND LANG_ID = ?";
        return getJdbcTemplate().queryForObject(sql, setParam(id, getLanguage().getId()), this::mapper);
    }

    public void                             updateDepartmentsInfoText(DepartmentsInfo departmentsInfo){
        sql = "UPDATE DEPARTMENTS_INFO SET TEXT = ? WHERE ID = ? AND LANG_ID = ?";
        getJdbcTemplate().update(sql, setParam(departmentsInfo.getText(), departmentsInfo.getId(), getLanguage().getId()));
    }

    public void                             updateDepartmentsInfoPhoto(DepartmentsInfo departmentsInfo){
        sql = "UPDATE DEPARTMENTS_INFO SET PHOTO = ? WHERE ID = ?";
        getJdbcTemplate().update(sql, setParam(departmentsInfo.getPhoto(), departmentsInfo.getId()));
    }

    @Override
    protected DepartmentsInfo mapper(ResultSet rs, int index) throws SQLException {
        DepartmentsInfo departmentsInfo = new DepartmentsInfo();
        departmentsInfo.setId           (rs.getInt                      (1));
        departmentsInfo.setText         (rs.getString                   (2));
        departmentsInfo.setPhoto        (rs.getString                   (3));
        departmentsInfo.setDocument     (rs.getString                   (4));
        departmentsInfo.setLanguage     (Language.getById(rs.getInt     (5)));
        return departmentsInfo;
    }
}
