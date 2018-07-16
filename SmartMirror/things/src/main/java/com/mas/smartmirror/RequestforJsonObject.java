package com.mas.smartmirror;

import com.mas.smartmirror.models.WeatherJson;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by DroidQ on 29.05.2018.
 */

public interface RequestforJsonObject {
    /*@GET: Bu metot URL bilgisi verilen sunucudan veri almak için kullanılır.
    Retroft içinde önceden tanımlanan bir yapıdır. Bizim yaptığımız ise, bu yapıya
    erişmektir. Buraya URL bilgisinin sadece son kısmı eklenecektir.
    id=321337 değeri Besni için kullanılmaktadır. Siz hangi şehir için verileri almak
    istiyorsanız o şehirle ilgili id bilgisini öğrenmeniz gerekiyor. Tüm şehirler için ID bilgisini
    şu linkten alabilirsiniz http://bulk.openweathermap.org/sample/
    APPID=79a59ac7f017322e3ca1a0939746d83f burada bulunan API bilgisini ise openweathermap sitesine
    üye olduktan sonra alabilirsiniz. Bu iki parametre sizin uygulamanızda kesinlikle değişkenlik gösterecektir.*/
    @GET("data/2.5/weather?id=321337&APPID=79a59ac7f017322e3ca1a0939746d83f")

    /*getCityName: Hava durumu bilgilerini almak için bu metodu kullanacağız.
    WeatherJson ise, sunucudan gelen veriyi saklamak için kullandığımız yapıdır.
    Bu arayüz ile JSON NESNESİ (YANİ TEK BİR VERİ KÜMESİ) alacağımız için,
    Call<WeatherJson> tipinde bir metot oluşturduk.*/
    Call<WeatherJson> getCityName();
}