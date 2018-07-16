package com.mas.smartmirror;

import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.widget.ListView;

import com.mas.smartmirror.adapter.CreateList;

import java.util.ArrayList;

/*ListenerService ile akıllı telefondan gelen
faaliyet veya faaliyetler buraya gönderilir.*/
public class NotesListActivity extends WearableActivity {
    private ArrayList<String> notes = null;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes_list);

        /*Arayüzde bulunan ListView kontrolüne erişim
        sağlarız. Gelen tüm veriler burada listelenir.*/
        listView = findViewById(R.id.listView);

        /*Başlangıçta listview içeriğini temizliyoruz.*/
        listView.setAdapter(null);

        /*ListenerService ile gönderdiğimiz Bundle nesnesine
        erişmek için key bilgisi olarak "note" bilgisini
        kullandık.*/
        Bundle data = getIntent().getBundleExtra("note");

        /*Eğer data içeriği null değilse,*/
        if (data != null) {

            /*Veriler note key bilgisi ile ArrayList
            tipindeki notes nesnesine atanır.*/
            notes = data.getStringArrayList("note");
        }

        /*CreateList sınıfından bir nesn oluşturulur
        ve gelen veriler ListView içinde listelenir.*/
        listView.setAdapter(new CreateList(getApplicationContext(),notes));

    }
}