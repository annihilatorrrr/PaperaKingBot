package it.stredox02.duckbot.bot;

import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;

@RequiredArgsConstructor
public class DuckKingBot extends TelegramLongPollingBot {

    private final String token;
    private HashMap<Long, User> duckKing = new HashMap<>();

    @Override
    public String getBotUsername() {
        return "RePaperaBot";
    }

    @Override
    public String getBotToken() {
        return token;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (!update.hasMessage() || update.getMessage().getText() == null || update.hasCallbackQuery()) {
            return;
        }
        Message message = update.getMessage();
        if (!message.getText().equalsIgnoreCase("papera")) {
            return;
        }
        User user = message.getFrom();
        Long chatID = message.getChatId();
        ZoneId zoneId = ZoneId.of("Europe/Rome");
        ZonedDateTime start = ZonedDateTime.ofInstant(Instant.now(), zoneId).toLocalDate().atStartOfDay(zoneId);
        ZonedDateTime tomorrow = start.plusDays(1);

        if ((tomorrow.toEpochSecond() < System.currentTimeMillis() / 1000) && duckKing.get(chatID) != null) {
            duckKing.remove(chatID);
        }

        if (duckKing.get(chatID) != null && duckKing.get(chatID) != user) {
            SendMessage alreadyTakenMessage = new SendMessage();
            alreadyTakenMessage.setChatId(message.getChatId().toString());
            alreadyTakenMessage.enableMarkdownV2(true);
            alreadyTakenMessage.setText("[ ](https://www.memecreator.org/static/images/templates/1366382.jpg) \uD83D\uDE2D\uD83E\uDD86 *Mi dispiace* \uD83E\uDD86\uD83D\uDE2D\nMa " + user.getFirstName() + (user.getLastName() != null ? " " + user.getLastName() : "") + " ha gia' preso il posto di *re papera*");
            try {
                executeAsync(alreadyTakenMessage);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
            return;
        }

        /*SendAnimation newDuckKing = new SendAnimation();
        newDuckKing.setChatId(message.getChatId().toString());
        newDuckKing.setParseMode("HTML");
        newDuckKing.setCaption("\uD83E\uDD86 Sei il nuovo re papera");
        newDuckKing.setAnimation(new InputFile(new File("gif.mp4")));*/

        SendMessage newDuckKing = new SendMessage();
        newDuckKing.setChatId(message.getChatId().toString());
        newDuckKing.enableMarkdownV2(true);
        newDuckKing.setText("[ ](https://i.imgur.com/2JTqaSI.jpeg) \uD83E\uDD86 *Complimenti* \uD83E\uDD86 \n_Sei il nuovo re papera_\n\n");
        try {
            executeAsync(newDuckKing);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
        duckKing.putIfAbsent(chatID, user);
    }

}
