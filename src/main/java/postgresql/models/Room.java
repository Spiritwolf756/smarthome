package postgresql.models;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table (name="rooms")
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;


    @OneToMany(mappedBy = "room", orphanRemoval = true)
    private List<UserChat> userChats;

    @OneToMany(mappedBy = "room")
    private List<Device> devices;

    public Room(){
    }

    public Room(String name){
        this.name=name;
    }
    public Long getId(){
        return id;
    }

    public String getName(){
        return name;
    }
    public void setName(Boolean access){
        this.name=name;
    }

      public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }
    public void addUserChat(UserChat userChat){
        userChats.add(userChat);
    }

    public void addDevices(Device devices) {
        this.devices.add(devices);
    }

    public List<Device> getDevices(){
        return devices;
    }

    @Override
    public String toString(){
        return "toString не настроен";
    }
    @PreRemove
    public void preRemove(){
        for(Device device : devices){
            device.setRoom_id(null);
        }
    }

}
