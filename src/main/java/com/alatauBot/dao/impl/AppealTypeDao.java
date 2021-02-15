package com.alatauBot.dao.impl;

import com.alatauBot.dao.AbstractDao;
import com.alatauBot.entity.custom.AppealType;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class AppealTypeDao extends AbstractDao<AppealType> {

    public List<AppealType>         getAppealTypes(){
        sql = "SELECT * FROM APPEAL_TYPE WHERE LANG_ID = ?";
        return getJdbcTemplate().query(sql, setParam(getLanguage().getId()), this::mapper);
    }

    public String                   getNameById(int id){
        sql = "SELECT NAME FROM APPEAL_TYPE WHERE ID = ? AND LANG_ID = ?";
        return getJdbcTemplate().queryForObject(sql, setParam(id, getLanguage().getId()), String.class);
    }

    public List<AppealType>         getAppealTypesByReceptionTypeId(int id){
        sql = "SELECT * FROM APPEAL_TYPE WHERE RECEPTION_TYPE_ID = ? AND LANG_ID = ?";
        return getJdbcTemplate().query(sql, setParam(id, getLanguage().getId()), this::mapper);
    }

    public List<AppealType>         getAppealTypesKz(AppealType appealType){
        sql = "SELECT * FROM APPEAL_TYPE WHERE LANG_ID = ?";
        return getJdbcTemplate().query(sql, setParam(appealType.getLanguageId()), this::mapper);
    }

    public AppealType               getAppealType(int appId){
        sql = "SELECT * FROM APPEAL_TYPE WHERE ID = ? AND LANG_ID = ?";
        return getJdbcTemplate().queryForObject(sql, setParam(appId, getLanguage().getId()), this::mapper);
    }

    public List<AppealType>         getAppealTypeByReceptionType(int appId){
        sql = "SELECT * FROM APPEAL_TYPE WHERE RECEPTION_TYPE_ID = ? AND LANG_ID = ?";
        return getJdbcTemplate().query(sql, setParam(appId, getLanguage().getId()), this::mapper);
    }

    public void                     update(AppealType appealType) {
        sql = "UPDATE APPEAL_TYPE SET NAME = ? WHERE ID = ? AND LANG_ID = ?";
        getJdbcTemplate().update(sql, appealType.getName(), appealType.getId(), appealType.getLanguageId());
    }

    @Override
    protected AppealType mapper(ResultSet rs, int index) throws SQLException {
        AppealType appealType = new AppealType();
        appealType.setId                (rs.getInt      (1));
        appealType.setName              (rs.getString   (2));
        appealType.setReceptionTypeId   (rs.getInt      (3));
        appealType.setLanguageId        (rs.getInt      (4));
        return appealType;
    }
}
