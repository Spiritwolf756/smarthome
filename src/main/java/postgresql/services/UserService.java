package postgresql.services;

import org.hibernate.Session;
import postgresql.dao.UserDao;
import postgresql.models.AtHome;
import postgresql.models.Device;
import postgresql.models.User;
import postgresql.models.Telegram;


import java.util.List;

public class UserService {

    private UserDao usersDao;

    public UserService() {
        usersDao = new UserDao();
    }

    public UserService(Session session) {
        usersDao = new UserDao(session);
    }

    public Session getSession() {
        return usersDao.getSession();
    }

    public void userDaoClose() {
        usersDao.close();
    }

    public User findUser(Long id) {
        return usersDao.findById(id);
    }

    public void saveUser(User user) {
        usersDao.save(user);
    }

    public void deleteUser(User user) {
        usersDao.delete(user);
    }

    public void updateUser(User user) {
        usersDao.update(user);
    }

    public List<User> findAllUsers() {
        return usersDao.findAll();
    }

    public Telegram findTelegramByRoom(Long room) {
        return usersDao.findTelegramByRoom(room);
    }

    public boolean addRoomFromTelegram(Long room, String text) {
        return usersDao.addRoomFromTelegram(room, text);
    }

    public boolean addRoomFromTelegram(Long room) {
        return usersDao.addRoomFromTelegram(room);
    }

    public Boolean checkTelegramByRoom(Long room) {
        return usersDao.checkTelegramByRoom(room);
    }

    public Boolean checkTelegramByPhone(String phone) {
        return usersDao.checkTelegramByPhone(phone);
    }

    public AtHome findAtHomeById(Long id) {
        return usersDao.findAtHomeById(id);
    }

    public List<User> findAllAdmins() {
        return usersDao.findAllAdmins();
    }

    public Telegram findTelegramByTelegramId(Long telegramid) {
        return usersDao.findTelegramByTelegramId(telegramid);
    }

    public Telegram findTelegramById(Long id) {
        return usersDao.findTelegramById(id);
    }

    public void findTelegramByRoom2(Long room, String text) {
        usersDao.findTelegramByRoom(room);
    }

    public void delRoomById(Long id) {
        usersDao.delRoomById(id);
    }

    public List<Device> getAllFreeUserDevices() {
        return usersDao.getAllFreeUserDevices();
    }

    public List<Device> getDevicesBySpecialname(String specialname) {
        return usersDao.getDevicesBySpecialname(specialname);
    }

    public boolean addRoomDevFromTelegram(long room, String text) {
        Telegram telegram = findTelegramByRoom(room);
        telegram.getUser().getUserChat().setAddroom(text);
        updateUser(telegram.getUser());
        return true;
    }

    public String isAddRoom(Long room) {
        Telegram telegram = findTelegramByRoom(room);
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

    public String getDevicesBySpecialname(Long room, String specialname) {
        List<Device> devices = getDevicesBySpecialname(specialname);
        if (devices == null || devices.size() == 0) {
            return null;
        } else {
            Device device = devices.get(0);
            User user = findTelegramByRoom(room).getUser();
            return addDeviceToUser(user, device);
        }
    }

    private String addDeviceToUser(User user, Device device) {
        user.getUserChat().setAddroom(null);
        user.addDevice(device);
        //device.setUser(user);
        usersDao.update(user, 1);

        device.setUser(user);
        new DeviceService(usersDao.getSession()).updateDevice(device);
        return device.getName();
    }

    public long findUserIdByTelegramRoom(long room) {
        return findTelegramByRoom(room).getUser().getId();
    }
}