package main;



import TelegramBot.BotMain;
import TelegramBot.Users;
import controller.Controller;
import controller.plannig.Daily;
import mqtt.Mqtt;



public class Main {
    static public void main(String[] args) {
        //запуск бота
        BotMain bot = new BotMain();
        //запуск блокирующей очереди
        Controller controller = Controller.getINSTANCE();
        controller.setBot(bot.getBot());
        controller.runThreads();
    }

}


