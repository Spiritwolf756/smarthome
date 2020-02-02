package controller.rooms;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import controller.Tasks;
import controller.jsonBody.TaskJSONBody;
import controller.weather.WeatherJSONBody;
import org.hibernate.Session;
import org.json.JSONObject;
import postgresql.manage.PSQL;
import postgresql.models.Device;
import postgresql.models.Room;
import postgresql.services.DeviceService;
import postgresql.services.RoomService;
import postgresql.utils.HibernateSessionFactoryUtil;

import java.util.ArrayList;
import java.util.List;

public class Rooms {
    public static void getUsersRooms(String task, boolean setting) {
        Gson gson = new GsonBuilder().serializeNulls().create();
        TaskJSONBody taskJSONBody = gson.fromJson(task, TaskJSONBody.class);

        long chatId = taskJSONBody.chatId;
        String[][] str = PSQL.getAllRoomsFromTelegram(chatId);
        //формируем json
        JSONObject jo = new JSONObject();
        List<JSONObject> rooms = new ArrayList<>();
        if (str == null) {
            jo.put("chatId", chatId)
                    .put("task", "view")
                    .put("view", "simpleMsg")
                    .put("msg", "У Вас нет доступных помещений");
        } else {
            for (String[] str1 : str) {
                rooms.add(new JSONObject().put("id", str1[0])
                        .put("name", str1[1]));
            }
            jo.put("chatId", chatId)
                    .put("task", "view")
                    .put("rooms", rooms);
            if (setting)
                jo.put("view", "roomsList");
            else
                jo.put("view", "rooms");
        }
        //кладем json-ответ
        try {
            Tasks.getINSTANCE().getOutgoingTasks().put(jo.toString());
        } catch (InterruptedException e) {
            System.out.println("Не удалось распарсить json из Rooms");
            e.printStackTrace();
        }
    }

    public static void setDeviceRoomNull(TaskJSONBody json) {
        PSQL.clearAddRoomDevice(json.chatId);
    }

    public static void setRoom(TaskJSONBody json) {
        for (TaskJSONBody.room room : json.rooms) {
            PSQL.setRoomByTelegram(json.chatId, room.name);
            PSQL.clearAddRoomDevice(json.chatId);
            //формируем json
            JSONObject jo = new JSONObject();

            jo.put("chatId", json.chatId)
                    .put("task", "view")
                    .put("view", "simpleMsg")
                    .put("msg", "Добавлено помещение с названием: " + room.name);

            try {
                Tasks.getINSTANCE().getOutgoingTasks().put(jo.toString());
            } catch (InterruptedException e) {
                System.out.println("Не удалось распарсить json из Rooms");
                e.printStackTrace();
            }
        }
    }

    public static void delRoom(TaskJSONBody json) {
        for (TaskJSONBody.room room : json.rooms) {
            //сначала разорвем связь девайс-комната
            Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();

            //DeviceService deviceService = new DeviceService(session);
            // deviceService.
            RoomService roomService = new RoomService(session);
            Room room1 = roomService.findRoom(Long.parseLong(room.id));

            DeviceService deviceService = new DeviceService(session);

            List<Device> devices = room1.getDevices();
            for (int i=0; i<devices.size(); i++){
            //for(Device device : room1.getDevices()){
                devices.get(i).setRoom_id(null);
                deviceService.updateDevice(devices.get(i));
            }

            session.close();

            PSQL.delRoomById(Long.valueOf(room.id));
            //формируем json на удаление сообщения
            JSONObject joDel = new JSONObject();

            joDel.put("chatId", json.chatId)
                    .put("task", "view")
                    .put("view", "delMsg")
                    .put("chatId", json.chatId)
                    .put("msgId", json.msgId);

            //формируем json на всплывающее сообщение сообщения
            JSONObject joAlertMsg = new JSONObject();

            joAlertMsg.put("chatId", json.chatId)
                    .put("task", "view")
                    .put("view", "alertMsg")
                    .put("msgId", json.msgId)
                    .put("clbId", json.clbId)
                    .put("msg", "Удалено: " + room.name);

            try {
                Tasks.getINSTANCE().getOutgoingTasks().put(joDel.toString());
                Tasks.getINSTANCE().getOutgoingTasks().put(joAlertMsg.toString());
            } catch (InterruptedException e) {
                System.out.println("Не удалось распарсить json из Rooms");
                e.printStackTrace();
            }
        }
    }

}
