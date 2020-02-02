package postgresql.services;

import org.hibernate.Session;
import postgresql.dao.RoomDao;
import postgresql.models.Room;


public class RoomService {

    private RoomDao roomDao;

    public RoomService() {
        roomDao = new RoomDao();
    }
    public RoomService(Session session) {
        roomDao = new RoomDao(session);
    }
    public Room findRoom(Long id) {
        return roomDao.findById(id);
    }

    public void saveRoom(Room room) {
        roomDao.save(room);
    }

    public void deleteRoom(Room room) {
        roomDao.delete(room);
    }

    public void updateRoom(Room room) {
        roomDao.update(room);
    }
    public void close(){
        roomDao.close();
    }

}