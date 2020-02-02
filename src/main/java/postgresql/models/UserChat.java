package postgresql.models;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="userchats")
public class UserChat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String addroom;
    private String adddevice;
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "room_id")
    private Room room;

    @OneToOne(optional = true)
    @JoinColumn (name = "user_id")
    private User user;

    @OneToOne()
    @JoinColumn(name="device_id")
    private Device device;

    public UserChat() {
    }

    public UserChat(String addroom) {
        this.addroom = addroom;
    }

    public Long getId() {
        return id;
    }
    public void setAddroom(String addroom){
        this.addroom = addroom;
    }
    public String getAddroom(){
        return addroom;
    }
    public User getUser() {
        return user;
    }
    public void setUser(User user){
        this.user=user;
    }
    public User getRoom() {
        return user;
    }
    public void setRoom(User user){
        this.user=user;
    }
    public Device getDevice(){
        return device;
    }
    public void setDevice(Device device){
        this.device=device;
    }
    public String getAdddevice(){
        return adddevice;
    }
    public void setAdddevice(String adddevice) {
        this.adddevice = adddevice;
    }
}


