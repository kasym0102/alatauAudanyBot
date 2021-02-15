package com.alatauBot.dao;

import com.alatauBot.dao.impl.*;
import com.alatauBot.utils.PropertiesUtil;
import lombok.NoArgsConstructor;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

@NoArgsConstructor
public class DaoFactory {

    private static DataSource source;
    private static DaoFactory daoFactory = new DaoFactory();

    public static DaoFactory getInstance() { return daoFactory; }

    public static DataSource getDataSource() {
        if (source == null) source = getDriverManagerDataSource();
        return source;
    }

    private static DriverManagerDataSource getDriverManagerDataSource() {
        DriverManagerDataSource driver = new DriverManagerDataSource();
        driver.setDriverClassName(PropertiesUtil.getProperty("jdbc.driverClassName"));
        driver.setUrl(PropertiesUtil.getProperty("jdbc.url"));
        driver.setUsername(PropertiesUtil.getProperty("jdbc.username"));
        driver.setPassword(PropertiesUtil.getProperty("jdbc.password"));
        return driver;
    }

    public AppealTaskRequestToRenewalDao getAppealTaskRequestToRenewalDao(){
        return new AppealTaskRequestToRenewalDao();
    }

    public PropertiesDao            getPropertiesDao()          { return new PropertiesDao(); }

    public LanguageUserDao          getLanguageUserDao()        { return new LanguageUserDao(); }

    public ButtonDao getButtonDao()              { return new ButtonDao(); }

    public MessageDao getMessageDao()             { return new MessageDao(); }

    public KeyboardMarkUpDao        getKeyboardMarkUpDao()      { return new KeyboardMarkUpDao(); }

    public UserDao getUserDao()                { return new UserDao(); }

    public AdminDao                 getAdminDao()               { return new AdminDao(); }

    public ReceptionTypeDao         getReceptionTypeDao()       { return new ReceptionTypeDao(); }

    public ReceptionInfoDao getReceptionInfoDao()       { return new ReceptionInfoDao(); }

    public ReceptionEmployeeDao getReceptionEmployeeDao()   { return new ReceptionEmployeeDao(); }

    public CitizensInfoDao          getCitizensInfoDao()        { return new CitizensInfoDao(); }

    public CitizensRegistrationDao  getCitizensRegistrationDao(){ return new CitizensRegistrationDao(); }

    public AppealTypeDao            getAppealTypeDao()          { return new AppealTypeDao(); }

    public AppealTegDao getAppealTegDao()           { return new AppealTegDao(); }

    public AppealTaskDao            getAppealTaskDao()          { return new AppealTaskDao(); }

    public DepartmentsTypeDao getDepartmentTypeDao()      { return new DepartmentsTypeDao(); }

    public DepartmentsInfoDao       getDepartmentsInfoDao()     { return new DepartmentsInfoDao(); }

    public MapsDao getMapsDao()                { return new MapsDao(); }

    public AppealEmployeeDao        getAppealEmployeeDao()      { return new AppealEmployeeDao(); }

    public ReminderTaskDao          getReminderTaskDao()        { return new ReminderTaskDao(); }

    public AppealTaskArchiveDao getAppealTaskArchiveDao()   { return new AppealTaskArchiveDao(); }

    public SendMemberDao            getSendMemberDao()          { return new SendMemberDao(); }

    public AppealTegQuestionAndOptionDao getAppealTegQuestionAndOptionDao() { return new AppealTegQuestionAndOptionDao(); }

    public DirectorsDepartmentsDao getDirectorsDepartmentsDao() { return new DirectorsDepartmentsDao(); }
}
