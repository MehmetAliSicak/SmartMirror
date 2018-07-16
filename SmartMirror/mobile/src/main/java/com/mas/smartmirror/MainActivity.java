package com.mas.smartmirror;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ProgressBar;
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
import com.mas.smartmirror.adapter.MyAdapter;
import com.mas.smartmirror.models.Note;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements ChildEventListener, View.OnClickListener {


    private Toolbar mToolbar;
    private ProgressBar mProgressBar;
    private FloatingActionButton mFab;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;
    private ArrayList<Note> mNotes = new ArrayList<>();
    private ArrayList<String> mTodayNote = new ArrayList<>();
    private HashMap<String, Note> mMap = new HashMap<String, Note>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getControls();
        getFirebase();
        setRecyclerView();
        setEventListener();

        /*Aşağıdaki kodlar ile FirstService sınıfı yani
        servis başlatılır.*/
        Intent i = new Intent(MainActivity.this,FirstService.class);
        startService(i);
    }

    /*activity_main'de bulunan kontrollere erişim sağlanır.*/
    private void getControls() {
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mProgressBar = findViewById(R.id.pbLoading);
        mRecyclerView = findViewById(R.id.my_recycler_view);
        mFab = findViewById(R.id.fab);
    }

    private void getFirebase() {
       /*Firebase veritabanına erişim sağlanır.*/
        mDatabase = FirebaseDatabase.getInstance();

        /*Veritabanında bulunan mynotes JSON dizisine erişim sağlanır.*/
        mReference = mDatabase.getReference("mynotes");
    }

    /*RecyclerView için ihtiyaç duyulan özellikleri
    set eden metodumuz*/
    private void setRecyclerView() {
        /*Bu özelliği set ettiğimizde performansı arttırır.
        Eğer içeriğin değişmesi, RecyclerView düzen boyutunu
        değiştirmiyorsa bu özelliği set edebilirsiniz.*/
        mRecyclerView.setHasFixedSize(true);

        /*Her bir satırın nasıl hizalanacağı belirlenir.
        Her satır dikey olarak hizalanır.*/
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        /*Adapter nesnesi oluşturulur. Parametre olarak
        faaliyetleri tutan liste kullanılır.*/
        mAdapter = new MyAdapter(getApplicationContext(), mNotes, mReference);

        /*RecyclerView, adapter ile doldurulur.*/
        mRecyclerView.setAdapter(mAdapter);
    }

    private void setEventListener() {
        /*mynotes JSON dizisinde meydana gelen silme, ekleme vb.
        olayları algılayan bir olay dinleyici tanımladık.*/
        mReference.addChildEventListener(this);

        /*FloatingActionButton için click olayı tanımladık.*/
        mFab.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab:
                /*FloatingActionButton kontrolüne tıklandığı zaman
                SaveActivity arayüzü başlatılır. Kullanıcı bu arayüzü
                kullanarak Firebase ortamına faaliyet kaydını
                yapabilir.*/
                Intent i = new Intent(MainActivity.this, SaveActivity.class);
                startActivity(i);
                break;
        }
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


    /*onChildAdded: Firebase ortamında bulunan mynotes JSON
    dizisine bir veri eklendiğinde bu metot çağrılır.*/
    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        /*Eklenen yeni faaliyet önce map sonra listeye eklenir. */
        Note note = parseNote(dataSnapshot);
        mMap.put(dataSnapshot.getKey(), note);
        mNotes.add(note);

        /*Listenin güncellenmesi sağlanır.
        Böylece yeni bir faaliyet eklediğinizde
        arayüzde bulunan listenin uzadığını görürsünüz.*/
        mAdapter.notifyDataSetChanged();
        mProgressBar.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);
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

        /*Silme işleminin arayüzde bulunan ve kullanıcaya
        gösterilen listeye uygulanmasını sağlar.
        Eğer bunu yapmazsanız silinen veri arayüzde
        listelenmeye devam eder.*/
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }

}
