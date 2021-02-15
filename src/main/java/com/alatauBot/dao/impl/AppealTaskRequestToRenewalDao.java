package com.alatauBot.dao.impl;

import com.alatauBot.dao.AbstractDao;
import com.alatauBot.entity.custom.AppealTaskRequestToRenewal;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class AppealTaskRequestToRenewalDao extends AbstractDao<AppealTaskRequestToRenewal> {
    // 1 = на рассмотрении 2 = принято 3 = отклонено

    public boolean isExistByTaskId(int taskId){
        sql = "SELECT COUNT (*) FROM REQUESTS_FROM_RESPONSIBLE WHERE APPEAL_TASK_ID =? AND ACCEPTED = ?";
        return getJdbcTemplate().queryForObject(sql, setParam(taskId, 1), Integer.class) > 0;
    }

    public List<AppealTaskRequestToRenewal> getAll() {
        sql = "SELECT * FROM REQUESTS_FROM_RESPONSIBLE WHERE ACCEPTED = 1";
        return getJdbcTemplate().query(sql, this::mapper);
    }

    public AppealTaskRequestToRenewal getByIdAppealTaskRequestToRenewal(int id){
        sql = "SELECT * FROM REQUESTS_FROM_RESPONSIBLE WHERE APPEAL_TASK_ID = ?";
        return getJdbcTemplate().queryForObject(sql, setParam(id), this::mapper);
    }

    public int insertAppealTask(AppealTaskRequestToRenewal appealTaskRequestToRenewal) {
        sql = "INSERT INTO REQUESTS_FROM_RESPONSIBLE ( RESPONSIBLE_CHAT_ID, APPEAL_TASK_ID, NEW_DEADLINE, TEXT_CAUSE, ACCEPTED, OLD_DEADLINE) VALUES(?,?,?,?,?,?)";
        return (int) getDBUtils().updateForKeyId(sql, setParam(appealTaskRequestToRenewal.getChatId(), appealTaskRequestToRenewal.getTaskId(),
                appealTaskRequestToRenewal.getDateDeadline(), appealTaskRequestToRenewal.getTextCause(), 1, appealTaskRequestToRenewal.getOldDateDeadline()));
    }

    public void updateAcceptedTrue(int id){
        sql = "UPDATE REQUESTS_FROM_RESPONSIBLE SET ACCEPTED = 2 WHERE ID = ?";
        getJdbcTemplate().update(sql, setParam(id));
    }
    public void updateStatus(){
        sql = "UPDATE REQUESTS_FROM_RESPONSIBLE SET ACCEPTED = 3 WHERE OLD_DEADLINE <  NOW()";
        getJdbcTemplate().update(sql);
    }

    public void updateAcceptedFalse(int id){
        sql = "UPDATE REQUESTS_FROM_RESPONSIBLE SET ACCEPTED = 3 WHERE ID = ?";
        getJdbcTemplate().update(sql, setParam(id));
    }


    @Override
    protected AppealTaskRequestToRenewal mapper(ResultSet rs, int index) throws SQLException {
        AppealTaskRequestToRenewal appealTaskRequestToRenewal = new AppealTaskRequestToRenewal();
        appealTaskRequestToRenewal.setId(rs.getInt(1));
        appealTaskRequestToRenewal.setChatId(rs.getLong(2));
        appealTaskRequestToRenewal.setTaskId(rs.getInt(3));
        appealTaskRequestToRenewal.setDateDeadline(rs.getDate(4));
        appealTaskRequestToRenewal.setTextCause(rs.getString(5));
        return appealTaskRequestToRenewal;
    }
}
