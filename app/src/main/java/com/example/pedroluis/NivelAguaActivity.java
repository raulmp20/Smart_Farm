package com.example.pedroluis;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.UnsupportedEncodingException;

public class NivelAguaActivity extends AppCompatActivity {
    MqttHelper mqttHelper;

    boolean firstCheckNivel = true;

    // Botão atualizar
    Button atualizar;
    Button voltar;
    // Text Views
    TextView mediaHora;
    TextView mediaDia;
    TextView valorInstantaneio;
    TextView modulo;
    // Variáveis para controle de tempo
    long tempo;
    long tempoAntes = 0;
    // Adicinando uma informação inicial aos Text's View
    String info = "Em análise";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nivel_agua);
        startMqtt();

        // Pegando os valores mais recentes do BD
        if (firstCheckNivel) {
            publish("1", "Smart_Farm/Sensores/ph/Info");
        }

        // Instanciando os botões
        atualizar = findViewById(R.id.button_atualizar_n);
        voltar = findViewById(R.id.button_voltar_n);

        // Instanciando texts view
        mediaHora = findViewById(R.id.media_hora_valor_nivel);
        mediaDia = findViewById(R.id.media_dia_valor_nivel);
        valorInstantaneio = findViewById(R.id.ult_oco_valor_nivel);
        modulo = findViewById(R.id.valor_modulo_nivel);
        // Dando informações iniciais a eles
        mediaHora.setText(info);
        valorInstantaneio.setText(info);
        mediaDia.setText(info);
        modulo.setText(info);

        voltar.setOnClickListener(v-> {
            // mudando a tela para a tela menu
            Intent mudar = new Intent(NivelAguaActivity.this,MenuActivity.class);
            startActivity(mudar);
        });
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
                if (firstCheckNivel) {
                    if (topic.equals("Smart_Farm/Sensores/nivel/valorInstantaneo"))
                        valorInstantaneio.setText(mqttMessage.toString());

                    if (topic.equals("Smart_Farm/Sensores/nivel/valorMedioUmaHora"))
                        mediaHora.setText(mqttMessage.toString());

                    if (topic.equals("Smart_Farm/Sensores/nivel/valorMedioUmDia"))
                        mediaDia.setText(mqttMessage.toString());

                    if(topic.equals("Smart_Farm/Sensores/nivel/Status"))
                        switch (mqttMessage.toString()){
                            case("1"):
                                modulo.setText("Em funcionamento");
                                break;
                            case("0"):
                                modulo.setText("Não está funcionando");
                                break;
                            default:
                                break;
                        }
                    firstCheckNivel = false;
                }
                atualizar.setOnClickListener(view -> {
                    publish("1", "Smart_Farm/Sensores/nivel/Info");
                    Toast.makeText(NivelAguaActivity.this, "Aguarde as leituras", Toast.LENGTH_SHORT).show();
                    while (true) {
                        tempo = System.currentTimeMillis();
                        if (tempo - tempoAntes >= 1000) {
                            tempoAntes = tempo;
                            if (topic.equals("Smart_Farm/Sensores/nivel/valorInstantaneo"))
                                valorInstantaneio.setText(mqttMessage.toString());

                            if (topic.equals("Smart_Farm/Sensores/nivel/valorMedioUmaHora"))
                                mediaHora.setText(mqttMessage.toString());

                            if (topic.equals("Smart_Farm/Sensores/nivel/valorMedioUmDia"))
                                mediaDia.setText(mqttMessage.toString());

                            if(topic.equals("Smart_Farm/Sensores/nivel/Status"))
                                switch (mqttMessage.toString()){
                                    case("1"):
                                        modulo.setText("Em funcionamento");
                                        break;
                                    case("0"):
                                        modulo.setText("Não está funcionando");
                                        break;
                                    default:
                                        break;
                                }
                            break;
                        }
                    }
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