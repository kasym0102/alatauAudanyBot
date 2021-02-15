package com.alatauBot.services;

import com.alatauBot.dao.DaoFactory;
import com.alatauBot.dao.impl.*;
import com.alatauBot.config.Bot;
import com.alatauBot.entity.custom.AppealTask;
import com.alatauBot.entity.custom.AppealTaskArchive;
import com.alatauBot.entity.custom.AppealTeg;
import com.alatauBot.entity.custom.Director_departments;
import com.alatauBot.entity.enums.Language;
import com.alatauBot.utils.Const;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class CharityReportService {
    private DaoFactory daoFactory              = DaoFactory.getInstance();
    private UserDao userDao                 = daoFactory.getUserDao();
    private AppealTaskArchiveDao appealTaskArchiveDao    = daoFactory.getAppealTaskArchiveDao();
    private DirectorsDepartmentsDao directorsDepartmentsDao    = daoFactory.getDirectorsDepartmentsDao();
    private MessageDao messageDao              = daoFactory.getMessageDao();
    private AppealTypeDao appealTypeDao           = daoFactory.getAppealTypeDao();
    private ReceptionInfoDao receptionInfoDao           = daoFactory.getReceptionInfoDao();
    private ReceptionTypeDao receptionTypeDao           = daoFactory.getReceptionTypeDao();
    private AppealTegDao appealTegDao            = daoFactory.getAppealTegDao();
    private AppealTaskArchive appealTaskArchive;
    private XSSFWorkbook            workbook                = new XSSFWorkbook();
    private XSSFCellStyle           style                   = workbook.createCellStyle();
    private Language currentLanguage         = Language.ru;
    private Sheet                   sheets;
    private Sheet                   sheet;

    public void             sendCharityReport(long chatId, DefaultAbsSender bot, int messagePrevReport, List<AppealTask> appealTasks) {
        try {
            sendCompReport(chatId, bot, messagePrevReport, appealTasks);
        } catch (Exception e) {
            log.error("Can't create/send report", e);
            try {
                bot.execute(new SendMessage(chatId,"Ошибка при создании отчета"));
            } catch (TelegramApiException ex) {
                log.error("Can't send message", ex);
            }
        }
    }

    private void            sendCompReport(long chatId, DefaultAbsSender bot, int messagePrevReport, List<AppealTask> appealTasks) throws TelegramApiException, IOException {
        sheets                      = workbook.createSheet("Отчет");
        sheet                       = workbook.getSheetAt(0);
        AppealTeg           appealTeg = new AppealTeg();

        if (appealTasks == null || appealTasks.size() == 0) {
            bot.execute(new SendMessage(chatId, "Записи отсутствуют"));

        }

        BorderStyle thin            = BorderStyle.THIN;
        short black                 = IndexedColors.BLACK.getIndex();
        short red                   = IndexedColors.RED.getIndex();
        XSSFCellStyle styleTitle    = setStyle(workbook, thin, black, style);
        int rowIndex                = 0;
        createTitle(styleTitle, rowIndex, Arrays.asList("№;ФИО;Номер телефона;Текст обращения;Категория;Тег;Дата обращения;Крайний срок рассмотрение;Фото/Видео;Карта;Статус;Текст пояснения;Оценка;      Одобрено      ".split(Const.SPLIT)));
        List<List<String>> appealTask = appealTasks.stream().map(x -> {
            List<String> list = new ArrayList<>();
            if (x.getIdStatus() <=2){
                appealTaskArchive = appealTaskArchiveDao.getOneByTaskId(x.getId()); // x.
            }else {
                appealTaskArchive = new AppealTaskArchive();
            }

            list.add(String.valueOf(x.getId()));
            list.add(userDao.getFullNameById(x.getFullName()));
            list.add(x.getPhone());
            list.add(x.getText());
            list.add(appealTypeDao.getNameById(x.getAppealTypeId()));
            list.add(appealTegDao.getNameById(x.getAppealTegId()));
            list.add(String.valueOf(x.getDataBegin()));
            list.add(String.valueOf(x.getDataDeadline()));

            if (x.getPhoto() != (null)) {
                list.add("https://api.telegram.org/file/bot" + daoFactory.getPropertiesDao().getPropertiesValue(Const.BOT_TOKEN) + "/" + uploadFile(x.getPhoto()));
            }else {
                list.add(" ");
            }
            list.add(String.format("https://www.google.ru/maps/@%s,%s,17.5z/data=!4m5!3m4!1s0x38836ea1773f7ef1:0xa6c17b8bc2c48d1c!8m2!3d%s!4d%s", x.getLocation().split("#")[0], x.getLocation().split("#")[1], x.getLocation().split("#")[0], x.getLocation().split("#")[1]));
            if (x.getIdStatus() != null){
                if (x.getIdStatus() == 1) list.add("Выполнено");
                if (x.getIdStatus() == 5) list.add("На рассмотрении у начальника");
                if (x.getIdStatus() == 3) list.add("В процессе");
                if (x.getIdStatus() == 4) list.add("Просрочные");
            }else {
                list.add(" ");
            }


            if (appealTaskArchive.getText() != null){
                list.add(appealTaskArchive.getText());
            }else {
                list.add(" ");
            }

            if (x.getAppraisalId() != null){
                if (x.getAppraisalId() == 5) list.add("Отлично");
                if (x.getAppraisalId() == 3) list.add("Хорошо");
                if (x.getAppraisalId() == 1) list.add("Удовлетварительно");
            }
            if (x.getAppraisalId() == 0) {
                list.add(" ");
            }

            if (receptionTypeDao.getById(appealTegDao.getOneById1(x.getAppealTegId()).getReceptionTypeId()) != null){
                List<Director_departments> directors = directorsDepartmentsDao.getAllByAppealTypeId(x.getAppealTypeId());
                StringBuilder str = new StringBuilder();
                for (Director_departments director: directors){
                    str.append(userDao.getFullNameByChatId(director.getChatId())).append("\n");
                }
                list.add(str.toString());
            }else {
                list.add(" ");
            }

           // list.add(String.valueOf(x.getDataDeadline()));

            return list;
        }).collect(Collectors.toList());
        addInfo(appealTask, rowIndex);
        sendFile(chatId, bot);
    }

    private void            addInfo(List<List<String>> reports, int rowIndex) {
        int cellIndex;
        for (List<String> report : reports) {
            sheets.createRow(++rowIndex);
            insertToRow(rowIndex, report, style);
        }
        cellIndex = 0;
        sheets.autoSizeColumn(cellIndex++);
        sheets.autoSizeColumn(cellIndex++);
        sheets.autoSizeColumn(cellIndex++);
        sheets.autoSizeColumn(cellIndex++);
        sheets.autoSizeColumn(cellIndex++);
        sheets.autoSizeColumn(cellIndex++);
        sheets.autoSizeColumn(cellIndex++);
        sheets.autoSizeColumn(cellIndex++);
        sheets.autoSizeColumn(cellIndex++);
        sheets.autoSizeColumn(cellIndex++);
        sheets.autoSizeColumn(cellIndex++);
        sheets.autoSizeColumn(cellIndex++);
        sheets.autoSizeColumn(cellIndex++);
        sheets.autoSizeColumn(cellIndex++);
    }

    private void            createTitle(XSSFCellStyle styleTitle, int rowIndex, List<String> title) {
        sheets.createRow(rowIndex);
        insertToRow(rowIndex, title, styleTitle);
    }

    private void            insertToRow(int row, List<String> cellValues, CellStyle cellStyle) {
        int cellIndex = 0;
        for (String cellValue : cellValues) {
            addCellValue(row, cellIndex++, cellValue, cellStyle);
        }
    }

    private void            addCellValue(int rowIndex, int cellIndex, String cellValue, CellStyle cellStyle) {
        sheets.getRow(rowIndex).createCell(cellIndex).setCellValue(getString(cellValue));
        sheet.getRow(rowIndex).getCell(cellIndex).setCellStyle(cellStyle);
    }

    private String          getString(String nullable) {
        if (nullable == null) return "";
        return nullable;
    }

    private XSSFCellStyle   setStyle(XSSFWorkbook workbook, BorderStyle thin, short black, XSSFCellStyle style) {
        style.setWrapText(true);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setFillBackgroundColor(IndexedColors.BLUE.getIndex());
        style.setBorderTop(thin);
        style.setBorderBottom(thin);
        style.setBorderRight(thin);
        style.setBorderLeft(thin);
        style.setTopBorderColor(black);
        style.setRightBorderColor(black);
        style.setBottomBorderColor(black);
        style.setLeftBorderColor(black);
        style.getFont().setBold(true);
        BorderStyle tittle = BorderStyle.MEDIUM;

        XSSFFont titleFont = workbook.createFont();
        titleFont.setBold(true);
        titleFont.setColor(black);
        titleFont.setFontHeight(10);

        XSSFCellStyle styleTitle = workbook.createCellStyle();
        styleTitle.setWrapText(true);
        styleTitle.setAlignment(HorizontalAlignment.CENTER);
        styleTitle.setVerticalAlignment(VerticalAlignment.CENTER);
        styleTitle.setBorderTop(tittle);
        styleTitle.setBorderBottom(tittle);
        styleTitle.setBorderRight(tittle);
        styleTitle.setBorderLeft(tittle);
        styleTitle.setTopBorderColor(black);
        styleTitle.setRightBorderColor(black);
        styleTitle.setBottomBorderColor(black);
        styleTitle.setLeftBorderColor(black);
        styleTitle.setFont(titleFont);
        style.setFillForegroundColor(new XSSFColor(new Color(0, 94, 94)));
        return styleTitle;
    }



    private void            sendFile(long chatId, DefaultAbsSender bot) throws IOException, TelegramApiException {
        String fileName = "Отчет.xlsx";
        String.format(fileName, new Date().getTime());
        String path = "C:\\botApps\\" + fileName;
//        path += new Date().getTime();
        try (FileOutputStream stream = new FileOutputStream(path)) {
            workbook.write(stream);
        } catch (IOException e) {
            log.error("Can't send File error: ", e);
        }
        sendFile(chatId, bot, fileName, path);
    }

    private void            sendFile(long chatId, DefaultAbsSender bot, String fileName, String path) throws IOException, TelegramApiException {
        File file = new File(path);
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            bot.execute(new SendDocument().setChatId(chatId).setDocument(fileName, fileInputStream));
        }
        file.delete();
    }

    private String uploadFile(String fileId){
        Bot bot = new Bot();
        Objects.requireNonNull(fileId);
        GetFile getFile = new GetFile().setFileId(fileId);
        try{
            org.telegram.telegrambots.meta.api.objects.File file = bot.execute(getFile);
            return file.getFilePath();
        } catch (TelegramApiException e){
            throw new IllegalMonitorStateException();
        }
    }
}
