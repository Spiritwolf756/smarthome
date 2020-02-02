package controller;

import TelegramBot.Bot;
import TelegramBot.Users;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.internal.asm.$ClassReader;
import controller.devices.ConnectDeviceToRoom;
import controller.devices.Devices;
import controller.devices.FreeDevices;
import controller.devicesCommands.DevicesCommands;
import controller.devicesDetails.DevicesDetails;
import controller.jsonBody.FileDecorator;
import controller.jsonBody.TaskJSONBody;
import controller.rooms.Rooms;
import controller.user.CreateUser;
import controller.user.User;
import controller.weather.Weather;
import devices.light.yeelight.Yeelight;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import postgresql.manage.PSQL;
import postgresql.models.Command;
import postgresql.models.Device;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class Controller {
    private static Controller INSTANCE;
    private Tasks tasks;
    private Bot bot;

    private Controller() {
        tasks = Tasks.getINSTANCE();
    }

    public static Controller getINSTANCE() {
        if (INSTANCE == null)
            INSTANCE = new Controller();
        return INSTANCE;
    }

    public void setBot(Bot bot) {
        this.bot = bot;
    }

    public void runThreads() {
        //поток view -> model
        Thread toModel = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true)
                    try {
                        String str = tasks.getIncomingTasks().take().toString();
                        System.out.println("Incoming: " + str);
                        //парсим json
                        Gson gson = new GsonBuilder().serializeNulls().create();
                        TaskJSONBody json = gson.fromJson(str, TaskJSONBody.class);
                        if ("controller".equals(json.task)) {
                            //просим очистить статус ожидания названия комнаты/помещения
                            if ("setDeviceRoomNull".equals(json.controller)) {
                                Rooms.setDeviceRoomNull(json);
                            }
                            //просим удалить помещение
                            if ("delRoom".equals(json.controller)) {
                                Rooms.delRoom(json);
                            }
                            //просим создать помещение у пользователя
                            if ("setRoom".equals(json.controller)) {
                                Rooms.setRoom(json);
                            }
                            //просим создать пользователя
                            if ("createUser".equals(json.controller)) {
                                User.create(json);
                            }
                            //обрабатываем погоду
                            if ("weather".equals(json.controller)) {
                                Weather.weatherImg(str);
                            }
                            if ("weather-0".equals(json.controller)) {
                                Weather.weatherImg(str, 0);
                            }
                            if ("weather-1".equals(json.controller)) {
                                Weather.weatherImg(str, 1);
                            }
                            //запрашиваем список помещений
                            if ("rooms".equals(json.controller)) {
                                Rooms.getUsersRooms(str, false);
                            }
                            //запрашиваем список помещений для управления ими
                            if ("roomsList".equals(json.controller)) {
                                Rooms.getUsersRooms(str, true);
                            }
                            //запрашиваем список девайсов в помещении
                            if ("devices".equals(json.controller)) {
                                Devices.getRoomsDevices(str);
                            }
                            //просим привязать девайс к пользователю
                            if ("setDevice".equals(json.controller)) {
                                Devices.setDevices(json);
                            }
                            //запрашиваем список всех девайсов
                            if ("showDevices".equals(json.controller)) {
                                Devices.showDevices(json);
                            }
                            //запрашиваем список незакрепленных девайсов
                            if ("freeDevices".equals(json.controller)) {
                                FreeDevices.getFreeDevices(str);
                            }
                            //запрашиваем привязку устройства к помещению
                            if ("connectDeviceToRoom".equals(json.controller)) {
                                ConnectDeviceToRoom.setConnect(str);
                            }
                            //запрашиваем систему управления устройством
                            if ("devicesDetails".equals(json.controller)) {
                                DevicesDetails.getDevicesDetails(json);
                            }
                            //получили команду на управление устройством
                            if ("devicesCommands".equals(json.controller)) {
                                DevicesCommands.control(json);
                            }
                            //получили команду на предоставление доступа пользователю
                            if ("setUserAccess".equals(json.controller)) {
                                User.setAccess(json);
                            }
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
            }
        });
        toModel.start();
        //model -> view
        Thread toView = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true)
                    try {
                        String str = "";
                        Object obj = tasks.getOutgoingTasks().take();
                        if (obj instanceof FileDecorator) {
                            //если получен файл
                            //вызываем у бота отправку изображения
                            FileDecorator fileDecorator = (FileDecorator) obj;
                            bot.sendPhoto(fileDecorator.getChatId(), fileDecorator.getFile());
                        } else {
                            str = (String) obj;

                            // System.out.println(obj.getClass());
                            //String str = (String) tasks.getOutgoingTasks().take();
                            System.out.println("Outgoing: " + str);
                            //парсим json
                            Gson gson = new GsonBuilder().serializeNulls().create();
                            TaskJSONBody json = gson.fromJson(str, TaskJSONBody.class);
                            if ("view".equals(json.task)) {
                                //меняем уровень доступа у юзера
                                if ("changeUserAccess".equals(json.view)) {
                                    Users.getInstance().changeAccess(json.chatId, json.users[0].accessInt);
                                }
                                //вызываем у бота отправку обычного сообщения
                                if ("simpleMsg".equals(json.view)) {
                                    bot.sendMSG(json.msg, json.chatId);
                                }
                                //вызываем у бота редактирование сообщения
                                if ("editMsg".equals(json.view)) {
                                    bot.editMessage(json.chatId, json.msgId, json.msg);
                                }
                                //вызываем у бота удаление сообщения
                                if ("delMsg".equals(json.view)) {
                                    bot.deleteMessage(json.chatId, json.msgId);
                                }
                                //вызываем у бота алерт-сообщение
                                if ("alertMsg".equals(json.view)) {
                                    bot.alertMsg(json.clbId, json.msg);
                                }
                                //вызываем у бота отправку сообщения погоды
                                if ("weather".equals(json.view)) {
                                    bot.sendMSG(prepareWeather(str), json.chatId);
                                }
                                //вызываем у бота отправку сообщения с перечнем помещений
                                if ("rooms".equals(json.view)) {
                                    String[][] rooms = new String[json.rooms.length][2];
                                    int i = 0;
                                    for (TaskJSONBody.room room : json.rooms) {
                                        rooms[i][0] = room.id;
                                        rooms[i][1] = room.name;
                                        i++;
                                    }
                                    System.out.println("gh");
                                    bot.InlineButtonShowItems(json.chatId, rooms, "showDevicesByRoom", "Помещения:");
                                }
                                //вызываем у бота отправку сообщения с перечнем помещений для добавления устройства
                                if ("roomsList".equals(json.view)) {
                                    int i = 0;
                                    for (TaskJSONBody.room room : json.rooms) {
                                        bot.InlineButtonRooms(json.chatId, room.name, "room-" + room.id + "-" + room.name);
                                    }
                                }
                                //вызываем у бота отправку сообщения с перечнем нераспределенных устройтсв
                                if ("freeDevices".equals(json.view)) {
                                    bot.deleteMessage(json.chatId, json.msgId);
                                    String[][] devices = new String[json.devices.length][2];
                                    int i = 0;
                                    for (TaskJSONBody.device device : json.devices) {
                                        devices[i][0] = device.id;
                                        devices[i][1] = device.name;
                                        i++;
                                    }
                                    bot.InlineButtonAddDeviceToRoom(json.chatId, "Выберите устройство, которое хотите добавить в: " + json.rooms[0].name, devices, json.rooms[0].id + "-" + json.rooms[0].name);
                                }
                                //вызываем у бота отправку сообщения с перечнем устройств
                                if ("devices".equals(json.view)) {
                                    String[][] devices = new String[json.devices.length][2];
                                    int i = 0;
                                    for (TaskJSONBody.device device : json.devices) {
                                        devices[i][0] = device.id;
                                        devices[i][1] = device.name;
                                        i++;
                                    }
                                    bot.InlineButtonShowItems(json.chatId, devices, "showDeviceDetails", "Устройства:");
                                }
                                //вызываем у бота отправку сообщения с перечнем устройств и информацией по комнате
                                if ("showDevices".equals(json.view)) {
                                    //String[][] devices = new String[json.devices.length][2];
                                    //int i = 0;
                                    for (TaskJSONBody.device device : json.devices) {
                                        bot.sendMSG(device.name + " - " + device.room, json.chatId);
                                    }
                                }
                                //вызываем у бота отправку сообщения с инструкциями по девайсу
                                if ("devicesDetails".equals(json.view)) {
                                    for (TaskJSONBody.device device : json.devices) {
                                        int i = 0;
                                        String[][] details = new String[device.commands.length][2];
                                        for (TaskJSONBody.device.command command : device.commands) {
                                            details[i][0] = command.id;
                                            System.out.println("id: " + command.id);
                                            details[i][1] = command.text;
                                            System.out.println("text: " + command.text);
                                            i++;
                                        }
                                        bot.InlineButtonShowItems(json.chatId, details, "deviceCommands", device.name + ":");
                                    }
                                }
                            }
                        }
                    } catch (InterruptedException | TelegramApiException e) {
                        e.printStackTrace();
                    }
            }
        });
        toView.start();
    }

    private String prepareWeather(String task) {
        Gson gson = new GsonBuilder().serializeNulls().create();
        TaskJSONBody json = gson.fromJson(task, TaskJSONBody.class);
        StringBuilder ret = new StringBuilder();
        ret.append("За окном:")
                .append("\n")
                .append("температура: ")
                .append(json.weather[0].temp)
                .append(" градусов")
                .append("\n")
                .append("ветер: ")
                .append(json.weather[0].wind)
                .append(" м/с");

        return ret.toString();
    }
}
