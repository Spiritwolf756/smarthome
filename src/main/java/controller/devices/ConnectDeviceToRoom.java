package controller.devices;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import controller.Tasks;
import controller.jsonBody.TaskJSONBody;
import org.json.JSONObject;
import postgresql.manage.PSQL;
import postgresql.services.DeviceService;

import java.util.ArrayList;
import java.util.List;

public class ConnectDeviceToRoom {
    public static void setConnect(String task) {
        Gson gson = new GsonBuilder().serializeNulls().create();
        TaskJSONBody taskJSONBody = gson.fromJson(task, TaskJSONBody.class);

        long chatId = taskJSONBody.chatId;
        //предполагаем, что пришла только одна комната с только одним устройством
        new DeviceService().setDeviceToRoom(Long.parseLong(taskJSONBody.rooms[0].id), Long.parseLong(taskJSONBody.devices[0].id));
        //формируем json
        JSONObject jo = new JSONObject();
        List<JSONObject> devices = new ArrayList<>();

        jo.put("chatId", chatId)
                .put("msgId", taskJSONBody.msgId)
                .put("task", "view")
                .put("view", "editMsg")
                .put("msg", "Устройство " + taskJSONBody.devices[0].name + " успешно добавлено в помещение " + taskJSONBody.rooms[0].name);
        //кладем json-ответ
        try {
            Tasks.getINSTANCE().getOutgoingTasks().put(jo.toString());
        } catch (
                InterruptedException e) {
            System.out.println("Не удалось распарсить json из Rooms");
            e.printStackTrace();
        }
    }
}

