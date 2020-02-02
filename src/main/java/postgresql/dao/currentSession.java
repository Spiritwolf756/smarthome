package postgresql.dao;

import org.hibernate.Session;
import org.hibernate.Transaction;
import postgresql.utils.HibernateSessionFactoryUtil;

public class currentSession {
    private Session currentSession;

    private Transaction currentTransaction;

    public Session openCurrentSession() {
        currentSession = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        return currentSession;
    }

    public Session openCurrentSessionwithTransaction() {
        currentSession = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        currentTransaction = currentSession.beginTransaction();
        return currentSession;
    }

    public void closeCurrentSession() {
        currentSession.close();
    }

    public void closeCurrentSessionwithTransaction() {
        currentTransaction.commit();
        currentSession.close();
    }

    public void update(Object o) {
        this.currentSession.update(o);
    }
}
