package com.mas.smartmirror;

import android.content.Intent;

import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.WearableListenerService;

/*WearableListenerService: Bu sınıf, veri katmanı olaylarını (data layer event) bir
servis içinde dinlemeyi sağlar. Servisin yaşam döngüsü sistem tarafından yönetilir.
Veri öğeleri ve mesajları göndermek istediğinizde servise bağlanılır, aksi durumda
yani ihtiyaç olmayan durumlarda bağlantı kesilir. Servislerle çalışmak için bu sınıf
kullanılır.*/
public class ListenerService extends WearableListenerService {

    /* onDataChanged(): Data Layer da meydana gelen
    veri değişiklikleri algılayan metodumuz */
    @Override
    public void onDataChanged(DataEventBuffer dataEventBuffer) {

        /*Akıllı saatte veri olaylarını kontrol eden döngümüz*/
        for (DataEvent event : dataEventBuffer) {

            /*DataEvent.TYPE_CHANGED: Data Layer üzerinde
            veri değişikliği olduğunda (örneğin data layer’a
            yeni bir veri eklendiğinde) yapılacak
            işlemleri belirlemede kullanılır.*/
            if (event.getType() == DataEvent.TYPE_CHANGED) {

                /*Verinin path bilgisi alınır ve değişkene atanır.
                Bu bilgi mobil uygulama tarafında geliştirdiğimiz
                FirstService isimli sınıftan gelmektedir.
                Bu sınıfta yazdığımız sendWatch() isimli metot
                içinde bulunan aşağıdaki kod ile path bilgisini
                "/wearable_data" olarak belirledik.
                PutDataMapRequest.create("/wearable_data").setUrgent();*/
                String path = event.getDataItem().getUri().getPath();

                /*Path bilgisi /wearable_data ise, akıllı telefondan
                faaliyet listesi gönderildiği anlaşılır.*/
                if (path.equalsIgnoreCase("/wearable_data")) {

                    /*DataItem yapısında gönderilen
                    faaliyeti dataMap nesnesine atarız*/
                    DataMap dataMap = DataMapItem.fromDataItem(event.getDataItem()).getDataMap();

                    /*Yeni bir intent oluşturduk.
                    Bu intent ile NotesListActivity
                    isimli etkinlik başlatılacaktır.*/
                    Intent i = new Intent(this, NotesListActivity.class);

                    /*Aldığımız DataMap verisini intent
                    içerisine ekleriz. DataMap doğrudan eklenemediği
                    için toBundle() metodu ile veri Bundle
                    nesnesine dönüştürülür. NotesListActivity isimli
                    etkinlikte veriyi almak için “note” key
                    değerini kullanırız.*/
                    i.putExtra("note", dataMap.toBundle());

                    /*Etkinlik için bayrak tanımladık.
                    Her veri değişiminde etkinlik yeniden
                    başlatılır ve gelen yeni
                    veriyi kullanıcıya gösterir.

                    FLAG_ACTIVITY_CLEAR_TASK: Çalışan bir
                    görev varsa temizlemeyi sağlar.

                    FLAG_ACTIVITY_NEW_TASK: Etkinliği yeni
                    bir görev içinde başlatır.*/
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

                    /*Etkinlik başlatılır.*/
                    startActivity(i);
                }
            }
        }
    }
}
