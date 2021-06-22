package it.stredox02.duckbot;

import it.stredox02.duckbot.bot.DuckKingBot;
import it.stredox02.duckbot.tasks.ClearTask;
import lombok.Getter;
import org.simpleyaml.configuration.file.YamlFile;
import org.simpleyaml.exceptions.InvalidConfigurationException;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Bot {

    @Getter private ScheduledExecutorService executorService;
    @Getter private YamlFile cacheFile;

    public void start(){
        executorService = Executors.newSingleThreadScheduledExecutor();

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
        cacheFile = new YamlFile(configFile.getString("bot.cache") + ".yml");

        try {
            if (!cacheFile.exists()) {
                cacheFile.createNewFile(true);
            }
            cacheFile.load();
            if(cacheFile.getConfigurationSection("chats") == null){
                cacheFile.createSection("chats");
            }
            cacheFile.save();
            cacheFile.load();
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }

        try {
            cacheFile.load();
        } catch (InvalidConfigurationException | IOException e) {
            e.printStackTrace();
        }

        executorService.scheduleAtFixedRate(new ClearTask(this),0,1,TimeUnit.SECONDS);

        try {
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            telegramBotsApi.registerBot(new DuckKingBot(configFile.getString("bot.token"), configFile.getString("bot.username"), cacheFile));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
        System.out.println("BOT STARTED");
    }

}
