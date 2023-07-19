package com.example.it32007telegram.services;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface TelegramService {

    SendMessage listCommandReceived(CallbackQuery callbackQuery);

    SendMessage startCommandReceived(Message message);

    SendMessage createCommandReceived(CallbackQuery callbackQuery);

    SendMessage searchCommandReceived(CallbackQuery callbackQuery);

    SendMessage getEvent(Update update);

    SendMessage chooseLanguageCommandReceived(Message message);

    SendMessage chooseLanguage(Update update);

    SendMessage sendGreetingMessage(Message message, String lang);

    SendMessage sendChoosingActionButtons(CallbackQuery callbackQuery);

    SendMessage makeMainAction(Update update);

    SendMessage chooseCity(Update update);

    SendMessage chooseCategory(Update update);
}
