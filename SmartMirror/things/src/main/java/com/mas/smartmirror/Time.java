package com.mas.smartmirror;

import java.util.Calendar;

/**
 * Created by DroidQ on 29.08.2017.
 */

class Time {
    /*Gün ve Ay bilgilerini tutan dizilerimiz.*/
    private static String[] days = new String[]{"Pazartesi", "Salı", "Çarşamba", "Perşembe", "Cuma", "Cumartesi", "Pazar"};
    private static String[] months = new String[]{"Oca", "Şub", "Mar", "Nis", "May", "Haz", "Tem", "Ağu", "Eyl", "Eki", "Kas", "Ara"};

    static String getAndSet() {
        /*getInstance: Belirtilen saat dilimini kullanarak bir takvim alınmasını sağlar.*/
        Calendar calendar = Calendar.getInstance();

        /*Date: zaman içinde belirli bir anı almak için kullanılır.
        setTime: takvim için saat ayarlanmasını sağlar.*/
        calendar.setTime(new java.util.Date());

        /*Calendar.HOUR_OF_DAY: günün o anki saat bilgisini alır.
        get: verilen calendar alanının değerini döndürür.*/
        int hours =calendar.get(Calendar.HOUR_OF_DAY);


        /*Saat bilgisi 0 ile 9 arasında ise, saat bilgisinin yanına sıfır eklenir. Eğer 10 ve 23 arasında
        ise, saat bilgisi olduğu gibi kullanıcıya gönderilir.*/
        String modifyhours = hours<10 ? "0"+hours : ""+hours;

        /*Calendar.MINUTE: dakika bilgisini alır.*/
        int minutes = calendar.get(Calendar.MINUTE);

        /*Dakika bilgisi 0 ile 9 arasında ise, dakika bilgisinin yanına sıfır eklenir. Eğer 10 ve 59 arasında
        ise, dakika bilgisi olduğu gibi kullanıcıya gönderilir.*/
        String modifyMinutes = minutes<10 ? "0"+minutes : ""+minutes;

        /*İçinde olduğumuz ay'ın gün bilgisini alan kodumuz.*/
        int monthDay = calendar.get(Calendar.DAY_OF_MONTH);

        /*Ay bilgisi sayısal olarak alınır. Bu değer 0 ile 11 arasında olur.*/
        int month = calendar.get(Calendar.MONTH);

        /*months dizinde bulunan ay bilgisi alınır ve değişkene atanır.*/
        String currentMonth = months[month];

        /*Yıl bilgisi alınır*/
        int year = calendar.get(Calendar.YEAR);


        /*Gün bilgisi sayısal olarak alınır. Bu değer 0 ile 6 arasında olur.*/
        int day = calendar.get(Calendar.DAY_OF_WEEK);

        /*days dizinde bulunan gün bilgisi alınır ve değişkene atanır.*/
        String currentDay = days[day];

        /*Yukarıda alınan veriler tek bir değişkene atanır ve return ile çağrılan yere gönderilir.*/
        String time = modifyhours + ":" + modifyMinutes + "\n" + monthDay + " " + currentMonth + " " + year + "\n" + currentDay;
        return time;

    }
}
