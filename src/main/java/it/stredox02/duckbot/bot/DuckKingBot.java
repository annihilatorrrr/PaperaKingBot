package it.stredox02.duckbot.bot;

import it.stredox02.duckbot.Bot;
import it.stredox02.duckbot.object.UserData;
import it.stredox02.duckbot.perfection.Perfection;
import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChatMember;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@RequiredArgsConstructor
public class DuckKingBot extends TelegramLongPollingBot implements Perfection {

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
            User user = message.getFrom();
            if(message.getText().equalsIgnoreCase("/start") && message.getChat().isUserChat()){
                SendMessage sendMessage = new SendMessage();
                sendMessage.setText("\uD83D\uDC4B  • Benvenuto " + "<a href=\"tg://user?id=" + update.getMessage().getFrom().getId() + "\">" +
                        update.getMessage().getFrom().getFirstName() + "</a>" +
                        "\n\n" +
                        "➥ Come funziona? —\n" +
                        "Aggiungimi ad un gruppo e ogni giorno chi scriverà per primo \"papera\" diventerà il re papera." +
                        "\n\n" +
                        "➥ Hai bisogno di Supporto? —\n" +
                        "» Contatta @Stredox02");
                sendMessage.enableHtml(true);
                sendMessage.setChatId(chatID.toString());
                executeAsync(sendMessage);
                return;
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
            if(message.getText().startsWith("/adddking") && message.getText().split(" ").length == 2){
                long target = Long.parseLong(message.getText().split(" ")[1]);
                GetChatMember chatMember = new GetChatMember();
                chatMember.setUserId(user.getId());
                chatMember.setChatId(chatID.toString());
                CompletableFuture<ChatMember> result = executeAsync(chatMember);
                ChatMember member = result.get();

                GetChatMember targetChatMember = new GetChatMember();
                targetChatMember.setUserId(target);
                targetChatMember.setChatId(chatID.toString());
                CompletableFuture<ChatMember> result2 = executeAsync(targetChatMember);
                ChatMember targetresult = result2.get();
                if(targetresult == null){
                    SendMessage sendMessage = new SendMessage();
                    sendMessage.setText("<a href=\"https://i.imgur.com/AA1hyTV.jpg\">&#8205</a> \uD83D\uDE2D || <b>HEY</b> •" +
                            " \uD83E\uDD86\n\n" +
                            "\uD83D\uDC51 — <b>" +
                            (user.getLastName() != null ? " " + user.getFirstName() + user.getLastName() : user.getFirstName()) +
                            "</b> non è stato trovato questo utente, riprova");
                    sendMessage.setChatId(chatID.toString());
                    sendMessage.enableHtml(true);
                    executeAsync(sendMessage);
                    return;
                }
                if(member.getStatus().equalsIgnoreCase("creator") || member.getStatus().equalsIgnoreCase("administrator")){
                    bot.getDatabaseHandler().insertKing(chatID.toString(), targetresult.getUser());
                    SendMessage sendMessage = new SendMessage();
                    sendMessage.setChatId(message.getChatId().toString());
                    sendMessage.enableHtml(true);
                    sendMessage.setText("<a href=\"https://i.imgur.com/Qt8aXbp.jpg\">&#8205</a> \uD83C\uDF89 || <b>HEY!</b> • \uD83E\uDD86 \n\n" +
                            "\uD83D\uDC51 — <b>" + user.getFirstName() + (user.getLastName() != null ? " " + user.getLastName() : "") +
                            "</b> hai aggiunto <b>" + targetresult.getUser().getFirstName() +
                            (targetresult.getUser().getLastName() != null ? " " + targetresult.getUser().getLastName() : "") +
                            "</b>come re papera di questo gruppo" + "!\n\n");
                    executeAsync(sendMessage);
                    return;
                }
                SendMessage sendMessage = new SendMessage();
                sendMessage.setText("<a href=\"https://i.imgur.com/AA1hyTV.jpg\">&#8205</a> \uD83D\uDE2D || <b>HEY</b> •" +
                        " \uD83E\uDD86\n\n" +
                        "\uD83D\uDC51 — <b>" +
                        (user.getLastName() != null ? " " + user.getFirstName() + user.getLastName() : user.getFirstName()) +
                        "</b> non puoi usare questo comando");
                sendMessage.setChatId(chatID.toString());
                sendMessage.enableHtml(true);
                executeAsync(sendMessage);
                return;
            }
            if(message.getText().equalsIgnoreCase("/removedking")){
                GetChatMember chatMember = new GetChatMember();
                chatMember.setUserId(user.getId());
                chatMember.setChatId(chatID.toString());
                CompletableFuture<ChatMember> result = executeAsync(chatMember);
                ChatMember member = result.get();
                if(member.getStatus().equalsIgnoreCase("creator") || member.getStatus().equalsIgnoreCase("administrator")){
                    bot.getDatabaseHandler().removeKing(chatID.toString());
                    SendMessage sendMessage = new SendMessage();
                    sendMessage.setChatId(message.getChatId().toString());
                    sendMessage.enableHtml(true);
                    sendMessage.setText("<a href=\"https://i.imgur.com/Qt8aXbp.jpg\">&#8205</a> \uD83C\uDF89 || <b>HEY!</b> • \uD83E\uDD86 \n\n" +
                            "\uD83D\uDC51 — <b>" + user.getFirstName() + (user.getLastName() != null ? " " + user.getLastName() : "") +
                            "</b> hai rimosso il re papera da questa chat!\n\n");
                    executeAsync(sendMessage);
                    return;
                }
                SendMessage sendMessage = new SendMessage();
                sendMessage.setText("<a href=\"https://i.imgur.com/AA1hyTV.jpg\">&#8205</a> \uD83D\uDE2D || <b>HEY</b> •" +
                        " \uD83E\uDD86\n\n" +
                        "\uD83D\uDC51 — <b>" +
                        (user.getLastName() != null ? " " + user.getFirstName() + user.getLastName() : user.getFirstName()) +
                        "</b> non puoi usare questo comando");
                sendMessage.setChatId(chatID.toString());
                sendMessage.enableHtml(true);
                executeAsync(sendMessage);
                return;
            }
            if (!message.getText().equalsIgnoreCase("papera")) {
                return;
            }
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
                            (data.getLastName() != null ? " " + data.getFirstName() + data.getLastName() : data.getFirstName()) +
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
                            (data.getLastName() != null ? " " + data.getFirstName() + data.getLastName() : data.getFirstName()) +
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
                        "\uD83D\uDC51 — <b>" + user.getFirstName() + (user.getLastName() != null ? " " + user.getLastName() : "") +
                        "</b> sei il Re Papera di oggi!\n\n");
                executeAsync(newDuckKing);
                bot.getDatabaseHandler().insertKing(chatID.toString(), user);
            }
        } catch (TelegramApiException | ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }

}
