package com.mas.smartmirror;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.google.android.gms.wearable.DataClient;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mas.smartmirror.models.Note;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/*FirstService sınıfını Service üst sınıfından türettik.
Servisler arkaplanda çalışan ve arayüzleri olamayan uygulama
bileşenleridir.
ChildEventListener: Firebase veritabanında bulunan verilere erişmek
veya veri değişimlerini algılamak için bu arayüzü uyguladık.
Bu arayüzü uyguladıktan sonra aşağıdaki metotların eklenmesi gerek:
onChildAdded
onChildChanged
onChildRemoved
onChildMoved
onCancelled
*/
public class FirstService extends Service implements ChildEventListener {
    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;
    private ArrayList<Note> mNotes = new ArrayList<>();
    private ArrayList<String> mTodayNote;
    private HashMap<String, Note> mMap = new HashMap<String, Note>();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /*onStartCommand: Activity gibi bir bileşenden startService() metodu
    kullanılarak bir servis başlatıldığı zaman sistem onStartCommand()
    metodunu başlatır. Bu metot servisi süresiz olarak başlatmaya yarar.
    Bu şekilde başlattığınız bir metodu sizin kapatmanız gerekmektedir. */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Toast.makeText(this, "Servis başlatıldı.", Toast.LENGTH_SHORT).show();
        getFirebase();

        /*mynotes JSON dizisinde meydana gelen silme, ekleme vb.
        olayları algılayan bir olay dinleyici tanımladık.*/
        mReference.addChildEventListener(this);

        /*Kayıtlı olan faaliyetleri sürekli kontrol etmek için handler
        ve timer yapılarını kullandık.*/
        final Handler handler1 = new Handler();
        Timer timer1 = new Timer();
        timer1.schedule(new TimerTask() {
            @Override
            public void run() {
                handler1.post(new Runnable() {
                    @Override
                    public void run() {

                        /*Firebase ortamında bulunan faaliyetlerin tarih bilgisi
                        sürekli kontrol edilir. Eğer uygun bir faaliyet varsa
                        sendWatch metodu ile faaliyet bilgisi akıllı saate gönderilir.*/
                        mTodayNote = new ArrayList<>();
                        for (int i = 0; i < mNotes.size(); i++) {
                            if (getCurrentDate().equalsIgnoreCase(mNotes.get(i).getDate())) {
                                mTodayNote.add(mNotes.get(i).getTime() + "\n" + mNotes.get(i).getTitle());

                            }
                        }
                        if (mTodayNote != null) {
                            if (mTodayNote.size() != 0) sendWatch(mTodayNote);
                        }
                    }
                });
            }
        }, 0, 10000);


        return START_STICKY;
    }

    /*onDestroy:Bir servis uzun bir süre kullanılmadığı zaman veya
    kullanıcı servisin yok edilmesini istediği zaman, sistem bu
    metodu başlatır. */
    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "Servis yok edildi.", Toast.LENGTH_SHORT).show();
    }

    private void getFirebase() {
        /*Firebase veritabanına erişim sağlanır.*/
        mDatabase = FirebaseDatabase.getInstance();

        /*Veritabanında bulunan mynotes JSON dizisine erişim sağlanır.*/
        mReference = mDatabase.getReference("mynotes");
    }

    /*Firebase ortamından alınan herbir faaliyet
    Note yapısına ayrıştırılır*/
    public Note parseNote(DataSnapshot item) {
        /*Gelen faaliyet bilgisi key ve value
        bilgisi alınır değişkenlere atanır.*/
        String note = item.getValue(String.class);
        String key = item.getKey();

        /*Hem mobil hem de web uygulamasında tarih,
        saat, başlık ve açıklam verilerini - karakteri
        ile birleştirdik. Burada - karakteri yardımıyla
        value kısmının bir dizi olarak parçalanması
        sağlanır.*/
        String[] separated = note.split("-");

        /*Dizinin ilk sırasında tarih bilgisi bulunur.*/
        String date = separated[0];

        /*Dizinin ikinci sırasında zaman bilgisi bulunur.*/
        String time = separated[1];

        /*Dizinin üçüncü sırasında başlık bilgisi bulunur.*/
        String title = separated[2];

        /*Dizinin son sırasında açıklama bilgisi bulunur.*/
        String description = separated[3];

        /*Note sınıfından bir nesne oluşturup yukarıda
        elde ettiğimiz verileri ilgili parametrelere atadık.*/
        Note n = new Note();
        n.setKey(key);
        n.setTime(time);
        n.setDate(date);
        n.setTitle(title);
        n.setDescription(description);

        /*Son olarak faaliyeti yani Note nesnesini
        return ile gönderdik.*/
        return n;
    }

    /*sendWatch metodu, akıllı telefondan akıllı saate
    veri göndermeyi sağlar.*/
    private void sendWatch(ArrayList<String> n) {
        /*DataClient sınıf ile Wearable Data Layer'a erişim sağlanır*/
        DataClient mDataClient = Wearable.getDataClient(getApplicationContext());

        /*DataMap: Öncelikle DataMap yapısını oluşturmalıyız.
        Bu yapı içerisine, akıllı saate göndermek istediğimiz
        değerleri eklemeliyiz.*/
        DataMap dataMap = new DataMap();

        /*DataMap sınıfı yapı olarak Bundle sınıfında benzer.
        Veriler key-value olarak tutulur. key ifadesinin bu
        veriye erişmek için kullanacağız. Burada key bilgisi
        note olarak belirledik.*/
        dataMap.putStringArrayList("note", n);

        /*DataMap oluşturduktan sonra PutDataMapRequest nesnesi
        oluşturmalıyız. Bu nesnenin temel amacı, iletilecek verinin path yani yol
        bilgisini belirtmektir. Bunun için create() metodu kullanılır.
        Path bilgisini akıllı saatte bu veriye erişmek için kullanacağız.

        setUrgent(): Eğer yapılacak işlem çok önemli ise setUrgent() metodu ile
        işlemin acil olduğunu belirtmelisiniz. setUrgent() metodu çağırılmadığı
        zaman,  sistem yapılan istekleri 30 dakika kadar geciktirebilir.
        Bu maksimum değerdir. Genellikle birkaç dakika içinde işlem yapılır. */
        PutDataMapRequest putDataMapRequest = PutDataMapRequest.create("/wearable_data").setUrgent();

        /*DataMap ile oluşturulan verilerin, PutDataMapRequest nesnesine
        eklenmesi için getDataMap() ve putAll() metotlarını kullanırız.
        İletilecek veriler aşağıdaki gibi nesneye eklenir.*/
        putDataMapRequest.getDataMap().putAll(dataMap);

        /*PutDataMapRequest içinde bulunan verilere sahip olan PutDataRequest
        nesnesi oluşturulur. */
        PutDataRequest putDataReq = putDataMapRequest.asPutDataRequest();

        /*Son olarak veriler akıllı saate gönderilir. */
        mDataClient.putDataItem(putDataReq);
    }

    /*Bu metot mevcut günün tarih bilgisini dd.MM.yyyy formatında
    hesaplamayı sağlar. Firebase ortamında kayıtlı olan bir verinin
    date bilgisi bu metot ile elde edilen değere eşit ise akıllı saate
    bilgi gönderilmesi sağlanır.*/
    private String getCurrentDate() {
        Date date = Calendar.getInstance().getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        String today = dateFormat.format(date);
        return today;
    }

    /*onChildAdded: Firebase ortamında bulunan mynotes JSON
    dizisine bir veri eklendiğinde bu metot çağrılır.*/
    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        /*Eklenen yeni faaliyet önce map sonra listeye eklenir. */
        Note note = parseNote(dataSnapshot);
        mMap.put(dataSnapshot.getKey(), note);
        mNotes.add(note);
    }

    /*onChildChanged: Firebase ortamında kayıtlı bir veride değişim olduğunda çağrılır.*/
    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

    }

    /*onChildRemoved:Firebase ortamında bulunan mynotes
    JSON dizisinden bir veri silindiğinde bu metot çağrılır.*/
    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {
        /*Silinen faaliyet map yapısından silinir.*/
        mMap.remove(dataSnapshot.getKey());

        /*Daha sonra listede bulunan tüm faaliyetler silinir.*/
        mNotes.clear();

        /*Son olarak map yapısında kalan tüm faaliyetler
        listeye yeniden eklenir.*/
        for (Map.Entry<String, Note> entry : mMap.entrySet()) {
            mNotes.add(entry.getValue());
        }
    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }
}

