package postgresql.dao;

import org.hibernate.Session;
import org.hibernate.Transaction;
import postgresql.models.Command;
import postgresql.models.Device;
import postgresql.utils.HibernateSessionFactoryUtil;

import java.util.List;

public class CommandDao {
    Session session;
    public CommandDao(){
        session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
    }
    public CommandDao(Session session){
        this.session=session;
    }
    public void close(){
        session.close();
    }

    public Command findById(Long id) {
        //Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        Command command=session.get(Command.class, id);
        //session.close();
        return command;
    }

    public void save(Command command) {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        Transaction tx1 = session.beginTransaction();
        session.save(command);
        tx1.commit();
        session.close();
    }
    public void update(Command command) {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        Transaction tx1 = session.beginTransaction();
        session.update(command);
        tx1.commit();
        session.close();
    }
    public void delete(Command command) {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        Transaction tx1 = session.beginTransaction();
        session.delete(command);
        tx1.commit();
        session.close();
    }
    public List<Command> findAll() {
        List<Command> commands = (List<Command>)  HibernateSessionFactoryUtil.getSessionFactory().openSession().createQuery("From Command").list();
        return commands;
    }
}
