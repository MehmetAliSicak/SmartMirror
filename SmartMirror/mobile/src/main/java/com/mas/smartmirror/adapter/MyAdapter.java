package com.mas.smartmirror.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.google.firebase.database.DatabaseReference;
import com.mas.smartmirror.R;
import com.mas.smartmirror.models.Note;

import java.util.ArrayList;

/*RecyclerView.Adapter: Adapter oluşturmak için
bu sınıf kullanılır. Adapter sınıfları, veri ile
AdapterView arasındaki bağlantıyı sağlar.*/
public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    Context ctx;
    private ArrayList<Note> activities = new ArrayList<>();
    DatabaseReference reference;

    /*Kurucu metot ile ihtiyaç duyulan parametreler alınır.*/
    public MyAdapter(Context ctx, ArrayList<Note> activities, DatabaseReference reference) {
        this.ctx = ctx;
        this.activities = activities;
        this.reference = reference;
    }

    /*RecyclerView ve ListView içerisinde periyodik olarak
    findViewById metodunu çağırmak performansı düşürebilir.
    Bundan dolayı bu işlem için ViewHolder sınıfı kullanılır.
    Bu sınıf ile her bir görünüme tag etiketi atanabilir.
    Bu etiket ile çok kolay bir şekilde erişim sağlanır.
    Yani her defasında findViewById metodunu çağrımak
    zorunda kalmayız.*/
    class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tvTimeDate;
        public TextView tvTitleDescription;
        ImageView imgView;

        public ViewHolder(View v) {
            super(v);
            tvTimeDate = v.findViewById(R.id.tvTimeDate);
            tvTitleDescription = v.findViewById(R.id.tvTitleDescription);
            imgView = v.findViewById(R.id.imgView);
        }
    }


    /*Her bir satır temsil edecek arayüz seçilir.
    Burada single_activites arayüzünü geliştirdik.*/
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        /*View nesnesi oluşturulur.*/
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_activites, parent, false);
        /*ViewHolder sınıfından bir nesne türetilir.
        Parametre olarak View nesnesi atanır*/
        ViewHolder vh = new ViewHolder(v);
        /*ViewHolder ile gelen vh nesnemiz döndürülür.*/
        return vh;
    }


    /*Her bir görünümün içeriği belirlenir.*/
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        /*Her bir kontrol için gelen değerler,
        position kullanılarak set edilir.*/
        viewHolder.tvTimeDate.setText(activities.get(position).getTime() + "\n" + activities.get(position).getDate());
        viewHolder.tvTitleDescription.setText(activities.get(position).getTitle() + "\n" + activities.get(position).getDescription());
        viewHolder.imgView.setTag(viewHolder);

        /*single_activites'de bulunan ImageView
        kontrolüne click olayı tanımlanır.
        Tıklandığı zaman ilgili etkinlik
        Firebase ortamında ve cihazdan silinir.*/
        viewHolder.imgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                reference.child(activities.get(position).getKey()).removeValue();
                activities.remove(position);
                updateList(activities);

            }
        });


    }

    /*Herhangi bir veri eklendiği veya silindiği
    zaman listenin güncellenmesini sağlar. */
    public void updateList(ArrayList<Note> activities) {
        activities = activities;
        notifyDataSetChanged();
    }

    /*Kayıtlı faaliyetlerin sayısı döndürülür*/
    @Override
    public int getItemCount() {
        return activities.size();
    }
}