package TelegramBot;

import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.ApiContext;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.net.Authenticator;
import java.net.PasswordAuthentication;

public class BotMain {

    private static String BOT_NAME = "SmartHome";
    private static String BOT_TOKEN = ""; /* your bot's token here */

    private static String PROXY_HOST = ""; /* proxy host */
    private static Integer PROXY_PORT = 123456; /* proxy port */
    private static String PROXY_USER = "";
    private static String PROXY_PASSWORD = "";
    Bot bot;

    public Bot getBot() {
        return bot;
    }

    public BotMain() {
        try {
            Authenticator.setDefault(new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(PROXY_USER, PROXY_PASSWORD.toCharArray());
                }
            });
            ApiContextInitializer.init();
            TelegramBotsApi botsApi = new TelegramBotsApi();

            // Set up Http proxy
            DefaultBotOptions botOptions = ApiContext.getInstance(DefaultBotOptions.class);

            botOptions.setProxyHost(PROXY_HOST);
            botOptions.setProxyPort(PROXY_PORT);
            // Select proxy type: [HTTP|SOCKS4|SOCKS5] (default: NO_PROXY)
            botOptions.setProxyType(DefaultBotOptions.ProxyType.SOCKS5);

            bot = new Bot(botOptions);

            botsApi.registerBot(bot);

        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
