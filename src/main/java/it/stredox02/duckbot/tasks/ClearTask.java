package it.stredox02.duckbot.tasks;

import it.stredox02.duckbot.Bot;
import it.stredox02.duckbot.object.UserData;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ClearTask implements Runnable {

    private final Bot bot;

    @Override
    public void run() {
        for (UserData data : bot.getDatabaseHandler().getAllUsers()) {
            if (data.getId() != 0 && data.getTime() < (System.currentTimeMillis() / 1000)) {
                bot.getDatabaseHandler().removeKing(data.getChatid(), data.getId());
            }
        }
    }

}
