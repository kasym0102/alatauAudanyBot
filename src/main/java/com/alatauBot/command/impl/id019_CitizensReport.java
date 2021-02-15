package com.alatauBot.command.impl;

import com.alatauBot.command.Command;
import com.alatauBot.entity.enums.WaitingType;
import com.alatauBot.services.CitizensReportService;
import com.alatauBot.utils.Const;
import com.alatauBot.utils.DateKeyboard;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;

public class id019_CitizensReport extends Command {

    private int             deleteMessageId;
    private String          suggestionType;
    private DateKeyboard    dateKeyboard;
    private Date            start;
    private Date            end;

    @Override
    public boolean  execute()               throws TelegramApiException, IOException, SQLException {
        if (!isReceptionEmployee()) {
            sendMessage(Const.NO_ACCESS);
            return EXIT;
        }
        switch (waitingType) {
            case START:
                deleteMessage(updateMessageId);
                dateKeyboard    = new DateKeyboard();
                deleteMessageId = sendStartDate();
                waitingType     = WaitingType.START_DATE;
                return COMEBACK;
            case START_DATE:
                deleteMessage(updateMessageId);
                deleteMessage(deleteMessageId);
                if (hasCallbackQuery()) {
                    if (dateKeyboard.isNext(updateMessageText)) {
                        sendStartDate();
                        return COMEBACK;
                    }
                    start       = dateKeyboard.getDateDate(updateMessageText);
                    start.setHours(0);
                    start.setMinutes(0);
                    start.setSeconds(0);
                    sendEndDate();
                    waitingType = WaitingType.END_DATE;
                }
                return COMEBACK;
            case END_DATE:
                deleteMessage(updateMessageId);
                if (hasCallbackQuery()) {
                    if (dateKeyboard.isNext(updateMessageText)) {
                        sendStartDate();
                        return COMEBACK;
                    }
                    end         = dateKeyboard.getDateDate(updateMessageText);
                    end.setHours(23);
                    end.setMinutes(59);
                    end.setSeconds(59);
                    sendReport(start, end);
                    waitingType = WaitingType.END_DATE;
                }
                return COMEBACK;
        }
        return EXIT;
    }

    private int     sendStartDate()         throws TelegramApiException {
        return toDeleteKeyboard(sendMessageWithKeyboard(1065, dateKeyboard.getCalendarKeyboard()));
    }

    private int     sendEndDate()           throws TelegramApiException {
        return toDeleteKeyboard(sendMessageWithKeyboard(1066, dateKeyboard.getCalendarKeyboard()));
    }

    private void    sendReport(Date start, Date end)            throws TelegramApiException {
        int preview                             = sendMessage("Отчет подготавливается...");
        CitizensReportService reportService     = new CitizensReportService();
        reportService.sendCitizenReport(chatId, bot, start, end, suggestionType, preview);
    }
}
