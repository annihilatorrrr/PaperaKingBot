package it.stredox02.duckbot;

import it.stredox02.duckbot.bot.DuckKingBot;
import it.stredox02.duckbot.database.Database;
import it.stredox02.duckbot.database.DatabaseHandler;
import it.stredox02.duckbot.mongodb.MongoConnection;
import it.stredox02.duckbot.tasks.ClearTask;
import lombok.Getter;
import org.simpleyaml.configuration.file.YamlFile;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Bot {

    @Getter
    private ScheduledExecutorService executorService;
    @Getter
    private MongoConnection mongoConnection;
    @Getter
    private DatabaseHandler databaseHandler;

    public void start() {
        System.out.print("\033[H\033[2J");
        System.out.flush();

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
            configFile.addDefault("mongodb.server", "");
            configFile.addDefault("mongodb.port", 27017);
            configFile.addDefault("mongodb.username", "");
            configFile.addDefault("mongodb.password", "");
            configFile.save();
            configFile.load();
        } catch (final Exception e) {
            e.printStackTrace();
        }

        mongoConnection = new MongoConnection(configFile.getString("mongodb.server"),
                configFile.getInt("mongodb.port"),
                configFile.getString("mongodb.username"),
                configFile.getString("mongodb.password"));
        mongoConnection.init();

        databaseHandler = new Database(this);

        executorService.scheduleAtFixedRate(new ClearTask(this), 0, 1, TimeUnit.SECONDS);

        try {
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            telegramBotsApi.registerBot(new DuckKingBot(this, configFile.getString("bot.token"), configFile.getString("bot.username")));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

        Thread printingHook = new Thread(() -> mongoConnection.getMongoClient().close());
        Runtime.getRuntime().addShutdownHook(printingHook);

        System.out.println("BOT STARTED");
    }

}
