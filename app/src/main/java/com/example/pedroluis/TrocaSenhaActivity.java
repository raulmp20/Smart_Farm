package com.example.pedroluis;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.UnsupportedEncodingException;
// Serve para usuários que querem trocar a senha antes mesmo de fazerem o login
public class TrocaSenhaActivity extends AppCompatActivity {
    // Pra consultar o banco de dados
    public MqttAndroidClient mqttAndroidClient;
    // Acessar o banco de dados, var. aux. para banco de dados
    MqttHelper mqttHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_troca_senha);
        startMqtt();
        // Pegando as informações das caixas texto
        EditText email_att;
        email_att = findViewById(R.id.emailAntesLogin);
        EditText senha_att;
        senha_att = findViewById(R.id.senhaAntesLogin);
        EditText confirm_senha_att;
        confirm_senha_att = findViewById(R.id.confirm_senhaAntesLogin);
        EditText pin_att;
        pin_att = findViewById(R.id.confirm_pinAntesLogin);
        // Botão "Enviar Pin"
        Button enviaPin;
        enviaPin = findViewById(R.id.button_envia_pinAntesLogin);
        // Botão "salvar"
        Button salvar;
        salvar = findViewById(R.id.button_salvar_configAntesLogin);
        // Botão "voltar"
        Button voltar;
        voltar = findViewById(R.id.button_voltarLogin);

        // Botão de ocultar senha
        CheckBox esconderSenha = findViewById(R.id.oculta_senhaAntesLogin);
        // Verifica se a caixa está selecionada
        esconderSenha.setOnCheckedChangeListener((buttonView, isChecked) -> {
            //se estiver marcado exibe a senha caso contrário mantém protegida
            if(isChecked){
                senha_att.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            }else{
                senha_att.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
        });
        // Botão de ocultar senha
        CheckBox esconderSenhaConfirm = findViewById(R.id.oculta_senha_confirm_senhaAntesLogin);
        // Verifica se a caixa está selecionada
        esconderSenhaConfirm.setOnCheckedChangeListener((buttonView, isChecked) -> {
            //se estiver marcado exibe a senha caso contrário mantém protegida
            if(isChecked){
                confirm_senha_att.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            }else{
                confirm_senha_att.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
        });

        // Enviando Pin
        enviaPin.setOnClickListener(v -> {
            String email = email_att.getText().toString();
            publish(email, "Smart_Farm/Atualiza/enviaPin");
        });

        // Atualizando os dados
        salvar.setOnClickListener(v -> {
            // Pegando as informações dos EditText e adicionando a uma string
            String senhaNova = senha_att.getText().toString();
            String confirmSenhaNova = confirm_senha_att.getText().toString();
            String email = email_att.getText().toString();
            String pin = pin_att.getText().toString();
            // Verificando se a senha é igual a senha confirmada
            if (senhaNova.equals(confirmSenhaNova) && !confirmSenhaNova.isEmpty()) {
                publish(email, "Smart_Farm/Atualiza/Email");
                publish(senhaNova, "Smart_Farm/Atualiza/Senha");
                publish(pin, "Smart_Farm/Atualiza/Token");
            } else if (!senhaNova.equals(confirmSenhaNova)) {
                Toast.makeText(TrocaSenhaActivity.this, "As senhas não se coincidem", Toast.LENGTH_SHORT).show();
            }
            if(senhaNova.isEmpty()) {
                Toast.makeText(TrocaSenhaActivity.this, "Insira uma senha", Toast.LENGTH_SHORT).show();
            }
            if(confirmSenhaNova.isEmpty()) {
                Toast.makeText(TrocaSenhaActivity.this, "Insira uma confirmação de senha", Toast.LENGTH_SHORT).show();
            }
            if (email.isEmpty()) {
                Toast.makeText(TrocaSenhaActivity.this, "Insira um e-mail", Toast.LENGTH_SHORT).show();
            }
        });
        // Voltando ao menu
        voltar.setOnClickListener(v -> {
            // Mudando a tela para a tela menu
            Intent mudar = new Intent(TrocaSenhaActivity.this, LoginActivity.class);
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
                // Aparece essa mensagem sempre que a conexão for perdida
                Toast.makeText(getApplicationContext(), "Conexão perdida", Toast.LENGTH_SHORT).show();
            }

            @Override
            // messageArrived é uma função que é chamada toda vez que o cliente MQTT recebe uma mensagem
            public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
                Log.w("Debug", mqttMessage.toString());
                if (topic.equals("Smart_Farm/Atualiza/Status")) {
                    switch (mqttMessage.toString()) {
                        // TROCAR: 00 -> E-mail não encontrado, 01 -> Pin inválido, 11 -> Senha atualizada
                        case ("00"):
                            Toast.makeText(TrocaSenhaActivity.this, "E-mail não encontrado", Toast.LENGTH_SHORT).show();
                            break;
                        case ("10"):
                            Toast.makeText(TrocaSenhaActivity.this, "Pin inválido", Toast.LENGTH_SHORT).show();
                            break;
                        case ("11"):
                            Toast.makeText(TrocaSenhaActivity.this, "Senha atualizada", Toast.LENGTH_SHORT).show();
                            break;
                        default:
                            break;
                    }
                }
                if(topic.equals("Smart_Farm/Atualiza/StatusPin")) {
                    switch (mqttMessage.toString()) {
                        case ("0"):
                            Toast.makeText(TrocaSenhaActivity.this, "E-mail não encontrado", Toast.LENGTH_SHORT).show();
                            break;
                        case ("1"):
                            Toast.makeText(TrocaSenhaActivity.this, "Pin Enviado", Toast.LENGTH_SHORT).show();
                            break;
                        default:
                            break;
                    }
                }
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