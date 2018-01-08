package com.example.hmtteol.bleyoklama;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.hmtteol.BleYoklama.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Login extends AppCompatActivity {


    EditText kullanici_no, kullanici_sifre;
    Button giris;
    FirebaseDatabase db;
    String no,upass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN );
        setContentView(R.layout.activity_login);



        kullanici_no = (EditText) findViewById(R.id.kullanici_no);
        kullanici_sifre = (EditText) findViewById(R.id.kullanici_sifre);
        giris = (Button) findViewById(R.id.giris);

        giris.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                no = kullanici_no.getText().toString();
                upass = kullanici_sifre.getText().toString();
                giris_kontrol();
            }
        });
     }
     public void giris_kontrol() {
         db = FirebaseDatabase.getInstance();
         final DatabaseReference databaseReference = db.getReference("user");
         databaseReference.addValueEventListener(new ValueEventListener() {
             @Override
             public void onDataChange(DataSnapshot dataSnapshot) {

                 for (DataSnapshot gelen: dataSnapshot.getChildren()) {

                     String gelen_no = gelen.getValue(user.class).getNo().toString();
                     String gelen_pass = gelen.getValue(user.class).getPass().toString();

                     if (gelen_no.equals(no)) {
                         if (gelen_pass.equals(upass))
                         {
                             if (gelen.getValue(user.class).getNo().equals("14029f")){

                                 Intent i = new Intent(Login.this,MainActivity.class);
                                 startActivity(i);

                             }else if (gelen.getValue(user.class).getNo().equals("987654")) {

                                 Intent i = new Intent(Login.this,transmitter.class);
                                 startActivity(i);
                             }
                         }

                     }
                 }

             }

             @Override
             public void onCancelled(DatabaseError error) {
                 // Failed to read value
                 Toast.makeText(Login.this,"Girilmedi",Toast.LENGTH_SHORT).show();
             }
         });

     }
}
