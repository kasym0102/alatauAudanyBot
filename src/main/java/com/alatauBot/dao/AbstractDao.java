package com.alatauBot.dao;

import com.alatauBot.config.Conversation;
import com.alatauBot.entity.enums.Language;
import com.alatauBot.services.LanguageService;
import com.alatauBot.utils.DataBaseUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;

@Slf4j
public abstract class AbstractDao<T> {

    protected        String     sql;
    protected static DaoFactory factory = DaoFactory.getInstance();

    protected Object[]                  setParam(Object... args) { return args; }

    protected abstract T                mapper(ResultSet rs, int index) throws SQLException;

    protected static    JdbcTemplate    getJdbcTemplate() {return new JdbcTemplate(getDataSource());}

    protected Language getLanguage() {
        if (getChatId() == 0) return Language.ru;
        return LanguageService.getLanguage(getChatId());
    }

    protected static DataBaseUtils getDBUtils() { return new DataBaseUtils(DaoFactory.getDataSource()); }

    private static      DataSource      getDataSource() {   return DaoFactory.getDataSource();}

    private long                        getChatId() {       return Conversation.getCurrentChatId(); }

}
