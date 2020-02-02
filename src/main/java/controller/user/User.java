package controller.user;

import controller.Tasks;
import controller.jsonBody.TaskJSONBody;
import org.json.JSONObject;
import postgresql.manage.PSQL;

import java.util.ArrayList;
import java.util.List;

public class User {
    public static void setAccess(TaskJSONBody json) {
        for (TaskJSONBody.user user : json.users) {
            long userId = PSQL.setAccessByTelegramChatId(json.chatId, user.access);
            int accessInt = user.access ? 1 : 0;
            //формируем json
            JSONObject jo = new JSONObject();

            jo.put("chatId", json.chatId)
                    .put("task", "view")
                    .put("view", "simpleMsg")
                    .put("msg", "Вам предоставлен доступ к сервису умного дома. Воспользуйтесь командой /start для продолжения");

            JSONObject jo1 = new JSONObject();
            List<JSONObject> users = new ArrayList<>();
            users.add(new JSONObject().put("id", json.chatId)
                    .put("accessInt", accessInt));

            jo1.put("chatId", json.chatId)
                    .put("task", "view")
                    .put("view", "changeUserAccess")
                    .put("users", users);

            //кладем json-ответ
            try {
                Tasks.getINSTANCE().getOutgoingTasks().put(jo.toString());
                Tasks.getINSTANCE().getOutgoingTasks().put(jo1.toString());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void create(TaskJSONBody json) {
        for (TaskJSONBody.user user : json.users) {
            String firstname = user.firstname == null ? "n/a" : user.firstname;
            String lastname = user.lastname == null ? "n/a" : user.lastname;
            String username = user.username == null ? "n/a" : user.username;

            PSQL.createUserFromTelegram(json.chatId, firstname, lastname, username, user.phone, user.admin, user.access);
            //формируем json
            JSONObject jo = new JSONObject();

            String str = user.admin ? " с правами администратора." : ".";
            jo.put("chatId", json.chatId)
                    .put("task", "view")
                    .put("view", "simpleMsg")
                    .put("msg", "Пользователь " + user.username + " добавлен" + str);
            //кладем json-ответ
/*            try {
                Tasks.getINSTANCE().getOutgoingTasks().put(jo.toString());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }*/
        }
    }
}
