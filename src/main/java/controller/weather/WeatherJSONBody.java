package controller.weather;

public class WeatherJSONBody {
    public coord coord;
    public weather[] weather;
    public main main;
    public wind wind;
    public sys sys;
    public class coord {
        public String lon;
        public String lat;
    }

    public class weather {
        public String id;
        public String main;
        public String description;
        public String icon;
    }
    public class main {
        public String temp;
        public String pressure;
        public String humidity;
        public String temp_min;
        public String temp_max;
    }
    public class wind{
        public String speed;
        public String deg;
    }
    public class sys{
        public String sunrise;
        public String sunset;
    }
}
