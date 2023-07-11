package com.example.it32007telegram.services;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface TelegramService {

    SendMessage listCommandReceived(Update update);

    SendMessage editCommandReceived(Update update);

    SendMessage startCommandReceived(Update update);

    SendMessage createCommandReceived(Update update);

    SendMessage searchCommandReceived(Update update);

    SendMessage getEvent(Update update);
}
