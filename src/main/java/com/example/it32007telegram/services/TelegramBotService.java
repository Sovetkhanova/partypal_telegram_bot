package com.example.it32007telegram.services;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
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
            if (callbackData.startsWith("nameEdit-")) {
             //   execute(telegramService.editEventTitle(update));
            }
        }
        if (message != null && message.hasText()) {
            String messageText = message.getText();
            switch (messageText) {
                case "/start":
                    execute(telegramService.startCommandReceived(update));
                    break;
                case "\uD83D\uDD8B Создать":
                    execute(telegramService.createCommandReceived(update));
                    break;
                case "\uD83D\uDD8B Редактировать":
                    execute(telegramService.editCommandReceived(update));
                    break;
                case "\uD83D\uDCCB Мои мероприятия":
                    execute(telegramService.listCommandReceived(update));
                    break;
                case "\uD83D\uDD0D Искать":
                    execute(telegramService.searchCommandReceived(update));
                    break;
                    /*    case "\uD83C\uDFCBСпорт и активности":
                    telegramService.createEvent(Category.RoleCode.Sport, message);
                    break;
                case "\uD83C\uDFA8Культура и искусство":
                    telegramService.createEvent(Category.RoleCode.Culture, message);
                    break;
                case "\uD83C\uDF89Вечеринки, встречи, сетевые мероприятия и другие события":
                    telegramService.createEvent(Category.RoleCode.Party, message);
                    break;
                case "\uD83C\uDFA5Фильмы и кино":
                    telegramService.createEvent(Category.RoleCode.Cinema, message);
                    break;
                case "\uD83C\uDF72Кулинария и рестораны":
                    telegramService.createEvent(Category.RoleCode.Restaurant, message);
                    break;
                case "\uD83D\uDCD6Образование и лекции":
                    telegramService.createEvent(Category.RoleCode.Education, message);
                    break;
                default:
                    telegramService.processDefaultStates(update);*/
            }
        }
    }

    @Override
    public void onUpdatesReceived(List<Update> updates) {
        super.onUpdatesReceived(updates);
    }
}
