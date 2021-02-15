package com.alatauBot.dao.impl;

import com.alatauBot.dao.AbstractDao;
import com.alatauBot.entity.custom.AppealEmployee;
import com.alatauBot.entity.custom.Director_departments;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class DirectorsDepartmentsDao extends AbstractDao<Director_departments> {

    public List<Director_departments>     getAll(){
        sql = "SELECT * FROM directors_department";
        return getJdbcTemplate().query(sql, this::mapper);
    }

    public List<Director_departments>     getAllByAppealTypeId(int appealTypeId){
        sql = "SELECT * FROM directors_department WHERE appealType_id = ?";
        return getJdbcTemplate().query(sql, setParam(appealTypeId), this::mapper);
    }

    public int getChatIdByAppealTypeId(int appealTypeId){
        sql = "SELECT CHAT_ID FROM directors_department WHERE appealType_id = ?";
        return getJdbcTemplate().queryForObject(sql, setParam(appealTypeId), Integer.class);
    }

    public List<Director_departments> getAllByChatId(long chatId){
            sql = "SELECT * FROM directors_department WHERE CHAT_ID = ? ORDER BY appealType_id ASC";
        return getJdbcTemplate().query(sql, setParam(chatId), this::mapper);
    }

    public Director_departments getOneByAppealTypeId(int appealTypeId){
        sql = "SELECT * FROM directors_department WHERE APPEAL_TEG_ID = ?";
        return getJdbcTemplate().queryForObject(sql, setParam(appealTypeId), this::mapper);
    }

    public Director_departments getOneByChatId(long chat_id){
        sql = "SELECT * FROM directors_department WHERE CHAT_ID = ?";
        return getJdbcTemplate().queryForObject(sql, setParam(chat_id), this::mapper);
    }

    public boolean isDirector(long chatId) {
        sql = "SELECT count(*) FROM directors_department WHERE CHAT_ID = ?";
        int count  = getJdbcTemplate().queryForObject(sql, setParam(chatId), Integer.class);
        if (count > 0) return true;
        return false;
    }

//    public List<AppealEmployee>     getAllByAppealTypeId(int id){
//        sql = "SELECT * FROM directors_department WHERE RECEPTION_TYPE_ID = ?";
//        return getJdbcTemplate().query(sql,setParam(id) ,this::mapper);
//    }

//    public List<AppealEmployee>     getById(int id){
//        sql = "SELECT * FROM directors_department WHERE id = ?";
//        return getJdbcTemplate().query(sql, setParam(id), this::mapper);
//    }

    public void delete(int id){
        sql = "DELETE FROM directors_department WHERE ID = ?";
        getJdbcTemplate().update(sql, id);
    }

    public List<Director_departments>     getOneById(int id){
        sql = "SELECT * FROM directors_department WHERE id = ?";
        return getJdbcTemplate().query(sql, setParam(id), this::mapper);
    }

    public int                      insert(Director_departments directors_departments) {
        sql = "INSERT INTO directors_department( chat_id, appealType_id) VALUES(?, ?)";
        return (int) getDBUtils().updateForKeyId(sql, setParam(directors_departments.getChatId(), directors_departments.getAppealType_id()));
    }

    @Override
    protected Director_departments mapper(ResultSet rs, int index) throws SQLException {

        Director_departments director_departments = new Director_departments();
        director_departments.setId                (rs.getInt      (1));
        director_departments.setChatId            (rs.getLong     (2));
        director_departments.setAppealType_id     (rs.getInt      (3));
        return director_departments;
    }
}
