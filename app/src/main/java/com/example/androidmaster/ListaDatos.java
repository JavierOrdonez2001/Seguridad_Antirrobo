package com.example.androidmaster;

import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import androidx.core.view.ViewCompat;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.core.app.NotificationCompat;



public class ListaDatos extends AppCompatActivity {

    private RecyclerView recyclerView;
    private DatoAdapter adapter;
    private List<Dato> listaDatos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_lista_datos);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        recyclerView = findViewById(R.id.recyclerViewDatos);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        listaDatos = new ArrayList<>();
        listaDatos.add(new Dato("Informacion del vehiculo"));
        listaDatos.add(new Dato("Ultimo chequeo: ..."));
        listaDatos.add(new Dato("Alarma: ..."));
        listaDatos.add(new Dato("Altitud: ..."));
        listaDatos.add(new Dato("Velocidad: ..."));


        adapter = new DatoAdapter(listaDatos);
        recyclerView.setAdapter(adapter);

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference vibracionRef = FirebaseDatabase.getInstance()
                .getReference("UsersData")
                .child(userId)
                .child("sensor")
                .child("vibracion");

        DatabaseReference ultimaVezRef = FirebaseDatabase.getInstance()
                .getReference("UsersData")
                .child(userId)
                .child("sensor")
                .child("ultimaVez");

        DatabaseReference altitud = FirebaseDatabase.getInstance()
                .getReference("UsersData")
                .child(userId)
                .child("gps")
                .child("altitud");

        DatabaseReference velocidad = FirebaseDatabase.getInstance()
                .getReference("UsersData")
                .child(userId)
                .child("gps")
                .child("velocidad");

        vibracionRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Boolean vibracion = snapshot.getValue(Boolean.class);
                if (vibracion != null){
                    listaDatos.set(2, new Dato("Alarma: "+ (vibracion ? "ACTIVADA" : "DESACTIVADA")));
                    adapter.notifyDataSetChanged();

                    // mostrar alerta
                    if (vibracion) {
                        mostrarNotificacion("Alerta de vehiculo", "!Se ah detectado movimiento en su vehiculo¡");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        ultimaVezRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Long ultimaVezMillis = snapshot.getValue(Long.class);
                String textoFechaHora;

                if(ultimaVezMillis == null) {
                    textoFechaHora = "SIN DATOS";
                } else {
                    Date date = new Date(ultimaVezMillis);
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
                    textoFechaHora = sdf.format(date);
                }
                listaDatos.set(1, new Dato("Ultima vibracion:  " + textoFechaHora));
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        altitud.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Double altitud = snapshot.getValue(Double.class);

                if (altitud != null){
                    listaDatos.set(3, new Dato("Altitud: "+ String.format(Locale.getDefault(),"%.2f",altitud)+ " m"));
                    adapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        velocidad.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Double velocidad = snapshot.getValue(Double.class);

                if (velocidad != null){
                    listaDatos.set(4, new Dato("Velocidad: " + String.format(Locale.getDefault(), "%.2f", velocidad) + " km/h"));
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        Button btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });


        Button btnSeguimiento = findViewById(R.id.btnSeguimiento);
        btnSeguimiento.setOnClickListener(v -> {
            Intent intent = new Intent(this, Seguimiento.class);
            startActivity(intent);
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 1);
            }
        }



    }


    private void mostrarNotificacion(String titulo, String mensaje){
        String channelId = "canal_alarma";
        String channelName = "Notificaciones de alarma";

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        //crear canal solo si android es android 8 o superior
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        //accion al tocar la notificacion
        Intent intent = new Intent(this, ListaDatos.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        // contruccion de la notificacion
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.ic_launcher_foreground)// icono de la app
                .setContentTitle(titulo)
                .setContentText(mensaje)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);
        //mostrar notificacion
        notificationManager.notify(1, builder.build());

    }


}



































