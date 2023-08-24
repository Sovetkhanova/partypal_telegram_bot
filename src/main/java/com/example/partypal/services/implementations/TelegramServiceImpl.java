package com.example.partypal.services.implementations;

import com.example.partypal.daos.repositories.*;
import com.example.partypal.models.SubscriptionEventLink;
import com.example.partypal.models.entities.*;
import com.example.partypal.models.entities.telegram.Document;
import com.example.partypal.models.entities.telegram.State;
import com.example.partypal.models.entities.users.User;
import com.example.partypal.services.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.interfaces.Validable;
import org.telegram.telegrambots.meta.api.methods.AnswerPreCheckoutQuery;
import org.telegram.telegrambots.meta.api.methods.invoices.SendInvoice;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.api.objects.payments.LabeledPrice;
import org.telegram.telegrambots.meta.api.objects.payments.SuccessfulPayment;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import javax.validation.constraints.NotNull;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TelegramServiceImpl implements TelegramService {
    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;
    private final EventService eventService;
    private final UserService userService;
    private final MessageSource messageSource;
    private final TranslatorService translatorService;
    private final CityRepository cityRepository;
    private final StateRepository stateRepository;
    private final GeocoderService geocoderService;
    private final UserEventLinkRepository userEventLinkRepository;
    private final DocumentServiceImpl documentService;
    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionEventLinkRepository subscriptionEventLinkRepository;
    @Value("${bots.telegram.paymentToken:}")
    private String providedToken;

    @Override
    public SendMessage startCommandReceived(Message message) {
        return chooseLanguageCommandReceived(message);
    }

    private SendMessage promoteCommandReceived(long userId, long eventId, Update update) {
        List<List<InlineKeyboardButton>> inlineButtons = new ArrayList<>();
        List<InlineKeyboardButton> inlineKeyboardButtons = new ArrayList<>();
        List<String> subscriptions = new ArrayList<>();
        Optional<User> user = userService.findUserById(userId);
        if(user.isPresent()){
            User user1 = user.get();
            subscriptions.add(getTextByLanguage(user1.getLang(), "WEEK.TOP5").concat("  -  ").concat(String.valueOf(subscriptionRepository.findByCode(Subscription.Code.WEEK_5.name()).get().getPrice())));
            subscriptions.add(getTextByLanguage(user1.getLang(), "2WEEK.TOP5").concat("  -  ").concat(String.valueOf(subscriptionRepository.findByCode(Subscription.Code.TWO_WEEK_5.name()).get().getPrice())));
            subscriptions.add(getTextByLanguage(user1.getLang(), "MONTH.TOP5").concat("  -  ").concat(String.valueOf(subscriptionRepository.findByCode(Subscription.Code.MONTH_5.name()).get().getPrice())));
            int buttonsPerRow = 1;
            for (int i = 0; i < subscriptions.size(); i++) {
                InlineKeyboardButton button = new InlineKeyboardButton(subscriptions.get(i));
                button.setCallbackData("subs-".concat(String.valueOf(i)).concat("u-").concat(String.valueOf(userId).concat("e-").concat(String.valueOf(eventId))));
                inlineKeyboardButtons.add(button);
                if (inlineKeyboardButtons.size() == buttonsPerRow) {
                    inlineButtons.add(inlineKeyboardButtons);
                    inlineKeyboardButtons = new ArrayList<>();
                }
            }
            if(!inlineKeyboardButtons.isEmpty()){
                inlineButtons.add(inlineKeyboardButtons);
            }
            InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
            keyboardMarkup.setKeyboard(inlineButtons);
            SendMessage messageReturn = createMessage(update.getCallbackQuery().getMessage(), getTextByLanguage(user1.getLang(), "CHOOSE.SUBSCRIPTION"), true);
            messageReturn.setReplyMarkup(keyboardMarkup);
            return messageReturn;
        }
        else return null;
    }

    @Override
    public SendMessage chooseLanguageCommandReceived(Message message) {
        List<List<InlineKeyboardButton>> inlineButtons = new ArrayList<>();
        List<InlineKeyboardButton> inlineKeyboardButtons = new ArrayList<>();
        List<String> langList = new ArrayList<>();
        langList.add(getTextByLanguage("kz", "lang.KZ"));
        langList.add(getTextByLanguage("ru", "lang.RU"));
        langList.add(getTextByLanguage("en", "lang.EN"));
        for (String lang : langList) {
            InlineKeyboardButton button = new InlineKeyboardButton(lang);
            button.setCallbackData("lang-" + lang);
            inlineKeyboardButtons.add(button);
        }

        inlineButtons.add(inlineKeyboardButtons);
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        keyboardMarkup.setKeyboard(inlineButtons);

        SendMessage messageReturn = createMessage(message, "Выберите язык", true);
        messageReturn.setReplyMarkup(keyboardMarkup);
        return messageReturn;
    }

    @Override
    public SendMessage chooseLanguage(Update update) {
        CallbackQuery callbackQuery = update.getCallbackQuery();
        User user = userService.getOrCreateUser(callbackQuery.getFrom());
        String callbackLang = callbackQuery.getData().substring(5);
        if (callbackLang.contains("Қазақша")) {
            user.setLang("kz");
        }
        if (callbackLang.contains("Русский")) {
            user.setLang("ru");
        }
        if (callbackLang.contains("English")) {
            user.setLang("en");
        }
        userService.saveUser(user);
        return sendGreetingMessage(update.getCallbackQuery().getMessage(), user.getLang());
    }

    @Override
    public SendMessage sendGreetingMessage(Message message, String lang) {
        String answer;
        switch (lang) {
            case "kz":
                answer = getTextByLanguage("kz", "GREETING");
                break;
            case "en":
                answer = getTextByLanguage("en", "GREETING");
                break;
            default:
                answer = getTextByLanguage("ru", "GREETING");
                break;
        }
        return createMessage(message, answer, false);
    }

    @Override
    public SendMessage sendChoosingActionButtons(CallbackQuery callbackQuery) {
        List<List<InlineKeyboardButton>> inlineButtons = new ArrayList<>();
        List<InlineKeyboardButton> inlineKeyboardButtons = new ArrayList<>();
        List<String> mainActionsList = new ArrayList<>();
        User user = userService.getOrCreateUser(callbackQuery.getFrom());
        String answer;
        switch (user.getLang()) {
            case "kz":
                mainActionsList.add("1. " + getTextByLanguage("kz", "EVENT.CREATE"));
                mainActionsList.add("2. " + getTextByLanguage("kz", "EVENT.SEARCH"));
                mainActionsList.add("3. " + getTextByLanguage("kz", "EVENT.MINE"));
                answer = getTextByLanguage("kz", "EVENT.CHOOSE.MAIN.ACTION");
                break;
            case "en":
                mainActionsList.add("1. " + getTextByLanguage("en", "EVENT.CREATE"));
                mainActionsList.add("2. " + getTextByLanguage("en", "EVENT.SEARCH"));
                mainActionsList.add("3. " + getTextByLanguage("en", "EVENT.MINE"));
                answer = getTextByLanguage("en", "EVENT.CHOOSE.MAIN.ACTION");
                break;
            default:
                mainActionsList.add("1. " + getTextByLanguage("ru", "EVENT.CREATE"));
                mainActionsList.add("2. " + getTextByLanguage("ru", "EVENT.SEARCH"));
                mainActionsList.add("3. " + getTextByLanguage("ru", "EVENT.MINE"));
                answer = getTextByLanguage("ru", "EVENT.CHOOSE.MAIN.ACTION");
                break;
        }
        for (String action : mainActionsList) {
            InlineKeyboardButton button = new InlineKeyboardButton(action);
            button.setCallbackData("action-" + action);
            inlineKeyboardButtons.add(button);
        }
        inlineButtons.add(inlineKeyboardButtons);
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        keyboardMarkup.setKeyboard(inlineButtons);
        SendMessage messageReturn = createMessage(callbackQuery.getMessage(), answer, false);
        messageReturn.setReplyMarkup(keyboardMarkup);
        return messageReturn;
    }

    @Override
    public SendMessage sendEventActionButtons(CallbackQuery callbackQuery, User user, Event event) {
        boolean isCreator = (event.getCreatedUser() != null && event.getCreatedUser().getId().equals(user.getId()));
        List<List<InlineKeyboardButton>> inlineButtons = new ArrayList<>();
        List<InlineKeyboardButton> inlineKeyboardButtons = new ArrayList<>();
        Map<Integer, String> mainActionsMap = new HashMap<>();
        String answer;
        switch (user.getLang()) {
            case "kz":
                if (isCreator) {
                    mainActionsMap.put(0, "1. " + getTextByLanguage("kz", "EVENT.EDIT"));
                    mainActionsMap.put(1, "2. " + getTextByLanguage("kz", "EVENT.DELETE"));
                    mainActionsMap.put(4, "3. " + getTextByLanguage("kz", "EVENT.PROMOTE"));
                } else {
                    mainActionsMap.put(2, "1. " + getTextByLanguage("kz", "EVENT.REMARK"));
                    mainActionsMap.put(3, "2. " + getTextByLanguage("kz", "EVENT.REMARK.DELETE"));
                }
                answer = getTextByLanguage("kz", "EVENT.CHOOSE.MAIN.ACTION");
                break;
            case "en":
                if (isCreator) {
                    mainActionsMap.put(0, "1. " + getTextByLanguage("en", "EVENT.EDIT"));
                    mainActionsMap.put(1, "2. " + getTextByLanguage("en", "EVENT.DELETE"));
                    mainActionsMap.put(4, "3. " + getTextByLanguage("en", "EVENT.PROMOTE"));
                } else {
                    mainActionsMap.put(2, "1. " + getTextByLanguage("en", "EVENT.REMARK"));
                    mainActionsMap.put(3, "2. " + getTextByLanguage("en", "EVENT.REMARK.DELETE"));
                }
                answer = getTextByLanguage("en", "EVENT.CHOOSE.MAIN.ACTION");
                break;
            default:
                if (isCreator) {
                    mainActionsMap.put(0, "1. " + getTextByLanguage("ru", "EVENT.EDIT"));
                    mainActionsMap.put(1, "2. " + getTextByLanguage("ru", "EVENT.DELETE"));
                    mainActionsMap.put(4, "3. " + getTextByLanguage("ru", "EVENT.PROMOTE"));
                } else {
                    mainActionsMap.put(2, "1. " + getTextByLanguage("ru", "EVENT.REMARK"));
                    mainActionsMap.put(3, "2. " + getTextByLanguage("ru", "EVENT.REMARK.DELETE"));
                }
                answer = getTextByLanguage("ru", "EVENT.CHOOSE.MAIN.ACTION");
                break;
        }
        for (int i = 0; i < 5; i++) {
            if (mainActionsMap.containsKey(i)) {
                InlineKeyboardButton button = new InlineKeyboardButton(mainActionsMap.get(i));
                button.setCallbackData("eventAction-" + i + "user-" + user.getId() + "event-" + event.getId());
                inlineKeyboardButtons.add(button);
            }
        }
        inlineButtons.add(inlineKeyboardButtons);
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        keyboardMarkup.setKeyboard(inlineButtons);
        SendMessage messageReturn = createMessage(callbackQuery.getMessage(), answer, false);
        messageReturn.setReplyMarkup(keyboardMarkup);
        return messageReturn;
    }

    @Override
    public List<SendMessage> makeEventAction(Update update) {
        List<SendMessage> sendMessageList = new ArrayList<>();
        CallbackQuery callbackQuery = update.getCallbackQuery();
        String[] parts = callbackQuery.getData().split("-");
        long actionId = Long.parseLong(parts[1].replaceAll("\\D", ""));
        long userId = Long.parseLong(parts[2].replaceAll("\\D", ""));
        long eventId = Long.parseLong(parts[3].replaceAll("\\D", ""));
        switch ((int) actionId) {
            case 0:
                sendMessageList.add(editCommandReceived(userId, eventId, update));
                break;
            case 1:
                sendMessageList.add(deleteCommandReceived(userId, eventId, update));
                sendMessageList.add(sendChoosingActionButtons(callbackQuery));
                break;
            case 2:
                sendMessageList.add(remarkCommandReceived(userId, eventId, update));
                sendMessageList.add(sendChoosingActionButtons(callbackQuery));
                break;
            case 3:
                sendMessageList.add(deleteRemarkCommandReceived(userId, eventId, update));
                sendMessageList.add(sendChoosingActionButtons(callbackQuery));
                break;
            case 4:
                SendMessage sendMessage = promoteCommandReceived(userId, eventId, update);
                if (sendMessage != null) {
                    sendMessageList.add(sendMessage);
                    break;
                }
                else sendMessageList.add(sendChoosingActionButtons(callbackQuery));
                break;
            default:
                return null;
        }
        return sendMessageList;
    }

    @Override
    public void handlePhoto(Message message) {
        User user = userService.getOrCreateUser(message.getFrom());
        Event event = user.getActualEvent();
        if (event != null) {
            List<PhotoSize> photoSizes = message.getPhoto();
            List<Document> s = new ArrayList<>();
            PhotoSize maxPS = photoSizes.get(0);
            int maxId = 0;
            for (int i = 0; i < photoSizes.size(); i++) {
                Document file = Document.builder()
                        .tgId(photoSizes.get(i).getFileId())
                        .tgUniqueId(photoSizes.get(i).getFileUniqueId())
                        .size(photoSizes.get(i).getFileSize().longValue())
                        .event(event)
                        .name(event.getName())
                        .build();
                if (maxPS.getFileSize() < photoSizes.get(i).getFileSize()) {
                    maxPS = photoSizes.get(i);
                    maxId = i;
                }
                s.add(file);
            }
            List<Document> documentList = documentService.saveAll(s);
            event.setDocument(documentList.get(maxId));
            eventService.saveEvent(event);
        }
    }

    @Override
    public SendInvoice sendInvoice(Update update) {
        SendInvoice sendInvoice = new SendInvoice();
        CallbackQuery callbackQuery = update.getCallbackQuery();
        String[] parts = callbackQuery.getData().split("-");
        int subscriptionId = Integer.parseInt(parts[1].replaceAll("\\D", ""));
        long userId = Long.parseLong(parts[2].replaceAll("\\D", ""));
        Optional<Subscription> subscription;
        String code;
        switch (subscriptionId){
            case 0:
                subscription = subscriptionRepository.findByCode(Subscription.Code.WEEK_5.name());
                code = "WEEK.TOP5";
                break;
            case 1:
                subscription = subscriptionRepository.findByCode(Subscription.Code.TWO_WEEK_5.name());
                code = "2WEEK.TOP5";
                break;
            case 2:
                subscription = subscriptionRepository.findByCode(Subscription.Code.MONTH_5.name());
                code = "MONTH.TOP5";
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + subscriptionId);
        }
        Optional<User> user = userService.findUserById(userId);
        if(subscription.isPresent() && user.isPresent()){
            User user1 = user.get();
            sendInvoice.setChatId(callbackQuery.getMessage().getChatId());
            sendInvoice.setTitle(getTextByLanguage(user1.getLang(), "SUBSCRIPTION.PAYMENT"));
            sendInvoice.setDescription(getTextByLanguage(user1.getLang(), code));
            sendInvoice.setPayload(callbackQuery.getData());
            sendInvoice.setProviderToken(providedToken);
            sendInvoice.setCurrency("KZT");
            LabeledPrice labeledPrice = new LabeledPrice("price", subscription.get().getPrice() * 100);
            sendInvoice.setPrices(Collections.singletonList(labeledPrice));
            return sendInvoice;
        }
        else throw new IllegalStateException("Unexpected value: " + subscriptionId);
    }

    @Override
    public AnswerPreCheckoutQuery handlePayment(Update update) {
        return new AnswerPreCheckoutQuery(update.getPreCheckoutQuery().getId(), true);
    }

    @Override
    @Transactional
    public SendMessage handleSuccessfulPayment(Update update) {
        SuccessfulPayment successfulPayment = update.getMessage().getSuccessfulPayment();
        String invoicePayload = successfulPayment.getInvoicePayload();
        String[] parts = invoicePayload.split("-");
        int subscriptionId = Integer.parseInt(parts[1].replaceAll("\\D", ""));
        int userId = Integer.parseInt(parts[2].replaceAll("\\D", ""));
        long eventId = Long.parseLong(parts[3].replaceAll("\\D", ""));
        Optional<Event> event = eventRepository.findById(eventId);
        String code;
        switch (subscriptionId){
            case 0:
                code = Subscription.Code.WEEK_5.name();
                break;
            case 1:
                code = Subscription.Code.TWO_WEEK_5.name();
                break;
            case 2:
                code = Subscription.Code.MONTH_5.name();
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + subscriptionId);

        }
        Optional<Subscription> subscription = subscriptionRepository.findByCode(code);
        User user = userService.findUserById((long) userId).orElseThrow();
        if(event.isPresent() && subscription.isPresent()){
            Event event1 = event.get();
            SubscriptionEventLink subscriptionEventLink = SubscriptionEventLink.builder()
                    .event(event1)
                    .subscription(subscription.get())
                    .promoteUntil(Date.valueOf(LocalDate.now().plusDays(subscription.get().getDaysCount())))
                    .build();
            event1.setSubscriptionEventLink(subscriptionEventLinkRepository.save(subscriptionEventLink));
            eventService.saveEvent(event1);
        }
        return createMessage(update.getMessage(), getTextByLanguage(user.getLang(), "CREATED"),false);
    }

    public SendMessage deleteRemarkCommandReceived(long userId, long eventId, Update update) {
        Message message = (update.getMessage() == null) ? update.getCallbackQuery().getMessage() : update.getMessage();
        Optional<User> userOptional = userService.findUserById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            Optional<Event> eventOptional = eventRepository.findById(eventId);
            if (eventOptional.isPresent()) {
                userService.deleteRemark(userId, eventId);
                return createMessage(message, getTextByLanguage(user.getLang(), "EVENT.DELETED"), false);
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    @Transactional
    public SendMessage remarkCommandReceived(long userId, long eventId, Update update) {
        Message message = (update.getMessage() == null) ? update.getCallbackQuery().getMessage() : update.getMessage();
        Optional<User> userOptional = userService.findUserById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            Optional<Event> eventOptional = eventRepository.findById(eventId);
            if (eventOptional.isPresent()) {
                if (!userEventLinkRepository.existsByUser_IdAndEvent_Id(userId, eventId)) {
                    UserEventLink userEventLink = UserEventLink.builder()
                            .user(user)
                            .event(eventOptional.get())
                            .build();
                    userEventLinkRepository.save(userEventLink);
                    return createMessage(message, getTextByLanguage(user.getLang(), "CREATED"), false);
                } else return null;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    @Transactional
    public SendMessage deleteCommandReceived(long userId, long eventId, Update update) {
        Message message = (update.getMessage() == null) ? update.getCallbackQuery().getMessage() : update.getMessage();
        Optional<User> userOptional = userService.findUserById(userId);
        if (userOptional.isPresent()) {
            eventRepository.deleteById(eventId);
            return createMessage(message, getTextByLanguage(userOptional.get().getLang(), "EVENT.DELETED"), false);
        } else {
            return null;
        }
    }

    private SendMessage editCommandReceived(long userId, long eventId, Update update) {
        Optional<User> optionalUser = userService.findUserById(userId);
        Message message = (update.getMessage() != null) ? update.getMessage() : update.getCallbackQuery().getMessage();
        Optional<Event> eventOptional = eventRepository.findById(eventId);
        List<List<InlineKeyboardButton>> inlineButtons = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();
        if (optionalUser.isPresent() && eventOptional.isPresent()) {
            User user = optionalUser.get();
            user.setCurrent_state(stateRepository.findByCode(State.StateCode.EVENT_UPDATE.name()));
            userService.saveUser(user);
            String lang = user.getLang();
            String answer;
            List<String> actions = getEventVars(lang);
            actions.remove(actions.size() - 2);
            for (int i = 0; i < actions.size(); i++) {
                InlineKeyboardButton button = new InlineKeyboardButton(actions.get(i));
                button.setCallbackData("edit-" + i + "u-" + userId + "e-" + eventId);
                row.add(button);
                inlineButtons.add(row);
                row = new ArrayList<>();

            }
            InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
            keyboardMarkup.setKeyboard(inlineButtons);
            answer = getTextByLanguage(lang, "CHOOSE.WHAT.TO.EDIT");
            SendMessage messageReturn = createMessage(message, answer, false);
            messageReturn.setReplyMarkup(keyboardMarkup);
            return messageReturn;
        } else {
            return null;
        }
    }

    public Validable chooseEditCommand(Update update) {
        CallbackQuery callbackQuery = update.getCallbackQuery();
        String callbackData = callbackQuery.getData();
        Pattern pattern = Pattern.compile("^edit-(\\d+)u-(\\d+)e-(\\d+)$");
        Matcher matcher = pattern.matcher(callbackData);
        Event event;
        String answer = null;
        if (matcher.find()) {
            int i = Integer.parseInt(matcher.group(1));
            int userId = Integer.parseInt(matcher.group(2));
            int eventId = Integer.parseInt(matcher.group(3));
            Optional<Event> eventOptional = eventRepository.findById((long) eventId);
            Optional<User> optionalUser = userService.findUserById((long) userId);
            if (eventOptional.isPresent() && optionalUser.isPresent()) {
                User user = optionalUser.get();
                event = eventOptional.get();
                if (event.getCreatedUser().getId().equals((long) userId)) {
                    user.setActualEvent(event);
                    switch (i) {
                        case 0:
                            user.setCurrent_state(stateRepository.findByCode(State.StateCode.EVENT_UPDATE_NAME_SELECT.name()));
                            answer = getTextByLanguage(user.getLang(), "EVENT.NAME");
                            break;
                        case 1:
                            user.setCurrent_state(stateRepository.findByCode(State.StateCode.EVENT_UPDATE_DESCRIPTION_SELECT.name()));
                            answer = getTextByLanguage(user.getLang(), "EVENT.DESCRIPTION");
                            break;
                        case 2:
                            user.setCurrent_state(stateRepository.findByCode(State.StateCode.EVENT_UPDATE_REQUIREMENTS_SELECT.name()));
                            answer = getTextByLanguage(user.getLang(), "EVENT.REQUIREMENTS");
                            break;
                        case 3:
                            user.setCurrent_state(stateRepository.findByCode(State.StateCode.EVENT_UPDATE_CITY_SELECT.name()));
                            userService.saveUser(user);
                            return cityChoose(callbackQuery.getMessage(), user.getLang());
                        case 4:
                            user.setCurrent_state(stateRepository.findByCode(State.StateCode.EVENT_UPDATE_LOCATION_SELECT.name()));
                            answer = getTextByLanguage(user.getLang(), "CHOOSE.LOCATION");
                            break;
                        case 5:
                            user.setCurrent_state(stateRepository.findByCode(State.StateCode.EVENT_DATE_SELECT.name()));
                            userService.saveUser(user);
                            return sendDateSelectionKeyboard(callbackQuery.getMessage().getChatId(), user.getLang());
                        case 6:
                            user.setCurrent_state(stateRepository.findByCode(State.StateCode.EVENT_TIME_SELECT.name()));
                            userService.saveUser(user);
                            return sendTimeSelectionKeyboard(callbackQuery.getMessage().getChatId(), user.getLang());
                        case 7:
                            user.setCurrent_state(stateRepository.findByCode(State.StateCode.EVENT_PHOTO_SELECT.name()));
                            answer = getTextByLanguage(user.getLang(), "UPLOAD.PHOTO");
                            break;
                        default:
                            user.setCurrent_state(stateRepository.findByCode(State.StateCode.EVENT_UPDATE.name()));
                            break;
                    }
                    userService.saveUser(user);
                }
            }

        }
        if (answer != null) {
            return createMessage(callbackQuery.getMessage(), answer, false);
        } else {
            return sendChoosingActionButtons(callbackQuery);
        }
    }


    @Override
    public SendMessage makeMainAction(Update update) {
        CallbackQuery callbackQuery = update.getCallbackQuery();
        String callbackLang = callbackQuery.getData().substring(7);
        if (callbackLang.startsWith("1")) {
            return createCommandReceived(callbackQuery);
        }
        if (callbackLang.startsWith("2")) {
            return searchCommandReceived(callbackQuery);
        }
        if (callbackLang.startsWith("3")) {
            return listCommandReceived(callbackQuery);
        } else return sendChoosingActionButtons(callbackQuery);
    }

    @Override
    public SendMessage listCommandReceived(CallbackQuery callbackQuery) {
        org.telegram.telegrambots.meta.api.objects.User userTg = callbackQuery.getFrom();
        User user = userService.getOrCreateUser(userTg);
        Map<String, List<Event>> events = eventService.getUserEvents(user);
        List<List<InlineKeyboardButton>> inlineButtons = new ArrayList<>();
        if (events == null) {
            return null;
        }
        List<String> buttonsTexts = new ArrayList<>();
        buttonsTexts.add(getTextByLanguage(user.getLang(), "MINE.EVENTS"));
        buttonsTexts.add(getTextByLanguage(user.getLang(), "ENROLLED.EVENTS"));
        buttonsTexts.add(getTextByLanguage(user.getLang(), "CHOOSE.EVENT"));

        for (List<Event> eventList : events.values()) {
            extractedMethod(eventList, inlineButtons);
        }
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        keyboardMarkup.setKeyboard(inlineButtons);

        SendMessage message = createMessage(callbackQuery.getMessage(), buttonsTexts.get(2), false);
        message.setReplyMarkup(keyboardMarkup);
        return message;
    }

    private String getTextByLanguage(String lang, String code) {
        switch (lang) {
            case "kz":
                return messageSource.getMessage(code, null, new Locale("kz", "KZ"));
            case "en":
                return messageSource.getMessage(code, null, Locale.ENGLISH);
            case "global":
                return messageSource.getMessage(code, null, Locale.getDefault());
            default:
                return messageSource.getMessage(code, null, new Locale("ru", "RU"));
        }
    }

    private List<String> getEventVars(String lang) {
        List<String> eventVars = new ArrayList<>();
        eventVars.add(0, getTextByLanguage(lang, "EVENT.NAME"));
        eventVars.add(1, getTextByLanguage(lang, "EVENT.DESCRIPTION"));
        eventVars.add(2, getTextByLanguage(lang, "EVENT.REQUIREMENTS"));
        eventVars.add(3, getTextByLanguage(lang, "EVENT.CITY"));
        eventVars.add(4, getTextByLanguage(lang, "EVENT.PLACE"));
        eventVars.add(5, getTextByLanguage(lang, "EVENT.DATE"));
        eventVars.add(6, getTextByLanguage(lang, "EVENT.TIME"));
        eventVars.add(7, getTextByLanguage(lang, "EVENT.CREATED.BY"));
        eventVars.add(8, getTextByLanguage(lang, "EVENT.PHOTO"));
        return eventVars;
    }

    private String createEventCardsMessage(String lang, List<Event> eventCards) {
        StringBuilder messageText = new StringBuilder();
        List<String> buttonsTexts = getEventVars(lang);
        buttonsTexts.remove(0);
        for (Event eventCard : eventCards) {
            boolean isNeedToTranslate = (!eventCard.getDetectedLanguage().equals(lang));
            String name = (!isNeedToTranslate) ? eventCard.getName() + getTextByLanguage("global", "URA") : translatorService.translateText(lang, eventCard.getName());
            messageText.append("*").append(name).append("*").append("\n");
            messageText.append("*").append(buttonsTexts.get(0)).append(":* ").append(eventCard.getDescription()).append("\n");
            String requirement = eventCard.getRequirement();
            if (requirement != null) {
                messageText.append("*").append(buttonsTexts.get(1)).append(":* ").append(eventCard.getRequirement()).append("\n");
            }
            messageText.append("\n----------------------\n\n");
            City city = eventCard.getCity();
            if (city != null) {
                messageText.append("*").append(buttonsTexts.get(2)).append(":* ").append(eventCard.getCity().getLanguages().get(lang)).append("\n");
            }
            String place = eventCard.getPlace();
            if (place != null) {
                messageText.append("*").append(buttonsTexts.get(3)).append(":* ").append((!isNeedToTranslate) ? eventCard.getPlace() : translatorService.translateText(lang, eventCard.getPlace())).append("\n");
            }
            messageText.append("\n----------------------\n\n");
            messageText.append("*").append(buttonsTexts.get(4)).append(":* ").append(eventCard.getDate()).append("\n");
            messageText.append("*").append(buttonsTexts.get(5)).append(":* ").append(eventCard.getTime()).append("\n");
            messageText.append("\n----------------------\n\n");
            User createdUser = eventCard.getCreatedUser();
            if (createdUser != null) {
                messageText.append("*").append(buttonsTexts.get(6)).append(":* ").append("@").append(eventCard.getCreatedUser().getTelegramUsername()).append("\n");
            }
        }
        return messageText.toString();
    }

    @Override
    public List<Validable> getEvent(Update update) {
        List<Validable> sendMessageList = new ArrayList<>();
        CallbackQuery callbackQuery = update.getCallbackQuery();
        String callbackData = callbackQuery.getData();
        String id = callbackData.substring(6);
        Optional<Event> eventOptional = eventRepository.findById(Long.valueOf(id));
        String answer;
        if (eventOptional.isPresent()) {
            User user = userService.getOrCreateUser(callbackQuery.getFrom());
            user.setActualEvent(eventOptional.get());
            userService.saveUser(user);
            answer = createEventCardsMessage(user.getLang(), Collections.singletonList(eventOptional.get()));
            sendMessageList.add(createMessage(callbackQuery.getMessage(), answer, false));
            if(eventOptional.get().getDocument() != null){
                sendMessageList.add(new SendPhoto(String.valueOf(callbackQuery.getMessage().getChatId()), new InputFile(eventOptional.get().getDocument().getTgId())));
            }
            sendMessageList.add(sendEventActionButtons(callbackQuery, user, eventOptional.get()));
        }
        return sendMessageList;
    }

    @Override
    public SendMessage createCommandReceived(CallbackQuery callbackQuery) {
        User user = userService.getOrCreateUser(callbackQuery.getFrom());
        user.setCurrent_state(stateRepository.findByCode(State.StateCode.EVENT_CREATED.name()));
        userService.saveUser(user);
        eventService.createEvent(user, Long.valueOf(callbackQuery.getMessage().getMessageId()));
        return createMessage(callbackQuery.getMessage(), getTextByLanguage(user.getLang(), "EVENT.NAME"), false);
    }

    @Override
    public SendMessage searchCommandReceived(CallbackQuery callbackQuery) {
        User user = userService.getOrCreateUser(callbackQuery.getFrom());
        return cityChoose(callbackQuery.getMessage(), user.getLang());
    }

    private SendMessage cityChoose(Message message, String lang) {
        List<City> cities = cityRepository.findAll(Sort.by("id"));
        List<List<InlineKeyboardButton>> inlineButtons = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();
        int buttonsPerRow = 3;
        for (int i = 0; i < cities.size() + 1; i++) {
            @NotNull long temp = (cities.size() <= i) ? 0 : cities.get(i).getId();
            String cityName = (cities.size() <= i) ? getTextByLanguage(lang, "ALL") : cities.get(i).getLanguages().get(lang);
            String callbackData = "city-" + temp;
            InlineKeyboardButton button = new InlineKeyboardButton(cityName);
            button.setCallbackData(callbackData);
            row.add(button);
            if (row.size() == buttonsPerRow) {
                inlineButtons.add(row);
                row = new ArrayList<>();
            }
        }
        if (!row.isEmpty()) {
            inlineButtons.add(row);
        }
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        keyboardMarkup.setKeyboard(inlineButtons);
        SendMessage sendMessage = createMessage(message, getTextByLanguage(lang, "CHOOSE.CITY"), false);
        sendMessage.setReplyMarkup(keyboardMarkup);
        return sendMessage;
    }

    public List<Validable> chooseCity(Update update) {
        CallbackQuery callbackQuery = update.getCallbackQuery();
        String callbackCityId = callbackQuery.getData().substring(5);
        User user = userService.getOrCreateUser(callbackQuery.getFrom());
        State.StateCode state = State.StateCode.valueOf(user.getCurrent_state().getCode());
        Event event = user.getActualEvent();
        if (event != null) {
            City city = cityRepository.findById(Long.valueOf(callbackCityId)).orElse(null);
            event.setCity(city);
            String answer;
            switch (state) {
                case EVENT_CREATED_CATEGORY_SELECTED:
                    user.setCurrent_state(stateRepository.findByCode(State.StateCode.EVENT_CREATED_CITY_SELECTED.name()));
                    userService.saveUser(user);
                    eventService.saveEvent(event);
                    answer = getTextByLanguage(user.getLang(), "CHOOSE.LOCATION");
                    break;
                case EVENT_UPDATE_CITY_SELECT :
                case EVENT_UPDATE:
                    user.setCurrent_state(stateRepository.findByCode(State.StateCode.DEFAULT.name()));
                    userService.saveUser(user);
                    eventService.saveEvent(event);
                    callbackQuery.setData("event-" + event.getId());
                    update.setCallbackQuery(callbackQuery);
                    return getEvent(update);
                default:
                    return Collections.singletonList(chooseCategory(callbackQuery.getMessage(), user.getLang(), callbackCityId));
            }
            return Collections.singletonList(createMessage(callbackQuery.getMessage(), answer, false));
        } else {
            return Collections.singletonList(chooseCategory(callbackQuery.getMessage(), user.getLang(), callbackCityId));
        }
    }

    private SendMessage chooseCategory(Message message, String lang, String callbackCityId) {
        List<Category> categories = categoryRepository.findAll(Sort.by("id"));
        List<List<InlineKeyboardButton>> inlineButtons = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();
        int buttonsPerRow = 3;
        for (int i = 0; i < categories.size() + 1; i++) {
            String categoryName = (categories.size() <= i) ? getTextByLanguage(lang, "ALL") : categories.get(i).getLanguages().get(lang);
            int temp = (categories.size() <= i) ? 0 : i + 1;
            String callbackData = "category-" + temp + "city-" + callbackCityId;
            InlineKeyboardButton button = new InlineKeyboardButton(categoryName);
            button.setCallbackData(callbackData);
            row.add(button);
            if (row.size() == buttonsPerRow) {
                inlineButtons.add(row);
                row = new ArrayList<>();
            }
        }
        if (!row.isEmpty()) {
            inlineButtons.add(row);
        }
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        keyboardMarkup.setKeyboard(inlineButtons);
        SendMessage sendMessage = createMessage(message, getTextByLanguage(lang, "CHOOSE.CATEGORY"), false);
        sendMessage.setReplyMarkup(keyboardMarkup);
        return sendMessage;
    }

    @Transactional
    public SendMessage chooseCategoryForSearch(Update update) {
        CallbackQuery callbackQuery = update.getCallbackQuery();
        String[] parts = callbackQuery.getData().split("-");
        long categoryId = Long.parseLong(parts[1].replaceAll("\\D", ""));
        long cityId = Long.parseLong(parts[2].replaceAll("\\D", ""));
        List<Event> eventList = eventRepository.findAll();
        List<Event> events = eventList.stream().filter(event -> event.getDetectedLanguage() != null && ((event.getDate().toLocalDate().isAfter(LocalDate.now())) || (event.getDate().toLocalDate().isEqual(LocalDate.now()))))
                .collect(Collectors.toList());
        eventList.removeAll(events);
        eventService.deleteAll(eventList);
        events.stream()
                .filter(event -> event.getSubscriptionEventLink() != null)
                .forEach(event -> {
                    SubscriptionEventLink subscriptionEventLink = event.getSubscriptionEventLink();
                    if(subscriptionEventLink.getPromoteUntil().toLocalDate().isBefore(LocalDate.now())){
                        subscriptionEventLinkRepository.delete(subscriptionEventLink);
                    }
                });
        if (events.isEmpty()) {
            return sendChoosingActionButtons(callbackQuery);
        }
        List<Event> sortedEvents = events.stream()
                .filter(event -> ((cityId == 0) && (categoryId == 0)) ||
                        ((cityId == 0) && (event.getCategory() != null && event.getCategory().getId().equals(categoryId))) ||
                        ((categoryId == 0) && (event.getCity() != null && event.getCity().getId().equals(cityId))) ||
                        ((event.getCity() != null && event.getCity().getId().equals(cityId)) && (event.getCategory() != null && event.getCategory().getId().equals(categoryId))))
                .sorted(Comparator.comparing(Event::getDate).thenComparing(Event::getTime))
                .sorted(Comparator.comparing(event -> event.getSubscriptionEventLink() == null ? 1 : 0))
                .collect(Collectors.toList());
        InlineKeyboardMarkup keyboardMarkup = getInlineKeyboardMarkup(sortedEvents);
        SendMessage message = createMessage(callbackQuery.getMessage(), "------------", false);
        message.setReplyMarkup(keyboardMarkup);
        return message;
    }

    @org.jetbrains.annotations.NotNull
    private static InlineKeyboardMarkup getInlineKeyboardMarkup(List<Event> sortedEvents) {
        List<List<InlineKeyboardButton>> inlineButtons = new ArrayList<>();
        extractedMethod(sortedEvents, inlineButtons);
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        keyboardMarkup.setKeyboard(inlineButtons);
        return keyboardMarkup;
    }

    private static void extractedMethod(List<Event> sortedEvents, List<List<InlineKeyboardButton>> inlineButtons) {
        for (Event eventCard : sortedEvents) {
            String buttonText = eventCard.getDate() + " - " + eventCard.getName();
            InlineKeyboardButton button = new InlineKeyboardButton(buttonText);
            button.setCallbackData("event-" + eventCard.getId());
            List<InlineKeyboardButton> inlineKeyboardButtons = new ArrayList<>();
            inlineKeyboardButtons.add(button);
            inlineButtons.add(inlineKeyboardButtons);
        }
    }

    private List<Validable> handleMainCommands(CallbackQuery callbackQuery){
        String[] actionsArray = new String[]{"EVENT.CREATE", "EVENT.SEARCH", "EVENT.MINE"};
        List<String> actions = new ArrayList<>();
        for (String action : actionsArray) {
            String actionText = getTextByLanguage("ru", action);
            actions.add(actionText);
        }
        String messageText = callbackQuery.getMessage().getText();
        for (int i = 0; i < actions.size(); i++) {
            if (messageText != null) {
                if (messageText.equals(actions.get(0))) {
                    return Collections.singletonList(createCommandReceived(callbackQuery));
                }
                if (messageText.equals(actions.get(1))) {
                    return Collections.singletonList(searchCommandReceived(callbackQuery));
                }
                if (messageText.equals(actions.get(2))) {
                    return Collections.singletonList(listCommandReceived(callbackQuery));
                }
            }
        }
        return null;
    }

    @Override
    public List<Validable> handleDefaultMessages(Update update) {
        List<Validable> sendMessageList = new ArrayList<>();
        CallbackQuery callbackQuery = update.getCallbackQuery();
        Message message = update.getMessage();
        org.telegram.telegrambots.meta.api.objects.User userTemp;
        if (callbackQuery != null && message == null) {
            message = callbackQuery.getMessage();
            userTemp = callbackQuery.getFrom();
        } else {
            userTemp = message.getFrom();
            callbackQuery = new CallbackQuery();
            callbackQuery.setMessage(message);
            callbackQuery.setFrom(message.getFrom());
        }
        String messageText = message.getText();
        if(messageText != null){
            sendMessageList = handleMainCommands(callbackQuery);
            if(sendMessageList != null && !sendMessageList.isEmpty()){
                return sendMessageList;
            }
            else {
                sendMessageList = new ArrayList<>();
            }
        }
        User user = userService.getOrCreateUser(userTemp);
        State.StateCode state = State.StateCode.valueOf(user.getCurrent_state().getCode());
        Event event = user.getActualEvent();
        if (event == null) {
            return Collections.singletonList(sendChoosingActionButtons(callbackQuery));
        }
        String answer = " some ";
        switch (state) {
            case EVENT_CREATED:
                event.setName(messageText);
                user.setCurrent_state(stateRepository.findByCode(State.StateCode.EVENT_CREATED_NAME_SELECTED.name()));
                answer = getTextByLanguage(user.getLang(), "EVENT.DESCRIPTION");
                break;
            case EVENT_CREATED_NAME_SELECTED:
                event.setDescription(messageText);
                user.setCurrent_state(stateRepository.findByCode(State.StateCode.EVENT_CREATED_DESCRIPTION_SELECTED.name()));
                answer = getTextByLanguage(user.getLang(), "EVENT.REQUIREMENTS");
                break;
            case EVENT_CREATED_DESCRIPTION_SELECTED:
                event.setRequirement(messageText);
                user.setCurrent_state(stateRepository.findByCode(State.StateCode.EVENT_CREATED_REQUIREMENTS_SELECTED.name()));
                userService.saveUser(user);
                eventService.saveEvent(event);
                return Collections.singletonList(chooseCategory(message, user.getLang(), null));
            case EVENT_CREATED_REQUIREMENTS_SELECTED:
                long categoryId;
                try {
                    categoryId = Long.parseLong(callbackQuery.getData().split("-")[1].replaceAll("\\D", ""));
                    event.setCategory(categoryRepository.findById(categoryId).orElse(null));
                }
                catch (Exception ignored){
                }
                finally {
                    user.setCurrent_state(stateRepository.findByCode(State.StateCode.EVENT_CREATED_CATEGORY_SELECTED.name()));
                    userService.saveUser(user);
                    eventService.saveEvent(event);
                }
                return Collections.singletonList(cityChoose(message, user.getLang()));
            case EVENT_CREATED_CATEGORY_SELECTED:
            case EVENT_UPDATE_CITY_SELECT:
                return Collections.singletonList(cityChoose(message, user.getLang()));
            case EVENT_CREATED_CITY_SELECTED:
                if (message.hasLocation()) {
                    Location location = message.getLocation();
                    event.setPlace(geocoderService.getPlace(location.getLongitude(), location.getLatitude()));
                } else event.setPlace(messageText);
                user.setCurrent_state(stateRepository.findByCode(State.StateCode.EVENT_DATE_SELECT.name()));
                userService.saveUser(user);
                eventService.saveEvent(event);
                return Collections.singletonList(sendDateSelectionKeyboard(message.getChatId(), user.getLang()));
            case EVENT_DATE_SELECT:
                java.sql.Date date;
                if (messageText == null) {
                    date = java.sql.Date.valueOf(LocalDate.now());
                } else {
                    try {
                        date = java.sql.Date.valueOf(messageText);
                    } catch (java.lang.IllegalArgumentException e) {
                        date = java.sql.Date.valueOf(LocalDate.now());
                    }
                }
                event.setDate(date);
                user.setCurrent_state(stateRepository.findByCode(State.StateCode.EVENT_TIME_SELECT.name()));
                userService.saveUser(user);
                eventService.saveEvent(event);
                return Collections.singletonList(sendTimeSelectionKeyboard(message.getChatId(), user.getLang()));
            case EVENT_TIME_SELECT:
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm a");
                LocalTime time;
                if (messageText == null) {
                    time = LocalTime.parse(LocalTime.now().toString(), formatter);
                } else {
                    try {
                        time = LocalTime.parse(messageText, formatter);
                    } catch (Exception e) {
                        time = LocalTime.now();
                    }
                }
                event.setTime(java.sql.Time.valueOf(time));
                user.setCurrent_state(stateRepository.findByCode(State.StateCode.EVENT_PHOTO_SELECT.name()));
                event.setDetectedLanguage(translatorService.detectTextsLang(event.getName() + " " + event.getDescription() + " " + event.getRequirement()));
                answer = getTextByLanguage(user.getLang(), "UPLOAD.PHOTO");
                break;
            case EVENT_PHOTO_SELECT:
                if (message.hasPhoto()) {
                    handlePhoto(message);
                }
                callbackQuery.setData("event-" + event.getId());
                update.setCallbackQuery(callbackQuery);
                user.setCurrent_state(stateRepository.findByCode(State.StateCode.DEFAULT.name()));
                userService.saveUser(user);
                return getEvent(update);
            case DEFAULT:
                return Collections.singletonList(sendChoosingActionButtons(callbackQuery));
            case EVENT_UPDATE:
                sendMessageList.add(chooseEditCommand(update));
                return sendMessageList;
            case EVENT_UPDATE_CATEGORY_SELECT:
                Long catId = Long.parseLong(callbackQuery.getData().split("-")[1].replaceAll("\\D", ""));
                event.setCategory(categoryRepository.findById(catId).orElse(null));
                user.setCurrent_state(stateRepository.findByCode(State.StateCode.DEFAULT.name()));
                userService.saveUser(user);
                eventService.saveEvent(event);
                answer = createEventCardsMessage(user.getLang(), Collections.singletonList(event));
                break;
            case EVENT_UPDATE_LOCATION_SELECT:
                if (message.hasLocation()) {
                    Location location = message.getLocation();
                    event.setPlace(geocoderService.getPlace(location.getLongitude(), location.getLatitude()));
                } else event.setPlace(messageText);
                user.setCurrent_state(stateRepository.findByCode(State.StateCode.DEFAULT.name()));
                userService.saveUser(user);
                eventService.saveEvent(event);
                callbackQuery.setData("event-" + event.getId());
                update.setCallbackQuery(callbackQuery);
                return getEvent(update);
            case EVENT_UPDATE_NAME_SELECT:
                event.setName(messageText);
                user.setCurrent_state(stateRepository.findByCode(State.StateCode.DEFAULT.name()));
                userService.saveUser(user);
                eventService.saveEvent(event);
                callbackQuery.setData("event-" + event.getId());
                update.setCallbackQuery(callbackQuery);
                return getEvent(update);
            case EVENT_UPDATE_DESCRIPTION_SELECT:
                event.setDescription(messageText);
                user.setCurrent_state(stateRepository.findByCode(State.StateCode.DEFAULT.name()));
                userService.saveUser(user);
                eventService.saveEvent(event);
                callbackQuery.setData("event-" + event.getId());
                update.setCallbackQuery(callbackQuery);
                return getEvent(update);
            case EVENT_UPDATE_REQUIREMENTS_SELECT:
                event.setRequirement(messageText);
                user.setCurrent_state(stateRepository.findByCode(State.StateCode.DEFAULT.name()));
                userService.saveUser(user);
                eventService.saveEvent(event);
                callbackQuery.setData("event-" + event.getId());
                update.setCallbackQuery(callbackQuery);
                return getEvent(update);
            default:
                sendChoosingActionButtons(callbackQuery);
                break;
        }
        userService.saveUser(user);
        eventService.saveEvent(event);
        sendMessageList.add(createMessage(message, answer, false));
        return sendMessageList;
    }

    private SendMessage sendDateSelectionKeyboard(Long chatId, String lang) {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboardRows = new ArrayList<>();
        LocalDate currentDate = LocalDate.now();
        for (int i = 0; i < 7; i++) {
            LocalDate date = currentDate.plusDays(i);
            String buttonText = date.toString();
            KeyboardRow row = new KeyboardRow();
            row.add(new KeyboardButton(buttonText));
            keyboardRows.add(row);
        }
        keyboardMarkup.setKeyboard(keyboardRows);
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(getTextByLanguage(lang, "CHOOSE.DATE"));
        message.setReplyMarkup(keyboardMarkup);
        return message;
    }


    private SendMessage sendTimeSelectionKeyboard(Long chatId, String lang) {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboardRows = new ArrayList<>();
        for (int hour = 1; hour <= 12; hour++) {
            KeyboardRow row = new KeyboardRow();
            for (int minute = 0; minute < 60; minute += 15) {
                String timeText = String.format("%02d:%02d AM", hour, minute);
                row.add(new KeyboardButton(timeText));
                timeText = String.format("%02d:%02d PM", hour, minute);
                row.add(new KeyboardButton(timeText));
            }
            keyboardRows.add(row);
        }
        keyboardMarkup.setKeyboard(keyboardRows);
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(getTextByLanguage(lang, "CHOOSE.TIME"));
        message.setReplyMarkup(keyboardMarkup);
        return message;
    }

    private SendMessage createMessage(Message message, String answer, Boolean isReplyMessage) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(message.getChatId().toString());
        if (isReplyMessage) {
            sendMessage.setReplyToMessageId(message.getMessageId());
        }
        sendMessage.setText(answer);
        return sendMessage;
    }
}
