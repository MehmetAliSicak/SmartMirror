package com.mas.smartmirror.dateandtime;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.widget.TextView;
import android.widget.TimePicker;

import com.mas.smartmirror.R;

import java.util.Calendar;

/*DialogFragment: Belirli bir işlemi iletişim penceresi olarak
kullanıcıya sunmayı sağlayan sınıf.
TimePickerDialog.OnTimeSetListener: Kullanıcının bir saat seçtikten
sonra seçilen değerin kullanılmasını sağlayan arayüz.
Bu arayüzü uyguladıktan sonra onTimeSet() metodunu eklemeniz gerekiyor.*/
public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

   TextView tvTime;
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        /*Bu fragment bileşenine SaveActivity sınıfından erişeceğiz.
        Amacımız kullanıcı saati seçtikten sonra SaveActivity'de
        bulunan tvTime id bilgisine sahip kontrolde saat bilgisini
        göstermektir. Fragment bileşeninden bu kontrole ulaşabilmek için
        getActivity() metodunu kullanırız. */
        tvTime=getActivity().findViewById(R.id.tvTime);
    }

    /*Fragment için bir Dialog oluşturulur.*/
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        /*Sistemden alınan saat ve dakika bilgilerine uyumlu olan
        bir dialog oluşturmayı sağlar.*/
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        return new TimePickerDialog(getActivity(), this, hour, minute, DateFormat.is24HourFormat(getActivity()));
    }

    /*Kullanıcı saat seçimini yapıp onayladıktan sonra bu metot çağrılır.*/
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

        /*Seçilen saat bilgisi textView kontrolünde gösterilir.
        Burada saat ve dakika bilgileri arasında (:) ekledik.*/
        String hour1 = hourOfDay<10 ? "0"+hourOfDay : ""+hourOfDay;
        String minute1 = minute<10 ? "0"+minute : ""+minute;
        tvTime.setText(hour1+":"+minute1);
    }
}