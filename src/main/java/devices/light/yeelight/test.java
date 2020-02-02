package devices.light.yeelight;

public class test {
    public static void main(String[] args) {
        Yeelight yeelight = new Yeelight(1, false, "192.168.0.101");
        yeelight.swith("on");
    }
}
