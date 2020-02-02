package postgresql.services;

import org.hibernate.Session;
import postgresql.dao.DeviceDao;
import postgresql.dao.UserDao;
import postgresql.models.Device;
import postgresql.models.User;

import javax.swing.event.DocumentEvent;
import java.util.List;

public class DeviceService {

    private DeviceDao deviceDao;

    public DeviceService(Session session) {
        deviceDao = new DeviceDao(session);
    }
    public DeviceService() {
        deviceDao = new DeviceDao();
    }
    public void close(){
        deviceDao.close();
    }
    public Device findDevice(Long id) {
        return deviceDao.findById(id);
    }

    public void saveDevice(Device device) {
        deviceDao.save(device);
    }

    public void deleteDevice(Device device) {
        deviceDao.delete(device);
    }

    public void updateDevice(Device device) {
        deviceDao.update(device);
    }
    public Device getDevicesBySpecialname(String specialname){
        return deviceDao.getDevicesBySpecialname(specialname);
    }
    public List<Device> getAllDevices(){
        return deviceDao.findAll();
    }

    public String[][] getAllFreeUserDevices(long room){
        long userId = new UserDao().findTelegramByRoom(room).getUser().getId();
        List<Device> qdevices = deviceDao.getAllFreeUserDevices(userId);
        int count = qdevices.size();
        if (count == 0) {
            return null;
        }
        String[][] devices = new String[count][2];
        int i = 0;
        for (Device device : qdevices) {
            devices[i][0] = device.getId().toString();
            devices[i][1] = device.getName();
            i++;
        }
        return devices;
    }
    public void setDeviceToRoom(Long room_id, Long device_id) {
        Device device = findDevice(device_id);
        device.setRoom_id(room_id);
        updateDevice(device);
    }
}