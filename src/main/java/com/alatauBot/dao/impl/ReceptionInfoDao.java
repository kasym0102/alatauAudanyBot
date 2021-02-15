package com.alatauBot.dao.impl;

import com.alatauBot.dao.AbstractDao;
import com.alatauBot.entity.custom.ReceptionInfo;
import com.alatauBot.entity.enums.Language;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class ReceptionInfoDao extends AbstractDao<ReceptionInfo> {

    public List<ReceptionInfo>          getAll() {
        sql = "SELECT * FROM RECEPTION_INFO";
        return getJdbcTemplate().query(sql, this::mapper);
    }

    public void                         updateInfoText(ReceptionInfo receptionInfo) {
        sql = "UPDATE RECEPTION_INFO SET TEXT = ? WHERE ID = ? AND LANG_ID = ?";
        getJdbcTemplate().update(sql, receptionInfo.getText(), receptionInfo.getId(), getLanguage().getId());
    }

    public void                         updateInfoPhoto(ReceptionInfo receptionInfo) {
        sql = "UPDATE RECEPTION_INFO SET PHOTO = ? WHERE ID = ?";
        getJdbcTemplate().update(sql, receptionInfo.getPhoto(), receptionInfo.getId());
    }

    public ReceptionInfo                getReceptionId(int receptionId) {
        sql = "SELECT * FROM RECEPTION_INFO WHERE RECEPTION_ID = ? AND LANG_ID = ?";
        return getJdbcTemplate().queryForObject(sql, setParam(receptionId, getLanguage().getId()), this::mapper);
    }

    @Override
    protected ReceptionInfo mapper(ResultSet rs, int index) throws SQLException {
        ReceptionInfo receptionInfo = new ReceptionInfo();
        receptionInfo.setId                             (rs.getInt      (1));
        receptionInfo.setText                           (rs.getString   (2));
        receptionInfo.setReceptionId                    (rs.getInt      (3));
        receptionInfo.setPhoto                          (rs.getString   (4));
        receptionInfo.setLanguage(Language.getById      (rs.getInt      (5)));
        return receptionInfo;
    }
}
