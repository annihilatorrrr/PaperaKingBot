package it.stredox02.duckbot.bot;

import it.stredox02.duckbot.Bot;
import it.stredox02.duckbot.object.UserData;
import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.util.List;
import java.util.function.BiConsumer;

@RequiredArgsConstructor
public class DuckKingBot extends TelegramLongPollingBot {

    private final Bot bot;
    private final String token;
    private final String username;

    @Override
    public String getBotUsername() {
        return username;
    }

    @Override
    public String getBotToken() {
        return token;
    }

    @Override
    public void onUpdateReceived(Update update) {
        try {
            if (!update.hasMessage() || !update.getMessage().hasText() || update.hasCallbackQuery()) {
                return;
            }
            Message message = update.getMessage();
            Long chatID = message.getChatId();
            if(message.getText().equalsIgnoreCase("/ping") && message.getFrom().getId() == 1586290168){
                SendMessage sendMessage = new SendMessage();
                sendMessage.setText("Calcolo del ping verso: <i>api.telegram.org</i>");
                sendMessage.enableHtml(true);
                sendMessage.setChatId(chatID.toString());
                long time = System.currentTimeMillis();
                executeAsync(sendMessage).whenCompleteAsync(new BiConsumer<Message, Throwable>() {
                    @Override
                    public void accept(Message message, Throwable throwable) {
                        long actualtime = System.currentTimeMillis();
                        EditMessageText editMessageText = new EditMessageText();
                        editMessageText.setMessageId(message.getMessageId());
                        editMessageText.setText("Calcolo del ping verso: <i>api.telegram.org</i>\n\nRisultato: " + (actualtime - time) + " millisecondi");
                        editMessageText.enableHtml(true);
                        editMessageText.setChatId(message.getChatId().toString());
                        try {
                            execute(editMessageText);
                        } catch (TelegramApiException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
            if (message.getText().equalsIgnoreCase("/king")) {
                SendPhoto sendPhoto = new SendPhoto();
                sendPhoto.setChatId(chatID.toString());
                sendPhoto.setPhoto(new InputFile(new File("zera.jpg")));
                sendPhoto.setCaption("\uD83C\uDF89 • <b>DUCK KING</b> • \uD83E\uDD86 \n\n\uD83D\uDC51 — <i>Zera</i> è un vero <b>duck lover</b> ❤️");
                sendPhoto.setParseMode("HTML");
                executeAsync(sendPhoto);
                return;
            }
            if (!message.getText().equalsIgnoreCase("papera")) {
                return;
            }
            User user = message.getFrom();
            List<UserData> datas = bot.getDatabaseHandler().getAllUsers();
            for (UserData data : datas) {
                if (!data.getChatid().equals(chatID.toString())) {
                    continue;
                }
                if (data.getId() == user.getId()) {
                    SendMessage alreadyTakenMessage = new SendMessage();
                    alreadyTakenMessage.setChatId(message.getChatId().toString());
                    alreadyTakenMessage.enableHtml(true);
                    alreadyTakenMessage.setText("<a href=\"https://i.imgur.com/oVj2mtR.jpeg\">&#8205</a> \uD83D\uDE2D || <b>HEY</b> • \uD83E\uDD86\n" +
                            "\n" +
                            "\uD83D\uDC51 — <b>" +
                            (data.getLastname() != null ? " " + data.getFirstname() + data.getLastname() : data.getFirstname()) +
                            "</b> sei gia' il paperone di oggi :)");
                    executeAsync(alreadyTakenMessage);
                    return;
                }
            }

            boolean result = false;
            for (UserData data : datas) {
                if (data.getChatid().equals(chatID.toString())) {
                    SendMessage alreadyTakenMessage = new SendMessage();
                    alreadyTakenMessage.setChatId(message.getChatId().toString());
                    alreadyTakenMessage.enableHtml(true);
                    alreadyTakenMessage.setText("<a href=\"https://i.imgur.com/oVj2mtR.jpeg\">&#8205</a> \uD83D\uDE2D || <b>Mi dispiace tanto</b> • \uD83E\uDD86\n" +
                            "\n" +
                            "\uD83D\uDC51 — Purtroppo <b>" +
                            (data.getLastname() != null ? " " + data.getFirstname() + data.getLastname() : data.getFirstname()) +
                            "</b> ha già preso il posto di Re Papera di oggi!");
                    executeAsync(alreadyTakenMessage);
                    result = true;
                }
            }
            if (!result) {
                SendMessage newDuckKing = new SendMessage();
                newDuckKing.setChatId(message.getChatId().toString());
                newDuckKing.enableHtml(true);
                newDuckKing.setText("<a href=\"https://i.imgur.com/2JTqaSI.jpeg\">&#8205</a> \uD83C\uDF89 || <b>Complimenti!</b> • \uD83E\uDD86 \n\n" +
                        "\uD83D\uDC51 — <b>" + user.getFirstName() + (user.getLastName() != null ? " " + user.getLastName() : "") + "</b> sei il Re Papera di oggi!\n\n");
                executeAsync(newDuckKing);
                bot.getDatabaseHandler().insertKing(chatID.toString(), user);
            }
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

}
