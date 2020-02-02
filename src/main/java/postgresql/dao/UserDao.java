package postgresql.dao;

import org.hibernate.query.Query;
import postgresql.models.*;
import org.hibernate.Session;
import org.hibernate.Transaction;
import postgresql.services.UserService;
import postgresql.utils.HibernateSessionFactoryUtil;
import java.util.List;
import java.util.Queue;

public class UserDao {
    Session session;
    public UserDao(){
        session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
    }
    public UserDao(Session session){
        this.session=session;
    }

    public Session getSession() {
        return session;
    }

    public void close(){
        session.close();
    }
    public User findById(Long id) {
        return HibernateSessionFactoryUtil.getSessionFactory().openSession().get(User.class, id);
    }

    public void save(User user) {
        //Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        session.merge(user);
        Transaction tx1 = session.beginTransaction();
        session.save(user);
        tx1.commit();
        //session.close();
    }
    public void update(User user) {
        //Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        session.merge(user);
        Transaction tx1 = session.beginTransaction();
        session.update(user);
        tx1.commit();
        //session.close();
    }
    public void update(User user, int i) {
        //Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        Transaction tx1 = session.beginTransaction();
        session.update(user);
        tx1.commit();
    }
    public void delete(User user) {
        //Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        Transaction tx1 = session.beginTransaction();
        session.delete(user);
        tx1.commit();
        session.close();
    }
    public Telegram findTelegramById(Long id){
        return HibernateSessionFactoryUtil.getSessionFactory().openSession().get(Telegram.class, id);
    }
    public Telegram findTelegramByTelegramId(Long telegramid){
        return HibernateSessionFactoryUtil.getSessionFactory().openSession().get(Telegram.class, telegramid);
    }
    public AtHome findAtHomeById(Long id){
        return HibernateSessionFactoryUtil.getSessionFactory().openSession().get(AtHome.class, id);
    }
    public Telegram findTelegramByRoom(Long room){
        Telegram telegrams = (Telegram)  session.createQuery("from Telegram where telegram_room = :room").setParameter("room", room).getSingleResult();
        //session.close();
        return telegrams;
    }
    public void findTelegramByRoom2(Long room, String text){
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        UserService userService=new UserService();
        Room room1 = new Room(text);
        Telegram telegrams = (Telegram)  session.createQuery("from Telegram where telegram_room = :room").setParameter("room", room).getSingleResult();
        telegrams.getUser().addRoom(room1);
        session.close();
    }
    public boolean addRoomFromTelegram(Long room, String text){
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        UserService userService=new UserService();
        Telegram telegrams = (Telegram)  session.createQuery("from Telegram where telegram_room = :room").setParameter("room", room).getSingleResult();
        telegrams.getUser().getUserChat().setAddroom(text);
        userService.updateUser(telegrams.getUser());
        session.close();
        return true;
    }
    public boolean addRoomFromTelegram(Long room){
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        UserService userService=new UserService();
        Telegram telegrams = (Telegram)  session.createQuery("from Telegram where telegram_room = :room").setParameter("room", room).getSingleResult();
        telegrams.getUser().getUserChat().setAddroom(null);
        userService.updateUser(telegrams.getUser());
        session.close();
        return true;
    }
    public Boolean checkTelegramByRoom(Long room){
        Query telegram = HibernateSessionFactoryUtil.getSessionFactory().openSession().createSQLQuery("select exists (select 1 from telegrams where telegram_room = :room)").setParameter("room", room);
        return Boolean.valueOf(telegram.getResultList().get(0).toString());
    }
    public Boolean checkTelegramByPhone(String phone){
        Query telegram = HibernateSessionFactoryUtil.getSessionFactory().openSession().createSQLQuery("select exists (select 1 from telegrams where phone = :phone)").setParameter("phone", phone);
        return Boolean.valueOf(telegram.getResultList().get(0).toString());
    }
    public List<Telegram> findTelegramByRoom1(Long room){
        List<Telegram> telegrams = (List<Telegram>)  HibernateSessionFactoryUtil.getSessionFactory().openSession().createQuery("from Telegram where telegram_room = :room").setParameter("room", room).list();
        return telegrams;
    }

    public List<User> findAllAdmins(){
            List<User> users = (List<User>) HibernateSessionFactoryUtil.getSessionFactory().openSession().createQuery("from User where isadmin=:admin").setParameter("admin", true).list();
            return users;
    }
    public List<User> findAll() {
        List<User> users = (List<User>)  HibernateSessionFactoryUtil.getSessionFactory().openSession().createQuery("From User").list();
        return users;
    }
    public void delRoomById(Long id){
        Transaction tx1 = session.beginTransaction();
        session.delete(session.get(Room.class, id));
        tx1.commit();
        session.close();
    }
    public List<Device> getAllFreeUserDevices(){
        List<Device> Devices = (List<Device>)  HibernateSessionFactoryUtil.getSessionFactory().openSession().createQuery("From Device as D where D.room_id is null").list();
        return Devices;
    }

    public List<Device> getDevicesBySpecialname(String specialname){
        List<Device> Devices = (List<Device>)  HibernateSessionFactoryUtil.getSessionFactory().openSession().createQuery("From Device as D where D.specialname=:specialname and D.user_id is null").setParameter("specialname", specialname).list();
        return Devices;
    }
}
