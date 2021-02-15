package com.alatauBot.dao.impl;

import com.alatauBot.dao.AbstractDao;
import com.alatauBot.entity.custom.AppealEmployee;
import com.alatauBot.entity.custom.AppealTask;
import org.joda.time.DateTime;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AppealTaskDao extends AbstractDao<AppealTask> {
    // 1 = выполнено
    // 2 = не выполнено // уже нету
    // 3 = в процессе
    // 4 = просрочноые
    // 5 = на рассмотрений у начальника
    public List<AppealTask>         getAll(){
        sql = "SELECT * FROM APPEAL_TASK";
        return getJdbcTemplate().query(sql, this::mapper);
    }

    public List<AppealTask>         getIdByName                 (String text){
        sql = "SELECT ID FROM APPEAL_TASK WHERE RECEPTION_TYPE_ID = ?";
        return getJdbcTemplate().query(sql, setParam(text), this::mapper);
    }

    public List<AppealTask> getStatus(int status){
        sql = "SELECT * FROM APPEAL_TASK WHERE STATUS_ID = ?";
        return getJdbcTemplate().query(sql,setParam(status), this::mapper);
    }

    public List<AppealTask> getAllByStatusAndAppealTegId(int status, int tegId){
        sql = "SELECT * FROM APPEAL_TASK WHERE STATUS_ID = ? and APPEAL_TEG_ID = ?";
        return getJdbcTemplate().query(sql,setParam(status, tegId), this::mapper);
    }

     public List<AppealTask> getAllByAppealTypeIdAndStatus(int appealTypeId,int status){
        sql = "SELECT * FROM APPEAL_TASK WHERE APPEAL_TYPE_ID = ? and STATUS_ID = ?";
        return getJdbcTemplate().query(sql,setParam(appealTypeId, status), this::mapper);
    }

    public List<AppealTask> getAllByStatusAndAppealTegId(int status, List<AppealEmployee> appealEmployees){
        List<AppealTask> appealTasks = new ArrayList<>();
        for (AppealEmployee appealEmployee: appealEmployees) {
            sql = "SELECT * FROM APPEAL_TASK WHERE STATUS_ID = ? and APPEAL_TEG_ID = ?";
            appealTasks.addAll( getJdbcTemplate().query(sql,setParam(status, appealEmployee.getTegId()), this::mapper));
        }

        return appealTasks;
    }

    public int getAppealTegIdByTaskId(int id){
        sql = "SELECT APPEAL_TEG_ID FROM APPEAL_TASK WHERE ID = ?";
        return getJdbcTemplate().queryForObject(sql, setParam(id), Integer.class);
    }

    public int getAppealStatusIdByTaskId(int id){
        sql = "SELECT STATUS_ID FROM APPEAL_TASK WHERE ID = ?";
        return getJdbcTemplate().queryForObject(sql, setParam(id), Integer.class);
    }

    public void                     updateStatus                (int statusId, int id){
        sql = "UPDATE APPEAL_TASK SET STATUS_ID = ? WHERE ID = ?";
        getJdbcTemplate().update(sql, setParam(statusId, id));
    }

    public void                     updateStatusAutomatically               (){
        sql = "UPDATE APPEAL_TASK SET STATUS_ID = 4 WHERE DATE_DEADLINE < NOW()";
        getJdbcTemplate().update(sql);

    }

    public void                     updateAppraisalId(int appraisalId, int id){
        sql = "UPDATE APPEAL_TASK SET APPRAISAL_ID = ? WHERE ID = ?";
        getJdbcTemplate().update(sql, setParam(appraisalId, id));
    }

    public void                     updateAppealDeadline(Date date, int id){
        sql = "UPDATE APPEAL_TASK SET DATE_DEADLINE = ? WHERE ID = ?";
        getJdbcTemplate().update(sql, setParam(date, id));
    }

    public int                      insertAppealTask            (AppealTask appealTask){
        sql = "INSERT INTO APPEAL_TASK(RECEPTION_TYPE_ID, FULL_NAME, PHONE, TEXT, CHAT_ID, APPEAL_TYPE_ID, APPEAL_TEG_ID, DATA_BEGIN, LOCATIONS, STATUS_ID, PHOTO, VIDEO, DATE_DEADLINE) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?)";
        Date date = new Date(appealTask.getDataBegin().getTime());
        DateTime dateTime = new DateTime(date).plusDays(3);
        date.setTime(dateTime.getMillis());

        return (int) getDBUtils().updateForKeyId(sql, setParam(appealTask.getReceptionTypeId(), appealTask.getFullName(), appealTask.getPhone() , appealTask.getText(), appealTask.getChatId(), appealTask.getAppealTypeId(), appealTask.getAppealTegId(), appealTask.getDataBegin(), appealTask.getLocation(), appealTask.getIdStatus(), appealTask.getPhoto(), appealTask.getVideo(), date));
    }

    public List<AppealTask>         getAllByDate                (Date firstDate, Date secondDate){
        sql = "SELECT * FROM APPEAL_TASK WHERE  DATA_BEGIN BETWEEN ? AND ?";
        return getJdbcTemplate().query(sql, setParam(firstDate, secondDate), this::mapper);
    }

    public AppealTask getIdById(int id){
        sql = "SELECT * FROM APPEAL_TASK WHERE ID = ?";
        return getJdbcTemplate().queryForObject(sql, setParam(id), this::mapper);
    }

    public AppealTask               getAppealTaskByReceptionTypeId (int id){
        sql = "SELECT * FROM APPEAL_TASK WHERE RECEPTION_TYPE_ID = ?";
        return getJdbcTemplate().queryForObject(sql, setParam(id), this::mapper);
    }

    public AppealTask               getAppealTaskByAppealTypeId (int id){
        sql = "SELECT * FROM APPEAL_TASK WHERE APPEAL_TYPE_ID = ?";
        return getJdbcTemplate().queryForObject(sql, setParam(id), this::mapper);
    }

    public AppealTask               getAppealTaskById (int id){
        sql = "SELECT * FROM APPEAL_TASK WHERE ID = ?";
        return getJdbcTemplate().queryForObject(sql, setParam(id), this::mapper);
    }

    public AppealTask               getAppealTaskByAppealTegId (int id){
        sql = "SELECT * FROM APPEAL_TASK WHERE APPEAL_TEG_ID = ?";
        return getJdbcTemplate().queryForObject(sql, setParam(id), this::mapper);
    }

    public List<AppealTask>         getAllByReceptionTypeId (int id){
        sql = "SELECT * FROM APPEAL_TASK WHERE RECEPTION_TYPE_ID = ?";
        return getJdbcTemplate().query(sql, setParam(id), this::mapper);
    }

    public List<AppealTask>         getAllByAppealTypeId (int id){
        sql = "SELECT * FROM APPEAL_TASK WHERE APPEAL_TYPE_ID = ?";
        return getJdbcTemplate().query(sql, setParam(id), this::mapper);
    }

    public List<AppealTask>         getAllByAppealTegId (int id){
        sql = "SELECT * FROM APPEAL_TASK WHERE APPEAL_TEG_ID = ?";
        return getJdbcTemplate().query(sql, setParam(id), this::mapper);
    }

    public List<AppealTask>         getAllByReceptionNameDate   (int id, Date firstDate, Date secondDate){
        sql = "SELECT * FROM APPEAL_TASK WHERE RECEPTION_TYPE_ID = ? AND DATA_BEGIN BETWEEN ? AND ?";
        return getJdbcTemplate().query(sql,setParam(id, firstDate, secondDate), this::mapper);
    }

    public List<AppealTask>         getAllByAppealTypeNameDate  (int receptionId, int appealTypeId, Date firstDate, Date secondDate){
        sql = "SELECT * FROM APPEAL_TASK WHERE RECEPTION_TYPE_ID = ? AND APPEAL_TYPE_ID = ? AND DATA_BEGIN BETWEEN ? AND ?";
        return getJdbcTemplate().query(sql, setParam(receptionId, appealTypeId, firstDate, secondDate), this::mapper);
    }

    public List<AppealTask>         getAllByAppealTegNameDate   (int receptionId, int appealTypeId, int appealTegId, Date firstDate, Date secondDate){
        sql = "SELECT * FROM APPEAL_TASK WHERE RECEPTION_TYPE_ID = ? AND APPEAL_TYPE_ID = ? AND APPEAL_TEG_ID = ? AND DATA_BEGIN BETWEEN ? AND ?";
        return getJdbcTemplate().query(sql,setParam(receptionId, appealTypeId, appealTegId, firstDate, secondDate), this::mapper);
    }

    @Override
    protected AppealTask mapper(ResultSet rs, int index) throws SQLException {
        AppealTask appealTask = new AppealTask();
        appealTask.setId                (rs.getInt      (1));
        appealTask.setReceptionTypeId   (rs.getInt      (2));
        appealTask.setFullName          (rs.getInt      (3));
        appealTask.setPhone             (rs.getString   (4));
        appealTask.setText              (rs.getString   (5));
        appealTask.setChatId            (rs.getLong     (6));
        appealTask.setAppealTypeId      (rs.getInt      (7));
        appealTask.setAppealTegId       (rs.getInt      (8));
        appealTask.setDataBegin         (rs.getDate     (9));
        appealTask.setLocation          (rs.getString   (10));
        appealTask.setIdStatus          (rs.getInt      (11));
        appealTask.setPhoto             (rs.getString   (12));
        appealTask.setVideo             (rs.getString   (13));
        appealTask.setAppraisalId       (rs.getInt      (14));
        appealTask.setDataDeadline      (rs.getDate    (15));
        return appealTask;
    }
}
