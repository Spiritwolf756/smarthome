package postgresql.models;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "devices")
public class Device {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    //private Long user_id;
    private String description;
    private String specialinstructions;
    private String name;
    private String topic;
    private String lastcommand;
    private Long user_id;
    private Long room_id;
    private String specialname;
    @ManyToOne()
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "room_id", insertable = false, updatable = false)
    private Room room;

    @OneToMany(mappedBy = "device", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER )
    private List<Command> commands;

    @OneToOne(mappedBy = "device")
    private UserChat userChat;

    public Device() {
        lastcommand = null;
        //name="Имя не установлено";
    }

    public Device(String name) {
        this.name = name;
    }

    public void setRoom_id(Long room_id) {
        this.room_id = room_id;
    }

    public long getRoom_id() {
        return room_id;
    }

    public Long getId() {
        return id;
    }

    public void setUser_id(Long user_id) {
        this.user_id = user_id;
    }

    public void setSpecialinstructions() {
        this.specialinstructions = specialinstructions;
    }

    public String getSpecialinstructions() {
        return specialinstructions;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getTopic() {
        return topic;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }


    public void setRoom(Room room) {
        this.room = room;
    }

    public Room getRoom() {
        return room;
    }

    public void setUserChat(UserChat userChat) {
        this.userChat = userChat;
    }

    public UserChat getUserChat() {
        return userChat;
    }

    public void setDescription(String descriprion) {
        this.description = descriprion;
    }

    public String getDescription() {
        return description;
    }

    public void setLastcommand(String lastcommand) {
        this.lastcommand = lastcommand;
    }

    public String  getLastcommand() {
        return lastcommand;
    }

    public Long getUser_id() {
        return user_id;
    }

    public void setSpecialname(String specialname) {
        this.specialname = specialname;
    }

    public String getSpecialname() {
        return specialname;
    }

    public void setCommands(List<Command> commands) {
        this.commands = commands;
    }

    public void addCommand(Command command) {
        commands.add(command);
    }

    public List<Command> getCommands() {
        return commands;
    }
    /*
    public String getMqttoff() {
        return mqttoff;
    }

    public String getMqtton() {
        return mqtton;
    }

    public String getTextoff() {
        return textoff;
    }

    public String getTexton() {
        return texton;
    }

    public void setMqtton(String mqtton) {
        this.mqtton = mqtton;
    }

    public void setMqttoff(String mqttoff) {
        this.mqttoff = mqttoff;
    }

    public void setTextoff(String textoff) {
        this.textoff = textoff;
    }

    public void setTexton(String texton) {
        this.texton = texton;
    }
*/
}
