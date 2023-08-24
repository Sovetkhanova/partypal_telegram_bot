package com.example.partypal.controllers;

import com.example.partypal.services.TelegramService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.interfaces.Validable;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TelegramBotController extends TelegramLongPollingBot {

    @Value("${bots.telegram.name:}")
    private String name;
    @Value("${bots.telegram.token:}")
    private String token;

    private final TelegramService telegramService;

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
            handleCallBackQuery(update);
        }
        if(update.hasPreCheckoutQuery()){
            execute(telegramService.handlePayment(update));
        }
        if (message != null) {
            if(message.hasSuccessfulPayment()){
                execute(telegramService.handleSuccessfulPayment(update));
            }
            if (message.hasText()) {
                handleMessageText(update);
            }
            if (message.hasLocation()) {
                handleMessageLocation(update);
            }
            if (message.hasPhoto()) {
                handleMessagePhoto(update);
            }
        }
    }

    @SneakyThrows
    private void handleMessagePhoto(Update update) {
        List<Validable> messages = telegramService.handleDefaultMessages(update);
        if (messages != null) {
            for (Validable m : messages) {
                if (m.getClass().equals(SendMessage.class)) {
                    execute((SendMessage) m);
                }
                if (m.getClass().equals(SendPhoto.class)) {
                    execute((SendPhoto) m);
                }
            }
        }
    }

    @SneakyThrows
    private void handleMessageLocation(Update update) {
        List<Validable> messages = telegramService.handleDefaultMessages(update);
        if (messages != null) {
            for (Validable m : messages) {
                if (m.getClass().equals(SendMessage.class)) {
                    execute((SendMessage) m);
                }
            }
        }
    }

    @SneakyThrows
    private void handleMessageText(Update update) {
        Message message = update.getMessage();
        String messageText = message.getText();
        CallbackQuery callbackQuery = new CallbackQuery();
        callbackQuery.setFrom(message.getFrom());
        callbackQuery.setMessage(message);
        if ("/start".equals(messageText)) {
            execute(telegramService.startCommandReceived(message));
        }
        if ("/create".equals(messageText)) {
            execute(telegramService.createCommandReceived(callbackQuery));
        }
        if ("/list".equals(messageText)) {
            execute(telegramService.listCommandReceived(callbackQuery));
        }
        if ("/search".equals(messageText)) {
            execute(telegramService.searchCommandReceived(callbackQuery));
        }
        if ("/lang".equals(messageText)) {
            execute(telegramService.chooseLanguageCommandReceived(message));
        }
        if (!messageText.startsWith("/")) {
            handleMessagePhoto(update);
        }
    }

    @SneakyThrows
    private void handleCallBackQuery(Update update) {
        CallbackQuery callbackQuery = update.getCallbackQuery();
        String callbackData = callbackQuery.getData();
        if (callbackData.startsWith("event-")) {
            List<Validable> messages = telegramService.getEvent(update);
            for (Validable m : messages) {
                if (m.getClass().equals(SendPhoto.class)) {
                    execute((SendPhoto) m);
                }
                if (m.getClass().equals(SendMessage.class)) {
                    execute((SendMessage) m);
                }
            }
        }
        if (callbackData.startsWith("lang-")) {
            execute(telegramService.chooseLanguage(update));
            execute(telegramService.sendChoosingActionButtons(callbackQuery));
        }
        if (callbackData.startsWith("action-")) {
            SendMessage sendMessage = telegramService.makeMainAction(update);
            if (sendMessage != null) {
                execute(sendMessage);
            }
        }
        if (callbackData.startsWith("city-")) {
            List<Validable> v = telegramService.chooseCity(update);
            for (Validable m : v) {
                if (m.getClass().equals(SendMessage.class)) {
                    execute((SendMessage) m);
                }
            }
        }
        if (callbackData.startsWith("category-")) {
            if (callbackData.endsWith("null")) {
                List<Validable> messages = telegramService.handleDefaultMessages(update);
                if (messages != null) {
                    for (Validable m : messages) {
                        if (m.getClass().equals(SendMessage.class)) {
                            execute((SendMessage) m);
                        }
                    }
                }
            } else {
                execute(telegramService.chooseCategoryForSearch(update));
            }
        }
        if (callbackData.startsWith("eventAction-")) {
            List<SendMessage> messages = telegramService.makeEventAction(update);
            if (messages != null) {
                for (SendMessage m : messages) {
                    if (m != null) {
                        execute(m);
                    }
                }
            }
        }
        if(callbackData.startsWith("edit-")) {
            List<Validable> messages = telegramService.handleDefaultMessages(update);
            if (messages != null) {
                for (Validable m : messages) {
                    if (m.getClass().equals(SendMessage.class)) {
                        execute((SendMessage) m);
                    }
                }
            }
        }
        if(callbackData.startsWith("subs-")){
            execute(telegramService.sendInvoice(update));
        }
    }

    @Override
    public void onUpdatesReceived(List<Update> updates) {
        super.onUpdatesReceived(updates);
    }
}
