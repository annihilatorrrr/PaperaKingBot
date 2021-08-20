package it.stredox02.duckbot.tasks;

import it.stredox02.duckbot.Bot;
import it.stredox02.duckbot.object.UserData;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ClearTask implements Runnable {

    private final Bot bot;

    @Override
    public void run() {
        for (UserData userData : bot.getDatabaseHandler().getAllUsers()) {
            if (userData.getId() != 0 && userData.getTime() < (System.currentTimeMillis() / 1000)) {
                bot.getDatabaseHandler().removeKing(userData.getChatid(), userData.getId());
                bot.getDatabaseHandler().clearResetPerDay(userData.getChatid());
            }
        }
    }

}
