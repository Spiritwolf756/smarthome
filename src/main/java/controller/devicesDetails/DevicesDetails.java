package controller.devicesDetails;

import controller.Tasks;
import controller.jsonBody.TaskJSONBody;
import org.json.JSONObject;
import postgresql.manage.PSQL;
import postgresql.models.Command;

import java.util.ArrayList;
import java.util.List;

public class DevicesDetails {
    //метод определения, какой именно девайс спрашивают, чтобы вызвать нужный метод
    public static void getDevicesDetails(TaskJSONBody json) {
        for (TaskJSONBody.device device : json.devices) {
            device.name = PSQL.getDeviceName(Long.parseLong(device.id));
            String[][] commands = PSQL.getCommadsByDeviceId(Long.parseLong(device.id));
            //формируем json
            JSONObject jo = new JSONObject();
            List<JSONObject> commands1 = new ArrayList<>(); //список для команд в девайсе
            List<JSONObject> devices = new ArrayList<>(); //список девайсов

            if (commands == null || commands.length == 0) {
                jo.put("chatId", json.chatId)
                        .put("task", "view")
                        .put("view", "simpleMsg")
                        .put("msg", "У выбранного устройства " + json.devices[0].name + " нет доступной системы управления");
            } else {
                for (int i = 0; i < commands.length; i++) {
                    commands1.add(new JSONObject().put("id", commands[i][0])
                            .put("text", commands[i][1])
                            .put("command", commands[i][2]));
                }
                devices.add(new JSONObject().put("id", device.id)
                        .put("name", device.name)
                        .put("commands", commands1));

                //добавляем все команды в список девайсов
                jo.put("chatId", json.chatId)
                        .put("task", "view")
                        .put("view", "devicesDetails")
                        .put("devices", devices);
            }
            //кладем json-ответ
            try {
                Tasks.getINSTANCE().getOutgoingTasks().put(jo.toString());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
