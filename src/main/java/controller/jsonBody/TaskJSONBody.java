package controller.jsonBody;

import java.io.File;

public class TaskJSONBody {
    public Long chatId;
    public String task;
    public weather[] weather;
    public String controller; //пояснение для контроллера
    public String view; //пояснение для вью
    public room[] rooms; //массив комнат пользователя
    public device[] devices; //массив устройств пользователя
    public String msg; //сообщение пользователю через бота
    public int msgId;
    public String clbId;
    public user[] users;


    public class weather {
        public String cityId;
        public String discription;
        public String icon;
        public String temp;
        public String wind;
    }

    public class room {
        public String id;
        public String name;
        // public device[] devices;
    }

    public class device {
        public String id;
        public String name;
        public String room;
        public String specialname;
        public command[] commands;

        public class command {
            public String id;
            public String text;
            public String command;
        }
    }

    public class user {
        public String firstname;
        public String lastname;
        public String username;
        public String phone;
        public int id;
        public boolean admin;
        public boolean access;
        public int accessInt;
    }
}
