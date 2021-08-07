package it.stredox02.duckbot.database;

import it.stredox02.duckbot.object.UserData;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.List;

public interface DatabaseHandler {

    void insertKing(String chatid, User user);

    List<UserData> getAllUsers();

    void removeKing(String chatid, long id);

    void removeKing(String chatid);

}
