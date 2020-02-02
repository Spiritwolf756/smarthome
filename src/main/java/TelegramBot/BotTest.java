package TelegramBot;

import org.junit.Before;

import static org.junit.Assert.*;

public class BotTest {
    private Bot bot = new BotMain().getBot();
    long chatId = 123456789;
    String clbId = "132";
    long msgId = 987654321;
    String roomId = "456";
    String roomName = "room";
    String text = "text";
    boolean access = false;
    String firstname = "Dima";
    String lastname = "Volkov";
    String username = "DimaVolkov";
    String phone = "+79110102785";
    boolean isAdmin = true;
    String deviceId = "123";
    String deviceName = "device";
    String[] cbt = new String[]{"0", "1", "2"};
    String cbqId = "2";

    @Before
    public void setUp() {
        //bot = new BotMain().getBot();
    }

    @org.junit.Test
    public void showDevicesTask() throws InterruptedException {
        assertEquals("{\"controller\":\"showDevices\",\"task\":\"controller\",\"chatId\":123456789}", bot.showDevicesTask(chatId));
    }

    @org.junit.Test
    public void delRoomTask() throws InterruptedException {
        assertEquals("{\"clbId\":\"132\",\"controller\":\"delRoom\",\"rooms\":[{\"name\":\"room\",\"id\":\"456\"}],\"task\":\"controller\",\"chatId\":123456789,\"msgId\":987654321}", bot.delRoomTask(chatId, clbId, msgId, roomId, roomName));
    }

    @org.junit.Test
    public void setRoomTask() throws InterruptedException {
        assertEquals("{\"controller\":\"setRoom\",\"rooms\":[{\"name\":\"text\"}],\"task\":\"controller\",\"chatId\":123456789}", bot.setRoomTask(chatId, text));
    }

    @org.junit.Test
    public void setUserAccess() throws InterruptedException {
        assertEquals("{\"controller\":\"setUserAccess\",\"task\":\"controller\",\"chatId\":123456789,\"users\":[{\"access\":false}]}", bot.setUserAccess(chatId, access));
    }

    @org.junit.Test
    public void createUserTask() throws InterruptedException {
        assertEquals("{\"controller\":\"createUser\",\"task\":\"controller\",\"chatId\":123456789,\"users\":[{\"firstname\":\"Dima\",\"access\":true,\"phone\":\"+79110102785\",\"admin\":true,\"lastname\":\"Volkov\",\"username\":\"DimaVolkov\"}]}", bot.createUserTask(chatId, firstname, lastname, username, phone, isAdmin, true));
    }

    @org.junit.Test
    public void connectDeviceToRoomTask() throws InterruptedException {
        assertEquals("{\"controller\":\"connectDeviceToRoom\",\"rooms\":[{\"name\":\"room\",\"id\":\"456\"}],\"task\":\"controller\",\"chatId\":123456789,\"devices\":[{\"name\":\"device\",\"id\":\"123\"}],\"msgId\":987654321}", bot.connectDeviceToRoomTask(chatId, msgId, deviceId, deviceName, roomId, roomName));
    }

    @org.junit.Test
    public void freeDevicesTask() throws InterruptedException {
        assertEquals("{\"controller\":\"freeDevices\",\"rooms\":[{\"name\":\"2\",\"id\":\"1\"}],\"task\":\"controller\",\"chatId\":123456789,\"msgId\":987654321}", bot.freeDevicesTask(chatId, msgId, cbt[1], cbt[2]));
    }

    @org.junit.Test
    public void deviceCommandTask() throws InterruptedException {
        assertEquals("{\"clbId\":\"2\",\"controller\":\"devicesCommands\",\"task\":\"controller\",\"chatId\":123456789,\"devices\":[{\"commands\":[{\"id\":2}]}]}", bot.deviceCommandTask(chatId, Integer.parseInt(cbt[2]), cbqId));
    }

    @org.junit.Test
    public void devicesDetailsTask() throws InterruptedException {
        assertEquals("{\"controller\":\"devicesDetails\",\"task\":\"controller\",\"chatId\":123456789,\"devices\":[{\"id\":2}]}", bot.devicesDetailsTask(chatId, Integer.parseInt(cbt[2])));
    }

    @org.junit.Test
    public void devicesTask() throws InterruptedException {
        assertEquals("{\"controller\":\"devices\",\"rooms\":[{\"id\":2}],\"task\":\"controller\",\"chatId\":123456789}", bot.devicesTask(chatId, Integer.parseInt(cbt[2])));
    }

    @org.junit.Test
    public void weatherTask() throws InterruptedException {
        assertEquals("{\"controller\":\"weather-0\",\"task\":\"controller\",\"chatId\":123456789,\"weather\":[{\"cityId\":536203}]}", bot.weatherTask(chatId, 536203, 0));
    }
}