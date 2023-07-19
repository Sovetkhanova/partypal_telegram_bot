package com.example.it32007telegram.services;

import com.example.it32007telegram.models.entities.users.User;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TelegramBotService extends TelegramLongPollingBot {

    @Value("${bots.telegram.name:}")
    private String name;
    @Value("${bots.telegram.token:}")
    private String token;

    private final TelegramService telegramService;
    private final EventService eventService;

    @Override
    public String getBotUsername() {
        return name;
    }

    @Override
    public String getBotToken() {
        return token;
    }

    @Override
    public void onRegister() {
        super.onRegister();
    }

    @SneakyThrows
    @Override
    public void onUpdateReceived(Update update) {
        Message message = update.getMessage();
        if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            String callbackData = callbackQuery.getData();
            if (callbackData.startsWith("event-")) {
                execute(telegramService.getEvent(update));
            }
            if(callbackData.startsWith("lang-")){
                execute(telegramService.chooseLanguage(update));
                execute(telegramService.sendChoosingActionButtons(callbackQuery));
            }
            if(callbackData.startsWith("action-")){
                String callbackLang = callbackQuery.getData().substring(7);
                if(callbackLang.startsWith("1")){
                    List<Object> objs = telegramService.createCommandReceived(callbackQuery);
                    Integer messageId = execute((SendMessage) objs.get(0)).getMessageId();
                    eventService.createEvent((User) objs.get(1), Long.valueOf(messageId));
                }
                else{
                    execute(telegramService.makeMainAction(update));
                }
            }
            if(callbackData.startsWith("city-")) {
                execute(telegramService.chooseCity(update));
            }
            if(callbackData.startsWith("category-")){
                execute(telegramService.chooseCategory(update));
            }
        }
        if (message != null && message.hasText()) {
            String messageText = message.getText();
            if ("/start".equals(messageText)) {
                execute(telegramService.startCommandReceived(message));
            }
        }
    }

    @Override
    public void onUpdatesReceived(List<Update> updates) {
        super.onUpdatesReceived(updates);
    }
}
