package com.example.partypal.services;

import com.example.partypal.daos.repositories.CategoryRepository;
import com.example.partypal.daos.repositories.CityRepository;
import com.example.partypal.daos.repositories.EventRepository;
import com.example.partypal.daos.repositories.StateRepository;
import com.example.partypal.models.entities.Event;
import com.example.partypal.models.entities.base.Category;
import com.example.partypal.models.entities.base.City;
import com.example.partypal.models.entities.telegram.State;
import com.example.partypal.models.entities.users.User;
import com.example.partypal.models.enums.Lang;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Location;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
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

    @Override
    public SendMessage startCommandReceived(Message message) {
        return chooseLanguageCommandReceived(message);
    }

    @Override
    public SendMessage chooseLanguageCommandReceived(Message message) {
        List<List<InlineKeyboardButton>> inlineButtons = new ArrayList<>();
        List<InlineKeyboardButton> inlineKeyboardButtons = new ArrayList<>();
        List<String> langList = new ArrayList<>();
        langList.add(getTextByLanguage("kk", "lang.KZ"));
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
            user.setLang("kk");
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
            case "kk":
                answer = getTextByLanguage("kk", "GREETING");
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
            case "kk":
                mainActionsList.add("1. " + getTextByLanguage("kk", "EVENT.CREATE"));
                mainActionsList.add("2. " + getTextByLanguage("kk", "EVENT.SEARCH"));
                mainActionsList.add("3. " + getTextByLanguage("kk", "EVENT.MINE"));
                answer = getTextByLanguage("kk", "EVENT.CHOOSE.MAIN.ACTION");
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
    public SendMessage makeMainAction(Update update) {
        CallbackQuery callbackQuery = update.getCallbackQuery();
        String callbackLang = callbackQuery.getData().substring(7);
        if (callbackLang.startsWith("1")) {
            return (SendMessage) createCommandReceived(callbackQuery).get(0);
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

        List<String> buttonsTexts = new ArrayList<>();
        buttonsTexts.add(getTextByLanguage(user.getLang(), "MINE.EVENTS"));
        buttonsTexts.add(getTextByLanguage(user.getLang(), "ENROLLED.EVENTS"));
        buttonsTexts.add(getTextByLanguage(user.getLang(), "CHOOSE.EVENT"));

        for (Event eventCard : events.get("created")) {
            String buttonText = eventCard.getId() + ". " + eventCard.getName();
            InlineKeyboardButton button = new InlineKeyboardButton(buttonText);
            button.setCallbackData("event-" + eventCard.getId());
            List<InlineKeyboardButton> inlineKeyboardButtonsRow = new ArrayList<>();
            inlineKeyboardButtonsRow.add(button);
            inlineButtons.add(inlineKeyboardButtonsRow);
        }

        for (Event eventCard : events.get("enrolled")) {
            User createdUser = eventCard.getCreatedUser();
            String translatedEventName = ((createdUser != null && createdUser.getLang().equals(user.getLang())) ? eventCard.getName() : translatorService.translateText(Lang.valueOf(user.getLang()), eventCard.getName()));
            String buttonText = eventCard.getId() + ". " + translatedEventName;
            InlineKeyboardButton button = new InlineKeyboardButton(buttonText);
            button.setCallbackData("event-" + eventCard.getId());
            List<InlineKeyboardButton> inlineKeyboardButtonsRow = new ArrayList<>();
            inlineKeyboardButtonsRow.add(button);
            inlineButtons.add(inlineKeyboardButtonsRow);
        }

        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        keyboardMarkup.setKeyboard(inlineButtons);

        SendMessage message = createMessage(callbackQuery.getMessage(), buttonsTexts.get(2), false);
        message.setReplyMarkup(keyboardMarkup);
        return message;
    }

    private String getTextByLanguage(String lang, String code) {
        switch (lang) {
            case "kk":
                return messageSource.getMessage(code, null, new Locale("kz", "KZ"));
            case "en":
                return messageSource.getMessage(code, null, Locale.ENGLISH);
            case "global":
                return messageSource.getMessage(code, null, Locale.getDefault());
            default:
                return messageSource.getMessage(code, null, new Locale("ru", "RU"));
        }
    }

    private String createEventCardsMessage(String lang, List<Event> eventCards) {
        StringBuilder messageText = new StringBuilder();
        List<String> buttonsTexts = new ArrayList<>();
        buttonsTexts.add(getTextByLanguage(lang, "EVENT.DESCRIPTION"));
        buttonsTexts.add(getTextByLanguage(lang, "EVENT.REQUIREMENTS"));
        buttonsTexts.add(getTextByLanguage(lang, "EVENT.CITY"));
        buttonsTexts.add(getTextByLanguage(lang, "EVENT.PLACE"));
        buttonsTexts.add(getTextByLanguage(lang, "EVENT.DATE"));
        buttonsTexts.add(getTextByLanguage(lang, "EVENT.TIME"));
        buttonsTexts.add(getTextByLanguage(lang, "EVENT.CREATED.BY"));
        for (Event eventCard : eventCards) {
            boolean isNeedToTranslate = ((eventCard.getCreatedUser() == null) || ((eventCard.getCreatedUser() != null) && !eventCard.getCreatedUser().getLang().equals(lang)));
            messageText.append("*").append((!isNeedToTranslate) ? eventCard.getName() : translatorService.translateText(Lang.valueOf(lang), eventCard.getName())).append("*").append("\n");
            messageText.append("*").append(buttonsTexts.get(0)).append(":* ").append(eventCard.getDescription()).append("\n");
            String requirement = eventCard.getRequirement();
            if (requirement != null) {
                messageText.append("*").append(buttonsTexts.get(1)).append(":* ").append(eventCard.getRequirement()).append("\n");
            }
            messageText.append("\n----------------------\n\n");
            City city = eventCard.getCity();
            if (city != null) {
                String tempLang = lang;
                if (lang.equals("kk")) {
                    tempLang = "kz";
                }
                messageText.append("*").append(buttonsTexts.get(2)).append(":* ").append(eventCard.getCity().getLanguages().get(tempLang)).append("\n");
            }
            String place = eventCard.getPlace();
            if (place != null) {
                messageText.append("*").append(buttonsTexts.get(3)).append(":* ").append((!isNeedToTranslate) ? eventCard.getPlace() : translatorService.translateText(Lang.valueOf(lang), eventCard.getPlace())).append("\n");
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
    public SendMessage getEvent(Update update) {
        CallbackQuery callbackQuery = update.getCallbackQuery();
        String callbackData = callbackQuery.getData();
        String id = callbackData.substring(6);
        Optional<Event> eventOptional = eventRepository.findById(Long.valueOf(id));
        String answer = "";
        if (eventOptional.isPresent()) {
            User user = userService.getOrCreateUser(callbackQuery.getFrom());
            answer = createEventCardsMessage(user.getLang(), Collections.singletonList(eventOptional.get()));
        }
        return createMessage(callbackQuery.getMessage(), answer, false);
    }

    @Override
    public List<Object> createCommandReceived(CallbackQuery callbackQuery) {
        User user = userService.getOrCreateUser(callbackQuery.getFrom());
        user.setCurrent_state(stateRepository.findByCode(State.StateCode.EVENT_CREATED.name()));
        userService.saveUser(user);
        SendMessage message = enterEventName(callbackQuery.getMessage(), user.getLang());
        List<Object> objs = new ArrayList<>();
        objs.add(message);
        objs.add(user);
        return objs;
    }

    private SendMessage enterEventName(Message message, String lang) {
        return createMessage(message, getTextByLanguage(lang, "EVENT.NAME"), false);
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
        String langTemp = lang.equals("kk") ? "kz" : lang;
        for (City city : cities) {
            String cityName = city.getLanguages().get(langTemp);
            String callbackData = "city-" + city.getId();
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

    public SendMessage chooseCity(Update update) {
        CallbackQuery callbackQuery = update.getCallbackQuery();
        String callbackCityId = callbackQuery.getData().substring(5);
        User user = userService.getOrCreateUser(callbackQuery.getFrom());
        if (user.getCurrent_state().getCode().equals(State.StateCode.EVENT_CREATED_CATEGORY_SELECTED.name())) {
            Optional<Event> eventOptional = eventService.findEventByMessageId((long) (callbackQuery.getMessage().getMessageId() - 1));
            if (eventOptional.isPresent()) {
                City city = cityRepository.findById(Long.valueOf(callbackCityId)).orElse(null);
                Event event = eventOptional.get();
                event.setCity(city);
                event.setTgId(Long.valueOf(callbackQuery.getMessage().getMessageId()));
                user.setCurrent_state(stateRepository.findByCode(State.StateCode.EVENT_CREATED_CITY_SELECTED.name()));
                userService.saveUser(user);
                eventService.saveEvent(eventOptional.get());
                return createMessage(callbackQuery.getMessage(), getTextByLanguage(user.getLang(), "CHOOSE.LOCATION"), false);
            } else {
                return handleDefaultMessages(update);
            }
        } else {
            return chooseCategory(callbackQuery.getMessage(), user.getLang(), callbackCityId);
        }
    }

    private SendMessage chooseCategory(Message message, String lang, String callbackCityId) {
        List<Category> categories = categoryRepository.findAll(Sort.by("id"));
        List<List<InlineKeyboardButton>> inlineButtons = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();
        int buttonsPerRow = 3;
        String langTemp = lang.equals("kk") ? "kz" : lang;
        for (int i = 0; i < categories.size() + 1; i++) {
            String categoryName = (categories.size() <= i) ? "    " : categories.get(i).getLanguages().get(langTemp);
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

    public SendMessage chooseCategory(Update update) {
        CallbackQuery callbackQuery = update.getCallbackQuery();
        String[] parts = callbackQuery.getData().split("-");
        long categoryId = Long.parseLong(parts[1].replaceAll("\\D", ""));
        long cityId = Long.parseLong(parts[2].replaceAll("\\D", ""));
        if (cityId == 0) {
            return searchCommandReceived(callbackQuery);
        }
        List<Event> eventList = eventRepository.findAllByCity_IdAndDateAfter(cityId, new Date());
        List<Event> sortedEvents = eventList.stream()
                .filter(event -> (categoryId == 0) || event.getCategory().getId().equals(categoryId))
                .sorted(Comparator.comparing(Event::getDate).thenComparing(Event::getTime))
                .collect(Collectors.toList());
        List<List<InlineKeyboardButton>> inlineButtons = new ArrayList<>();
        for (Event eventCard : sortedEvents) {
            String buttonText = eventCard.getId() + ". " + eventCard.getName() + " - " + eventCard.getDate();
            InlineKeyboardButton button = new InlineKeyboardButton(buttonText);
            button.setCallbackData("event-" + eventCard.getId());
            List<InlineKeyboardButton> inlineKeyboardButtons = new ArrayList<>();
            inlineKeyboardButtons.add(button);
            inlineButtons.add(inlineKeyboardButtons);
        }
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        keyboardMarkup.setKeyboard(inlineButtons);
        SendMessage message = createMessage(callbackQuery.getMessage(),"------------", false);
        message.setReplyMarkup(keyboardMarkup);
        return message;
    }

    @Override
    public SendMessage handleDefaultMessages(Update update) {
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
        User user = userService.getOrCreateUser(userTemp);
        State.StateCode state = State.StateCode.valueOf(user.getCurrent_state().getCode());
        Long id = (state.equals(State.StateCode.EVENT_CREATED_CITY_SELECTED)) ? 2L : 1L;
        Optional<Event> eventOptional = eventService.findEventByMessageId(message.getMessageId() - id);
        Event event;
        if (eventOptional.isPresent()) {
            event = eventOptional.get();
        } else {
            return sendChoosingActionButtons(callbackQuery);
        }
        String messageText = message.getText();
        String answer = " some ";
        switch (state) {
            case EVENT_CREATED:
                event.setName(messageText);
                event.setTgId((long) (message.getMessageId() + 1));
                user.setCurrent_state(stateRepository.findByCode(State.StateCode.EVENT_CREATED_NAME_SELECTED.name()));
                answer = getTextByLanguage(user.getLang(), "EVENT.DESCRIPTION");
                break;
            case EVENT_CREATED_NAME_SELECTED:
                event.setDescription(messageText);
                event.setTgId((long) (message.getMessageId() + 1));
                user.setCurrent_state(stateRepository.findByCode(State.StateCode.EVENT_CREATED_DESCRIPTION_SELECTED.name()));
                answer = getTextByLanguage(user.getLang(), "EVENT.REQUIREMENTS");
                break;
            case EVENT_CREATED_DESCRIPTION_SELECTED:
                event.setRequirement(messageText);
                event.setTgId((long) (message.getMessageId()));
                user.setCurrent_state(stateRepository.findByCode(State.StateCode.EVENT_CREATED_REQUIREMENTS_SELECTED.name()));
                userService.saveUser(user);
                eventService.saveEvent(event);
                return chooseCategory(message, user.getLang(), null);
            case EVENT_CREATED_REQUIREMENTS_SELECTED:
                Long categoryId = Long.parseLong(callbackQuery.getData().split("-")[1].replaceAll("\\D", ""));
                event.setCategory(categoryRepository.findById(categoryId).orElse(null));
                event.setTgId((long) (message.getMessageId()));
                user.setCurrent_state(stateRepository.findByCode(State.StateCode.EVENT_CREATED_CATEGORY_SELECTED.name()));
                userService.saveUser(user);
                eventService.saveEvent(event);
                return cityChoose(message, user.getLang());
            case EVENT_CREATED_CATEGORY_SELECTED:
                return cityChoose(message, user.getLang());
            case EVENT_CREATED_CITY_SELECTED:
                if (message.hasLocation()) {
                    Location location = message.getLocation();
                    event.setPlace(geocoderService.getPlace(location.getLongitude(), location.getLatitude()));
                }
                user.setCurrent_state(stateRepository.findByCode(State.StateCode.EVENT_CREATED_LOCATION_SELECTED.name()));
                event.setTgId((long) (message.getMessageId() + 1));
                userService.saveUser(user);
                eventService.saveEvent(event);
                return sendDateSelectionKeyboard(message.getChatId(), user.getLang());
            case EVENT_CREATED_LOCATION_SELECTED:
                event.setDate(java.sql.Date.valueOf(messageText));
                event.setTgId((long) (message.getMessageId() + 1));
                user.setCurrent_state(stateRepository.findByCode(State.StateCode.EVENT_CREATED_DATE_SELECTED.name()));
                userService.saveUser(user);
                eventService.saveEvent(event);
                return sendTimeSelectionKeyboard(message.getChatId(), user.getLang());
            case EVENT_CREATED_DATE_SELECTED:
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm a");
                LocalTime localTime = LocalTime.parse(messageText, formatter);
                event.setTime(java.sql.Time.valueOf(localTime));
                event.setTgId((long) (message.getMessageId() + 1));
                user.setCurrent_state(stateRepository.findByCode(State.StateCode.EVENT_CREATED_TIME_SELECTED.name()));
                answer = createEventCardsMessage(user.getLang(), Collections.singletonList(event));
                break;
            case EVENT_CREATED_TIME_SELECTED:
                return sendChoosingActionButtons(callbackQuery);
            case EVENT_UPDATE:
            case ENROLLED_EVENT_SELECTED:
            case SOME_EVENT_SELECTED:
            case EVENT_DELETE:
            case EVENT_UPDATE_CITY_SELECT:
            case EVENT_UPDATE_CATEGORY_SELECT:
            case EVENT_UPDATE_LOCATION_SELECT:
            case EVENT_UPDATE_NAME_SELECT:
            case EVENT_UPDATE_DESCRIPTION_SELECT:
            case EVENT_UPDATE_REQUIREMENTS_SELECT:
            case EVENT_UPDATE_DATE_SELECT:
            case EVENT_UPDATE_TIME_SELECT:
            case MINE_EVENT_SELECTED:
            case REMARK_CREATE:
            case ENROLL_CREATE:
            case REMARK_DELETE:
                break;
            default:
                sendChoosingActionButtons(callbackQuery);
                break;
        }
        userService.saveUser(user);
        eventService.saveEvent(event);
        return createMessage(message, answer, false);
    }

    @Override
    public SendMessage sendChoosingActionKeyboard(Message message, String lang) {
        List<KeyboardRow> keyboardRows = new ArrayList<>();
        String answer;
        String[] actions = new String[]{"EVENT.CREATE", "EVENT.SEARCH", "EVENT.MINE"};
        KeyboardRow row = new KeyboardRow();
        for (int i = 0; i < actions.length; i++) {
            String actionText = (i + 1) + ". " + getTextByLanguage(lang, actions[i]);
            row.add(new KeyboardButton(actionText));
        }
        keyboardRows.add(row);
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setKeyboard(keyboardRows);

        answer = getTextByLanguage(lang, "EVENT.CHOOSE.MAIN.ACTION");
        SendMessage messageReturn = createMessage(message, answer, false);
        messageReturn.setReplyMarkup(keyboardMarkup);
        return messageReturn;
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
