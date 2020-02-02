package postgresql.services;

import org.hibernate.Session;
import postgresql.utils.HibernateSessionFactoryUtil;
import postgresql.dao.CommandDao;
import postgresql.dao.DeviceDao;
import postgresql.models.Command;
import postgresql.models.Device;

import java.util.List;
import java.util.ServiceConfigurationError;

public class CommandService {
    private CommandDao commandDao;

    public CommandService() {
        commandDao = new CommandDao();
    }
    public CommandService(Session session) {
        commandDao = new CommandDao(session);
    }
    public void close(){
        commandDao.close();
    }
    public Command findCommand(Long id) {
        return commandDao.findById(id);
    }

    public void saveCommand(Command command) {
        commandDao.save(command);
    }

    public void deleteCommand(Command command) {
        commandDao.delete(command);
    }

    public void updateCommand(Command command) {
        commandDao.update(command);
    }
    public List<Command> getAllCommands(){
        return commandDao.findAll();
    }
}
