package postgresql.dao;

import org.hibernate.Session;
import org.hibernate.Transaction;
import postgresql.models.Device;
import postgresql.utils.HibernateSessionFactoryUtil;

import java.util.List;

public class DeviceDao {
    Session session;
    public DeviceDao(){
        session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        //session = HibernateSessionFactoryUtil.getSessionFactory().getCurrentSession();
    }
    public DeviceDao(Session session){
        this.session=session;
    }
    public void close(){
        session.close();
    }

    public Device findById(Long id) {
        //Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        //session.close();
        return session.get(Device.class, id);
    }

    public void save(Device device) {
//        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        Transaction tx1 = session.beginTransaction();
        session.save(device);
        tx1.commit();
        session.close();
    }
    public void update(Device device) {
        //Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        session.merge(device);
        Transaction tx1 = session.beginTransaction();
        session.update(device);
        tx1.commit();
        //session.close();
    }
    public void delete(Device device) {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        Transaction tx1 = session.beginTransaction();
        session.delete(device);
        tx1.commit();
        session.close();
    }
    public List<Device> findAll() {
        List<Device> devices = (List<Device>)  HibernateSessionFactoryUtil.getSessionFactory().openSession().createQuery("From Device").list();
        return devices;
    }
    public List<Device> getAllFreeUserDevices(long user_id){
        List<Device> Devices = (List<Device>)  HibernateSessionFactoryUtil.getSessionFactory().openSession().createQuery("From Device as D where D.user_id=:user_id and D.room_id is null").setParameter("user_id", user_id) .list();
        return Devices;
    }
    public Device getDevicesBySpecialname(String specialname){
        List<Device> devices = (List<Device>)  HibernateSessionFactoryUtil.getSessionFactory().openSession().createQuery("From Device as D where D.specialname=:specialname and D.user_id is null").setParameter("specialname", specialname).list();
        if (devices.size()==0)
            return null;
        return devices.get(0);
    }
}
