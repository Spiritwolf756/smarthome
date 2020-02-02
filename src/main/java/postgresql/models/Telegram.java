package postgresql.models;

import javax.persistence.*;

@Entity
@Table(name = "telegrams")
public class Telegram {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long telegram_room;
    private Boolean access;
    private String firstname;
    private String lastname;
    private String username;
    private String phone;

    @OneToOne()

    @JoinColumn (name = "user_id")
    private User user;

    public Telegram(){

    }

    public Telegram(Long telegram_room, Boolean access, String firstname, String lastname, String username, String phone){
        this.telegram_room=telegram_room;
        this.access=access;
        this.firstname=firstname;
        this.lastname=lastname;
        this.username=username;
        this.phone=phone;
    }

    public Long getId(){
        return id;
    }

    public Long getTelegram_room(){
        return telegram_room;
    }
    public Boolean getAccess(){
        return access;
    }
    public void setTelegram_room(Long telegram_room){
        this.telegram_room=telegram_room;
    }
    public void setAccess(Boolean access){
        this.access=access;
    }
    public User getUser() {
        return user;
    }
    public void setUser(User user){
        this.user=user;
    }
    public void setFirstname(String firstname){
        this.firstname=firstname;
    }
    public String getFirstname(String firstname){
        return firstname;
    }
    public void setLastname(String lastname){
        this.lastname=lastname;
    }
    public String getLastname(String Lastname){
        return Lastname;
    }
    public void setUsername(String username){
        this.username=username;
    }
    public String getUsername(String username){
        return username;
    }
    public void setPhone(String phone){
        this.phone=phone;
    }
    public String getPhone(String phone){
        return phone;
    }

    @Override
    public String toString(){
        return telegram_room + " " + access.toString();
    }
}
