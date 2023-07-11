package com.example.it32007telegram.services;

import com.example.it32007telegram.daos.UserDao;
import com.example.it32007telegram.daos.repositories.CategoryRepository;
import com.example.it32007telegram.daos.repositories.CountryRepository;
import com.example.it32007telegram.daos.repositories.EventRepository;
import com.example.it32007telegram.models.entities.Event;
import com.example.it32007telegram.models.entities.base.Category;
import com.example.it32007telegram.models.entities.base.City;
import com.example.it32007telegram.models.entities.base.Country;
import com.example.it32007telegram.models.entities.users.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class TelegramServiceImpl implements TelegramService  {
    private final CategoryRepository categoryRepository;
    private final CountryRepository countryRepository;
    private final EventRepository eventRepository;
    private final EventService eventService;
    private final UserDao userDao;


    @Override
    public SendMessage startCommandReceived(Update update) {
        Message message = update.getMessage();
        String answer = "Привет, " + message.getChat().getFirstName() + ", добро пожаловать!" + "\n" +
                "Здесь вы найдете идеальный способ организовать встречу с новыми друзьями для похода в кино или незабываемого приключения в горах. \uD83C\uDF89️\uD83C\uDFA5" + "\n\n" +
                "Наш бот предоставит вам удобные инструменты, чтобы сделать процесс планирования и координации простым и веселым. Вы сможете предложить свои идеи, узнать предпочтения других участников и создать событие, которое будет подходить каждому. \uD83E\uDD1D\uD83D\uDCA1\uD83D\uDDD3️\n" +
                "\n" +
                "Откройте новые возможности для общения, обмена идеями, а главное, создайте незабываемые моменты, которые останутся в памяти на всю жизнь. Наш бот будет рядом на каждом шагу, чтобы помочь вам сделать ваше приключение неповторимым и запоминающимся. \uD83C\uDF1F\uD83C\uDF08\n" +
                "\n" +
                "Не теряйте время на нескончаемую переписку и сомнения – наш бот облегчит все процессы, связанные с планированием. Отправьте приглашения, проголосуйте за лучшие варианты, обсудите детали и ожидайте встречи с новыми знакомыми, готовыми поделиться увлечениями и радостью от совместных приключений. \uD83D\uDC8C\uD83D\uDCC6\uD83E\uDD29\n" +
                "\n" +
                "Готовы ли вы начать увлекательное путешествие? Добро пожаловать в мир возможностей и новых знакомств! \uD83D\uDE80\uD83C\uDF0D\uD83E\uDD73" + "\n";
        SendMessage sendMessage = createMessage(message, answer);
        KeyboardRow keyboardFirstRow = new KeyboardRow();
        keyboardFirstRow.add("\uD83D\uDD8B Создать");
        keyboardFirstRow.add("\uD83D\uDD8B Редактировать");
        KeyboardRow keyboardSecondRow = new KeyboardRow();
        keyboardSecondRow.add("\uD83D\uDD0D Искать");
        keyboardSecondRow.add("\uD83D\uDCCB Мои мероприятия");
        return createButtons(sendMessage, keyboardFirstRow, keyboardSecondRow);
    }
    @Override
    public SendMessage listCommandReceived(Update update) {
        org.telegram.telegrambots.meta.api.objects.User userTg = update.getMessage().getFrom();
        Optional<User> userOptional = userDao.findByUsername(userTg.getUserName());
        User user = userOptional.orElseGet(() -> userDao.createUser(userTg));
        Map<String, List<Event>> events = eventService.getUserEvents(user);
        List<List<InlineKeyboardButton>> inlineButtons = new ArrayList<>();
        List<InlineKeyboardButton> inlineKeyboardButtons = new ArrayList<>();
        List<InlineKeyboardButton> inlineKeyboardButtonss = new ArrayList<>();
        InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton("Мои мероприятия");
        inlineKeyboardButton.setCallbackData("get_event");
        inlineKeyboardButtons.add(inlineKeyboardButton);

        for (Event eventCard : events.get("created")) {
            String buttonText = eventCard.getId() + ". " + eventCard.getName();
            InlineKeyboardButton button = new InlineKeyboardButton(buttonText);
            button.setCallbackData("event-" + eventCard.getId());
            inlineKeyboardButtons.add(button);
        }
        InlineKeyboardButton inlineKeyboardButtonn = new InlineKeyboardButton("Зарегистрированные");
        inlineKeyboardButtonn.setCallbackData("get_event");
        inlineKeyboardButtonss.add(inlineKeyboardButtonn);
        for (Event eventCard : events.get("enrolled")) {
            String buttonText = eventCard.getId() + ". " + eventCard.getName();
            InlineKeyboardButton button = new InlineKeyboardButton(buttonText);
            button.setCallbackData("event-" + eventCard.getId());
            inlineKeyboardButtonss.add(button);
        }

        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        inlineButtons.add(inlineKeyboardButtons);
        inlineButtons.add(inlineKeyboardButtonss);
        keyboardMarkup.setKeyboard(inlineButtons);

        SendMessage message = createMessage(update.getMessage(), "Выберите мероприятие");
        message.setReplyMarkup(keyboardMarkup);
        return message;
    }

    private String createEventCardsMessage(List<Event> eventCards) {
        StringBuilder messageText = new StringBuilder();
        for (Event eventCard : eventCards) {
            messageText.append("Название: ").append(eventCard.getName()).append("\n");
            messageText.append("Описание: ").append(eventCard.getDescription()).append("\n");
            messageText.append("Дата: ").append(eventCard.getDate()).append("\n");
            messageText.append("Время: ").append(eventCard.getTime()).append("\n");
            City city = eventCard.getCity();
            if (city != null) {
                messageText.append("Город: ").append(city.getName()).append("\n");
            }
            String place = eventCard.getPlace();
            if (place != null) {
                messageText.append("Место: ").append(place).append("\n");
            }
            String requirement = eventCard.getRequirement();
            if (requirement != null) {
                messageText.append("Требования: ").append(requirement).append("\n");
            }
            User createdUser = eventCard.getCreatedUser();
            if (createdUser != null) {
                messageText.append("Ответственный: ").append(createdUser.getUsername()).append("\n");
            }
            messageText.append("\n");
        }
        return String.valueOf(messageText);
    }

    @Override
    public SendMessage getEvent(Update update) {
        CallbackQuery callbackQuery = update.getCallbackQuery();
        String callbackData = callbackQuery.getData();
        String id = callbackData.substring(6);

        Optional<Event> eventOptional = eventRepository.findById(Long.valueOf(id));
        String answer = "";
        if(eventOptional.isPresent()){
            answer = createEventCardsMessage(Collections.singletonList(eventOptional.get()));
        }
        return createMessage(update.getCallbackQuery().getMessage(), answer);
    }

    @Override
    public SendMessage createCommandReceived(Update update) {
        Message message = update.getMessage();
        List<String> categories = new ArrayList<>();
        List<Category> categoryList = categoryRepository.findAll();
        categoryList.forEach(category -> categories.add(category.getName()));
        SendMessage sendMessage = createMessage(message, "Выберите категорию: ");
        KeyboardRow keyboardFirstRow = new KeyboardRow();
        keyboardFirstRow.add("\uD83C\uDFCB" + categories.get(0));
        keyboardFirstRow.add("\uD83C\uDFA8" + categories.get(1));
        keyboardFirstRow.add("\uD83C\uDF89" + categories.get(2));
        KeyboardRow keyboardSecondRow = new KeyboardRow();
        keyboardSecondRow.add("\uD83C\uDFA5" + categories.get(3));
        keyboardSecondRow.add("\uD83C\uDF72" + categories.get(4));
        keyboardSecondRow.add("\uD83D\uDCD6" + categories.get(5));
        return createButtons(sendMessage, keyboardFirstRow, keyboardSecondRow);
    }

    @Override
    public SendMessage editCommandReceived(Update update) {
        List<List<InlineKeyboardButton>> inlineButtons = new ArrayList<>();
        List<InlineKeyboardButton> inlineKeyboardButtons = new ArrayList<>();
        Field[] fields = Event.class.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            InlineKeyboardButton button = new InlineKeyboardButton(field.getName());
            button.setCallbackData(field.getName() + "Edit-");
            inlineKeyboardButtons.add(button);
        }
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        inlineButtons.add(inlineKeyboardButtons);
        keyboardMarkup.setKeyboard(inlineButtons);

        SendMessage message = createMessage(update.getMessage(), "Выберите что хотите изменить");
        message.setReplyMarkup(keyboardMarkup);
        return message;
    }


    @Override
    public SendMessage searchCommandReceived(Update update) {
        return null;
    }


/*
    @Override
    public void processDefaultStates(Update update) {
        Optional<User> userOptional = userDao.findByUsername(message.getChat().getUserName());
        User user;
        user = userOptional.orElseGet(() -> createUser(message));
        Optional<Event> eventOptional = eventRepository.findByTgId(message.getChatId());
        if(eventOptional.isPresent()){
            Event event = eventOptional.get();
            switch (user.getState()){
                case "requiredTitle":
                    event.setName(message.getText());
                    user.setState("requiredDescription");
                    userRepository.save(user);
                    eventRepository.save(event);
                    String answer = "Напишите описание мероприятий";
                    try {
                        execute(createMessage(message, answer));
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                    break;
                case "requiredDescription":
                    event.setDescription(message.getText());
                    user.setState("requiredCountry");
                    userRepository.save(user);
                    eventRepository.save(event);
                    countryChoose(message);
                    break;
                case "requiredCountry":
                    event.setCountry(countryRepository.findByName(message.getText().substring(1)));
                    user.setState("requiredCity");
                    userRepository.save(user);
                    eventRepository.save(event);
                    String answerCountry = "Введите город";
                    try {
                        execute(createMessage(message, answerCountry));
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                    break;
                case "requiredCity":
                    Optional<City> cityOptional = cityRepository.findByCountryIdAndName(event.getCountry().getId(), message.getText());
                    City city;
                    city = cityOptional.orElseGet(() -> City.builder()
                            .country(event.getCountry())
                            .name(message.getText())
                            .build());
                    cityRepository.save(city);
                    event.setCity(city);
                    user.setState("requiredPlace");
                    userRepository.save(user);
                    eventRepository.save(event);
                    String answerCity = "Введите место";
                    try {
                        execute(createMessage(message, answerCity));
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                    break;
                case "requiredPlace":
                    event.setPlace(message.getText());
                    user.setState("requiredDate");
                    userRepository.save(user);
                    eventRepository.save(event);
                    sendDateSelectionKeyboard(message.getChatId());
                    break;
                case "requiredDate":
                    event.setDate(Date.valueOf(message.getText()));
                    user.setState("requiredTime");
                    userRepository.save(user);
                    eventRepository.save(event);
                    sendTimeSelectionKeyboard(message.getChatId());
                    break;
                case "requiredTime":
                    event.setTime(Time.valueOf(message.getText()));
                    user.setState("requiredRequirements");
                    userRepository.save(user);
                    eventRepository.save(event);
                    break;
                case "requiredRequirements":
                    event.setRequirement(message.getText());
                    user.setState("eventCreated");
                    userRepository.save(user);
                    eventRepository.save(event);
                    break;
                default:
                    break;
            }
        }
    }*/

    private SendMessage createMessage(Message message, String answer){
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(message.getChatId().toString());
        sendMessage.setReplyToMessageId(message.getMessageId());
        sendMessage.setText(answer);
        return sendMessage;
    }

    private SendMessage createButtons(SendMessage sendMessage, KeyboardRow keyboardFirstRow, KeyboardRow keyboardSecondRow) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);
        List<KeyboardRow> keyboard = new ArrayList<>();
        keyboard.add(keyboardFirstRow);
        keyboard.add(keyboardSecondRow);
        replyKeyboardMarkup.setKeyboard(keyboard);
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        return sendMessage;
    }



    private SendMessage countryChoose(Message message) {
        List<Country> countries = countryRepository.findAll(Sort.by(Sort.Direction.ASC, "name"));
        StringBuilder answer = new StringBuilder();
        for (Country c : countries) {
            answer.append("/").append(c.getName()).append("\n");
        }
        return createMessage(message, "Выберите страну: \n\n" + answer);
    }

    private SendMessage sendDateSelectionKeyboard(Long chatId) {
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
        message.setText("Выберите дату");
        message.setReplyMarkup(keyboardMarkup);
        return message;
    }


    private SendMessage sendTimeSelectionKeyboard(Long chatId) {
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
        message.setText("Выберите время");
        message.setReplyMarkup(keyboardMarkup);
        return message;
    }

}
