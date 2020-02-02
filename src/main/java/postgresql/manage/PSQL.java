package postgresql.manage;


import org.hibernate.Session;
import postgresql.models.*;
import postgresql.services.CommandService;
import postgresql.services.DeviceService;
import postgresql.services.RoomService;
import postgresql.services.UserService;
import postgresql.utils.HibernateSessionFactoryUtil;

import java.util.ArrayList;
import java.util.List;

public class PSQL {

    public static void createUserFromTelegram(Long room, String firstname, String lastname, String username, String phone) {
        UserService userService = new UserService();
        User user = new User();
        userService.saveUser(user);

        Telegram telegram = new Telegram(room, null, firstname, lastname, username, phone);
        user.setTelegram(telegram);
        telegram.setUser(user);

        UserChat userChat = new UserChat();
        user.setUserChat(userChat);
        userChat.setUser(user);

        userService.updateUser(user);
    }

    public static void createUserFromTelegram(Long room, String firstname, String lastname, String username, String phone, boolean admin, boolean access) {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        UserService userService = new UserService(session);
        User user = new User(true, admin);
        userService.saveUser(user);
        System.out.println("gh");
        Telegram telegram = new Telegram(room, access, firstname, lastname, username, phone);
        user.setTelegram(telegram);
        telegram.setUser(user);

        UserChat userChat = new UserChat();
        user.setUserChat(userChat);
        userChat.setUser(user);

        userService.updateUser(user);
        session.close();
    }

    public static void createUserFromTelegram(int room, String firstname, String lastname, String username, String phone) {
        UserService userService = new UserService();
        User user = new User(true, false);
        userService.saveUser(user);

        Telegram telegram = new Telegram(Long.valueOf(room), null, firstname, lastname, username, phone);
        user.setTelegram(telegram);
        telegram.setUser(user);

        userService.updateUser(user);
    }

    public static Boolean checkUser1(Long telegramid) {
        UserService userService = new UserService();
        try {
            if (userService.findTelegramByRoom(telegramid).getId() > 0) {
                return true;
            } else {
                return false;
            }
        } catch (NullPointerException e) {
            return false;
        }
    }

    public static Boolean checkUserByChatId(Long room) {
        UserService userService = new UserService();
        return userService.checkTelegramByRoom(room);
    }

    public static Boolean checkTelegramByPhone(String phone) {
        UserService userService = new UserService();
        return userService.checkTelegramByPhone(phone);
    }

    public static String accessTelegramByRoom(Long room) {
        UserService userService = new UserService();

        if (userService.checkTelegramByRoom(room)) {
            try {
                return String.valueOf(userService.findTelegramByRoom(room).getAccess());
            } catch (NullPointerException e) {
                return "null";
            }
        } else {
            return "отсутствует";
        }
    }

    public static long setAccessByTelegramChatId(Long room, Boolean access) {
        UserService userService = new UserService();
        Telegram telegram = userService.findTelegramByRoom(room);
        telegram.setAccess(access);
        userService.updateUser(telegram.getUser());
        return telegram.getUser().getId();
    }

    public static List<Long> findAllAdmins() {
        List<Long> rooms;
        rooms = new ArrayList<>();
        UserService userService = new UserService();
        for (User user : userService.findAllAdmins()) {
            rooms.add(user.getTelegram().getTelegram_room());
        }
        return rooms;
    }

    public static Boolean isAdminByChatId(Long room) {
        UserService userService = new UserService();
        Telegram telegram = userService.findTelegramByRoom(room);
        return telegram.getUser().getIsAdmin();
    }

    public static List<Device> getAllDevices() {
        DeviceService deviceService = new DeviceService();
        return deviceService.getAllDevices();
    }

    public static boolean addRoomDevFromTelegram(long room) {
        UserService userService = new UserService();
        Telegram telegram = userService.findTelegramByRoom(room);
        telegram.getUser().getUserChat().setAddroom(null);
        userService.updateUser(telegram.getUser());
        return true;
    }

    public static boolean addRoomDevFromTelegram(long room, String text) {
        UserService userService = new UserService();
        Telegram telegram = userService.findTelegramByRoom(room);
        telegram.getUser().getUserChat().setAddroom(text);
        userService.updateUser(telegram.getUser());
        return true;
    }

    public static String isAddRoom(Long room) {
        UserService userService = new UserService();
        Telegram telegram = userService.findTelegramByRoom(room);
        String addroom = telegram.getUser().getUserChat().getAddroom();
        if (addroom == null) {
            return "0";
        } else if (addroom.equals("room")) {
            return "room";
        } else if (addroom.equals("device")) {
            return "device";
        } else {
            return "0";
        }
    }

    public static void setRoomByTelegram(Long room, String text) {
        UserService userService = new UserService();
        Room room1 = new Room(text);
        Telegram telegram = userService.findTelegramByRoom(room);
        telegram.getUser().getUserChat().setAddroom(null);
        telegram.getUser().addRoom(room1);
        userService.updateUser(telegram.getUser());
    }

    public static void setDeviceByTelegram(Long room, String text) {
        UserService userService = new UserService();

    }

    public static String[][] getAllRoomsFromTelegram(Long room) {
        UserService userService = new UserService();
        Telegram telegram = userService.findTelegramByRoom(room);
        List<Room> qrooms = telegram.getUser().getRooms();
        int count = qrooms.size();
        if (count == 0) {
            return null;
        }
        String[][] rooms = new String[count][2];
        int i = 0;
        for (Room room1 : qrooms) {
            rooms[i][0] = room1.getId().toString();
            rooms[i][1] = room1.getName();
            i++;
        }
        userService.userDaoClose();
        return rooms;
    }

    public static void delRoomById(Long id) {
        UserService userService = new UserService();
        userService.delRoomById(id);
    }

    public static String[][] getAllFreeUserDevices() {
        UserService userService = new UserService();
        List<Device> qdevices = userService.getAllFreeUserDevices();
        int count = qdevices.size();
        if (count == 0) {
            return null;
        }
        String[][] devices = new String[count][2];
        int i = 0;
        for (Device device : qdevices) {
            devices[i][0] = device.getName();
            devices[i][1] = device.getId().toString();
            i++;
        }
        return devices;
    }

    public static String getDevicesBySpecialname(Long room, String specialname) {
        UserService userService = new UserService();
        List<Device> devices = userService.getDevicesBySpecialname(specialname);
        if (devices == null || devices.size() == 0) {
            return null;
        } else {
            Device device = devices.get(0);
            User user = userService.findTelegramByRoom(room).getUser();
            return addDeviceToUser(user, device, userService);
        }
    }

    private static String addDeviceToUser(User user, Device device, UserService userService) {
        user.getUserChat().setAddroom(null);
        user.addDevice(device);
        //device.setUser(user);
        userService.updateUser(user);
        DeviceService deviceService = new DeviceService();
        device.setUser(user); //device.setUser_id(user.getId());
        deviceService.updateDevice(device);
        return device.getName();
    }

    public static void clearAddRoomDevice(Long room) {
        UserService userService = new UserService();
        User user = userService.findTelegramByRoom(room).getUser();
        user.getUserChat().setAddroom(null);
        userService.updateUser(user);
    }

    public static String[][] getAllUserDevices(Long room) {
        UserService userService = new UserService();
        User user = userService.findTelegramByRoom(room).getUser();
        List<Device> qdevices = user.getDevices();
        if (qdevices == null || qdevices.size() == 0) {
            return null;
        }
        int count = qdevices.size();
        int i = 0;
        String[][] devices = new String[count][3];
        for (Device device : qdevices) {
            devices[i][0] = device.getName();
            devices[i][1] = device.getId().toString();
            devices[i][2] = ((device.getRoom() == null) ? "n/a" : device.getRoom().getName());
            i++;
        }
        return devices;
    }

    public static void setDeviceToRoom(Long room_id, Long device_id) {
        DeviceService deviceService = new DeviceService();
        Device device = deviceService.findDevice(device_id);
        device.setRoom_id(room_id);
        deviceService.updateDevice(device);
    }

    public static String[][] getDevicesByRoomId(Long id) {
        RoomService roomService = new RoomService();
        Room room = roomService.findRoom(id);
        List<Device> qdevices = room.getDevices();
        if (qdevices == null || qdevices.size() == 0) {
            return null;
        }
        int count = qdevices.size();
        int i = 0;
        String[][] devices = new String[count][3];
        for (Device device : qdevices) {
            devices[i][0] = device.getId().toString();
            devices[i][1] = device.getName();
            i++;
        }
        roomService.close();
        return devices;
    }

    public static String[] getDeviceById(Long id) {
        DeviceService deviceService = new DeviceService();
        Device device = deviceService.findDevice(id);
        if (device == null) {
            return null;
        }
        String[] rdevice = new String[6];
        rdevice[0] = device.getName();
        rdevice[5] = device.getTopic();

        return rdevice;
    }

    public static String[] getDeviceByIdNew(Long id) {
        DeviceService deviceService = new DeviceService();
        Device device = deviceService.findDevice(id);
        if (device == null) {
            return null;
        }
        String[] rdevice = new String[6];
        rdevice[0] = device.getName();
        rdevice[5] = device.getTopic();

        return rdevice;
    }

    public static Command getCommand1(Long id) {
        CommandService commandService = new CommandService();
        //commandService.close();
        return commandService.findCommand(id);
    }
    public static Command getCommand(Session session, Long id) {
        CommandService commandService = new CommandService(session);
        //commandService.close();
        return commandService.findCommand(id);
    }
    public static String getDeviceName(Long id) {
        DeviceService deviceService = new DeviceService();
        return deviceService.findDevice(id).getName();
    }

    public static String[][] getCommadsByDeviceId(Long id) {
        DeviceService deviceService = new DeviceService();
        Device device = deviceService.findDevice(id);
        List<Command> commands = device.getCommands();

        if (device == null) {
            return null;
        }
        int i = 0;
        String[][] ret = new String[commands.size()][3];
        for (Command command : commands) {
            ret[i][0] = command.getId() + "";
            ret[i][1] = command.getText();
            ret[i][2] = command.getCommand();
            i++;
        }
        deviceService.close();
        return ret;
    }

    public static int accessTelegramByChatID(Long room) {
        UserService userService = new UserService();

        if (userService.checkTelegramByRoom(room)) {
            try {
                if (userService.findTelegramByRoom(room).getAccess())
                    return 1;
                return 0;
            } catch (NullPointerException e) {
                return -1;
            }
        } else {
            return -2;
        }
    }

}
