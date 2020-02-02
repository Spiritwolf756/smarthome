package TelegramBot;

import controller.Tasks;
import controller.rooms.Rooms;
import imageGeneration.MainImage;
import mqtt.Mqtt;
import org.json.JSONObject;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import postgresql.manage.PSQL;
import postgresql.models.Device;

import postgresql.services.UserService;
import sensors.camera.Camera;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Bot extends TelegramLongPollingBot {
    public Bot(DefaultBotOptions botOptions) {
        super(botOptions);
    }

    private void putTask(Object obj) throws InterruptedException {
        Tasks tasks = Tasks.getINSTANCE();
        tasks.getIncomingTasks().put(obj);
    }

    private void setButtonUniversal(Long chatId, String text, List<String> buttons) throws TelegramApiException {
        SendMessage message = new SendMessage()
                .setChatId(chatId)
                .setText(text);

        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        message.setReplyMarkup(replyKeyboardMarkup);
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(true);

        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow keyboardRow;
        for (String button : buttons) {
            keyboardRow = new KeyboardRow();
            keyboardRow.add(new KeyboardButton(button));
            keyboard.add(keyboardRow);
        }
        replyKeyboardMarkup.setKeyboard(keyboard);
        execute(message);
    }

    private void setButtonShareContact(Long chatId) throws TelegramApiException {
        // Создаем клавиуатуру
        SendMessage message = new SendMessage()
                .setChatId(chatId);
        //кнопка
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        message.setReplyMarkup(replyKeyboardMarkup);
        replyKeyboardMarkup.setSelective(false);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(true);

        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow keyboardFirstRow = new KeyboardRow();
        keyboardFirstRow.add(new KeyboardButton("Отправить контакт").setRequestContact(true));

        message.setText("Для авторизации, пожалуйста, отправьте боту свой контакт");

        keyboard.add(keyboardFirstRow);
        replyKeyboardMarkup.setKeyboard(keyboard);
        execute(message);
        //return message;
    }

    public void InlineButtonRooms(Long chatId, String text, String callbackdata) throws TelegramApiException {
        SendMessage message = new SendMessage()
                .setText((text))
                .setChatId(chatId);
        //кнопка
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
        List<InlineKeyboardButton> buttonsRow = new ArrayList<>();
        InlineKeyboardButton button1 = new InlineKeyboardButton();
        InlineKeyboardButton button2 = new InlineKeyboardButton();

        button1.setText("del");
        button1.setCallbackData(callbackdata + "-del");

        button2.setText("add dev");
        button2.setCallbackData(callbackdata + "-add");

        buttonsRow.add(button1);
        buttonsRow.add(button2);

        buttons.add(buttonsRow);


        InlineKeyboardMarkup markupKeyboard = new InlineKeyboardMarkup();
        markupKeyboard.setKeyboard(buttons);
        message.setReplyMarkup(markupKeyboard);
        execute(message);
    }

    private void InlineButtonAllRoomsOrDevices(Long chatId, String[][] rooms, String callback, String text) throws TelegramApiException {
        SendMessage message = new SendMessage()
                .setText((text))
                .setChatId(chatId);
        //кнопка
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
        for (String[] room : rooms) {
            List<InlineKeyboardButton> buttonsRow = new ArrayList<>();
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(room[0]);
            button.setCallbackData(callback + "-" + room[1] + "-" + room[0]);
            buttonsRow.add(button);
            buttons.add(buttonsRow);
        }

        InlineKeyboardMarkup markupKeyboard = new InlineKeyboardMarkup();
        markupKeyboard.setKeyboard(buttons);
        message.setReplyMarkup(markupKeyboard);
        execute(message);
    }

    private void showInlineButtonsMsg(Long chatId, String deviceName, Boolean ternon) throws TelegramApiException {
        String text1;
        String callbackdata1;
        String text2;
        String callbackdata2;
        SendMessage message = new SendMessage()
                .setChatId(chatId)
                .setText(deviceName);
        //кнопка
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
        List<InlineKeyboardButton> buttons1 = new ArrayList<>();
        List<InlineKeyboardButton> buttons2 = new ArrayList<>();

        text1 = "включить";
        callbackdata1 = deviceName + "_вкл";
        text2 = "выключить";
        callbackdata2 = deviceName + "_вык";
        buttons1.add(new InlineKeyboardButton().setText(text1).setCallbackData(callbackdata1));
        buttons2.add(new InlineKeyboardButton().setText(text2).setCallbackData(callbackdata2));
        buttons.add(buttons1);
        buttons.add(buttons2);

        InlineKeyboardMarkup markupKeyboard = new InlineKeyboardMarkup();
        markupKeyboard.setKeyboard(buttons);

        message.setReplyMarkup(markupKeyboard);
        execute(message);
    }

    public void deleteMessage(Long chatId, int msgId) throws TelegramApiException {
        DeleteMessage dMSG = new DeleteMessage();
        dMSG.setChatId(chatId);
        dMSG.setMessageId(msgId);
        execute(dMSG);
    }

    public static SendMessage sendInlineKeyBoardMessage(long chatId) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton();
        InlineKeyboardButton inlineKeyboardButton2 = new InlineKeyboardButton();
        inlineKeyboardButton1.setText("Тык");
        inlineKeyboardButton1.setCallbackData("Button \"Тык\" has been pressed");
        inlineKeyboardButton2.setText("Тык2");
        inlineKeyboardButton2.setCallbackData("Button \"Тык2\" has been pressed");
        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
        List<InlineKeyboardButton> keyboardButtonsRow2 = new ArrayList<>();
        keyboardButtonsRow1.add(inlineKeyboardButton1);
        keyboardButtonsRow1.add(new InlineKeyboardButton().setText("Fi4a").setCallbackData("CallFi4a"));
        keyboardButtonsRow2.add(inlineKeyboardButton2);
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(keyboardButtonsRow1);
        rowList.add(keyboardButtonsRow2);
        inlineKeyboardMarkup.setKeyboard(rowList);
        return new SendMessage().setChatId(chatId).setText("Пример").setReplyMarkup(inlineKeyboardMarkup);
    }

    public synchronized void sendMSG(String msg, long chatId) throws TelegramApiException {
        SendMessage message = new SendMessage()
                .setChatId(chatId)
                .setText(msg);
        execute(message);
    }

    private void sendCallbackAnswer(AnswerCallbackQuery answer, String id, String text) throws TelegramApiException {
        answer.setCallbackQueryId(id);
        answer.setText(text);
        answer.setShowAlert(false);
        execute(answer);
    }

    public void alertMsg(String id, String text) throws TelegramApiException {
        AnswerCallbackQuery answer = new AnswerCallbackQuery();
        answer.setCallbackQueryId(id);
        answer.setText(text);
        answer.setShowAlert(true);
        execute(answer);
    }

    private void buttonStart(Long chatId) {
        List<String> buttonsStart = new ArrayList<>();
        buttonsStart.add("Управление");
        buttonsStart.add("Настройки");
        try {
            setButtonUniversal(chatId, "Для продолжения воспользуйтесь клавиатурой", buttonsStart);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void buttonManage(Long chatId) {
        List<String> buttonsSettings = new ArrayList<>();
        buttonsSettings.add("Погода");
        buttonsSettings.add("Помещения");
        buttonsSettings.add("<= в меню");
        try {
            setButtonUniversal(chatId, "Выберите следующий шаг", buttonsSettings);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void buttonSettings(long chatId) {
        List<String> buttonsSettings = new ArrayList<>();
        buttonsSettings.add("Добавить");
        buttonsSettings.add("Список");
        buttonsSettings.add("<= в меню");
        try {
            setButtonUniversal(chatId, "Что будем настраивать?", buttonsSettings);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void buttonAddChoise(long chatId) {
        List<String> buttonsSettings = new ArrayList<>();
        buttonsSettings.add("Добавить помещение");
        buttonsSettings.add("Добавить устройство");
        buttonsSettings.add("<= к настройкам");
        try {
            setButtonUniversal(chatId, "Что вы хотите добавить?", buttonsSettings);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void buttonListChoise(long chatId) {
        List<String> buttonsSettings = new ArrayList<>();
        buttonsSettings.add("Список помещений");
        buttonsSettings.add("Список устройств");
        buttonsSettings.add("<= к настройкам");
        try {
            setButtonUniversal(chatId, "Какой список Вы хотите посмотреть?", buttonsSettings);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void editMessage(Long chatId, int msgId, String text) throws TelegramApiException {
        EditMessageText editMSG = new EditMessageText();
        editMSG.setChatId(chatId);
        editMSG.setMessageId(msgId);
        editMSG.setText(text);
        execute(editMSG);
    }

    @Override
    public void onUpdateReceived(Update update) {
        Users users = Users.getInstance();
        Long chatId = 0L;
        String lastName = "";
        String firstName = "";
        String userName = "";
        String callbackText = "";
        if (update.hasMessage()) {
            //ФИксируем ID чата
            lastName = update.getMessage().getChat().getLastName();
            firstName = update.getMessage().getChat().getFirstName();
            userName = update.getMessage().getChat().getUserName();
            chatId = update.getMessage().getChatId();

//---ПОЛУЧЕНИЕ CALLBACK--//
        } else if (update.hasCallbackQuery()) {
            int msgId = 0;
            AnswerCallbackQuery answer = new AnswerCallbackQuery();
            callbackText = update.getCallbackQuery().getData();
            chatId = update.getCallbackQuery().getMessage().getChatId();
            msgId = update.getCallbackQuery().getMessage().getMessageId();
            String[][] cbd = new String[][]{callbackText.split("-")};
            String[] cbt = cbd[0];
            String msgText = update.getCallbackQuery().getMessage().getText();
            System.out.println(callbackText);


            if (cbt[0].equals("room")) {
                if (cbt[3].equals("del")) {
                    try {
                        putTask(delRoomTask(chatId, update.getCallbackQuery().getId(), msgId, cbt[1], cbt[2]));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else if (cbt[3].equals("add")) {
                    //выводим список девайсов, которые можно добавить
                    try {
                        putTask(freeDevicesTask(chatId, msgId, cbt[1], cbt[2]));
                        sendCallbackAnswer(answer, update.getCallbackQuery().getId(), "");
                    } catch (TelegramApiException | InterruptedException e) {
                        e.printStackTrace();
                    }
                }
//---ОБРАБОТКА КОЛБЕККВЕРИ ОТ ПОМЕЩЕНИЯ (ВЫВОДИМ СПИСОК ДЕВАЙСОВ В КОМНАТЕ)---//
            } else if ("showDevicesByRoom".equals(cbt[0])) {
                System.out.println(2);
                try {
                    putTask(devicesTask(chatId, Integer.valueOf(cbt[2])));
                    deleteMessage(chatId, msgId);
                } catch (InterruptedException | TelegramApiException e) {
                    e.printStackTrace();
                }
//---ОБРАБОТКА КОЛБЕККВЕРИ ОТ ДЕВАЙСА (ВЫВОДИМ ИНСТРУКЦИЮ УПРАВЛЕНИЯ ДЕВАЙСОМ)---
            } else if ("showDeviceDetails".equals(cbt[0])) {
                try {
                    putTask(devicesDetailsTask(chatId, Integer.parseInt(cbt[2])));
                    deleteMessage(chatId, msgId);
                } catch (TelegramApiException | InterruptedException e) {
                    e.printStackTrace();
                }
//---ОБРАБОТКА КОЛБЕКА УПРАВЛЕНИЯ ДЕВАЙСОМ---
            } else if ("deviceCommands".equals(cbt[0])) {
                try {
                    putTask(deviceCommandTask(chatId, Integer.parseInt(cbt[2]), update.getCallbackQuery().getId()));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
//---ОБРАБОТКА КОЛБЕККВЕРИ ОТ ДЕВАЙСА---//
            } else if (cbt[0].equals("device")) {
                //проверяем, что колбекквери нужной глубины
                if (cbt.length == 6) {
                    try {
                        putTask(connectDeviceToRoomTask(chatId, msgId, cbt[1], cbt[2], cbt[3], cbt[4]));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        //Обработка полученного контакта
        if (update.hasMessage() && update.getMessage().hasContact()) {
            String phone = update.getMessage().getContact().getPhoneNumber();
            if (PSQL.checkTelegramByPhone(phone)) {

            } else {
                try {
                    putTask(createUserTask(chatId, firstName, lastName, userName, phone, false, false));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                try {
                    //отправка уведомления админам
                    List<Long> rooms;
                    rooms = new ArrayList<>(PSQL.findAllAdmins());
                    for (Long room : rooms) {
                        sendMSG("Пользователь " + userName + " (" + firstName + " " + lastName + ") просит авторизацию. Для согласования авторизации отправьте в чат команду /add" + chatId, room);
                    }
                    //отправка уведомления запрашиваемому авторизацию
                    sendMSG("Заявка на авторизацию направлена", chatId);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
        }
        //ЛЮБОЙ ТЕКСТ
        if (update.hasMessage() && update.getMessage().hasText()) {
            //проверка статуса пользователя:
            //заблочен
            //не заблочен
            //null - ожидает подтверждения админом
            //отсутствует в базе
            String text = update.getMessage().getText();
            int access1 = users.getAccess(chatId);
            //System.out.println("access: " + access1);
            //обавления себя админом
            if (text.equals("test")) {
                try {
                    sendMSG("Test: OK", chatId);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            } else if (text.equals("758427")) {
                if (access1 == 0) {
                    try {
                        putTask(createUserTask(chatId, firstName, lastName, userName, "+7911010278", true, true));
                        sendMSG("Вам установлен статус администратора", chatId);
                    } catch (TelegramApiException | InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        sendMSG("Вы уже администратор", chatId);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                }
            } else if (access1 == 1) {
                //не заблочен
                //если сообщение об авторизации
                if (text.length() > 4 && text.substring(0, 4).equals("/add") && users.isAdmin(chatId)) {
                    //найти пользователя по чат айди
                    try {
                        putTask(setUserAccess(Long.parseLong(text.substring(4, text.length())), true));
                        sendMSG("Доступ успешно предоставлен", chatId);
                    } catch (TelegramApiException | InterruptedException e) {
                        e.printStackTrace();
                    }
                    // ожидаем название помещения
                } else if ("room".equals(users.getAddRoom(chatId))) {
                    try {
                        if (text.equals("отмена")) {
                            putTask(setDeviceRoomNullTask(chatId));
                            buttonStart(chatId);
                        } else {
                            putTask(setRoomTask(chatId, text));
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else if (new UserService().isAddRoom(chatId).equals("device")) {
                    //ожидаем код устройства
                    try {
                        if (text.equals("отмена")) {
                            putTask(setDeviceRoomNullTask(chatId));
                            buttonStart(chatId);
                        } else {
                            putTask(setDeviceTask(chatId, text));
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    switch (text.toLowerCase()) {
                        case "тест":
                            try {
                                execute(sendInlineKeyBoardMessage(chatId));
                            } catch (TelegramApiException e) {
                                e.printStackTrace();
                            }
                            break;
                        case "/start":
                        case "<= в меню":
                            buttonStart(chatId);
                            break;
                        case "управление":
                            buttonManage(chatId);
                            break;
                        case "настройки":
                        case "<= к настройкам":
                            buttonSettings(chatId);
                            break;
                        case "добавить":
                            buttonAddChoise(chatId);
                            break;
                        case "список":
                            buttonListChoise(chatId);
                            break;
                        case "добавить помещение":
                            if (PSQL.addRoomDevFromTelegram(chatId, "room")) {
                                try {
                                    sendMSG("Введите название помещения", chatId);
                                } catch (TelegramApiException e) {
                                    e.printStackTrace();
                                }
                            }
                            break;
                        case "добавить устройство":
                            if (new UserService().addRoomDevFromTelegram(chatId, "device")) {
                                try {
                                    sendMSG("Введите код устройства", chatId);
                                } catch (TelegramApiException e) {
                                    e.printStackTrace();
                                }
                            }
                            break;
                        case "список помещений":
                            try {
                                putTask(roomsTask(chatId, true));
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            break;
                        case "список устройств": {
                            try {
                                putTask(showDevicesTask(chatId));
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        break;
                        case "помещения": {
                            try {
                                putTask(roomsTask(chatId, false));
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            break;
                        }
                        case "gh": {
                            String[][] allrooms = PSQL.getAllRoomsFromTelegram(chatId);

                            if (allrooms == null) {
                                try {
                                    sendMSG("У Вас нет добавленных помещений", chatId);
                                } catch (TelegramApiException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                try {
                                    InlineButtonAllRoomsOrDevices(chatId, allrooms, "showDevicesByRoom", "Помещения:");

                                } catch (TelegramApiException e) {
                                    e.printStackTrace();
                                }
                            }
                            break;
                        }
                        case "погода": {
                            try {
                                putTask(weatherTask(chatId, 536203));
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            break;
                        }
                        case "погода-д": {
                            try {
                                putTask(weatherTask(chatId, 536203, 0));
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            break;
                        }
                        case "погода-н": {
                            try {
                                putTask(weatherTask(chatId, 536203, 1));
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            break;
                        }
                        case "фото": {
                            SendPhoto message = null;
                            try {
                                message = new SendPhoto()
                                        .setChatId(chatId)
                                        .setPhoto(MainImage.joke(Camera.getPhoto()));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            try {
                                execute(message);
                            } catch (TelegramApiException e) {
                                e.printStackTrace();
                            }
                            break;
                        }
                        case "никогда": {
                            List<Device> devices = PSQL.getAllDevices();
                            for (Device device : devices) {
                                try {
                                    showInlineButtonsMsg(chatId, device.getName(), true);
                                } catch (TelegramApiException e) {
                                    e.printStackTrace();
                                }
                            }
                            break;
                        }
                        case "Список1": {
                        }

                        break;
                        default:
                            throw new IllegalStateException("Unexpected value: " + text);
                    }
                }
            } else if (access1 == 0) {
                //заблочен
                try {
                    sendMSG("Доступ заблокирован", chatId);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            } else if (access1 == -1) {
                //заявка направлена ранее
                try {
                    sendMSG("Ваша заявка на авторизацию уже направлена. Дождитесь, пожалуйста, подтверждения администратором", chatId);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            } else if (access1 == -2) {
                //отсутствует в базе
                //нажал кнопку "Обновить данные"
                if (text.equals("Обновить данные")) {
                    List<Long> rooms;
                    rooms = new ArrayList<>(PSQL.findAllAdmins());
                    for (Long room : rooms) {
                        try {
                            sendMSG("Пользователь " + userName + " (" + firstName + " " + lastName + ") просит закрепить новый чат телеграмма. Для согласования авторизации отправьте в чат команду /add" + chatId, room);
                            sendMSG("Ваша заявка на авторизацию уже направлена. Дождитесь, пожалуйста, подтверждения администратором", chatId);
                        } catch (TelegramApiException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    //просим отправить контакт
                    try {
                        setButtonShareContact(chatId);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                }

            }
        }
    }

    String setDeviceRoomNullTask(long chatId) {
        JSONObject jo = new JSONObject();
        jo.put("chatId", chatId)
                .put("task", "controller")
                .put("controller", "setDeviceRoomNull");
        return jo.toString();
    }

    String showDevicesTask(long chatId) {
        JSONObject jo = new JSONObject();
        jo.put("chatId", chatId)
                .put("task", "controller")
                .put("controller", "showDevices");
        return jo.toString();
    }

    String delRoomTask(long chatId, String clbId, long msgId, String roomId, String roomName) {
        JSONObject jo = new JSONObject();
        List<JSONObject> rooms = new ArrayList<>();
        rooms.add(new JSONObject().put("id", roomId)
                .putOnce("name", roomName));
        jo.put("chatId", chatId)
                .put("task", "controller")
                .put("controller", "delRoom")
                .put("clbId", clbId)
                .put("msgId", msgId)
                .put("rooms", rooms);
        return jo.toString();
    }

    String setDeviceTask(long chatId, String text) {
        JSONObject jo = new JSONObject();
        List<JSONObject> devices = new ArrayList<>();
        devices.add(new JSONObject().put("specialname", text));
        jo.put("chatId", chatId)
                .put("task", "controller")
                .put("controller", "setDevice")
                .put("devices", devices);
        return jo.toString();
    }

    String setRoomTask(long chatId, String text) {
        JSONObject jo = new JSONObject();
        List<JSONObject> rooms = new ArrayList<>();
        rooms.add(new JSONObject().put("name", text));
        jo.put("chatId", chatId)
                .put("task", "controller")
                .put("controller", "setRoom")
                .put("rooms", rooms);
        return jo.toString();
    }

    String setUserAccess(long chatId, boolean access) {
        JSONObject jo = new JSONObject();
        List<JSONObject> users = new ArrayList<>();
        users.add(new JSONObject().put("access", access));
        jo.put("chatId", chatId)
                .put("task", "controller")
                .put("controller", "setUserAccess")
                .put("users", users);
        return jo.toString();
    }

    String createUserTask(long chatId, String firstname, String lastname, String username, String phone, boolean isAdmin, boolean access) {
        JSONObject jo = new JSONObject();
        List<JSONObject> users = new ArrayList<>();
        users.add(new JSONObject().put("firstname", firstname)
                .put("lastname", lastname)
                .put("username", username)
                .put("phone", phone)
                .put("admin", isAdmin)
                .put("access", access));
        jo.put("chatId", chatId)
                .put("task", "controller")
                .put("controller", "createUser")
                .put("users", users);
        return jo.toString();
    }

    String connectDeviceToRoomTask(long chatId, long msgId, String deviceId, String deviceName, String roomId, String roomName) {
        JSONObject jo = new JSONObject();
        List<JSONObject> rooms = new ArrayList<>();
        rooms.add(new JSONObject().put("id", roomId)
                .put("name", roomName));
        List<JSONObject> devices = new ArrayList<>();
        devices.add(new JSONObject().put("id", deviceId)
                .put("name", deviceName));
        jo.put("chatId", chatId)
                .put("task", "controller")
                .put("controller", "connectDeviceToRoom")
                .put("msgId", msgId)
                .put("rooms", rooms)
                .put("devices", devices);
        return jo.toString();
    }

    String freeDevicesTask(long chatId, long msgId, String roomId, String roomName) {
        JSONObject jo = new JSONObject();
        List<JSONObject> rooms = new ArrayList<>();
        rooms.add(new JSONObject().put("id", roomId)
                .put("name", roomName));
        jo.put("chatId", chatId)
                .put("task", "controller")
                .put("controller", "freeDevices")
                .put("msgId", msgId)
                .put("rooms", rooms);
        return jo.toString();
    }

    String deviceCommandTask(long chatId, int commandId, String clbId) {
        JSONObject jo = new JSONObject();
        List<JSONObject> devices = new ArrayList<>();
        List<JSONObject> commands = new ArrayList<>();
        commands.add(new JSONObject().put("id", commandId));
        devices.add(new JSONObject().put("commands", commands));
        jo.put("chatId", chatId)
                .put("clbId", clbId)
                .put("task", "controller")
                .put("controller", "devicesCommands")
                .put("devices", devices);
        return jo.toString();
    }

    String devicesDetailsTask(long chatId, int deviceId) {
        JSONObject jo = new JSONObject();
        List<JSONObject> devices = new ArrayList<>();
        devices.add(new JSONObject().put("id", deviceId));
        jo.put("chatId", chatId)
                .put("task", "controller")
                .put("controller", "devicesDetails")
                .put("devices", devices);
        return jo.toString();
    }

    String devicesTask(long chatId, int roomId) {
        JSONObject jo = new JSONObject();
        List<JSONObject> rooms = new ArrayList<>();
        rooms.add(new JSONObject().put("id", roomId));
        jo.put("chatId", chatId)
                .put("task", "controller")
                .put("controller", "devices")
                .put("rooms", rooms);
        return jo.toString();
    }

    String weatherTask(long chatId, long cityId) {
        JSONObject jo = new JSONObject();
        List<JSONObject> weather = new ArrayList<>();
        weather.add(new JSONObject().put("cityId", cityId));
        jo.put("chatId", chatId)
                .put("task", "controller")
                .put("controller", "weather")
                .put("weather", weather);
        return jo.toString();
    }

    //для ручного управления день/ночь
    String weatherTask(long chatId, long cityId, int night) {
        JSONObject jo = new JSONObject();
        List<JSONObject> weather = new ArrayList<>();
        weather.add(new JSONObject().put("cityId", cityId));
        jo.put("chatId", chatId)
                .put("task", "controller")
                .put("controller", "weather" + "-" + night)
                .put("weather", weather);
        return jo.toString();
    }

    String roomsTask(long chatId, boolean setting) {
        JSONObject jo = new JSONObject();
        jo.put("chatId", chatId)
                .put("task", "controller");
        if (!setting)
            jo.put("controller", "rooms");
        else
            jo.put("controller", "roomsList");
        return jo.toString();
    }

    public void sendPhoto(long chatId, File file) throws TelegramApiException {
        SendPhoto message = new SendPhoto()
                .setChatId(chatId)
                .setPhoto(file);
        execute(message);
    }

    public void InlineButtonShowItems(Long chatId, String[][] rooms, String callback, String text) throws TelegramApiException {
        SendMessage message = new SendMessage()
                .setText((text))
                .setChatId(chatId);
        //кнопка
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();

        int i = 1;
        List<InlineKeyboardButton> buttonsRow = new ArrayList<>();
        for (String[] room : rooms) {
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(room[1]);
            button.setCallbackData(callback + "-" + room[1] + "-" + room[0]);
            buttonsRow.add(button);
            if (i % 3 == 0) {
            }
            i++;
        }
        buttons.add(buttonsRow);

        InlineKeyboardMarkup markupKeyboard = new InlineKeyboardMarkup();
        markupKeyboard.setKeyboard(buttons);
        message.setReplyMarkup(markupKeyboard);
        execute(message);
    }

    public void InlineButtonAddDeviceToRoom(Long chatId, String text, String[][] devices, String cbd) throws TelegramApiException {
        SendMessage message = new SendMessage()
                .setText((text))
                .setChatId(chatId);
        //кнопка
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
        int i = 1;
        List<InlineKeyboardButton> buttonsRow = new ArrayList<>();
        for (String[] device : devices) {
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(device[1]);
            button.setCallbackData("device-" + device[0] + "-" + device[1] + "-" + cbd + "-addToRoom");
            buttonsRow.add(button);
            if (i % 3 == 0) {
            }
            i++;
        }
        buttons.add(buttonsRow);
        InlineKeyboardMarkup markupKeyboard = new InlineKeyboardMarkup();
        markupKeyboard.setKeyboard(buttons);
        message.setReplyMarkup(markupKeyboard);
        execute(message);
    }

    @Override
    public String getBotUsername() {
        System.out.println("getBotUsername");
        return "SweetHome756Bot";
    }

    @Override
    public String getBotToken() {
        return "735371051:AAEiJ0MjelotDD2gETVEdrRu0rB-S0IfZi0";
    }
}

