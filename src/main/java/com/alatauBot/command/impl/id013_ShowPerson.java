package com.alatauBot.command.impl;

import com.alatauBot.command.Command;
import com.alatauBot.entity.standart.User;
import com.alatauBot.utils.Const;
import com.alatauBot.utils.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

@Slf4j
public class id013_ShowPerson extends Command {

    private List<User> allUsers;
    private int count;
    private int messagePrevReport;

    @Override
    public boolean execute() throws TelegramApiException, IOException, SQLException {
        deleteMessage(updateMessageId);
        if (!isAdmin()) {
            sendMessage(Const.NO_ACCESS);
            return EXIT;
        }
        if (hasMessageText() && isButton(24)) {
            count = userDao.count();
            allUsers = userDao.getAll();
        }
        if (count == 0) {
            sendMessage("Нет зарегестрированныx пользователей");
            return EXIT;
        }
        messagePrevReport = sendMessage(String.format("Список подготавливается. Всего пользователей %d.", count));
        new Thread(() -> {
            try {
                sendReport();
            } catch (Exception e) {
                log.warn("Cant send report ", e);
                try {
                    sendMessage("Ошибка отправки списка");
                } catch (TelegramApiException ex) {
                    log.warn("Cant send message ", ex);
                }
            }
        }).start();
        return COMEBACK;
    }

    private void sendReport() throws TelegramApiException {
        XSSFWorkbook workBook = new XSSFWorkbook();
        Sheet sheets = workBook.createSheet("Пользователи");
        BorderStyle thin = BorderStyle.THIN;
        short black = IndexedColors.BLACK.getIndex();
        XSSFCellStyle style = workBook.createCellStyle();
        style.setWrapText(true);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setFillForegroundColor(IndexedColors.BLUE.getIndex());
        style.setBorderTop(thin);
        style.setBorderBottom(thin);
        style.setBorderRight(thin);
        style.setBorderLeft(thin);
        style.setTopBorderColor(black);
        style.setRightBorderColor(black);
        style.setBottomBorderColor(black);
        style.setLeftBorderColor(black);
        BorderStyle tittle = BorderStyle.MEDIUM;

        XSSFFont titleFont = workBook.createFont();
        titleFont.setBold(true);
        titleFont.setFontHeight(10);

        XSSFCellStyle styleTitle = workBook.createCellStyle();
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
        style.setFillForegroundColor(new XSSFColor(new java.awt.Color(0, 52, 94)));
        Sheet sheet = workBook.getSheetAt(0);
        int rowIndex = 0;
        int cellIndex = 0;
        sheets.createRow(rowIndex).createCell(cellIndex).setCellValue("№");
        sheet.getRow(rowIndex).getCell(cellIndex).setCellStyle(styleTitle);
        sheets.getRow(rowIndex).createCell(++cellIndex).setCellValue("Регистрационные данные");
        sheet.getRow(rowIndex).getCell(cellIndex).setCellStyle(styleTitle);
        sheets.getRow(rowIndex).createCell(++cellIndex).setCellValue("Телефон");
        sheet.getRow(rowIndex).getCell(cellIndex).setCellStyle(styleTitle);
        sheets.getRow(rowIndex).createCell(++cellIndex).setCellValue("Данные телеграмм");
        sheet.getRow(rowIndex).getCell(cellIndex).setCellStyle(styleTitle);
        System.out.println(allUsers);
        int i = 0;
        for (User user : allUsers) {
            cellIndex = 0;
            sheets.createRow(++rowIndex).createCell(cellIndex).setCellValue(++i);
            sheet.getRow(rowIndex).getCell(cellIndex).setCellStyle(style);
            sheets.getRow(rowIndex).createCell(++cellIndex).setCellValue(user.getFullName());
            sheet.getRow(rowIndex).getCell(cellIndex).setCellStyle(style);
            sheets.getRow(rowIndex).createCell(++cellIndex).setCellValue(user.getPhone());
            sheet.getRow(rowIndex).getCell(cellIndex).setCellStyle(style);
            sheets.getRow(rowIndex).createCell(++cellIndex).setCellValue(user.getUserName());
            sheet.getRow(rowIndex).getCell(cellIndex).setCellStyle(style);
        }

        sheets.setColumnWidth(0, 2500);
        sheets.setColumnWidth(1, 10000);
        sheets.setColumnWidth(2, 10000);
        sheets.setColumnWidth(3, 10000);

        String fileName = String.format("List users %s.xlsx", DateUtil.getDayDate(new Date()));
        deleteMessage(messagePrevReport);
        bot.execute(new SendDocument().setChatId(chatId).setDocument(fileName, getFileInputStream(workBook)));
    }

    private InputStream getFileInputStream(XSSFWorkbook workBook) {
        ByteArrayOutputStream tables = new ByteArrayOutputStream();
        try {
            workBook.write(tables);
        } catch (IOException e) {
            log.error("cant write table to work book, case: {}", e);
        }
        return new ByteArrayInputStream(tables.toByteArray());
    }
}