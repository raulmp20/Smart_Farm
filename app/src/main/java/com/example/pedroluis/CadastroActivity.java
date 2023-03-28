package com.example.pedroluis;


import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.UnsupportedEncodingException;
import java.util.Locale;

public class CadastroActivity extends AppCompatActivity {

    MqttHelper mqttHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);
        startMqtt();
        // Pegando as informações das caixas texto
        EditText nome_c = findViewById(R.id.nome_cadastro);
        EditText senha_c = findViewById(R.id.senha_cadastro);
        EditText senha_c_confirm = findViewById(R.id.confirm_senha_cadastro);
        EditText email_c = findViewById(R.id.email_cadastro);

        Button realizar_Cadastro = findViewById(R.id.fazer_cadastro);

        // Botão de ocultar senha
        CheckBox esconderSenha = findViewById(R.id.oculta_senha_cadastro);
        // Verifica se a caixa está selecionada
        esconderSenha.setOnCheckedChangeListener((buttonView, isChecked) -> {
            //se estiver marcado exibe a senha caso contrário mantém protegida
            if(isChecked){
                senha_c.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            }else{
                senha_c.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
        });
        // Botão de ocultar senha
        CheckBox esconderConfirmSenha = findViewById(R.id.oculta_confirm_senha_cadastro);
        // Verifica se a caixa está selecionada
        esconderConfirmSenha.setOnCheckedChangeListener((buttonView, isChecked) -> {
            //se estiver marcado exibe a senha caso contrário mantém protegida
            if(isChecked){
                senha_c_confirm.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            } else {
                senha_c_confirm.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
        });

        realizar_Cadastro.setOnClickListener(view -> {
            String nome_cadastro = nome_c.getText().toString();
            String senha_cadastro = senha_c.getText().toString();
            String confirm_senha_cadastro = senha_c_confirm.getText().toString();
            String email_cadastro = email_c.getText().toString();

            // Verificando se a senha é igual a senha confirmada
            if(senha_cadastro.equals(confirm_senha_cadastro) && !senha_cadastro.isEmpty()) {
                // Aplicando REGEX no nome
                if (nome_cadastro.matches("^[a-zA-Z0-9Çç]+$")) {
                    // Aplicando REGEX no email
                    if (email_cadastro.matches("^[a-zA-Z0-9.]+@(gmail\\.com|hotmail\\.com|yahoo\\.com\\.br)$")) {
                        publish(nome_cadastro, "Smart_Farm/Cadastro/Nome");
                        publish(senha_cadastro, "Smart_Farm/Cadastro/Senha");
                        publish(email_cadastro, "Smart_Farm/Cadastro/E-mail");
                    }
                    else if (!email_cadastro.isEmpty())
                        Toast.makeText(CadastroActivity.this, "Coloque um e-mail válido", Toast.LENGTH_SHORT).show();
                } else if (!nome_cadastro.isEmpty())
                    Toast.makeText(CadastroActivity.this, "Coloque um nome válido", Toast.LENGTH_SHORT).show();

            } else if (!senha_cadastro.equals(confirm_senha_cadastro)){
                Toast.makeText(CadastroActivity.this, "As senhas não se coincidem", Toast.LENGTH_SHORT).show();
            }
            if(nome_cadastro.isEmpty()) {
                Toast.makeText(CadastroActivity.this, "Insira um nome", Toast.LENGTH_SHORT).show();
            }
            if(senha_cadastro.isEmpty()) {
                Toast.makeText(CadastroActivity.this, "Insira uma senha", Toast.LENGTH_SHORT).show();
            }
            if(confirm_senha_cadastro.isEmpty()) {
                Toast.makeText(CadastroActivity.this, "Insira uma confirmação de senha", Toast.LENGTH_SHORT).show();
            }
            if (email_cadastro.isEmpty()) {
                Toast.makeText(CadastroActivity.this, "Insira um e-mail", Toast.LENGTH_SHORT).show();
            }
        });
    }
    // Inicia comunicação com o MQTT
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
            @Override
            // messageArrived é uma função que é chamada toda vez que o cliente MQTT recebe uma mensagem
            public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
                Log.w("Debug", mqttMessage.toString());
                // Exibindo na tela os retornos do Banco de Dados
                if(topic.equals("Smart_Farm/Cadastro/Status")){
                    switch (mqttMessage.toString()){
                        case ("01"):
                            Toast.makeText(CadastroActivity.this, "Nome já cadastrado", Toast.LENGTH_SHORT).show();
                            break;
                        case ("10"):
                            Toast.makeText(CadastroActivity.this, "E-mail já cadastrado", Toast.LENGTH_SHORT).show();
                            break;
                        case ("0"):
                            Toast.makeText(CadastroActivity.this, "Erro no cadastro, tente novamente", Toast.LENGTH_SHORT).show();
                            break;
                        case ("1"):
                            Toast.makeText(CadastroActivity.this, "Cadastrado com sucesso", Toast.LENGTH_SHORT).show();
                            Intent cadastrado = new Intent(CadastroActivity.this, MainActivity.class);
                            startActivity(cadastrado);
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
