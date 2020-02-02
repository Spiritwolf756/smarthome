package controller.devices;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import controller.Tasks;
import controller.jsonBody.TaskJSONBody;
import org.json.JSONObject;
import postgresql.manage.PSQL;
import postgresql.services.DeviceService;
import postgresql.services.UserService;

import java.util.ArrayList;
import java.util.List;

public class FreeDevices {
    public static void getFreeDevices(String task) {
        Gson gson = new GsonBuilder().serializeNulls().create();
        TaskJSONBody taskJSONBody = gson.fromJson(task, TaskJSONBody.class);

        long chatId = taskJSONBody.chatId;
        for (TaskJSONBody.room room : taskJSONBody.rooms) {
            String[][] str = new DeviceService().getAllFreeUserDevices(taskJSONBody.chatId);
            //формируем json
            JSONObject jo = new JSONObject();
            List<JSONObject> devices = new ArrayList<>();
            List<JSONObject> rooms = new ArrayList<>();
            rooms.add(new JSONObject().put("id", room.id)
                    .put("name", room.name));
            if (str == null) {
                jo.put("chatId", chatId)
                        .put("task", "view")
                        .put("view", "simpleMsg")
                        .put("msg", "У Вас нет нераспределенных устройств");
            } else {
                for (String[] str1 : str) {
                    devices.add(new JSONObject().put("id", str1[0])
                            .put("name", str1[1]));
                }
                jo.put("chatId", chatId)
                        .put("msgId", taskJSONBody.msgId)
                        .put("task", "view")
                        .put("view", "freeDevices")
                        .put("devices", devices)
                        .put("rooms", rooms);
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
}
