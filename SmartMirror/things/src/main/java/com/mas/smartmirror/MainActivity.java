package com.mas.smartmirror;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mas.smartmirror.models.Note;
import com.mas.smartmirror.models.WeatherJson;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends Activity implements ChildEventListener {

    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;
    private TextView mTvWifi, mTvMsg, mTvActivities, mTvTime, mTvWeather;
    private ImageView mIvWeather;

    private static int currentAct = 0;
    private int i = 0;
    private static int sizeAct = 0;
    private String formattedDate;

    /*Kullanıcıya günlük üç mesaj gösterilecektir. Bu sayıyı arttırabilirsiniz*/
    private String[] words = new String[]{
            "Merhaba, Nasılsınız?",
            "Bugün harikasınız!",
            "Notlarınızı kontrol ediniz!"
    };

    /*activitiesComing: Firebase ortamında kayıtlı olan faaliyetleri tutacak bir liste tanımladık. Yapı olarak Note sınıfını kullandık */
    private ArrayList<Note> activitiesComing = new ArrayList<>();

    /*HashMap yapısı ile kayıtlı olan faaliyetler key-value çiftleri halinde hash yapısında tutulur.*/
    private HashMap<String, Note> meMap = new HashMap<String, Note>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getControls();
        getCurrentDate();
        getWeather();

        mTvTime.setText(Time.getAndSet());
        /*Firebase veritabanına erişim sağlanır.*/
        mDatabase = FirebaseDatabase.getInstance();

        /*Veritabanında bulunan mynotes JSON dizisine erişim sağlanır.*/
        mReference = mDatabase.getReference("mynotes");

        /*mynotes JSON dizisinde meydana gelen silme, ekleme vb. olayları algılayan bir olay dinleyici tanımladık.*/
        mReference.addChildEventListener(this);

        /*Kayıtlı olan faaliyetleri sırasıyla göstermek için handler ve timer yapılarını kullandık.*/
        final Handler mHandlerforActivities = new Handler();

        /*Timer ile varsa kayıtlı olan faaliyetler tek tek ve 5 saniye aralıkla kullanıcıya gösterilir.*/
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                mHandlerforActivities.post(new Runnable() {
                    @Override
                    public void run() {
                        /*checkStateWifi() metodu ile Raspberry Pi 3'ün herhangi bir ağa bağlı olup olmadığı kontrol edilir*/
                        if (checkStateWifi()) {

                            mTvTime.setVisibility(View.VISIBLE);
                            mTvWeather.setVisibility(View.VISIBLE);
                            mTvActivities.setVisibility(View.VISIBLE);
                            mTvTime.setVisibility(View.VISIBLE);
                            mIvWeather.setVisibility(View.VISIBLE);
                            mTvMsg.setVisibility(View.VISIBLE);

                            /*Eğer ağ bağlantısı varsa mTvWifi kontrolü kullanıcıya gösterilmez.*/
                            mTvWifi.setVisibility(View.INVISIBLE);

                            /*activitiesComing listesinde bulunan toplam faaliyet sayısı sizeAct değişkenine atanır*/
                            sizeAct = activitiesComing.size();

                            /*sizeAct değişkeni sıfır değilse yani listede faaliyet varsa*/
                            if (sizeAct != 0) {

                                /*currentAct değişkeni -1 değilse*/
                                if (currentAct == -1) {
                                    currentAct = sizeAct - 1;
                                }

                                /*currentAct değişkeni sizeAct değilse*/
                                if (currentAct == sizeAct) {
                                    currentAct = 0;
                                }

                                /*Öncelikle kontroll içeriği temizlenir*/
                                mTvActivities.setText("");

                                /*currentAct indis bilgisine sahip olan faaliyet TextView'de gösterilir.
                                activitiesComing listesi Note yapısında bir liste olduğundan dolayı
                                getTitle() ile faaliyetin başlığı,
                                getDescription() ile faaliyetin açıklaması istenir.*/
                                mTvActivities.setText(activitiesComing.get(currentAct).getTitle() + "\n"
                                        + activitiesComing.get(currentAct).getDescription());

                                /*currentAct indis bilgisine sahip faaliyet mevcut günün tarih bilgisine sahipse,
                                words dizisine ilgili faaliyet hakkında bilgi eklenir. Bu bilgi aşağıda veriln
                                ikinci bir handler ile kullanıcıya gösterilir*/
                                if (formattedDate.equalsIgnoreCase(activitiesComing.get(currentAct).getDate())) {
                                    words[2] = "Bugün saat " + activitiesComing.get(currentAct).getTime()
                                    +" da " + activitiesComing.get(currentAct).getTitle()+" var.";


                                } else {
                                    words[2] = "Notlarınızı kontrol ediniz!";
                                }

                                /*Sonraki faaliyete geçmek için currentAct değişkeni bir arttırılır ve hava
                                durumu bilgisi tekrar istenir.*/
                                currentAct++;
                                getWeather();
                            } else {
                                /*sizeAct değişkeni sıfır yani kayıtlı herhangi bir faaliyet bulunmuyorsa
                                Faaliyet bulunmadı bilgisi kullanıcıya gösterilir. */
                                mTvActivities.setText("Faaliyet bulunamadı");
                                words[2] = "Notlarınızı kontrol ediniz!";
                                currentAct = 0;
                            }
                        } else {
                            mTvTime.setVisibility(View.INVISIBLE);
                            mTvWeather.setVisibility(View.INVISIBLE);
                            mTvActivities.setVisibility(View.INVISIBLE);
                            mTvTime.setVisibility(View.INVISIBLE);
                            mIvWeather.setVisibility(View.INVISIBLE);
                            mTvMsg.setVisibility(View.INVISIBLE);
                            mTvWifi.setVisibility(View.VISIBLE);

                        }

                    }
                });
            }
        }, 0, 5000);

        /*Günlük mesajlar göstermek için handler ve timer yapılarını kullandık.*/
        final Handler mHandlerforMessage = new Handler();
        Timer timer1 = new Timer();
        timer1.schedule(new TimerTask() {
            @Override
            public void run() {
                mHandlerforMessage.post(new Runnable() {
                    @Override
                    public void run() {

                        if (i == -1) {
                            i = words.length - 1;
                        }
                        if (i == words.length) {
                            i = 0;
                        }
                        /*words dizisinde bulunan mesajlar sırasıyla kullanıcıya sunulur.*/
                        mTvMsg.setText(words[i]);
                        i++;
                    }
                });
            }
        }, 0, 5000);

        /*getWiFiIP metodu IP bilgisini öğrenmemizi sağlar. Uygulama açıldığı zaman bu veri kullanıcıya gösterilir.*/
        mTvActivities.setText("IP:" + getWiFiIP());
    }

    /*Mevcut günün tarih bilgisi dd.MM.yyyy formatında elde edilir.*/
    private void getCurrentDate() {
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy");
        formattedDate = df.format(c);
    }

    /*Arayüzde bulunan kontrollere erişimi sağlayan metot*/
    private void getControls() {
        mTvTime = findViewById(R.id.tvTime);
        mTvActivities = findViewById(R.id.tvActivities);
        mTvActivities = findViewById(R.id.tvActivities);
        mTvMsg = findViewById(R.id.tvMessage);
        mTvWifi = findViewById(R.id.tvWifi);
        mTvWeather = findViewById(R.id.tvWeather);
        mIvWeather = findViewById(R.id.ivWeather);
    }

    /*Hava Durumu bilgisini alan metot*/
    private void getWeather() {

        /*Hava durumu verisini openweathermap sitesinden aldık.
        Host Name aşağıdaki gibi olmalıdır.*/
        String HOST_NAME = "http://api.openweathermap.org/";

        /*Hava durumu verileri openweathermap ortamından JSON olarak alınır. JSON verisini
        ayrıştırmak için Retrofit kütüphanesinden faydalandık.*/
        /*Retroft.Builder(): Retroft nesnesi oluşturmak için kullanılır.
        baseUrl(): verilerin alınacağı URL bilgisi burada belirtilir.
        GsonConverterFactory.create(): JSON verilerinin erişilebilir nesneler haline
        dönüştürmek için kullanılır.
        Gson: Java nesnelerini JSON haline dönüştürmek için kullanılan bir java
        kütüphanesidir. */
        Retrofit retroft = new Retrofit.Builder()
                .baseUrl(HOST_NAME)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        /*Yukarıdaki işlemlerden sonra RequestforJsonObject arayüzümüze geçtik.
        create() metodu ile servisten bir istekte bulunuruz.*/
        RequestforJsonObject service =
                retroft.create(RequestforJsonObject.class);

        /*Hava durumu  bilgileri ayrıntılı olarak alınır.*/
        Call<WeatherJson> call = service.getCityName();

        /*Bu kısım çok önemlidir. Burada iki adet metot kullanılabilir.
        enqueue (asynchronously- asenkron): Bu metot verileri asenkron yani eş-
        zamansız olarak almayı sağlar. execute (synchronously - senkron): Bu
        metot verileri senkron yani eş zamanlı olarak almayı sağlar.
        Daha iyi bir performans için asenkron yapıları kullanmak çok önemlidir.
        Bu metodun içerisinde iki adet metot bulunmaktadır.
        onResponse(): Sunucudan bir yanıt geldiğinde bu metot otomatik olarak çağrılır.
        onFailure(): Sunucudan bir yanıt alınmadığı zaman bu metot otomatik olarak
        çağrılır.*/
        call.enqueue(new Callback<WeatherJson>() {
            @Override
            public void onResponse(Call<WeatherJson> call, Response<WeatherJson> response) {

                /*Sıcaklık bilgisi alınır.*/
                Double temp = response.body().getMain().getTemp();

                /*Alınan sıcaklık bilgisi santigrata çevrilir.*/
                int result_temp = (int) (temp - 273);

                /*Şehir  ve sıcaklık bilgisi ilgili kontrole yazılır.*/
                mTvWeather.setText(response.body().getName() + " " + result_temp + " °C");

                /*Picasso ile hava durumu simgesi ImageView'de gösterilir*/
                Picasso.get().load("http://openweathermap.org/img/w/" + response.body().getWeather().get(0).getIcon() + ".png").into(mIvWeather);
            }

            @Override
            public void onFailure(Call<WeatherJson> call, Throwable t) {

            }
        });
    }

    /*Raspberry'nin wifi bağlantısı kontrol edilir*/
    public boolean checkStateWifi() {
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if (mWifi.isConnected()) {
            return true;
        }
        return false;
    }

    /*IP bilgisini öğrenmeyi sağlayan metot*/
    private String getWiFiIP() {
        try {
            WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            int ipAddress = wifiInfo.getIpAddress();
            return String.format(Locale.getDefault(), "%d.%d.%d.%d",
                    (ipAddress & 0xff), (ipAddress >> 8 & 0xff),
                    (ipAddress >> 16 & 0xff), (ipAddress >> 24 & 0xff));
        } catch (Exception ex) {

            return null;
        }
    }

    /*Firebase ortamından alınan herbir faaliyet Note yapısına ayrıştırılır*/
    public Note getNote(DataSnapshot item) {
        /*Gelen faaliyet bilgisi key ve value bilgisi alınır değişkenlere atanır.*/
        String note = item.getValue(String.class);
        String key = item.getKey();

        /*Hem mobil hem de web uygulamasında tarih, saat, başlık ve açıklam verilerini - karakteri
        ile birleştirdik. Burada - karakteri yardımıyla value kısmının bir dizi olarak parçalanması sağlanır.*/
        String[] separated = note.split("-");

        /*Dizinin ilk sırasında tarih bilgisi bulunur.*/
        String date = separated[0];

        /*Dizinin ikinci sırasında zaman bilgisi bulunur.*/
        String time = separated[1];

        /*Dizinin üçüncü sırasında başlık bilgisi bulunur.*/
        String title = separated[2];

        /*Dizinin son sırasında açıklama bilgisi bulunur.*/
        String description = separated[3];

        /*Note sınıfından bir nesne oluşturup yukarıda elde ettiğimiz verileri ilgili parametrelere atadık.*/
        Note activity = new Note();
        activity.setKey(key);
        activity.setTime(time);
        activity.setDate(date);
        activity.setTitle(title);
        activity.setDescription(description);

        /*Son olarak faaliyeti yani Note nesnesini return ile gönderdik.*/
        return activity;
    }

    /*Firebase ortamında bulunan mynotes JSON dizisine bir veri eklendiğinde bu metot çağrılır.*/
    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        /*Eklenen yeni faaliyet önce map sonra listeye eklenir. */
        meMap.put(dataSnapshot.getKey(), getNote(dataSnapshot));
        activitiesComing.add(getNote(dataSnapshot));

    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

    }

    /*Firebase ortamında bulunan mynotes JSON dizisinden bir veri silindiğinde bu metot çağrılır.*/
    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {
        /*Silinen faaliyet map yapısından silinir.*/
        meMap.remove(dataSnapshot.getKey());

        /*Daha sonra listede bulunan tüm faaliyetler silinir.*/
        activitiesComing.clear();

        /*Son olarak map yapısında kalan tüm faaliyetler listeye yeniden eklenir.*/
        for (Map.Entry<String, Note> entry : meMap.entrySet()) {
            activitiesComing.add(entry.getValue());
        }
    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }
}
