package com.alatauBot.command;

import com.alatauBot.command.impl.*;
import com.alatauBot.exceptions.NotRealizedMethodException;
import com.alatauBot.command.impl.id001_ShowInfo;

import java.util.Optional;

public class CommandFactory {

    public  static Command getCommand(long id) { return Optional.ofNullable(getCommandWithoutReflection((int) id)).orElseThrow(() -> new NotRealizedMethodException("Not realized for type: " + id)); }

    private static Command getCommandWithoutReflection(int id) {
        switch (id) {
            case 1:
                return new id001_ShowInfo();

            case 2:
                return new id002_SelectionLanguage();

            case 3:
                return new id003_Registration();

            case 4:
                return new id004_ShowAdmin();

            case 5:
                return new id005_Citizens();

            case 6:
                return new id006_StructureShow();

            case 7:
                return new id007_AppealShow();

            case 8:
                return  new id008_Statistics();

            case 9:
                return new id009_EditOrAddAdmin();

            case 10:
                return new id010_EditCategory();

            case 11:
                return new id011_EditTeg();

            case 12:
                return new id012_EditMenu();

            case 13:
                return new id013_ShowPerson();

            case 14:
                return new id014_ReminderDelete();

            case 15:
                return new id015_Reminder();

            case 16:
                return new id016_ShowInfoFile();

            case 17:
                return new id017_MapLocationSend();

            case 18:
                return new id018_ShowEmployeeInfo();

            case 19:
                return new id019_CitizensReport();

            case 20:
                return new id020_SendMessInReception();

            case 21:
                return new id021_CitizensMember();

            case 22:
                return new id022_AcceptOrReject();

            case 24:
                return new id024_SendComment();

            case 40:
                return new id040_Photo();

            case 50:
                return new id050_CategoryEmployee();

            case 51:
                return new id051_PersonShow();

            case 54:
                return new id054_Сonsultation();

            case 55:
                return new id055_EditСonsultation();
            case 56:
                return new id056_Page_Nachalnik();

        }
        return null;
    }
}
