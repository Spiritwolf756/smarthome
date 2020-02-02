package mqtt;

import TelegramBot.Bot;
import TelegramBot.BotMain;
import main.Main;
import org.fusesource.hawtbuf.Buffer;
import org.fusesource.hawtbuf.UTF8Buffer;
import org.fusesource.mqtt.client.*;

import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class Mqtt {
    public static Mqtt INSTANCE;
    private MQTT mqtt;
    //private BotMain bot;

    public static Mqtt getINSTANCE() {
        if (INSTANCE == null) {
            try {
                INSTANCE = new Mqtt("", "", "", 1883);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return INSTANCE;
    }

    private Mqtt(String username, String password, String host, int port) throws Exception {
        mqtt = new MQTT();
        mqtt.setUserName(username);
        mqtt.setPassword(password);
        mqtt.setHost(host, port);
        final BlockingConnection publishConnection = mqtt.blockingConnection();
        publishConnection.connect();
        System.out.println("MQTT connected");
    }

    public String getData(String topic) throws Exception {
        final BlockingConnection publishConnection = mqtt.blockingConnection();
        publishConnection.connect();
        //подписываемся на опрашиваемый топик
        Topic[] topics = {new Topic(topic + "/in", QoS.AT_LEAST_ONCE)};
        publishConnection.subscribe(topics);
        //публикуем команду для получения данных от девайса
        publishConnection.publish(topic + "/out", "get".getBytes(), QoS.AT_LEAST_ONCE, false);

        //ждем ответ от девайса 2 секунды. Если ответа нет - оповещаем об этом
        String[] messageContent = new String[1];
        messageContent[0] = "n/a";
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    while (!Thread.currentThread().isInterrupted()) {
                        Message message = publishConnection.receive();
                        byte[] payload = message.getPayload();
                        messageContent[0] = new String(payload);

                        message.ack();
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();
        try {
            Thread.sleep(3000);
        } catch (
                InterruptedException e) {
            e.printStackTrace();
        }
        t.interrupt();
        publishConnection.unsubscribe(new String[]{topic + "/in"});
        return messageContent[0];
    }

    public void sendMSG(String topic, String text) throws Exception {
        final BlockingConnection publishConnection = mqtt.blockingConnection();
        publishConnection.connect();

        publishConnection.publish(topic, text.getBytes(), QoS.AT_LEAST_ONCE, false);
    }

    public void subListTopics(List<String> Ltopics) throws Exception {
        byte[] payload = new byte[1024 * 32];

        final BlockingConnection publishConnection = mqtt.blockingConnection();
        publishConnection.connect();

        Topic[] topics = new Topic[Ltopics.size()];
        for (int i = 0; i <= Ltopics.size(); i++) {
            topics[i] = new Topic(Ltopics.get(i), QoS.EXACTLY_ONCE);
        }
        publishConnection.subscribe(topics);

        int received = 0;
        for (int i = 0; i < topics.length; ++i) {
            Message message = publishConnection.receive();
            System.out.println(message);
            received++;
            payload = message.getPayload();
            String messageContent = new String(payload);
            System.out.println("Received message from topic: " + message.getTopic() + " Message content: " + messageContent);
            message.ack();
        }

        System.out.println("Should have received " + topics.length + " messages" + topics.length + received);
    }

    public void subAllTopics() throws Exception {
        byte[] payload = new byte[1024 * 32];

        final BlockingConnection publishConnection = mqtt.blockingConnection();
        publishConnection.connect();

        Topic[] topics = {new Topic("mqtt/#", QoS.AT_LEAST_ONCE)};
        publishConnection.subscribe(topics);

        int received = 0;
        for (int i = 0; i < topics.length; ++i) {
            Message message = publishConnection.receive();
            System.out.println(message);
            received++;
            payload = message.getPayload();
            String messageContent = new String(payload);
            System.out.println("Received message from topic: " + message.getTopic() + " Message content: " + messageContent);
            message.ack();
        }

        System.out.println("Should have received " + topics.length + " messages" + topics.length + received);
    }
}