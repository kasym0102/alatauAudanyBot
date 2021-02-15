package com.alatauBot.services;

import com.alatauBot.dao.DaoFactory;
import com.alatauBot.dao.impl.*;
import com.alatauBot.entity.custom.AppealTask;
import com.alatauBot.utils.Const;
import com.alatauBot.utils.UpdateUtil;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

@Slf4j
public class SendReportAndMapService {
    private File            createFile;
    private long            chatId;
    private Update          update;
    private String          next            = "\n";
    private DaoFactory factory         = DaoFactory.getInstance();
    private PropertiesDao propertiesDao   = factory.getPropertiesDao();
    private AppealTaskDao appealTask      = factory.getAppealTaskDao();
    private UserDao userDao         = factory.getUserDao();
    private AppealTegDao appealTegDao    = factory.getAppealTegDao();
    private AppealTypeDao appealTypeDao   = factory.getAppealTypeDao();

    public void sendReportAndMap(DefaultAbsSender bot, Update update, List<AppealTask> appealTasks){
        if (chatId == 0 || update == null) {
            this.update = update;
            this.chatId = UpdateUtil.getChatId(update);
            try {
                log.info("Successful send report and map");
                setTextList(appealTasks);
                sendMapAndDocument(bot, appealTasks);
            } catch (IOException ioException) {
                ioException.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    private File setTextList(List<AppealTask> appealTasks) throws IOException {
        StringBuilder sb = new StringBuilder();
        createFile = new File("C:\\botApps\\AlatauBot\\list123.txt");
        FileWriter fileWriter = new FileWriter("C:\\botApps\\AlatauBot\\list123.txt", true);
        if (!appealTasks.isEmpty()){
            appealTasks.forEach(e -> sb .append(e.getLocation()).append(Const.HESH)                                 // location
                                        .append(e.getText()).append(Const.HESH)                                     // text
                                        .append(userDao.getFullNameById(e.getFullName())).append(Const.HESH)        // full name
                                        .append(e.getIdStatus()).append(Const.HESH)                                 // status
                                        .append(appealTegDao.getNameById(e.getAppealTegId())).append(Const.HESH)    // teg
                                        .append(appealTypeDao.getNameById(e.getAppealTypeId())).append(Const.HESH)  // type
                                        .append(e.getPhone()).append(next));                                        // phone
            fileWriter.write(sb.toString());
            fileWriter.close();
        }
        return createFile;
    }

    private void sendMapAndDocument(DefaultAbsSender bot, List<AppealTask> appealTasks) throws InterruptedException, IOException, TelegramApiException {
        CharityReportService charityReportService = new CharityReportService();
        charityReportService.sendCharityReport(chatId, bot, 1020, appealTasks);
        if (!appealTasks.isEmpty()){
//            Runtime.getRuntime().exec("python3 /home/koro/Koro/java-app/test.py");
            Runtime.getRuntime().exec("cmd /c python C:\\botApps\\AlatauBot\\test.py");
//            Runtime.getRuntime().exec("cmd /c python K:\\real\\test.py");
            Thread.sleep(2000);
            File file = new File("C:\\botApps\\AlatauBot\\map.html");
            bot.execute(new SendDocument().setChatId(chatId).setDocument(file));
//            file.delete();
//            createFile.delete();
        }
    }
}
