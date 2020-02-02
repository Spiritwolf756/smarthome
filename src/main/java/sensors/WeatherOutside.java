package sensors;

public class WeatherOutside {
    private double temperature;
    private Wind wind;

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setWind(int speed, int deg) {
        this.wind = new Wind(speed, deg);
    }

    public Wind getWind() {
        return wind;
    }

    private class Wind {
        private int speed;
        private int deg;

        public Wind(int speed, int deg) {
            this.speed = speed;
            this.deg = deg;
        }

        public void setDeg(int deg) {
            this.deg = deg;
        }

        public int getDeg() {
            return deg;
        }

        public void setSpeed(int speed) {
            this.speed = speed;
        }

        public int getSpeed() {
            return speed;
        }
    }

    @Override
    public String toString() {
        return "За окном: \nтемпература: " + this.getTemperature() + " градусов" +
                "\nветер: " + this.getWind().getSpeed() + " м/с";
    }
}
