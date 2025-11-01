package ru.itmo.telecom.notification.bot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.itmo.telecom.notification.service.BotService;

import java.util.ArrayList;
import java.util.List;

public class TariffBot extends TelegramLongPollingBot {

    private static final Logger log = LoggerFactory.getLogger(TariffBot.class);

    private final String botUsername;
    private final BotService botService;

    public TariffBot(DefaultBotOptions options, String botToken, BotService botService) {
        super(options, botToken);
        this.botUsername = "TariffWizardBot";
        this.botService = botService;
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            Long chatId = update.getMessage().getChatId();
            String userFirstName = update.getMessage().getFrom().getFirstName();

            log.info("Received message from {} ({}): {}", userFirstName, chatId, messageText);

            try {
                String response = botService.handleMessage(chatId, messageText, userFirstName);
                sendResponse(chatId, response);
            } catch (Exception e) {
                log.error("Error processing message", e);
                sendResponse(chatId, "Произошла ошибка при обработке запроса. Пожалуйста, попробуйте позже.");
            }
        }
    }

    private void sendResponse(Long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(text);

        if (text.contains("Доступные команды") || text.contains("Привет")) {
            message.setReplyMarkup(createMainKeyboard());
        }

        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error("Error sending message to chat {}: {}", chatId, e.getMessage());
        }
    }

    private ReplyKeyboardMarkup createMainKeyboard() {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setOneTimeKeyboard(false);

        List<KeyboardRow> keyboard = new ArrayList<>();

        KeyboardRow row1 = new KeyboardRow();
        row1.add("/tariffs");
        row1.add("/constructor");

        KeyboardRow row2 = new KeyboardRow();
        row2.add("/profile");
        row2.add("/my_orders");

        KeyboardRow row3 = new KeyboardRow();
        row3.add("/help");
        row3.add("/cancel");

        keyboard.add(row1);
        keyboard.add(row2);
        keyboard.add(row3);

        keyboardMarkup.setKeyboard(keyboard);
        return keyboardMarkup;
    }
}