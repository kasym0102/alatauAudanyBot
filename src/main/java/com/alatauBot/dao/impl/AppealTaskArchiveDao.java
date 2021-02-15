package com.alatauBot.dao.impl;

import com.alatauBot.dao.AbstractDao;
import com.alatauBot.entity.custom.AppealTaskArchive;
import com.alatauBot.entity.enums.FileType;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class AppealTaskArchiveDao extends AbstractDao<AppealTaskArchive> {

    public List<AppealTaskArchive>          getAll(){
        sql = "SELECT * FROM APPEAL_TASK_ARCHIVE";
        return getJdbcTemplate().query(sql, this::mapper);
    }

    public void                             insertOrUpdate(AppealTaskArchive appealTaskArchive){
        if (getTaskById(appealTaskArchive.getTaskId()) > 0){
            update(appealTaskArchive);
        }else {
            insert(appealTaskArchive);
        }
    }

    public void                             updateAppraisal(int appraisalId, int taskId){
        sql = "UPDATE APPEAL_TASK_ARCHIVE SET APPRAISAL_ID = ? WHERE TASK_ID = ?";
        getJdbcTemplate().update(sql, setParam(appraisalId, taskId));
    }

    public AppealTaskArchive getOneByTaskId(int taskId){
        sql = "SELECT * FROM APPEAL_TASK_ARCHIVE WHERE TASK_ID = ?";
        return getJdbcTemplate().queryForObject(sql, setParam(taskId), this::mapper);
    }

    public List<AppealTaskArchive>          getAllByTaskId(int tegId){
        sql = "SELECT * FROM APPEAL_TASK_ARCHIVE WHERE TASK_ID = ?";
        return getJdbcTemplate().query(sql, setParam(tegId), this::mapper);
    }

    private int                             getTaskById(int appealTaskId){
        sql = "SELECT COUNT(*) FROM APPEAL_TASK_ARCHIVE WHERE TASK_ID = ?";
        return getJdbcTemplate().queryForObject(sql, setParam(appealTaskId), Integer.class);
    }

    private void                            insert(AppealTaskArchive appealTaskArchive){
        sql = "INSERT INTO APPEAL_TASK_ARCHIVE(TEXT, FILE, FILE_TYPE, TASK_ID)VALUES(?,?,?,?)";
        getJdbcTemplate().update(sql, appealTaskArchive.getText(), appealTaskArchive.getFile(), appealTaskArchive.getFileType() == null ? null : appealTaskArchive.getFileType().name(), appealTaskArchive.getTaskId());
    }

    private void                            update(AppealTaskArchive appealTaskArchive){
        sql = "UPDATE APPEAL_TASK_ARCHIVE SET TEXT = ?, FILE = ?, FILE_TYPE = ? WHERE TASK_ID = ?";
        getJdbcTemplate().update(sql, setParam(appealTaskArchive.getText(), appealTaskArchive.getFile(), appealTaskArchive.getFileType() == null ? null : appealTaskArchive.getFileType().name(), appealTaskArchive.getTaskId()));
    }

    @Override
    protected AppealTaskArchive mapper(ResultSet rs, int index) throws SQLException {
        AppealTaskArchive appealTaskArchive = new AppealTaskArchive();
        appealTaskArchive.setId                 (rs.getInt      (1));
        appealTaskArchive.setText               (rs.getString   (2));
        appealTaskArchive.setFile               (rs.getString   (3));
        appealTaskArchive.setFileType           (rs.getString   (4) != null ? FileType.valueOf(rs.getString(4)) : null);
        appealTaskArchive.setTaskId             (rs.getInt      (5));
        return appealTaskArchive;
    }
}
