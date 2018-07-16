package com.mas.smartmirror;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;


import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mas.smartmirror.dateandtime.DatePickerFragment;
import com.mas.smartmirror.dateandtime.TimePickerFragment;

public class SaveActivity extends AppCompatActivity implements View.OnClickListener {
    private Button mBtnSave;
    private ImageButton mTimePicker, mDatePicker;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;
    private TextView mTime, mDate;
    private EditText mTitle, mDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activitiy_save);
        getControls();
        getFirebase();
        setEventListener();
    }


    /*activity_save arayüzünde bulunan kontrollere
    erişim sağlarız.*/
    private void getControls() {
        mTime = findViewById(R.id.tvTime);
        mDate = findViewById(R.id.tvDate);
        mTitle = findViewById(R.id.etTitle);
        mDescription = findViewById(R.id.etDescription);
        mBtnSave = findViewById(R.id.btnSave);
        mTimePicker = findViewById(R.id.imgTimePicker);
        mDatePicker = findViewById(R.id.imgDatePicker);
    }

    private void getFirebase() {
        /*Firebase veritabanına erişim sağlanır.*/
        mDatabase = FirebaseDatabase.getInstance();

        /*Veritabanında bulunan mynotes JSON dizisine erişim sağlanır.*/
        mReference = mDatabase.getReference("mynotes");
    }

    /*Butonlara tıklama olayı tanımladık*/
    private void setEventListener() {
        mBtnSave.setOnClickListener(this);
        mTimePicker.setOnClickListener(this);
        mDatePicker.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSave:
                /*btnSave id bilgisine sahip
                butona tıklandığı zaman
                faaliyet kaydı yapılır.*/
                saveMyNote();
                break;

            case R.id.imgTimePicker:
                /*imgTimePicker id bilgisine sahip
                butona tıklandığı zaman
                saat seçimini yapmayı
                sağlayan Fragment açılır.*/
                showTimePickerDialog();
                break;

            case R.id.imgDatePicker:
                /*imgDatePicker id bilgisine sahip
                butona tıklandığı zaman
                tarih seçimini yapmayı
                sağlayan Fragment açılır.*/
                showDatePickerDialog();
                break;

        }
    }

    /*Kullanıcının girdiği başlık, açıklama,
    saat ve tarih bilgileri Firebase ortamına kayıt edilir.*/
    private void saveMyNote() {
        long i = System.currentTimeMillis();
        DatabaseReference newNote = mReference.child(String.valueOf(i));

        /*Veriler arasına - karakteri eklediğimize dikkat ediniz.
        Daha sonra bu karakteri kullanarak split metodu ile bir dizi
        oluşturacağız.*/
        newNote.setValue(mDate.getText().toString()
                + "-" + mTime.getText().toString()
                + "-" + mTitle.getText().toString()
                + "-" + mDescription.getText().toString());

        /*Kayıt işleminden sonra SaveActivity
        sonlandırılır.*/
        finish();
    }

    /*Kullanıcının saat seçebilmesini sağlayan
    showTimePickerDialog fragment bileşeni başlatılır.*/
    public void showTimePickerDialog() {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getSupportFragmentManager(), "timePicker");

    }

    /*Kullanıcının tarih seçebilmesini sağlayan
    DatePickerFragment fragment bileşeni başlatılır.*/
    public void showDatePickerDialog() {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }
}
