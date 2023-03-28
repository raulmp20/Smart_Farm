package com.example.pedroluis;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class MenuActivity extends AppCompatActivity {

    String firstCheckMain = "Tgg";
    // Passando esse parâmrtro para as próximas telas

    @Override  // coloca coisas basicas da tela, funcionalidades
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        // Representante dos botões do front-end para serem manipulados pelo back-end e enviados ao front-end
        Button ph;
        // Procure pelo componente com id = "ph" na tela citada acima
        ph = findViewById(R.id.button_ph);

        // Farei o mesmo esquema repetidamente
        ImageButton ph_image;
        ph_image = findViewById(R.id.ph);

        Button config;
        config = findViewById(R.id.button_config);

        ImageButton config_image;
        config_image = findViewById(R.id.config);

        Button umi_ar;
        umi_ar = findViewById(R.id.button_umi_ar);

        ImageButton umi_ar_image;
        umi_ar_image = findViewById(R.id.umi_ar);

        Button temp;
        temp = findViewById(R.id.button_temp);

        ImageButton temp_image;
        temp_image = findViewById(R.id.temp);

        Button calor;
        calor = findViewById(R.id.button_umi_substrato);

        ImageButton calor_image;
        calor_image = findViewById(R.id.umi_substrato);

        Button lum;
        lum = findViewById(R.id.button_lumi);

        ImageButton lum_image;
        lum_image = findViewById(R.id.lumi);

        Button nivel;
        nivel = findViewById(R.id.button_nivel);

        ImageButton nivel_image;
        nivel_image = findViewById(R.id.nivel);

        Button aerador;
        aerador = findViewById(R.id.button_aerador);

        ImageButton aerador_image;
        aerador_image = findViewById(R.id.aerador);

        Button sair;
        sair = findViewById(R.id.button_sair);

        // Condições caso eu clique nos botões
        config.setOnClickListener(v-> {
            // mudando a tela para a tela das informações do ph
            Intent mudar = new Intent(MenuActivity.this,ConfigActivity.class);
            startActivity(mudar);
        });
        config_image.setOnClickListener(v ->{
            // mudando a tela para a tela das informações do ph
            Intent mudar = new Intent(MenuActivity.this,ConfigActivity.class);
            startActivity(mudar);
            // Exclui essa tela ao sair para não guardar as info que pus nela
            onRestart();
        });
        ph.setOnClickListener(v-> {
            // mudando a tela para a tela das informações do ph
            Intent mudar = new Intent(MenuActivity.this,PhActivity.class);
            startActivity(mudar);
            // Exclui essa tela ao sair para não guardar as info que pus nela
            onRestart();
        });
        ph_image.setOnClickListener(v ->{
            // mudando a tela para a tela das informações do ph
            Intent mudar = new Intent(MenuActivity.this,PhActivity.class);
            startActivity(mudar);
            // Exclui essa tela ao sair para não guardar as info que pus nela
            onRestart();
        });
        umi_ar.setOnClickListener(v-> {
            // mudando a tela para a tela das informações do ph
            Intent mudar = new Intent(MenuActivity.this,UmiArActivity.class);
            startActivity(mudar);
            // Exclui essa tela ao sair para não guardar as info que pus nela
            onRestart();
        });
        umi_ar_image.setOnClickListener(v ->{
            // mudando a tela para a tela das informações do ph
            Intent mudar = new Intent(MenuActivity.this,UmiArActivity.class);
            startActivity(mudar);
            // Exclui essa tela ao sair para não guardar as info que pus nela
            onRestart();
        });
        temp.setOnClickListener(v-> {
            // mudando a tela para a tela das informações do ph
            Intent mudar = new Intent(MenuActivity.this,TemperaturaActivity.class);
            startActivity(mudar);
            // Exclui essa tela ao sair para não guardar as info que pus nela
            onRestart();
        });
        temp_image.setOnClickListener(v ->{
            // mudando a tela para a tela das informações do ph
            Intent mudar = new Intent(MenuActivity.this,TemperaturaActivity.class);
            startActivity(mudar);
            // Exclui essa tela ao sair para não guardar as info que pus nela
            onRestart();
        });
        calor.setOnClickListener(v-> {
            // mudando a tela para a tela das informações do ph
            Intent mudar = new Intent(MenuActivity.this,IndiceCalorActivity.class);
            startActivity(mudar);
            // Exclui essa tela ao sair para não guardar as info que pus nela
            onRestart();
        });
        calor_image.setOnClickListener(v ->{
            // mudando a tela para a tela das informações do ph
            Intent mudar = new Intent(MenuActivity.this,IndiceCalorActivity.class);
            startActivity(mudar);
            // Exclui essa tela ao sair para não guardar as info que pus nela
            onRestart();
        });
        lum.setOnClickListener(v-> {
            // mudando a tela para a tela das informações do ph
            Intent mudar = new Intent(MenuActivity.this,LuminosidadeActivity.class);
            startActivity(mudar);
            // Exclui essa tela ao sair para não guardar as info que pus nela
            onRestart();
        });
        lum_image.setOnClickListener(v ->{
            // mudando a tela para a tela das informações do ph
            Intent mudar = new Intent(MenuActivity.this,LuminosidadeActivity.class);
            startActivity(mudar);
            // Exclui essa tela ao sair para não guardar as info que pus nela
            onRestart();
        });
        nivel.setOnClickListener(v-> {
            // mudando a tela para a tela das informações do ph
            Intent mudar = new Intent(MenuActivity.this,NivelAguaActivity.class);
            startActivity(mudar);
            // Exclui essa tela ao sair para não guardar as info que pus nela
            onRestart();
        });
        nivel_image.setOnClickListener(v ->{
            // mudando a tela para a tela das informações do ph
            Intent mudar = new Intent(MenuActivity.this,NivelAguaActivity.class);
            startActivity(mudar);
            // Exclui essa tela ao sair para não guardar as info que pus nela
            onRestart();
        });
        aerador.setOnClickListener(v-> {
            // mudando a tela para a tela das informações do ph
            Intent mudar = new Intent(MenuActivity.this,AeradorActivity.class);
            startActivity(mudar);
            // Exclui essa tela ao sair para não guardar as info que pus nela
            onRestart();
        });
        aerador_image.setOnClickListener(v ->{
            // mudando a tela para a tela das informações do ph
            Intent mudar = new Intent(MenuActivity.this,AeradorActivity.class);
            startActivity(mudar);
            // Exclui essa tela ao sair para não guardar as info que pus nela
            onRestart();
        });
        sair.setOnClickListener(v-> {
            // mudando a tela para a tela das informações do ph
            Intent mudar = new Intent(MenuActivity.this,MainActivity.class);
            startActivity(mudar);
            // Exclui essa tela ao sair para não guardar as info que pus nela
            onRestart();
        });
    }
}
