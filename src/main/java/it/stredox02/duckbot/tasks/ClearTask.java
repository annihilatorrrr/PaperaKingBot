package it.stredox02.duckbot.tasks;

import it.stredox02.duckbot.Bot;
import lombok.AllArgsConstructor;
import org.simpleyaml.configuration.ConfigurationSection;
import org.simpleyaml.exceptions.InvalidConfigurationException;

import java.io.IOException;

@AllArgsConstructor
public class ClearTask implements Runnable {

    private final Bot bot;

    @Override
    public void run() {
        ConfigurationSection section = bot.getCacheFile().getConfigurationSection("chats");
        for (String key : section.getKeys(false)) {
            if (section.getString(key + ".id") != null && (section.getLong(key + ".time") < (System.currentTimeMillis() / 1000))) {
                section.remove(key);
                try {
                    bot.getCacheFile().save();
                    bot.getCacheFile().load();
                } catch (IOException | InvalidConfigurationException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
