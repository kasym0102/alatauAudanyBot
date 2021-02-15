package com.alatauBot.dao.impl;

import com.alatauBot.dao.AbstractDao;
import com.alatauBot.entity.custom.AppealTeg;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class AppealTegDao extends AbstractDao<AppealTeg> {

    public List<AppealTeg> getAll(){
        sql = "SELECT * FROM APPEAL_TEG WHERE LANG_ID = ?";
        return getJdbcTemplate().query(sql, setParam(getLanguage().getId()), this::mapper);
    }

    public String getNameById(int id){
        sql = "SELECT NAME FROM APPEAL_TEG WHERE ID = ? AND LANG_ID = ?";
        return getJdbcTemplate().queryForObject(sql, setParam(id, getLanguage().getId()), String.class);
    }

    public AppealTeg getAppealTegById(int id){
        sql = "SELECT * FROM APPEAL_TEG WHERE ID = ? AND LANG_ID = ?";
        return getJdbcTemplate().queryForObject(sql, setParam(id, getLanguage().getId()), this::mapper);
    }

    public List<AppealTeg> getAll(int id){
        sql = "SELECT * FROM APPEAL_TEG WHERE LANG_ID = ?";
        return getJdbcTemplate().query(sql, setParam(id), this::mapper);
    }

    public AppealTeg getOneById(int id, int langId){
        sql = "SELECT * FROM APPEAL_TEG WHERE ID = ? AND LANG_ID = ?";
        return getJdbcTemplate().queryForObject(sql, setParam(id, langId), this::mapper);
    }

    public AppealTeg getOneById1(int id){
        sql = "SELECT * FROM APPEAL_TEG WHERE ID = ? AND LANG_ID = ?";
        return getJdbcTemplate().queryForObject(sql, setParam(id, getLanguage().getId()), this::mapper);
    }

    public void delete (int id){
        sql = "DELETE FROM APPEAL_TEG WHERE ID = ?";
        getJdbcTemplate().update(sql, id);
    }

    public void insert (AppealTeg appealTeg){
        sql = "INSERT INTO APPEAL_TEG(NAME, APPEAL_TYPE_ID, LANG_ID) VALUES(?, ?, ?)";
        getJdbcTemplate().update(sql, appealTeg.getName(), appealTeg.getAppealTypeId(), appealTeg.getLang_id());
    }

    public void insertKz (AppealTeg appealTeg){
        sql = "INSERT INTO APPEAL_TEG(ID, NAME, APPEAL_TYPE_ID, RECEPTION_TYPE_ID, LANG_ID) VALUES(?, ?, ?, ?, ?)";
        getJdbcTemplate().update(sql, appealTeg.getId(), appealTeg.getName(), appealTeg.getAppealTypeId(), appealTeg.getReceptionTypeId(), appealTeg.getLang_id());
    }

    public List<AppealTeg> appealTegList(int appealTypeId){
        sql = "SELECT * FROM APPEAL_TEG WHERE APPEAL_TYPE_ID = ? AND LANG_ID = ?";
        return getJdbcTemplate().query(sql, setParam(appealTypeId, getLanguage().getId()), this::mapper);
    }

    public List<AppealTeg> appealTegList(AppealTeg appealTeg){
        sql = "SELECT * FROM APPEAL_TEG WHERE APPEAL_TYPE_ID = ? AND LANG_ID = ?";
        return getJdbcTemplate().query(sql, setParam(appealTeg.getAppealTypeId(), appealTeg.getLang_id()), this::mapper);
    }

    public AppealTeg getByIdAppeal(int id, int appealId){
        sql = "SELECT * FROM APPEAL_TEG WHERE ID = ? AND APPEAL_TYPE_ID = ? AND LANG_ID = ?";
        return getJdbcTemplate().queryForObject(sql, setParam(id, appealId, getLanguage().getId()), this::mapper);
    }

    public AppealTeg getByNameAppeal(String appealTeg){
        sql = "SELECT * FROM APPEAL_TEG WHERE NAME = ? AND LANG_ID = ?";
        return getJdbcTemplate().queryForObject(sql, setParam(appealTeg, getLanguage().getId()), this::mapper);
    }

    public String getNameByName(String name){
        sql = "SELECT NAME FROM APPEAL_TEG WHERE NAME = ? AND LANG_ID = ?";
        return getJdbcTemplate().queryForObject(sql, setParam(name, getLanguage().getId()), String.class);
    }

    public void updateAll(AppealTeg appealTeg) {
        sql = "UPDATE APPEAL_TEG SET APPEAL_TYPE_ID = ? WHERE ID = ? AND LANG_ID = ?";
        getJdbcTemplate().update(sql, setParam(appealTeg.getAppealTypeId(), appealTeg.getId(), appealTeg.getLang_id()));
    }

    public int update(AppealTeg appealTeg) {
        sql = "UPDATE APPEAL_TEG SET NAME = ? WHERE ID = ? AND LANG_ID = ?";
        return getJdbcTemplate().update(sql, setParam(appealTeg.getName(), appealTeg.getId(), appealTeg.getLang_id()));
    }

    public int getMaxId() {
        sql = "SELECT MAX(ID) FROM APPEAL_TEG";
        return getJdbcTemplate().queryForObject(sql, Integer.class);
    }

    public int getTegId(int id) {
        sql = "SELECT APPEAL_EMPLOYEE_ID FROM APPEAL_TEG WHERE ID = ? AND LANG_ID = ?";
        return getJdbcTemplate().queryForObject(sql,setParam(id, 1), Integer.class);
    }

    public void updateDescription(Integer id, int id1, String updateMessageText) {
        sql = "UPDATE APPEAL_TEG SET DESC = ? WHERE ID = ? AND LANG_ID = ?";
        getJdbcTemplate().update(sql, updateMessageText, id, id1);
    }

    @Override
    protected AppealTeg mapper(ResultSet rs, int index) throws SQLException {
        AppealTeg appealTeg = new AppealTeg();
        appealTeg.setId                         (rs.getInt      (1));
        appealTeg.setName                       (rs.getString   (2));
//        appealTeg.setDesc                       (rs.getString   (3));
        appealTeg.setAppealTypeId               (rs.getInt      (3));
        appealTeg.setReceptionTypeId            (rs.getInt      (4));
        appealTeg.setLang_id                    (rs.getInt      (5));
        return appealTeg;
    }
}
