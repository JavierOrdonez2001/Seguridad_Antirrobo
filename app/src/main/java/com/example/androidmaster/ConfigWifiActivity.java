package com.example.androidmaster;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import android.widget.TextView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;



public class ConfigWifiActivity extends AppCompatActivity {

    private EditText editTextSsid, editTextPassword;
    private Button btnGuardaWifi;
    private TextView textViewSsidActual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_config_wifi2);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.layoutConfigWifi), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        editTextSsid = findViewById(R.id.editTextSsid);
        editTextPassword = findViewById(R.id.editTextPassword);
        btnGuardaWifi = findViewById(R.id.btnGuardarWifi);

        btnGuardaWifi.setOnClickListener(view -> guardarWifiEnFirebase());

        textViewSsidActual = findViewById(R.id.textViewSsidActual);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null){
            String uid = user.getUid();
            DatabaseReference wifiRef = FirebaseDatabase.getInstance().getReference("UsersData").child(uid).child("wifi");

            wifiRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        String ssid = snapshot.child("ssid").getValue(String.class);
                        String password = snapshot.child("password").getValue(String.class);

                        if(ssid != null){
                            textViewSsidActual.setText("Red actual: " + ssid);
                            editTextSsid.setText(ssid);
                        }
                        if (password != null) {
                            editTextPassword.setText(password);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(ConfigWifiActivity.this, "Error al leer datos", Toast.LENGTH_SHORT).show();
                }
            });
        }


    }

    private void guardarWifiEnFirebase(){
        String ssid = editTextSsid.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (ssid.isEmpty() || password.isEmpty()){
            Toast.makeText(this, "Completa ambos campos", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null){
            Toast.makeText(this, "Usuario no autenticado", Toast.LENGTH_SHORT).show();
            return;
        }

        String uid = user.getUid();
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("UsersData").child(uid).child("wifi");

        dbRef.child("ssid").setValue(ssid);
        dbRef.child("password").setValue(password);

        Toast.makeText(this, "Datos guardados correctamente", Toast.LENGTH_SHORT).show();

        // actualiza layout
        textViewSsidActual.setText("Red actual: " + ssid);
        editTextSsid.setText(ssid);
        editTextPassword.setText(password);

    }
}

























