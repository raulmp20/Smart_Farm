package com.example.pedroluis;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.UnsupportedEncodingException;

public class AeradorActivity extends AppCompatActivity {
    // pra consultar o banco de dados
    public MqttAndroidClient mqttAndroidClient;
    // acessar o banco de dados, var. aux. para banco de dados
    MqttHelper mqttHelper;

    // Recebendo variáveis de outras telas
    /*Intent firstCheck  = getIntent();
    String firstCheckAerador = firstCheck.getStringExtra("firstCheckAerador");*/
    boolean firstCheckAerador = true;

    // Botão atualizar
    Button atualizar;
    // Text Views
    TextView valor_media_hora;
    TextView valor_media_dia;
    TextView valor_ultima_instancia;
    TextView valor_modulo_ar;
    // Strings que armazena os valores a serem exibidos em Text View
    String val_media_dia = "";
    String val_media_hora = "";
    String val_inst;  // Valor instantâneo do aerador
    // "Aguardando informações dos sensores
    String info = "Em análise";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aerador);
        startMqtt();

        System.out.println(firstCheckAerador);

        Button voltar;
        voltar = findViewById(R.id.button_voltar_a);

        atualizar = findViewById(R.id.button_atualizar_a);

        voltar.setOnClickListener(v -> {
            // Mudando a tela para a tela menu
            Intent mudar = new Intent(AeradorActivity.this, MenuActivity.class);
            startActivity(mudar);
        });
        valor_media_hora = findViewById(R.id.media_hora_valor_ar);
        valor_media_hora.setText(info);
        valor_ultima_instancia = findViewById(R.id.ult_oco_valor_ar);
        valor_ultima_instancia.setText(info);
        valor_media_dia = findViewById(R.id.media_dia_valor_ar);
        valor_media_dia.setText(info);
        valor_modulo_ar = findViewById(R.id.valor_modulo_ar);
        valor_modulo_ar.setText(info);
    }

    private void startMqtt() {
        mqttHelper = new MqttHelper(getApplicationContext());
        mqttHelper.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean b, String s) {
                System.out.println("MQTT OK");
            }

            @Override
            public void connectionLost(Throwable throwable) {
                //Aparece essa mensagem sempre que a conexão for perdida
                Toast.makeText(getApplicationContext(), "Conexão perdida", Toast.LENGTH_SHORT).show();
            }
            @SuppressLint("SetTextI18n")
            @Override
            // messageArrived é uma função que é chamada toda vez que o cliente MQTT recebe uma mensagem
            public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
                Log.w("Debug", mqttMessage.toString());
                // Calculando as médias 1h e 24h
                if (topic.equals("Smart_Farm/Sensores/Aerador")) {
                    // Pegando o valor instantâneo do aerador
                    long tempo = System.currentTimeMillis() / 1000;
                    long tempo_dia = System.currentTimeMillis() / 1000;
                    //**************************************************
                    valor_ultima_instancia = findViewById(R.id.ult_oco_valor_ar);
                    val_inst = mqttMessage.toString();
                    //**************************************************
                    // Calculando a media 1h dos valores do aerador
                    valor_media_hora = findViewById(R.id.media_hora_valor_ar);

                    int cont_min = 0;
                    int cont_hora = 0;
                    float media_hora = 0;
                    float soma_hora = 0;
                    float soma_media = 0;
                    float num;   // Transforma string em float para cálculo

                    num = Float.parseFloat(val_inst);
                    cont_min++;
                    soma_hora += num;

                    if ((System.currentTimeMillis() / 1000) - tempo >= 3600) {
                        media_hora = soma_hora / cont_min;
                        soma_media += media_hora;
                        cont_hora++;
                        val_media_hora = Float.toString(media_hora);
                        valor_media_hora.setText(val_media_hora);
                        tempo = System.currentTimeMillis() / 1000;
                        soma_hora = 0;
                        cont_min = 0;
                    }else {
                        valor_media_hora.setText("Aguarde medições");
                    }
                    //*****************************************************
                    valor_media_dia = findViewById(R.id.media_dia_valor_ar);
                    float media_dia = 0;

                    if ((System.currentTimeMillis() / 1000) - tempo_dia >= 86400) {
                        media_dia = soma_media / cont_hora;
                        val_media_dia = Float.toString(media_dia);
                        //val_media_dia = Arrays.toString(new String[]{val_media_d,ia});
                        //valor_media_dia.setText(val_media_dia);
                        tempo_dia = System.currentTimeMillis() / 1000;
                    } else {
                        valor_media_dia.setText("Aguarde medições");
                    }

                    if(firstCheckAerador){
                        val_inst = mqttMessage.toString();
                        valor_ultima_instancia.setText(val_inst);
                        valor_media_hora.setText(val_media_hora);
                        valor_media_dia.setText(val_media_dia);
                        firstCheckAerador = false;
                    }
                }
                if(topic.equals("Smart_Farm/Sensores/Aerador/Status")){
                    valor_modulo_ar = findViewById(R.id.valor_modulo_ar);
                    switch (mqttMessage.toString()){
                        case("1"):
                            valor_modulo_ar.setText("Em funcionamento");
                            break;
                        case("0"):
                            valor_modulo_ar.setText("Não está funcionando");
                            break;
                        default:
                            break;
                    }
                }

                atualizar.setOnClickListener(view -> {
                    try {
                        valor_ultima_instancia.setText(val_inst);
                        valor_media_hora.setText(val_media_hora);
                        valor_media_dia.setText(val_media_dia);
                    } catch (NullPointerException ignored){}
                });
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
            }
        });
    }

    //Bloco que envia comandos para o broker
    void publish(String payload, String topic) {
        byte[] encodedPayload = new byte[0];
        //teste de conexão
        try {
            encodedPayload = payload.getBytes("UTF-8");
            MqttMessage message = new MqttMessage(encodedPayload);
            mqttHelper.mqttAndroidClient.publish(topic, message);
        } catch (UnsupportedEncodingException | MqttException e) {
            e.printStackTrace();
        }
    }
}
