package com.example.pedroluis;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.UnsupportedEncodingException;

public class LoginActivity extends AppCompatActivity {
    // Pra consultar o banco de dados
    public MqttAndroidClient mqttAndroidClient;
    // Acessar o banco de dados, var. aux. para banco de dados
    MqttHelper mqttHelper;

    @Override  // coloca coisas basicas da tela, funcionalidades
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        startMqtt();
        // Botão login
        Button fazer_login = findViewById(R.id.fazer_login);
        // Botão "esqueci senha"
        Button rec_senha = findViewById(R.id.button_rec_senha);
        // Pra pegar as informações da caixa de texto
        EditText nome_login = findViewById(R.id.nome_login);
        EditText senha_login = findViewById(R.id.senha_login);
        // Botão de ocultar senha
        CheckBox esconderSenha = findViewById(R.id.oculta_senha);

        // Verifica se a caixa está selecionada
        esconderSenha.setOnCheckedChangeListener((buttonView, isChecked) -> {
            //se estiver marcado exibe a senha caso contrário mantém protegida
            if(isChecked){
                senha_login.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            }else{
                senha_login.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
        });
        // pra fazer algo quando clicamos no botão
        fazer_login.setOnClickListener(view -> {
            // atribuindo a "nome" o nome que eu escrever na tela
            String nome = nome_login.getText().toString();
            // atribuindo a "senha" a senha que eu escrever na tela
            String senha = senha_login.getText().toString();
            if(nome.isEmpty())
                Toast.makeText(LoginActivity.this, "Insira um nome", Toast.LENGTH_SHORT).show();
            if(senha.isEmpty())
                Toast.makeText(LoginActivity.this, "Insira uma senha", Toast.LENGTH_SHORT).show();
            if(!nome.isEmpty() && !senha.isEmpty()){
                // verificar se existe o login
                publish(nome, "Smart_Farm/Login/Nome");
                publish(senha, "Smart_Farm/Login/Senha");
            }
        });
        // Recuperando a senha
        rec_senha.setOnClickListener(view -> {
            // Enviando um email para o usuário trocar a senha
            Intent logado = new Intent(LoginActivity.this, TrocaSenhaActivity.class);
            startActivity(logado);
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
                if(topic.equals("Smart_Farm/Login/Status")){
                    switch (mqttMessage.toString()){
                        case ("00"):
                            Toast.makeText(LoginActivity.this, "Nome de usuário não encontrado", Toast.LENGTH_SHORT).show();
                            break;
                        case ("10"):
                            Toast.makeText(LoginActivity.this, "Senha incorreta", Toast.LENGTH_SHORT).show();
                            break;
                        /*case ("01"):
                            Toast.makeText(LoginActivity.this, "Senha incorreta", Toast.LENGTH_SHORT).show();
                            break;*/
                        case ("11"):
                            Intent logado = new Intent(LoginActivity.this, MenuActivity.class);
                            startActivity(logado);
                            // Exclui essa tela ao sair para não guardar as info que pus nela
                            onRestart();
                            break;
                        default:
                            break;
                    }
                }
                if(topic.equals("Smart_Farm/RecSenha/Status")){
                    switch (mqttMessage.toString()){
                        case ("0"):
                            Toast.makeText(LoginActivity.this, "E-mail não encontrado", Toast.LENGTH_SHORT).show();
                            break;
                        case ("1"):
                            // CODAR AQUI
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