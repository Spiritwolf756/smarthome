package postgresql.services;

import postgresql.dao.CommandDao;
import postgresql.dao.SensorDao;
import postgresql.models.Command;
import postgresql.models.Sensor;

import java.util.List;

public class SensorService {
    private SensorDao sensorDao = new SensorDao();

    public SensorService() {
    }
    public void close(){
        sensorDao.close();
    }
    public Sensor findSensor(Long id) {
        return sensorDao.findById(id);
    }

    public void saveSensor(Sensor command) {
        sensorDao.save(command);
    }

    public void deleteSensor(Sensor command) {
        sensorDao.delete(command);
    }

    public void updateSensor(Sensor command) {
        sensorDao.update(command);
    }
    public List<Sensor> getAllSensors(){
        return sensorDao.findAll();
    }
}
