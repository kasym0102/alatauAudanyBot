package com.alatauBot.dao.impl;

import com.alatauBot.dao.AbstractDao;
import com.alatauBot.entity.custom.AppealTegQuestionAndOption;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class AppealTegQuestionAndOptionDao extends AbstractDao<AppealTegQuestionAndOption> {

    public List<AppealTegQuestionAndOption> getAppealQuestionOrAnswer(int id){
        sql = "SELECT * FROM APPEAL_TEG_QUESTION_AND_OPTION WHERE APPEAL_TEG_ID = ? AND LANG_ID = ?";
        return getJdbcTemplate().query(sql, setParam(id, getLanguage().getId()), this::mapper);
    }

    public int insertQuestion(AppealTegQuestionAndOption appealTegQuestionAndOption){
        if (appealTegQuestionAndOption.getId() == null){
            sql = "INSERT INTO APPEAL_TEG_QUESTION_AND_OPTION(APPEAL_TEG_ID, QUESTION, ANSWER, LANG_ID) VALUES(?, ?, ?, ?)";
            return (int) getDBUtils().updateForKeyId(sql, appealTegQuestionAndOption.getAppealTegId(), appealTegQuestionAndOption.getQuestion(), appealTegQuestionAndOption.getAnswer(), appealTegQuestionAndOption.getLangId());
        }else {
            sql = "INSERT INTO APPEAL_TEG_QUESTION_AND_OPTION(ID, APPEAL_TEG_ID, QUESTION, ANSWER, LANG_ID) VALUES(?, ?, ?, ?, ?)";
            return getJdbcTemplate().update(sql, appealTegQuestionAndOption.getId(), appealTegQuestionAndOption.getAppealTegId(), appealTegQuestionAndOption.getQuestion(), appealTegQuestionAndOption.getAnswer(), appealTegQuestionAndOption.getLangId());
        }
    }

    public void deleteQuestion(int id){
        sql = "DELETE FROM APPEAL_TEG_QUESTION_AND_OPTION WHERE ID = ?";
        getJdbcTemplate().update(sql, id);
    }

    @Override
    protected AppealTegQuestionAndOption mapper(ResultSet rs, int index) throws SQLException {
        AppealTegQuestionAndOption appealTegQuestionAndOption = new AppealTegQuestionAndOption();
        appealTegQuestionAndOption.setId            (rs.getInt      (1));
        appealTegQuestionAndOption.setAppealTegId   (rs.getInt      (2));
        appealTegQuestionAndOption.setQuestion      (rs.getString   (3));
        appealTegQuestionAndOption.setAnswer        (rs.getString   (4));
        appealTegQuestionAndOption.setLangId        (rs.getInt      (5));
        return appealTegQuestionAndOption;
    }
}
