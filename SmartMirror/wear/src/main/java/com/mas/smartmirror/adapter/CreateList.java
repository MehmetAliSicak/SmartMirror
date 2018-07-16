package com.mas.smartmirror.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mas.smartmirror.R;

import java.util.ArrayList;

/*BaseAdapter en çok kullanılan ve tercih edilen bir Adapter sınıfıdır.
BaseAdapter sınıfını kullanmak için başka bir sınıfın bu sınıftan extend,
yani türetilmesi gerekiyor. Bu sınıf, verileri düzene eklemek için
kullanılır. Bu sınıf aşağıda verilen dört adet metoda sahiptir.
İlk olarak kurucu metot başlatılır. Dışarıdan gelen
parametreler sınıf içindeki değişkenlere atanır.*/
public class CreateList extends BaseAdapter {
    Context ctx;
    public ArrayList<String> allActivities;

    /*Kurucu metot ile dışarıdan gelen parametreler alınır*/
    public CreateList(Context ctx, ArrayList<String> allActivities) {
        this.ctx = ctx;
        this.allActivities = allActivities;
    }

    /*Burada kaç adet satır oluşturulacağı tespit edilir.
    Dizimiz kaç elemanlı ise o kadar oluşturabiliriz.*/
    @Override
    public int getCount() {
        return allActivities.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View row, ViewGroup parent) {
        /*Asıl işlemin yapıldığı yer burasıdır.
        Öncelikle LayoutInflater ile düzeni
        dolduracak olan xml dosyasına erişim sağlanır.
        Row_list isimli düzene erişim
        sağlanır. Bu düzene eriştikten sonra burada
        bulunan her bir kontrol için atama
        işlemini yani veriyle doldurmayı sağlarız.*/
        LayoutInflater layoutInflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        row = layoutInflater.inflate(R.layout.row_list, parent, false);

        /*allActivities içinde bulunan veri alınır ve değişkene atanır.*/
        String activity = allActivities.get(position);

        /*Erişim sağlanan kontrollere atama yapılır.*/
        TextView textView = row.findViewById(R.id.textView);
        textView.setText(activity);

        /*Erişilen row_list düzeni return edilir.*/
        return row;
    }
}