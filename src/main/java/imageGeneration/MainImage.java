package imageGeneration;

import com.google.inject.internal.asm.$Attribute;
import controller.weather.Weather;
import controller.weather.WeatherJSONBody;
import main.Main;
import mqtt.Mqtt;
import sensors.WeatherOutside;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

public class MainImage {
    private WeatherOutside weather;
    String path = "src/main/resources/weather/";

    public MainImage() {
    }

    public MainImage(WeatherOutside weather) {
        this.weather = weather;
    }

    /**
     * @param cityId
     * @param nightRul -1 - управляется автоматически, 0 - день, 1 - ночь
     * @return
     * @throws IOException
     */
    public File getWeather(long cityId, int nightRul) throws IOException {
        String partOfPath;

        //получение погоды в городе
        WeatherJSONBody weather = new Weather().getWeatherString(cityId);
        Long now = new Date().getTime();
        //now sunset sunrise - ночь
        //sunset now sunrise - день
        // sunset sunrise now - ночь
        boolean night = false;
        if (nightRul == -1) {
            night = (now < Long.parseLong(weather.sys.sunset) * 1000 && now < Long.parseLong(weather.sys.sunrise) * 1000) || (now > Long.parseLong(weather.sys.sunset) * 1000 && now > Long.parseLong(weather.sys.sunrise) * 1000);
        } else if (nightRul == 1) {
            night = true;
        }
        if (night) {
            partOfPath = "night/";
        } else {
            partOfPath = "day/";
        }
        File backgroundFie = new File(path + partOfPath + "background.png");

        BufferedImage background = ImageIO.read(backgroundFie);
        Graphics2D out = background.createGraphics();
        if (night) {
            out.setColor(Color.CYAN);
        } else {
            out.setColor(Color.ORANGE);
        }
        out.setFont(new Font("Default", Font.BOLD, 20)); //Georgia

        //получение погоды в квартире
        String[] weatherinside = new String[2];
        try {
            weatherinside = Mqtt.getINSTANCE().getData("mqtt/room/temperature").split(";", 2);
        } catch (Exception e) {
            weatherinside[0] = "n/a";
            weatherinside[1] = "n/a";
            //e.printStackTrace();
        }
        //рисуем погоду улицы
        out.drawString(absAndTransfer(weather.main.temp) + " C", 110, 140);
        out.drawString(absAndTransfer(weather.main.humidity) + " %", 110, 200);
        out.drawString(absAndTransfer(weather.wind.speed) + " m/s", 110, 250);

        //добавляем иконку погоды
        System.out.println(weather.weather[0].description);
        File img = null;
        if ("few cloud".equals(weather.weather[0].description)) {
            img = new File(path + partOfPath + "few_cloud.png");
        }
        if ("scattered clouds".equals(weather.weather[0].description)) {
            img = new File(path + partOfPath + "scattered_clouds.png");
        }
        if ("broken clouds".equals(weather.weather[0].description) ||
                "overcast clouds".equals(weather.weather[0].description)) {
            img = new File(path + partOfPath + "broken_clouds.png");
        }
        if ("light intensity shower rain".equals(weather.weather[0].description) ||
                "moderate rain".equals(weather.weather[0].description) ||
                "light rain".equals(weather.weather[0].description)) {
            img = new File(path + partOfPath + "light_intensity_shower_rain.png");
        }
        if (img != null)
            out.drawImage(ImageIO.read(img), 42, 275, null);

        //рисуем погоду дома
        out.drawString(absAndTransfer(weatherinside[0]) + " C", 388, 140);
        out.drawString(absAndTransfer(weatherinside[1]) + " %", 388, 200);

        ImageIO.write(background, "png", new File("export.jpg"));
        return new File("export.jpg");
    }

    private String absAndTransfer(String str) {
        if ("n/a".equals(str))
            return str;
        return String.valueOf(Math.round(Double.parseDouble(str) * 10) / 10);
    }

    public static File joke(File file) throws IOException {
        BufferedImage img = ImageIO.read(file);

        Graphics2D out = (Graphics2D) img.createGraphics();

        out.setFont(new Font("Georgia", Font.BOLD, 50));
        out.setColor(Color.GREEN);
        //FontMetrics fm=out.getFontMetrics();
        out.drawString(" ", 380, 680);

        ImageIO.write(img, "png", new File("export.jpg"));
        return new File("export.jpg");
    }
}
