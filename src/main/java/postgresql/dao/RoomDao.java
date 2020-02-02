package postgresql.dao;

import org.hibernate.Session;
import org.hibernate.Transaction;
import postgresql.models.Device;
import postgresql.models.Room;
import postgresql.utils.HibernateSessionFactoryUtil;

import java.util.List;

public class RoomDao {
    Session session;
    public RoomDao(){
        session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
    }
    public RoomDao(Session session){
        this.session = session;
    }
    public void close(){
        session.close();
    }

    public Room findById(Long id) {
        //Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        Room room=session.get(Room.class, id);
        //session.close();
        return room;
    }

    public void save(Room room) {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        Transaction tx1 = session.beginTransaction();
        session.save(room);
        tx1.commit();
        session.close();
    }
    public void update(Room room) {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        Transaction tx1 = session.beginTransaction();
        session.update(room);
        tx1.commit();
        session.close();
    }
    public void delete(Room room) {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        Transaction tx1 = session.beginTransaction();
        session.delete(room);
        tx1.commit();
        session.close();
    }
    public List<Room> findAll() {
        List<Room> rooms = (List<Room>)  HibernateSessionFactoryUtil.getSessionFactory().openSession().createQuery("From Room").list();
        return rooms;
    }
}
