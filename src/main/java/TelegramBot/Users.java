package TelegramBot;

import postgresql.manage.PSQL;

import java.util.ArrayList;
import java.util.List;

public class Users {
    private static Users INSTANCE;
    private final int capacity = 10;
    private List<User> users = new ArrayList<>(capacity);
    private int position;

    private Users() {
    }

    public static Users getInstance() {
        if (INSTANCE == null)
            INSTANCE = new Users();
        return INSTANCE;
    }

    private void addUser(long id) {
        if (position == capacity - 1) {
            delOne();
        }
        users.add(new User(id));
        position++;
    }

    private void addUser(User user) {
        if (position == capacity - 1) {
            delOne();
        }
        users.add(user);
        position++;
    }

    private Object getUser(long id) {
        for (User user : users) {
            if (user.getChatId() == id) {
                return user;
            }
        }
        return id;
    }

    public int getAccess(long id) {
        for (User user : users) {
            if (user.getChatId() == id) {
                return user.getAccess();
            }
        }
        User newUser = new User(id);
        addUser(newUser);
        return newUser.getAccess();
    }

    public boolean changeAccess(long id, int access) {
        for (User user : users) {
            if (user.getChatId() == id) {
                user.access = access;
                return true;
            }
        }
        return false;
    }
    public boolean changeAccess(long id, long access) {
        for (User user : users) {
            if (user.getChatId() == id) {
                user.access = (int)access;
                return true;
            }
        }
        return false;
    }
    public boolean isAdmin(long id) {
        for (User user : users) {
            if (user.getChatId() == id) {
                return user.isAdmin();
            }
        }
        User newUser = new User(id);
        addUser(newUser);
        return newUser.isAdmin();
    }

    public String getAddRoom(long id) {
        for (User user : users) {
            if (user.getChatId() == id) {
                return user.setAddRoom();
            }
        }
        User newUser = new User(id);
        addUser(newUser);
        return newUser.getAddRoom();
    }


    private void delOne() {
        long minTime = users.get(0).time;
        long curTime;
        int minPosition = 0;
        for (int i = 0; i < capacity; i++) {
            curTime = users.get(i).time;
            if (minTime > curTime) {
                minTime = curTime;
                minPosition = i;
            }
        }
        for (int i = minPosition; i < capacity - 1; i++) {
            users.set(i, users.get(i + 1));
        }
        users.set(9, null);
        position--;
    }

    private class User {
        private long chatId;
        private int access;
        private long time;
        private boolean admin;
        private String addRoom;

        public User(long id, int access, boolean admin, String addRoom) {
            this.chatId = id;
            this.access = access;
            this.admin = admin;
            this.addRoom = addRoom;
            time = System.currentTimeMillis();
        }

        User(long id) {
            this.chatId = id;
            this.access = PSQL.accessTelegramByChatID(id);
            if (access == -2) {
                return;
            }
            this.admin = PSQL.isAdminByChatId(id);
            this.addRoom = PSQL.isAddRoom(id);
            time = System.currentTimeMillis();
        }

        String setAddRoom() {
            addRoom = PSQL.isAddRoom(chatId);
            return addRoom;
        }

        int getAccess() {
            long now = System.currentTimeMillis();
            if (now - time > 1_800_000) {
                //проверяем авторизацию
                access = PSQL.accessTelegramByChatID(chatId);
                time = System.currentTimeMillis();
            }
            return access;
        }

        boolean isAdmin() {
            long now = System.currentTimeMillis();
            if (now - time > 1_800_000) {
                //проверяем авторизацию
                admin = PSQL.isAdminByChatId(chatId);
                time = System.currentTimeMillis();
            }
            return admin;
        }

        String getAddRoom() {
            long now = System.currentTimeMillis();
            if (now - time > 1_800_000) {
                //проверяем авторизацию
                addRoom = PSQL.isAddRoom(chatId);
                time = System.currentTimeMillis();
            }
            return addRoom;
        }

        public long getChatId() {
            return chatId;
        }
    }
}
