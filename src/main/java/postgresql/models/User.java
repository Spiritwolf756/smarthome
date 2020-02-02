package postgresql.models;


import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Boolean access;
    private Boolean isAdmin;
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)

    private Telegram telegram;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private UserChat userChat;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AtHome> atHomes;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Room> rooms;

    @OneToMany(mappedBy = "user", cascade = CascadeType.MERGE)
    private List<Device> devices;

    @OneToMany(mappedBy = "user")
    private List<Sensor> sensors;

    public User() {
        atHomes = new ArrayList<>();
        isAdmin = false;
    }

    public User(Boolean access, Boolean isAdmin) {
        this.access = access;
        this.isAdmin = isAdmin;
        atHomes = new ArrayList<>();
    }

    public User(Boolean isAdmin) {
        this.isAdmin = isAdmin;
        atHomes = new ArrayList<>();
    }

    public Long getId() {
        return id;
    }

    public Boolean getAccess() {
        return access;
    }

    public void setAccess(Boolean access) {
        this.access = access;
    }

    //работа с таблицей Telegram
    public void setTelegram(Telegram telegram) {
        telegram.setUser(this);
        //telegrams.add(telegram);
        this.telegram = telegram;
    }

    public Telegram getTelegram() {
        return telegram;
    }

    public void setUserChat(UserChat userChat) {
        this.userChat = userChat;
    }

    public UserChat getUserChat() {
        return userChat;
    }

    public void setTelegrams(Telegram telegram) {
        telegram.setUser(this);
        this.telegram = telegram;
    }

    //работа с таблицей AtHome

    public List<AtHome> getAtHome() {
        return atHomes;
    }

    public void setAtHome(List<AtHome> atHome) {
        atHomes = atHome;
    }

    public void setIsAdmin(Boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    public Boolean getIsAdmin() {
        return isAdmin;
    }

    public void addRoom(Room room) {
        room.setUser(this);
        rooms.add(room);
    }

    public List<Room> getRooms() {
        return rooms;
    }

    public void addDevice(Device device) {
        this.devices.add(device);
    }

    public List<Device> getDevices() {
        return devices;
    }

    @Override
    public String toString() {
        return "postgresql.models.User{" +
                "id=" + id + "}";
    }
}
