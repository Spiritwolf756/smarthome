package controller.devicesCommands;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import controller.Tasks;
import controller.jsonBody.FileDecorator;
import controller.jsonBody.TaskJSONBody;
import controller.jsonBody.YeelightJSONRequest;
import devices.light.yeelight.ResponseJSONBody;
import devices.light.yeelight.Yeelight;
import org.hibernate.Session;
import org.json.JSONObject;
import postgresql.manage.PSQL;
import postgresql.models.Command;
import postgresql.models.Device;
import postgresql.services.DeviceService;
import postgresql.utils.HibernateSessionFactoryUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DevicesCommands {
    public static void control(TaskJSONBody json) throws InterruptedException {
        for (TaskJSONBody.device device : json.devices)
            for (TaskJSONBody.device.command command : device.commands) {
                Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
                Command commandEntity = PSQL.getCommand(session, Long.parseLong(command.id));
                Device deviceEntity = commandEntity.getDevice();
                //работает с лампочкой
                if ("yeelight".equals(deviceEntity.getSpecialinstructions())) {
                    //if ("yeelight".equals(deviceEntity.getSpecialname())) {
                    for (String topic : deviceEntity.getTopic().split(";")) {

                        Yeelight yeelight = new Yeelight(1, false, topic);

                        String reply = yeelight.swith(commandEntity.getCommand());
                        Gson gson = new GsonBuilder().serializeNulls().create();
                        YeelightJSONRequest replyJSON = gson.fromJson(reply, YeelightJSONRequest.class);
                        if ("ok".equals(replyJSON.result[0])) {
                            deviceEntity.setLastcommand(commandEntity.getCommand());
                            DeviceService deviceService = new DeviceService(session);
                            deviceService.saveDevice(deviceEntity);

                            String str = "on".equals(commandEntity.getCommand()) ? "включен" : "выключен";
                            //формируем json
                            JSONObject jo = new JSONObject();
                            jo.put("chatId", json.chatId)
                                    .put("clbId", json.clbId)
                                    .put("task", "view")
                                    .put("view", "alertMsg")
                                    .put("msg", "Свет " + str);
                            Tasks.getINSTANCE().getOutgoingTasks().put(jo.toString());
                        }
                    }
                }else if ("camera".equals(deviceEntity.getSpecialinstructions())) {
                    //работаем с камерой
                    String com = commandEntity.getCommand();
                    System.out.println(com);
                    if ("autoLightOn".equals(com)) {
                        Camera.getINSTANCE().setAutoLightOn();
                        //формируем json
                        JSONObject jo = new JSONObject();
                        jo.put("chatId", json.chatId)
                                .put("clbId", json.clbId)
                                .put("task", "view")
                                .put("view", "alertMsg")
                                .put("msg", "Автоматическое включение света активировано");
                        Tasks.getINSTANCE().getOutgoingTasks().put(jo.toString());
                    }
                    if ("autoLightOff".equals(com)) {
                        Camera.getINSTANCE().setAutoLightOff();
                        //формируем json
                        JSONObject jo = new JSONObject();
                        jo.put("chatId", json.chatId)
                                .put("clbId", json.clbId)
                                .put("task", "view")
                                .put("view", "alertMsg")
                                .put("msg", "Автоматическое включение света дизактивировано");
                        Tasks.getINSTANCE().getOutgoingTasks().put(jo.toString());
                    }
                    if ("photo".equals(com)) {
                        //формируем json
                        JSONObject jo = new JSONObject();
                        jo.put("chatId", json.chatId)
                                .put("clbId", json.clbId)
                                .put("task", "view")
                                .put("view", "alertMsg")
                                .put("msg", "");
                        Tasks.getINSTANCE().getOutgoingTasks().put(jo.toString());
                        File photo = null;
                        photo = Camera.getINSTANCE().getPhoto();
                        if (photo != null) {
                            FileDecorator fileDecorator = new FileDecorator()
                                    .set(photo)
                                    .set(json.chatId);
                            try {
                                Tasks.getINSTANCE().getOutgoingTasks().put(fileDecorator);
                            } catch (InterruptedException e) {
                                System.out.println("Не удалось положить картинку в блокирующую очередь");
                                e.printStackTrace();
                            }
                        } else {
                            //формируем json
                            jo = new JSONObject();
                            jo.put("chatId", json.chatId)
                                    .put("clbId", json.clbId)
                                    .put("task", "view")
                                    .put("view", "simpleMsg")
                                    .put("msg", "Не удалось сделать фотографию. Возможно, камера не подключена");
                            Tasks.getINSTANCE().getOutgoingTasks().put(jo.toString());

                        }
                    }
                }else {
                    //формируем json
                    JSONObject jo = new JSONObject();
                    jo.put("chatId", json.chatId)
                            .put("clbId", json.clbId)
                            .put("task", "view")
                            .put("view", "alertMsg")
                            .put("msg", "Ошибка управления устройством");
                    Tasks.getINSTANCE().getOutgoingTasks().put(jo.toString());
                }

            }
    }
}
