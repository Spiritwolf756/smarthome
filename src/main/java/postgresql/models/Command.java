package postgresql.models;

import javax.persistence.*;

@Entity
@Table(name = "commands")
public class Command {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String text;
    private String command;

    @ManyToOne(fetch = FetchType.EAGER)
    //@ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "device_id")
    private Device device;

    public Command() {
    }

    public Long getId() {
        return id;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getCommand() {
        return command;
    }

    public Device getDevice() {
        return device;
    }

    @Override
    public String toString() {
        return "";
    }
}
