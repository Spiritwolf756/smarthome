package controller.weather;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import controller.Tasks;
import controller.jsonBody.FileDecorator;
import controller.jsonBody.TaskJSONBody;
import imageGeneration.MainImage;
import org.json.JSONObject;
import org.jsoup.Jsoup;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Weather {
    private long cityID;

    public Weather(Long CityID) {
        this.cityID = CityID;
   }

    public Weather() {

    }

    public static void weatherImg(String  task){
        String json;
        String url;
        //парсим полученный json
        Gson gson = new GsonBuilder().serializeNulls().create();
        TaskJSONBody taskJSONBody = gson.fromJson(task, TaskJSONBody.class);

        try {
            File file = new MainImage().getWeather(Long.parseLong(taskJSONBody.weather[0].cityId), -1);
            Tasks.getINSTANCE().getOutgoingTasks().put(new FileDecorator().set(file).set(taskJSONBody.chatId));
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

    }
    //если хотим управлять визуализацией день/ночь самостоятельно
    public static void weatherImg(String  task, int night){
        String json;
        String url;
        //парсим полученный json
        Gson gson = new GsonBuilder().serializeNulls().create();
        TaskJSONBody taskJSONBody = gson.fromJson(task, TaskJSONBody.class);

        try {
            File file = new MainImage().getWeather(Long.parseLong(taskJSONBody.weather[0].cityId), night);
            Tasks.getINSTANCE().getOutgoingTasks().put(new FileDecorator().set(file).set(taskJSONBody.chatId));
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

    }

    public static void weather(String task) throws InterruptedException {
        String json;
        String url;
        //парсим полученный json
        Gson gson = new GsonBuilder().serializeNulls().create();
        TaskJSONBody taskJSONBody = gson.fromJson(task, TaskJSONBody.class);
        //создаем json-ответ
        JSONObject jo = new JSONObject();
        List<JSONObject> weather = new ArrayList<>();
        //запрос погоды с сервера
        url = "http://api.openweathermap.org/data/2.5/weather?id=" + taskJSONBody.weather[0].cityId + "&units=metric&appid=7a5cf9844eb010197dc9c03069ccc77f";
        System.out.println(url);
        try {
            json = Jsoup.connect(url).userAgent("Opera/9.80 (Macintosh; Intel Mac OS X 10.14.1) Presto/2.12.388 Version/12.16").ignoreContentType(true).execute().body();
            //парсинг полученной погоды
            gson = new GsonBuilder().serializeNulls().create();
            WeatherJSONBody weatherJSONBody = gson.fromJson(json, WeatherJSONBody.class);
            //заполняем json-ответ
            weather.add(new JSONObject().put("sityId", taskJSONBody.weather[0].cityId)
                    .put("description", weatherJSONBody.weather[0].description)
                    .put("temp", weatherJSONBody.main.temp)
                    .put("wind", weatherJSONBody.wind.speed));
            jo.put("chatId", taskJSONBody.chatId)
                    .put("task", "view")
                    .put("view", "weather")
                    .put("weather", weather);
        } catch (IOException e) {
            //заполняем json-ответ
             weather.add(new JSONObject().put("sityId", taskJSONBody.weather[0].cityId)
                    .put("description", "n/a")
                    .put("temp", "n/a")
                    .put("wind", "n/a"));
            jo.put("chatId", taskJSONBody.chatId)
                    .put("task", "view")
                    .put("view", "weather")
                    .put("weather", weather);
            e.printStackTrace();
        }finally {
            //кладем json-ответ
            Tasks.getINSTANCE().getOutgoingTasks().put(jo.toString());
        }


    }

    public WeatherJSONBody getWeatherString(long cityId) {
        String json;
        String url;
        Gson gson;

        //запрос погоды с сервера
        url = "http://api.openweathermap.org/data/2.5/weather?id=" + cityId + "&units=metric&appid=7a5cf9844eb010197dc9c03069ccc77f";
        try {
            json = Jsoup.connect(url).userAgent("Opera/9.80 (Macintosh; Intel Mac OS X 10.14.1) Presto/2.12.388 Version/12.16").ignoreContentType(true).execute().body();
        } catch (IOException e) {
            e.printStackTrace();
            //return List.of(2.87, 5);
            System.out.println("alarm");
            return null;
        }
        //парсинг полученной погоды
        gson = new GsonBuilder().serializeNulls().create();
        WeatherJSONBody weatherJSONBody = gson.fromJson(json, WeatherJSONBody.class);
        System.out.println(weatherJSONBody);
        return weatherJSONBody;
    }

}
