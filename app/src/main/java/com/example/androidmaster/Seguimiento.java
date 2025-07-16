package com.example.androidmaster;

import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Seguimiento extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private Marker gpsMarker;
    private DatabaseReference gpsRef;
    private com.google.android.gms.maps.model.Circle zonaSegura;
    private boolean primerZoomHecho = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_seguimiento);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        SupportMapFragment mapFragment = (SupportMapFragment)
            getSupportFragmentManager().findFragmentById(R.id.mapFragment);

        if (mapFragment != null){
            mapFragment.getMapAsync(this);
        }

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        gpsRef = FirebaseDatabase.getInstance()
                .getReference("UsersData")
                .child(userId)
                .child("gps");

        gpsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Double latitud = snapshot.child("latitud").getValue(Double.class);
                Double longitud = snapshot.child("longitud").getValue(Double.class);
                Double velocidad = snapshot.child("velocidad").getValue(Double.class);
                Double altitud = snapshot.child("altitud").getValue(Double.class);

                String snippet = "";
                if (velocidad != null) snippet += "Velocidad: " + String.format("%.2f", velocidad) + " km/h\n";
                if (altitud != null) snippet += "Altitud: " + String.format("%.2f", altitud) + " m";

                if (latitud != null && longitud != null && mMap != null){
                    LatLng nuevaUbicacion = new LatLng(latitud, longitud);

                    if (gpsMarker == null){
                        gpsMarker = mMap.addMarker(new MarkerOptions()
                                .position(nuevaUbicacion)
                                .title("Ubicacion en tiempo real"));

                        gpsMarker.showInfoWindow();
                    } else {
                        gpsMarker.setPosition(nuevaUbicacion);
                        gpsMarker.setSnippet(snippet);
                        gpsMarker.showInfoWindow();
                    }

                    if (!primerZoomHecho) {
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(nuevaUbicacion, 16f));
                        primerZoomHecho = true;
                    }

                    if (zonaSegura == null) {
                        zonaSegura = mMap.addCircle(new com.google.android.gms.maps.model.CircleOptions()
                                .center(nuevaUbicacion)
                                .radius(20)
                                .strokeColor(android.graphics.Color.BLUE)
                                .fillColor(0x220000FF)
                                .strokeWidth(2f));
                    } else {
                        zonaSegura.setCenter(nuevaUbicacion);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        DatabaseReference vibracionRef = FirebaseDatabase.getInstance()
                .getReference("UsersData")
                .child(userId)
                .child("sensor")
                .child("vibracion");

        vibracionRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Boolean vibracion = snapshot.getValue(Boolean.class);
                if (zonaSegura != null && vibracion != null){
                    zonaSegura.setStrokeColor(vibracion ? android.graphics.Color.RED : android.graphics.Color.BLUE);
                    zonaSegura.setFillColor(vibracion ? 0x22FF0000 : 0x220000FF);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });


    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
    }
}