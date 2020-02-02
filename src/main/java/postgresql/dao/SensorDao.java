package postgresql.dao;

import org.hibernate.Session;
import org.hibernate.Transaction;
import postgresql.models.Command;
import postgresql.models.Sensor;
import postgresql.utils.HibernateSessionFactoryUtil;

import java.util.List;

public class SensorDao {
    Session session;

    public SensorDao() {
        session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
    }

    public void close() {
        session.close();
    }

    public Sensor findById(Long id) {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        Sensor sensor = session.get(Sensor.class, id);
        //session.close();
        return sensor;
    }

    public void save(Sensor sensor) {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        Transaction tx1 = session.beginTransaction();
        session.save(sensor);
        tx1.commit();
        session.close();
    }

    public void update(Sensor sensor) {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        Transaction tx1 = session.beginTransaction();
        session.update(sensor);
        tx1.commit();
        session.close();
    }

    public void delete(Sensor sensor) {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        Transaction tx1 = session.beginTransaction();
        session.delete(sensor);
        tx1.commit();
        session.close();
    }

    public List<Sensor> findAll() {
        List<Sensor> sensors = (List<Sensor>) HibernateSessionFactoryUtil.getSessionFactory().openSession().createQuery("From Command").list();
        return sensors;
    }
}
