package it.stredox02.duckbot.database;

import it.stredox02.duckbot.object.GroupData;
import it.stredox02.duckbot.object.UserData;
import it.stredox02.duckbot.permissions.PermissionType;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.List;

public interface DatabaseHandler {

    void insertKing(String chatid, User user);

    List<UserData> getAllUsers();

    void removeKing(String chatid, long id);

    void removeKing(String chatid);

    void addPermissionToGroup(String chatid, PermissionType permission);

    void removePermissionToGroup(String chatid, PermissionType permission);

    List<PermissionType> getActualSettings(String chatid);

    void updateResetPerDay(String chatid, int number);

    int getResetPerDay(String chatid);

    void clearResetPerDay(String chatid);

    void decreaseResetPerDay(String chatid);

    int getActualReset(String chatid);

    List<GroupData> getAllGroups();

}
