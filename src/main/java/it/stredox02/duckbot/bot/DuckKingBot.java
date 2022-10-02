package it.stredox02.duckbot.bot;

import it.stredox02.duckbot.Bot;
import it.stredox02.duckbot.object.UserData;
import it.stredox02.duckbot.perfection.Perfection;
import it.stredox02.duckbot.permissions.PermissionType;
import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChatMember;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMember;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.BiConsumer;

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
            if(update.hasCallbackQuery() && update.getCallbackQuery().getData().equalsIgnoreCase("admin_settings")){
                Long chatid = update.getCallbackQuery().getMessage().getChatId();

                EditMessageText editMessageText = new EditMessageText();
                editMessageText.setMessageId(update.getCallbackQuery().getMessage().getMessageId());
                editMessageText.setText("<b>Ecco qua le impostazioni per gli amministratori</b>\n" +
                        "<b>Gruppo:</b> <code>" + update.getCallbackQuery().getMessage().getChat().getTitle() + "</code>\n\n" +
                        "Clicca sul pulsante relativo alla categoria che vuoi modificare");
                editMessageText.setChatId(chatid.toString());
                editMessageText.enableHtml(true);

                InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
                List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
                List<InlineKeyboardButton> firstRow = new ArrayList<>();

                InlineKeyboardButton adminOption = new InlineKeyboardButton();
                adminOption.setText("\uD83C\uDD70 Admin");
                adminOption.setCallbackData("admin_reset");
                firstRow.add(adminOption);

                rowsInline.add(firstRow);

                markupInline.setKeyboard(rowsInline);
                editMessageText.setReplyMarkup(markupInline);
                executeAsync(editMessageText);
                return;
            }

            if(update.hasCallbackQuery() && update.getCallbackQuery().getData().equalsIgnoreCase("admin_reset")){
                Long chatid = update.getCallbackQuery().getMessage().getChatId();

                EditMessageText editMessageText = new EditMessageText();
                editMessageText.setMessageId(update.getCallbackQuery().getMessage().getMessageId());
                editMessageText.setText("<b>Ecco qua le impostazioni per gli amministratori</b>\n\n" +
                        "Scegli se gli admin possono resettare il re papera del giorno\n\nAttualmente: " +
                        (bot.getDatabaseHandler().getActualSettings(chatid.toString()).stream()
                                .anyMatch(permissionType -> permissionType == PermissionType.ADMIN_CAN_RESET_KING)
                                ? "<b>Si</b>" : "<b>No</b>") +
                        "\n\nReset: " + bot.getDatabaseHandler().getActualReset(chatid.toString()) + "/" +
                        bot.getDatabaseHandler().getResetPerDay(chatid.toString()));
                editMessageText.setChatId(chatid.toString());
                editMessageText.enableHtml(true);

                InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
                List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
                List<InlineKeyboardButton> firstRow = new ArrayList<>();
                List<InlineKeyboardButton> secondRow = new ArrayList<>();
                List<InlineKeyboardButton> thirdRow = new ArrayList<>();
                List<InlineKeyboardButton> fourthRow = new ArrayList<>();

                InlineKeyboardButton resetYesOption = new InlineKeyboardButton();
                resetYesOption.setText("\uD83D\uDFE2 Si");
                resetYesOption.setCallbackData("admin_reset_yes");
                firstRow.add(resetYesOption);

                InlineKeyboardButton resetNoOption = new InlineKeyboardButton();
                resetNoOption.setText("\uD83D\uDD34 No");
                resetNoOption.setCallbackData("admin_reset_no");
                firstRow.add(resetNoOption);

                InlineKeyboardButton increeseOption = new InlineKeyboardButton();
                increeseOption.setText("Incrementa");
                increeseOption.setCallbackData("admin_increese");
                secondRow.add(increeseOption);

                InlineKeyboardButton decreeseOption = new InlineKeyboardButton();
                decreeseOption.setText("Decrementa");
                decreeseOption.setCallbackData("admin_decreese");
                secondRow.add(decreeseOption);

                InlineKeyboardButton resetOption = new InlineKeyboardButton();
                resetOption.setText("Reset all");
                resetOption.setCallbackData("admin_resetall");
                thirdRow.add(resetOption);

                InlineKeyboardButton backOption = new InlineKeyboardButton();
                backOption.setText("\uD83D\uDD19 Indietro");
                backOption.setCallbackData("admin_settings");
                fourthRow.add(backOption);

                rowsInline.add(firstRow);
                rowsInline.add(secondRow);
                rowsInline.add(thirdRow);
                rowsInline.add(fourthRow);

                markupInline.setKeyboard(rowsInline);
                editMessageText.setReplyMarkup(markupInline);
                executeAsync(editMessageText);
                return;
            }

            if(update.hasCallbackQuery() && update.getCallbackQuery().getData().equalsIgnoreCase("admin_reset_yes")){
                Long chatid = update.getCallbackQuery().getMessage().getChatId();
                bot.getDatabaseHandler().addPermissionToGroup(update.getCallbackQuery().getMessage().getChatId().toString(),
                            PermissionType.ADMIN_CAN_RESET_KING);

                EditMessageText editMessageText = new EditMessageText();
                editMessageText.setMessageId(update.getCallbackQuery().getMessage().getMessageId());
                editMessageText.setText("<b>Ecco qua le impostazioni per gli amministratori</b>\n\n" +
                        "Scegli se gli admin possono resettare il re papera del giorno\n\nAttualmente: " +
                        (bot.getDatabaseHandler().getActualSettings(chatid.toString()).stream()
                                .anyMatch(permissionType -> permissionType == PermissionType.ADMIN_CAN_RESET_KING)
                                ? "<b>Si</b>" : "<b>No</b>") +
                        "\n\nReset: " + bot.getDatabaseHandler().getActualReset(chatid.toString()) + "/" +
                        bot.getDatabaseHandler().getResetPerDay(chatid.toString()));
                editMessageText.setChatId(chatid.toString());
                editMessageText.enableHtml(true);

                InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
                List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
                List<InlineKeyboardButton> firstRow = new ArrayList<>();
                List<InlineKeyboardButton> secondRow = new ArrayList<>();
                List<InlineKeyboardButton> thirdRow = new ArrayList<>();
                List<InlineKeyboardButton> fourthRow = new ArrayList<>();

                InlineKeyboardButton resetYesOption = new InlineKeyboardButton();
                resetYesOption.setText("\uD83D\uDFE2 Si");
                resetYesOption.setCallbackData("admin_reset_yes");
                firstRow.add(resetYesOption);

                InlineKeyboardButton resetNoOption = new InlineKeyboardButton();
                resetNoOption.setText("\uD83D\uDD34 No");
                resetNoOption.setCallbackData("admin_reset_no");
                firstRow.add(resetNoOption);

                InlineKeyboardButton increeseOption = new InlineKeyboardButton();
                increeseOption.setText("Incrementa");
                increeseOption.setCallbackData("admin_increese");
                secondRow.add(increeseOption);

                InlineKeyboardButton decreeseOption = new InlineKeyboardButton();
                decreeseOption.setText("Decrementa");
                decreeseOption.setCallbackData("admin_decreese");
                secondRow.add(decreeseOption);

                InlineKeyboardButton resetOption = new InlineKeyboardButton();
                resetOption.setText("Reset all");
                resetOption.setCallbackData("admin_resetall");
                thirdRow.add(resetOption);

                InlineKeyboardButton backOption = new InlineKeyboardButton();
                backOption.setText("\uD83D\uDD19 Indietro");
                backOption.setCallbackData("admin_settings");
                fourthRow.add(backOption);

                rowsInline.add(firstRow);
                rowsInline.add(secondRow);
                rowsInline.add(thirdRow);
                rowsInline.add(fourthRow);

                markupInline.setKeyboard(rowsInline);
                editMessageText.setReplyMarkup(markupInline);
                executeAsync(editMessageText);
                return;
            }

            if(update.hasCallbackQuery() && update.getCallbackQuery().getData().equalsIgnoreCase("admin_reset_no")){
                Long chatid = update.getCallbackQuery().getMessage().getChatId();
                bot.getDatabaseHandler().removePermissionToGroup(chatid.toString(),
                            PermissionType.ADMIN_CAN_RESET_KING);

                EditMessageText editMessageText = new EditMessageText();
                editMessageText.setMessageId(update.getCallbackQuery().getMessage().getMessageId());
                editMessageText.setText("<b>Ecco qua le impostazioni per gli amministratori</b>\n\n" +
                        "Scegli se gli admin possono resettare il re papera del giorno\n\nAttualmente: " +
                        (bot.getDatabaseHandler().getActualSettings(chatid.toString()).stream()
                                .anyMatch(permissionType -> permissionType == PermissionType.ADMIN_CAN_RESET_KING)
                                ? "<b>Si</b>" : "<b>No</b>") +
                        "\n\nReset: " + bot.getDatabaseHandler().getActualReset(chatid.toString()) + "/" +
                        bot.getDatabaseHandler().getResetPerDay(chatid.toString()));
                editMessageText.setChatId(chatid.toString());
                editMessageText.enableHtml(true);

                InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
                List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
                List<InlineKeyboardButton> firstRow = new ArrayList<>();
                List<InlineKeyboardButton> secondRow = new ArrayList<>();
                List<InlineKeyboardButton> thirdRow = new ArrayList<>();
                List<InlineKeyboardButton> fourthRow = new ArrayList<>();

                InlineKeyboardButton resetYesOption = new InlineKeyboardButton();
                resetYesOption.setText("\uD83D\uDFE2 Si");
                resetYesOption.setCallbackData("admin_reset_yes");
                firstRow.add(resetYesOption);

                InlineKeyboardButton resetNoOption = new InlineKeyboardButton();
                resetNoOption.setText("\uD83D\uDD34 No");
                resetNoOption.setCallbackData("admin_reset_no");
                firstRow.add(resetNoOption);

                InlineKeyboardButton increeseOption = new InlineKeyboardButton();
                increeseOption.setText("Incrementa");
                increeseOption.setCallbackData("admin_increese");
                secondRow.add(increeseOption);

                InlineKeyboardButton decreeseOption = new InlineKeyboardButton();
                decreeseOption.setText("Decrementa");
                decreeseOption.setCallbackData("admin_decreese");
                secondRow.add(decreeseOption);

                InlineKeyboardButton resetOption = new InlineKeyboardButton();
                resetOption.setText("Reset all");
                resetOption.setCallbackData("admin_resetall");
                thirdRow.add(resetOption);

                InlineKeyboardButton backOption = new InlineKeyboardButton();
                backOption.setText("\uD83D\uDD19 Indietro");
                backOption.setCallbackData("admin_settings");
                fourthRow.add(backOption);

                rowsInline.add(firstRow);
                rowsInline.add(secondRow);
                rowsInline.add(thirdRow);
                rowsInline.add(fourthRow);

                markupInline.setKeyboard(rowsInline);
                editMessageText.setReplyMarkup(markupInline);
                executeAsync(editMessageText);
                return;
            }

            if(update.hasCallbackQuery() && update.getCallbackQuery().getData().equalsIgnoreCase("admin_increese")){
                Long chatid = update.getCallbackQuery().getMessage().getChatId();

                if(bot.getDatabaseHandler().getActualSettings(chatid.toString()).stream()
                        .noneMatch(permissionType -> permissionType == PermissionType.ADMIN_CAN_RESET_KING)){
                    AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery();
                    answerCallbackQuery.setText("❗️ Devi permettere agli admin di usare i comandi");
                    answerCallbackQuery.setShowAlert(true);
                    answerCallbackQuery.setCallbackQueryId(update.getCallbackQuery().getId());
                    executeAsync(answerCallbackQuery);
                    return;
                }

                int times = bot.getDatabaseHandler().getResetPerDay(chatid.toString());
                times++;
                bot.getDatabaseHandler().updateResetPerDay(chatid.toString(), times);

                EditMessageText editMessageText = new EditMessageText();
                editMessageText.setMessageId(update.getCallbackQuery().getMessage().getMessageId());
                editMessageText.setText("<b>Ecco qua le impostazioni per gli amministratori</b>\n\n" +
                        "Scegli se gli admin possono resettare il re papera del giorno\n\nAttualmente: " +
                        (bot.getDatabaseHandler().getActualSettings(chatid.toString()).stream()
                                .anyMatch(permissionType -> permissionType == PermissionType.ADMIN_CAN_RESET_KING)
                                ? "<b>Si</b>" : "<b>No</b>") +
                        "\n\nReset: " + bot.getDatabaseHandler().getActualReset(chatid.toString()) + "/" +
                        bot.getDatabaseHandler().getResetPerDay(chatid.toString()));
                editMessageText.setChatId(update.getCallbackQuery().getMessage().getChatId().toString());
                editMessageText.enableHtml(true);

                InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
                List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
                List<InlineKeyboardButton> firstRow = new ArrayList<>();
                List<InlineKeyboardButton> secondRow = new ArrayList<>();
                List<InlineKeyboardButton> thirdRow = new ArrayList<>();
                List<InlineKeyboardButton> fourthRow = new ArrayList<>();

                InlineKeyboardButton resetYesOption = new InlineKeyboardButton();
                resetYesOption.setText("\uD83D\uDFE2 Si");
                resetYesOption.setCallbackData("admin_reset_yes");
                firstRow.add(resetYesOption);

                InlineKeyboardButton resetNoOption = new InlineKeyboardButton();
                resetNoOption.setText("\uD83D\uDD34 No");
                resetNoOption.setCallbackData("admin_reset_no");
                firstRow.add(resetNoOption);

                InlineKeyboardButton increeseOption = new InlineKeyboardButton();
                increeseOption.setText("Incrementa");
                increeseOption.setCallbackData("admin_increese");
                secondRow.add(increeseOption);

                InlineKeyboardButton decreeseOption = new InlineKeyboardButton();
                decreeseOption.setText("Decrementa");
                decreeseOption.setCallbackData("admin_decreese");
                secondRow.add(decreeseOption);

                InlineKeyboardButton resetOption = new InlineKeyboardButton();
                resetOption.setText("Reset all");
                resetOption.setCallbackData("admin_resetall");
                thirdRow.add(resetOption);

                InlineKeyboardButton backOption = new InlineKeyboardButton();
                backOption.setText("\uD83D\uDD19 Indietro");
                backOption.setCallbackData("admin_settings");
                fourthRow.add(backOption);

                rowsInline.add(firstRow);
                rowsInline.add(secondRow);
                rowsInline.add(thirdRow);
                rowsInline.add(fourthRow);

                markupInline.setKeyboard(rowsInline);
                editMessageText.setReplyMarkup(markupInline);
                executeAsync(editMessageText);
                return;
            }

            if(update.hasCallbackQuery() && update.getCallbackQuery().getData().equalsIgnoreCase("admin_decreese")){
                Long chatid = update.getCallbackQuery().getMessage().getChatId();

                if(bot.getDatabaseHandler().getActualSettings(chatid.toString()).stream()
                        .noneMatch(permissionType -> permissionType == PermissionType.ADMIN_CAN_RESET_KING)){
                    AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery();
                    answerCallbackQuery.setText("❗️ Devi permettere agli admin di usare i comandi");
                    answerCallbackQuery.setShowAlert(true);
                    answerCallbackQuery.setCallbackQueryId(update.getCallbackQuery().getId());
                    executeAsync(answerCallbackQuery);
                    return;
                }

                int times = bot.getDatabaseHandler().getResetPerDay(chatid.toString());
                if(times == 0){
                    AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery();
                    answerCallbackQuery.setText("❗️ Non puoi mettere un numero minore di 0");
                    answerCallbackQuery.setShowAlert(true);
                    answerCallbackQuery.setCallbackQueryId(update.getCallbackQuery().getId());
                    executeAsync(answerCallbackQuery);
                    return;
                }
                times--;
                bot.getDatabaseHandler().updateResetPerDay(chatid.toString(), times);

                EditMessageText editMessageText = new EditMessageText();
                editMessageText.setMessageId(update.getCallbackQuery().getMessage().getMessageId());
                editMessageText.setText("<b>Ecco qua le impostazioni per gli amministratori</b>\n\n" +
                        "Scegli se gli admin possono resettare il re papera del giorno\n\nAttualmente: " +
                        (bot.getDatabaseHandler().getActualSettings(chatid.toString()).stream()
                                .anyMatch(permissionType -> permissionType == PermissionType.ADMIN_CAN_RESET_KING)
                                ? "<b>Si</b>" : "<b>No</b>") +
                        "\n\nReset: " + bot.getDatabaseHandler().getActualReset(chatid.toString()) + "/" +
                        bot.getDatabaseHandler().getResetPerDay(chatid.toString()));
                editMessageText.setChatId(chatid.toString());
                editMessageText.enableHtml(true);

                InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
                List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
                List<InlineKeyboardButton> firstRow = new ArrayList<>();
                List<InlineKeyboardButton> secondRow = new ArrayList<>();
                List<InlineKeyboardButton> thirdRow = new ArrayList<>();
                List<InlineKeyboardButton> fourthRow = new ArrayList<>();

                InlineKeyboardButton resetYesOption = new InlineKeyboardButton();
                resetYesOption.setText("\uD83D\uDFE2 Si");
                resetYesOption.setCallbackData("admin_reset_yes");
                firstRow.add(resetYesOption);

                InlineKeyboardButton resetNoOption = new InlineKeyboardButton();
                resetNoOption.setText("\uD83D\uDD34 No");
                resetNoOption.setCallbackData("admin_reset_no");
                firstRow.add(resetNoOption);

                InlineKeyboardButton increeseOption = new InlineKeyboardButton();
                increeseOption.setText("Incrementa");
                increeseOption.setCallbackData("admin_increese");
                secondRow.add(increeseOption);

                InlineKeyboardButton decreeseOption = new InlineKeyboardButton();
                decreeseOption.setText("Decrementa");
                decreeseOption.setCallbackData("admin_decreese");
                secondRow.add(decreeseOption);

                InlineKeyboardButton resetOption = new InlineKeyboardButton();
                resetOption.setText("Reset all");
                resetOption.setCallbackData("admin_resetall");
                thirdRow.add(resetOption);

                InlineKeyboardButton backOption = new InlineKeyboardButton();
                backOption.setText("\uD83D\uDD19 Indietro");
                backOption.setCallbackData("admin_settings");
                fourthRow.add(backOption);

                rowsInline.add(firstRow);
                rowsInline.add(secondRow);
                rowsInline.add(thirdRow);
                rowsInline.add(fourthRow);

                markupInline.setKeyboard(rowsInline);
                editMessageText.setReplyMarkup(markupInline);
                executeAsync(editMessageText);
                return;
            }

            if(update.hasCallbackQuery() && update.getCallbackQuery().getData().equalsIgnoreCase("admin_resetall")){
                Long chatid = update.getCallbackQuery().getMessage().getChatId();

                if(bot.getDatabaseHandler().getActualSettings(chatid.toString()).stream()
                        .noneMatch(permissionType -> permissionType == PermissionType.ADMIN_CAN_RESET_KING)){
                    AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery();
                    answerCallbackQuery.setText("❗️ Devi permettere agli admin di usare i comandi");
                    answerCallbackQuery.setShowAlert(true);
                    answerCallbackQuery.setCallbackQueryId(update.getCallbackQuery().getId());
                    executeAsync(answerCallbackQuery);
                    return;
                }

                bot.getDatabaseHandler().clearResetPerDay(chatid.toString());

                EditMessageText editMessageText = new EditMessageText();
                editMessageText.setMessageId(update.getCallbackQuery().getMessage().getMessageId());
                editMessageText.setText("<b>Ecco qua le impostazioni per gli amministratori</b>\n\n" +
                        "Scegli se gli admin possono resettare il re papera del giorno\n\nAttualmente: " +
                        (bot.getDatabaseHandler().getActualSettings(chatid.toString()).stream()
                                .anyMatch(permissionType -> permissionType == PermissionType.ADMIN_CAN_RESET_KING)
                                ? "<b>Si</b>" : "<b>No</b>") +
                        "\n\nReset: " + bot.getDatabaseHandler().getActualReset(chatid.toString()) + "/" +
                        bot.getDatabaseHandler().getResetPerDay(chatid.toString()));
                editMessageText.setChatId(chatid.toString());
                editMessageText.enableHtml(true);

                InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
                List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
                List<InlineKeyboardButton> firstRow = new ArrayList<>();
                List<InlineKeyboardButton> secondRow = new ArrayList<>();
                List<InlineKeyboardButton> thirdRow = new ArrayList<>();
                List<InlineKeyboardButton> fourthRow = new ArrayList<>();

                InlineKeyboardButton resetYesOption = new InlineKeyboardButton();
                resetYesOption.setText("\uD83D\uDFE2 Si");
                resetYesOption.setCallbackData("admin_reset_yes");
                firstRow.add(resetYesOption);

                InlineKeyboardButton resetNoOption = new InlineKeyboardButton();
                resetNoOption.setText("\uD83D\uDD34 No");
                resetNoOption.setCallbackData("admin_reset_no");
                firstRow.add(resetNoOption);

                InlineKeyboardButton increeseOption = new InlineKeyboardButton();
                increeseOption.setText("Incrementa");
                increeseOption.setCallbackData("admin_increese");
                secondRow.add(increeseOption);

                InlineKeyboardButton decreeseOption = new InlineKeyboardButton();
                decreeseOption.setText("Decrementa");
                decreeseOption.setCallbackData("admin_decreese");
                secondRow.add(decreeseOption);

                InlineKeyboardButton resetOption = new InlineKeyboardButton();
                resetOption.setText("Reset all");
                resetOption.setCallbackData("admin_resetall");
                thirdRow.add(resetOption);

                InlineKeyboardButton backOption = new InlineKeyboardButton();
                backOption.setText("\uD83D\uDD19 Indietro");
                backOption.setCallbackData("admin_settings");
                fourthRow.add(backOption);

                rowsInline.add(firstRow);
                rowsInline.add(secondRow);
                rowsInline.add(thirdRow);
                rowsInline.add(fourthRow);

                markupInline.setKeyboard(rowsInline);
                editMessageText.setReplyMarkup(markupInline);
                executeAsync(editMessageText);
                return;
            }

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
            User user = message.getFrom();


            if (message.getText().equalsIgnoreCase("/ducksettings") || message.getText().equalsIgnoreCase("/ducksettings@" + getBotUsername())) {
                SendMessage sendMessage = new SendMessage();
                sendMessage.setText("<b>Impostazioni del gruppo</b>\n<i>" + message.getChat().getTitle() + "</i>");
                sendMessage.enableHtml(true);
                sendMessage.setChatId(message.getChatId().toString());

                InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
                List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
                List<InlineKeyboardButton> firstRow = new ArrayList<>();

                InlineKeyboardButton adminOption = new InlineKeyboardButton();
                adminOption.setText("Impostazioni admin");
                adminOption.setCallbackData("admin_settings");
                firstRow.add(adminOption);

                rowsInline.add(firstRow);

                markupInline.setKeyboard(rowsInline);
                sendMessage.setReplyMarkup(markupInline);
                executeAsync(sendMessage);
                return;
            }


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
            if (message.getText().equalsIgnoreCase("/king") || message.getText().equalsIgnoreCase("/king@" + getBotUsername())) {
                SendPhoto sendPhoto = new SendPhoto();
                sendPhoto.setChatId(chatID.toString());
                sendPhoto.setPhoto(new InputFile(new File("zera.jpg")));
                sendPhoto.setCaption("\uD83C\uDF89 • <b>DUCK KING</b> • \uD83E\uDD86 \n\n\uD83D\uDC51 — <i>Zera</i> è un vero <b>duck lover</b> ❤️");
                sendPhoto.setParseMode("HTML");
                executeAsync(sendPhoto);
                return;
            }

            if((message.getText().startsWith("/adddking") || message.getText().startsWith("/adddking@" + getBotUsername())) &&
                    message.getText().split(" ").length == 2){
                long target;
                try{
                    target = Long.parseLong(message.getText().split(" ")[1]);
                } catch (NumberFormatException e){
                    SendMessage sendMessage = new SendMessage();
                    sendMessage.setText("<a href=\"https://i.imgur.com/AA1hyTV.jpg\">&#8205</a> \uD83D\uDE2D || <b>HEY</b> •" +
                            " \uD83E\uDD86\n\n" +
                            "\uD83D\uDC51 — <b>" +
                            (user.getLastName() != null ? " " + user.getFirstName() + user.getLastName() : user.getFirstName()) +
                            "</b> devi inserire un id valido");
                    sendMessage.setChatId(chatID.toString());
                    sendMessage.enableHtml(true);
                    executeAsync(sendMessage);
                    return;
                }
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
                            " </b>come re papera di questo gruppo" + "!\n\n");
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


            if(message.getText().equalsIgnoreCase("/removedking") || message.getText().equalsIgnoreCase("/removedking@" + getBotUsername())){
                GetChatMember chatMember = new GetChatMember();
                chatMember.setUserId(user.getId());
                chatMember.setChatId(chatID.toString());
                CompletableFuture<ChatMember> result = executeAsync(chatMember);
                ChatMember member = result.get();
                if(bot.getDatabaseHandler().getActualSettings(message.getChatId().toString()).stream()
                        .anyMatch(permissionType -> permissionType == PermissionType.ADMIN_CAN_RESET_KING) &&
                        bot.getDatabaseHandler().getActualReset(chatID.toString()) <= 0 &&
                        (member.getStatus().equalsIgnoreCase("creator") ||
                                member.getStatus().equalsIgnoreCase("administrator"))) {

                    SendMessage sendMessage = new SendMessage();
                    sendMessage.setChatId(message.getChatId().toString());
                    sendMessage.enableHtml(true);
                    sendMessage.setText("<a href=\"https://i.imgur.com/AA1hyTV.jpg\">&#8205</a> \uD83D\uDE2D || <b>HEY</b> •" +
                            " \uD83E\uDD86\n\n" +
                            "\uD83D\uDC51 — <b>" +
                            (user.getLastName() != null ? " " + user.getFirstName() + user.getLastName() : user.getFirstName()) +
                            "</b> hai finito i reset giornalieri per il Re Papera");
                    executeAsync(sendMessage);
                    return;
                }
                if(member.getStatus().equalsIgnoreCase("creator") || member.getStatus().equalsIgnoreCase("administrator")){
                    bot.getDatabaseHandler().removeKing(chatID.toString());
                    int times = bot.getDatabaseHandler().getResetPerDay(chatID.toString());
                    if(bot.getDatabaseHandler().getActualSettings(message.getChatId().toString()).stream()
                            .anyMatch(permissionType -> permissionType == PermissionType.ADMIN_CAN_RESET_KING) &&
                            times > 0) {
                        bot.getDatabaseHandler().decreaseResetPerDay(chatID.toString());
                    }
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
                            (data.getLastName() != null ? data.getFirstName() + " " + data.getLastName() : data.getFirstName()) +
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
                        "\uD83D\uDC51 — <b>" +
                        (user.getLastName() != null ? user.getFirstName() + " " + user.getLastName() : user.getFirstName()) +
                        "</b> sei il Re Papera di oggi!\n\n");
                executeAsync(newDuckKing);
                bot.getDatabaseHandler().insertKing(chatID.toString(), user);
            }
        } catch (TelegramApiException | ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }

}
