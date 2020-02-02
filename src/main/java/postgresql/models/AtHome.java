package postgresql.models;

import javax.persistence.*;

@Entity
@Table(name = "athome")
public class AtHome {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //@Column (name = "telegram_room")
    //private int ;

    private Boolean at_home;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id")
    private Device device_id;

    public AtHome(){

    }

    public AtHome(Boolean at_home){
        this.at_home=at_home;
    }

    public Long getId(){
        return id;
    }

    public Boolean getAt_home(){
        return at_home;
    }

    public void setAt_home(Boolean at_home){
        this.at_home=at_home;
    }


    @Override
    public String toString(){
        return Boolean.toString(at_home);
    }
}
