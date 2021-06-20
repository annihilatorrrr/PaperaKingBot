package it.stredox02.duckbot;

import it.stredox02.duckbot.bot.DuckKingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class Main {

    public static void main(String[] args) {
        try {
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            telegramBotsApi.registerBot(new DuckKingBot(args[0]));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

    }

}
