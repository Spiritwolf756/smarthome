package controller.devices;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import controller.Tasks;
import controller.jsonBody.TaskJSONBody;
import org.hibernate.Session;
import org.json.JSONObject;
import postgresql.manage.PSQL;
import postgresql.models.Device;
import postgresql.models.User;
import postgresql.services.DeviceService;
import postgresql.services.UserService;
import postgresql.utils.HibernateSessionFactoryUtil;

import java.util.ArrayList;
import java.util.List;

public class Devices {
    public static void getRoomsDevices(String task) {
        Gson gson = new GsonBuilder().serializeNulls().create();
        TaskJSONBody taskJSONBody = gson.fromJson(task, TaskJSONBody.class);

        long chatId = taskJSONBody.chatId;
        for (TaskJSONBody.room room : taskJSONBody.rooms) {
            String[][] str = PSQL.getDevicesByRoomId(Long.valueOf(room.id));
            //формируем json
            JSONObject jo = new JSONObject();
            List<JSONObject> devices = new ArrayList<>();
            if (str == null) {
                jo.put("chatId", chatId)
                        .put("task", "view")
                        .put("view", "simpleMsg")
                        .put("msg", "У Вас нет доступных устройств");
            } else {
                for (String[] str1 : str) {
                    devices.add(new JSONObject().put("id", str1[0])
                            .put("name", str1[1]));
                }
                jo.put("chatId", chatId)
                        .put("task", "view")
                        .put("view", "devices")
                        .put("devices", devices);
            }
            //кладем json-ответ
            try {
                Tasks.getINSTANCE().getOutgoingTasks().put(jo.toString());
            } catch (InterruptedException e) {
                System.out.println("Не удалось распарсить json из Rooms");
                e.printStackTrace();
            }
        }
    }

    public static void showDevices(TaskJSONBody json) {
        String[][] devices = PSQL.getAllUserDevices(json.chatId);
        //формируем json
        JSONObject jo = new JSONObject();
        List<JSONObject> devices1 = new ArrayList<>();
        if (devices == null) {
            jo.put("chatId", json.chatId)
                    .put("task", "view")
                    .put("view", "simpleMsg")
                    .put("msg", "У Вас нет добавленных устройств");
        } else {
            for (String[] device : devices) {
                devices1.add(new JSONObject().put("id", device[1])
                        .put("name", device[0])
                        .put("room", device[2]));
            }

            jo.put("chatId", json.chatId)
                    .put("task", "view")
                    .put("view", "showDevices")
                    .put("devices", devices1);
        }
        //кладем json-ответ
        try {
            Tasks.getINSTANCE().getOutgoingTasks().put(jo.toString());
        } catch (InterruptedException e) {
            System.out.println("Не удалось распарсить json из Rooms");
            e.printStackTrace();
        }
    }

    public static void setDevices(TaskJSONBody json) {
        for (TaskJSONBody.device device : json.devices) {
            //выводим статус ожидания названия девайса в null
            Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();

            //session.close();
            //long userId = 10;
            //session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
            DeviceService deviceService = new DeviceService(session);
            Device deviceEntity = deviceService.getDevicesBySpecialname(device.specialname);

            //session.close();

            // List<Device> devices = userService.getDevicesBySpecialname(device.specialname);
            String str;
             if (deviceEntity == null ) {
                 str = "Ошибка добавления устройства. Попробуйте еще раз или введите \"отмена\"";

                 //str = deviceEntity != null ? "Добавлено помещение с названием: " + deviceName : "Ошибка добавления устройства. Попробуйте еще раз или введите \"отмена\"";
             }else {
                 String deviceName = deviceEntity.getName();
                 UserService userService = new UserService(session);
                 User user = userService.findTelegramByRoom(json.chatId).getUser();
                 user.getUserChat().setAddroom(null);
                 //userService.updateUser(user);
                 long userId = user.getId();
                 // return null;
                 //} else {
                 //  Device deviceEntity = devices.get(0);
                 //User user = userService.findTelegramByRoom(json.chatId).getUser();
                 //userService.updateUser(user);
                 deviceEntity.setUser_id(userId);
                 deviceEntity.setUser(user);
                 //deviceEntity.setUser(user);
                 user.addDevice(deviceEntity);
                 userService.updateUser(user);
                 //deviceService.updateDevice(deviceEntity);
                 str = "Добавлено помещение с названием: " + deviceName;

             }

            session.close();

            //формируем json
            JSONObject jo = new JSONObject();

            jo.put("chatId", json.chatId)
                    .put("task", "view")
                    .put("view", "simpleMsg")
                    .put("msg", str);
            // user.getUserChat().setAddroom(null);
            //userService.updateUser(user);

            //user.addDevice(deviceEntity);

            //deviceEntity.setUser(user);
            //userService.updateUser(user);

            //new DeviceService(session).updateDevice(deviceEntity);
            try {
                Tasks.getINSTANCE().getOutgoingTasks().put(jo.toString());
            } catch (InterruptedException e) {
                System.out.println("Не удалось распарсить json из Rooms");
                e.printStackTrace();
            }
        }
    }
}


