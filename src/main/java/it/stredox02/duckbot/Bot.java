package it.stredox02.duckbot;

import it.stredox02.duckbot.bot.DuckKingBot;
import org.simpleyaml.configuration.file.YamlFile;
import org.simpleyaml.exceptions.InvalidConfigurationException;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.io.IOException;

public class Bot {

    public void start(){
        YamlFile configFile = new YamlFile("config.yml");
        try {
            if (!configFile.exists()) {
                configFile.createNewFile(true);
            }
            configFile.load();
            configFile.addDefault("bot.token", "");
            configFile.addDefault("bot.username", "");
            configFile.addDefault("bot.cache", "");
            configFile.save();
            configFile.load();
        } catch (final Exception e) {
            e.printStackTrace();
        }
        YamlFile groupCache = new YamlFile(configFile.getString("bot.cache") + ".yml");

        try {
            if (!groupCache.exists()) {
                groupCache.createNewFile(true);
            }
            groupCache.load();
            if(groupCache.getConfigurationSection("chats") == null){
                groupCache.createSection("chats");
            }
            groupCache.save();
            groupCache.load();
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }

        try {
            groupCache.load();
        } catch (InvalidConfigurationException | IOException e) {
            e.printStackTrace();
        }

        try {
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            telegramBotsApi.registerBot(new DuckKingBot(configFile.getString("bot.token"), configFile.getString("bot.username"), groupCache));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
        System.out.println("BOT STARTED");
    }

}
