package com.example.androidmaster;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.util.Log;

public class MainActivity extends AppCompatActivity {
    Button activate;
    Button btnAbout;
    Boolean activo = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        super.setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        
        activate = (Button) findViewById(R.id.activate_deactivate);
        btnAbout = (Button) findViewById(R.id.btnAbout);


        activate.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //variable que se guardara en la nube fire base
                String new_boolean = revertir_variable_activo().toString();

                if (new_boolean.equals( "true")){
                    activate.setText("Desactivar");
                    activate.setBackgroundColor(Color.RED);
                }
                if (new_boolean.equals( "false")){
                    activate.setText("Activar");
                    activate.setBackgroundColor(Color.GREEN);
                }
                Log.i("BOLEAN", new_boolean);
            }
        });

        btnAbout.setOnClickListener(new View.OnClickListener(){
            @Override
            public  void onClick(View v){
                Intent intent = new Intent(MainActivity.this, About.class);
                startActivity(intent);
            }
        });




    }


    private Boolean revertir_variable_activo(){
        activo = !activo;
        return activo;
    }


}