package com.alatauBot.dao.impl;

import com.alatauBot.dao.AbstractDao;
import com.alatauBot.entity.custom.ReminderTask;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

public class ReminderTaskDao extends AbstractDao<ReminderTask> {

    public List<ReminderTask>       getByTime(Date dateBegin, Date dateEnd) {
        sql = "SELECT * FROM REMINDER_TASK WHERE DATE_BEGIN BETWEEN to_date(?,'YYYY-MM-DD') AND to_date(?,'YYYY-MM-DD HH:mm:SS')";
        return getJdbcTemplate().query(sql, setParam(dateBegin, dateEnd), this::mapper);
    }

    public void                     insert(ReminderTask reminderTask) {
        sql = "INSERT INTO REMINDER_TASK(TEXT,DATE_BEGIN) VALUES (?,?)";
        getJdbcTemplate().update(sql, reminderTask.getText(), reminderTask.getDateBegin());
    }

    public List<ReminderTask>       getAll() {
        sql = "SELECT * FROM REMINDER_TASK";
        return getJdbcTemplate().query(sql, this::mapper);
    }

    public void                     delete(int reminderTaskId) {
        sql = "DELETE FROM REMINDER_TASK WHERE ID = ?";
        getJdbcTemplate().update(sql, setParam(reminderTaskId));
    }

    public ReminderTask             getById(int reminderTaskId) {
        sql = "SELECT * FROM REMINDER_TASK WHERE ID = ?";
        return getJdbcTemplate().queryForObject(sql, setParam(reminderTaskId), this::mapper);
    }

    public void                     update(ReminderTask reminderTask) {
        sql = "UPDATE REMINDER_TASK SET DATE_BEGIN = ? , TEXT = ? WHERE ID = ?";
        getJdbcTemplate().update(sql, reminderTask.getDateBegin(),reminderTask.getText(), reminderTask.getId());
    }

    @Override
    protected ReminderTask      mapper(ResultSet rs, int index) throws SQLException {
        ReminderTask reminderTask = new ReminderTask();
        reminderTask.setId          (rs.getInt      (1));
        reminderTask.setText        (rs.getString   (2));
        reminderTask.setDateBegin   (rs.getDate     (3));
        reminderTask.setDateEnd     (rs.getDate     (4));
        return reminderTask;
    }
}
