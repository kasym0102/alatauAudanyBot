package com.alatauBot.dao.impl;

import com.alatauBot.dao.AbstractDao;
import com.alatauBot.entity.custom.DepartmentsType;
import com.alatauBot.entity.enums.Language;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class DepartmentsTypeDao extends AbstractDao<DepartmentsType> {

    public List<DepartmentsType>                getAll(){
        sql = "SELECT * FROM DEPARTMENTS_TYPE WHERE LANG_ID = ?";
        return getJdbcTemplate().query(sql, setParam(getLanguage().getId()), this::mapper);
    }

    @Override
    protected DepartmentsType mapper(ResultSet rs, int index) throws SQLException {
        DepartmentsType departmentsType = new DepartmentsType();
        departmentsType.setId           (rs.getInt                  (1));
        departmentsType.setName         (rs.getString               (2));
        departmentsType.setLanguage     (Language.getById(rs.getInt (3)));
        return departmentsType;
    }
}
