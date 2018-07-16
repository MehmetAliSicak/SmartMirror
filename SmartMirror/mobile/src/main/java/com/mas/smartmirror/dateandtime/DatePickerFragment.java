package com.mas.smartmirror.dateandtime;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;
import android.widget.TextView;

import com.mas.smartmirror.R;

import java.util.Calendar;

/*DialogFragment: Belirli bir işlemi iletişim penceresi olarak
kullanıcıya sunmayı sağlayan sınıf.
DatePickerDialog.OnDateSetListener: Kullanıcının bir tarih seçtikten
sonra seçilen değerin kullanılmasını sağlayan arayüz.
Bu arayüzü uyguladıktan sonra onDateSet() metodunu eklemeniz gerekiyor.*/
public  class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    TextView tvDate;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        /*Bu fragment bileşenine SaveActivity sınıfından erişeceğiz.
        Amacımız kullanıcı tarih seçtikten sonra SaveActivity'de
        bulunan tvDate id bilgisine sahip kontrolde tarih bilgisini
        göstermektir. Fragment bileşeninden bu kontrole ulaşabilmek için
        getActivity() metodunu kullanırız. */
        tvDate=getActivity().findViewById(R.id.tvDate);
    }

    /*Fragment için bir Dialog oluşturulur.*/
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar c = Calendar.getInstance();

        /*Sistemden alınan yıl, ay ve gün bilgilerine uyumlu olan
        bir dialog oluşturmayı sağlar.*/
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        /*SaveActivity arayüzünde iletişim penceresi
        halinde olan bir takvim başlatılır.*/
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    /*Kullanıcı tarih seçimini yapıp onayladıktan sonra bu metot çağrılır.*/
    public void onDateSet(DatePicker view, int year, int month, int day) {

        /*Seçilen tarih bilgisi textView kontrolünde gösterilir.
        Burada gün, ay ve yıl bilgileri arasında nokta ekledik.*/
        ++month;
        String month1 = month<10 ? "0"+month : ""+month;
        String day1 = day<10 ? "0"+day : ""+day;
        tvDate.setText(day1+"."+month1+"."+year);
    }
}