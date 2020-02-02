package controller.user;

import com.google.inject.internal.cglib.core.$AbstractClassGenerator;
import com.google.inject.internal.cglib.core.$RejectModifierPredicate;
import controller.Tasks;
import controller.jsonBody.TaskJSONBody;
import org.json.JSONObject;
import postgresql.manage.PSQL;

import java.util.ArrayList;
import java.util.List;

public class CreateUser {
    public static void create(TaskJSONBody json) {
        for (TaskJSONBody.user user : json.users) {
            //PSQL.createUserFromTelegram(json.chatId, user.firstname, user.lastname, user.username, user.phone, user.admin);

            //формируем json
            JSONObject jo = new JSONObject();

            String str = user.admin ? " с правами администратора." : ".";
            jo.put("chatId", json.chatId)
                    .put("task", "view")
                    .put("view", "simpleMsg")
                    .put("msg", "Пользователь " + user.username + " добавлен" + str);
            //кладем json-ответ
            try {
                Tasks.getINSTANCE().getOutgoingTasks().put(jo.toString());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
