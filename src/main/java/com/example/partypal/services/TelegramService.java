package com.example.partypal.services;

import com.example.partypal.models.entities.Event;
import com.example.partypal.models.entities.users.User;
import org.telegram.telegrambots.meta.api.interfaces.Validable;
import org.telegram.telegrambots.meta.api.methods.AnswerPreCheckoutQuery;
import org.telegram.telegrambots.meta.api.methods.invoices.SendInvoice;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

public interface TelegramService {
    SendMessage startCommandReceived(Message message);

    SendMessage listCommandReceived(CallbackQuery callbackQuery);

    SendMessage createCommandReceived(CallbackQuery callbackQuery);

    SendMessage searchCommandReceived(CallbackQuery callbackQuery);

    List<Validable> getEvent(Update update);

    SendMessage chooseLanguageCommandReceived(Message message);

    SendMessage chooseLanguage(Update update);

    SendMessage sendGreetingMessage(Message message, String lang);

    SendMessage sendChoosingActionButtons(CallbackQuery callbackQuery);

    SendMessage makeMainAction(Update update);

    List<Validable> chooseCity(Update update);

    SendMessage chooseCategoryForSearch(Update update);

    List<Validable> handleDefaultMessages(Update update);

    SendMessage sendEventActionButtons(CallbackQuery callbackQuery, User user, Event event);

    List<SendMessage> makeEventAction(Update update);

    void handlePhoto(Message message);

    SendInvoice sendInvoice(Update update);

    AnswerPreCheckoutQuery handlePayment(Update update);

    SendMessage handleSuccessfulPayment(Update update);

    SendMessage getMySubscriptionsCommandReceived(Message message);
}
