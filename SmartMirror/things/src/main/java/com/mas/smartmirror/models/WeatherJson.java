package com.mas.smartmirror.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by DroidQ on 29.05.2018.
 */

/*Hava durumu bilgilerini Retrofit ile ayrıştırmak için aşağıdaki yapıyı birebir kullanmanızı tavsiye ediyorum.
Burada verilen  name, coord, weather ve diğer tüm değişkenler http://api.openweathermap.org/data/2.5/weather?id=321337&APPID=79a59ac7f017322e3ca1a0939746d83f
adresinde bulunan JSON yapısından alınmıştır. Retrofit ile JSON ayrıştırma işleminin başarılı olması için linkte
bulunan JSON key bilgileri ile burada tanımlanan değişkenlerin birebir aynı olması gerekiyor.*/
public class WeatherJson {
    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("coord")
    @Expose
    private Coord coord;

    @SerializedName("weather")
    @Expose
    private ArrayList<Weather> weather =new ArrayList<>();

    @SerializedName("main")
    @Expose
    private Main main;

    public Main getMain() {
        return main;
    }

    public void setMain(Main main) {
        this.main = main;
    }

    public ArrayList<Weather> getWeather() {
        return weather;
    }

    public void setWeather(ArrayList<Weather> weather) {
        this.weather = weather;
    }

    public Coord getCoord() {
        return coord;
    }

    public void setCoord(Coord coord) {
        this.coord = coord;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /*JSON verisinde bulunan coord JSON key bilgisi bir dizi olup aşağıdaki key bilgilerine sahiptir.*/
    public class Coord {
        @SerializedName("lon")
        @Expose
        private Double lon;
        @SerializedName("lat")
        @Expose
        private Double lat;

        public Double getLon() {
            return lon;
        }

        public void setLon(Double lon) {
            this.lon = lon;
        }

        public Double getLat() {
            return lat;
        }

        public void setLat(Double lat) {
            this.lat = lat;
        }
    }

    /*JSON verisinde bulunan weather JSON key bilgisi bir dizi olup aşağıdaki key bilgilerine sahiptir.*/
    public class Weather {
        @SerializedName("id")
        @Expose
        private int id;

        @SerializedName("main")
        @Expose
        private String main;

        @SerializedName("description")
        @Expose
        private String description;

        @SerializedName("icon")
        @Expose
        private String icon;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getMain() {
            return main;
        }

        public void setMain(String main) {
            this.main = main;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }
    }

    /*JSON verisinde bulunan main JSON key bilgisi bir dizi olup aşağıdaki key bilgilerine sahiptir.*/
    public class Main{
        @SerializedName("temp")
        @Expose
        private Double temp;

        @SerializedName("pressure")
        @Expose
        private Double pressure;

        @SerializedName("humidity")
        @Expose
        private Double humidity;

        @SerializedName("temp_min")
        @Expose
        private Double temp_min;

        @SerializedName("temp_max")
        @Expose
        private Double temp_max;

        @SerializedName("sea_level")
        @Expose
        private Double sea_level;

        @SerializedName("grnd_level")
        @Expose
        private Double grnd_level;

        public Double getTemp() {
            return temp;
        }

        public void setTemp(Double temp) {
            this.temp = temp;
        }

        public Double getPressure() {
            return pressure;
        }

        public void setPressure(Double pressure) {
            this.pressure = pressure;
        }

        public Double getHumidity() {
            return humidity;
        }

        public void setHumidity(Double humidity) {
            this.humidity = humidity;
        }

        public Double getTemp_min() {
            return temp_min;
        }

        public void setTemp_min(Double temp_min) {
            this.temp_min = temp_min;
        }

        public Double getTemp_max() {
            return temp_max;
        }

        public void setTemp_max(Double temp_max) {
            this.temp_max = temp_max;
        }

        public Double getSea_level() {
            return sea_level;
        }

        public void setSea_level(Double sea_level) {
            this.sea_level = sea_level;
        }

        public Double getGrnd_level() {
            return grnd_level;
        }

        public void setGrnd_level(Double grnd_level) {
            this.grnd_level = grnd_level;
        }
    }
}
