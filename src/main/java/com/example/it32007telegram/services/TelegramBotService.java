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
    private final GeocoderService geocoderService;

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
            }
            if(callbackData.startsWith("placeLocation-")){
                if(message.hasLocation()){
                    System.out.println(geocoderService.getPlace(message.getLocation().getLongitude(),message.getLocation().getLatitude()));
                }
            }
            if(callbackData.startsWith("lang-")){
                execute(telegramService.chooseLanguage(update));
                execute(telegramService.sendChoosingActionButtons(callbackQuery));
            }
            if(callbackData.startsWith("action-")){
                execute(telegramService.makeMainAction(update));
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
            switch (messageText) {
                case "/start":
                    execute(telegramService.startCommandReceived(message));
                    break;
                case "\uD83D\uDD8B Создать":
                 //   execute(telegramService.createCommandReceived(update));
                    break;
                case "\uD83D\uDD8B Редактировать":
                    execute(telegramService.editCommandReceived(update));
                    break;
             //   case "\uD83D\uDCCB Мои мероприятия":
              //      execute(telegramService.listCommandReceived(update));
                //    break;
                case "\uD83D\uDD0D Искать":
              //      execute(telegramService.searchCommandReceived(update));
                    break;
                default:
                //  telegramService.processDefaultStates(update);
            }
        }
    }

    @Override
    public void onUpdatesReceived(List<Update> updates) {
        super.onUpdatesReceived(updates);
    }
}
