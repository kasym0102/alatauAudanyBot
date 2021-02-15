package com.alatauBot.services;

import com.alatauBot.dao.DaoFactory;
import com.alatauBot.command.Command;
import com.alatauBot.command.CommandFactory;
import com.alatauBot.dao.impl.ButtonDao;
import com.alatauBot.entity.standart.Button;
import com.alatauBot.exceptions.CommandNotFoundException;
import com.alatauBot.utils.Const;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

public class CommandService {

    private DaoFactory factory = DaoFactory.getInstance();
    private ButtonDao  buttonDao;

    public Command  getCommand(Update update) throws CommandNotFoundException {
        buttonDao               = factory.getButtonDao();
        Message updateMessage   = update.getMessage();
        String inputtedText;
        if (update.hasCallbackQuery()) {
            inputtedText = update.getCallbackQuery().getData().split(Const.SPLIT)[0];
            updateMessage = update.getCallbackQuery().getMessage();
            try {
                if (inputtedText != null && inputtedText.substring(0,6).equals(Const.ID_MARK)) {
                    try {
                        return getCommandById(Integer.parseInt(inputtedText.split(Const.SPLIT)[0].replaceAll(Const.ID_MARK, "")));
                    } catch (Exception e) {
                        inputtedText = updateMessage.getText();
                    }
                }
            } catch (Exception e) {}
        } else {
            try {
                inputtedText = updateMessage.getText();
            } catch (Exception e) {
                throw new CommandNotFoundException(new Exception("No data is available"));
            }
        }
        Button button = buttonDao.getButton(inputtedText);
        return getCommand(button);
    }

    private Command getCommand(Button button) throws CommandNotFoundException {
        if (button.getCommandId() == 0) throw new CommandNotFoundException(new Exception("No data is available"));
        Command command = CommandFactory.getCommand(button.getCommandId());
        command.setId(button.getCommandId());
        command.setMessageId(button.getMessageId());
        return command;
    }

    private Command getCommandById(int id) { return CommandFactory.getCommand(id); }
}
