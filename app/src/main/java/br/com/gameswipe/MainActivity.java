package br.com.gameswipe;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); // Bloquear rotação da tela

        getSupportActionBar().hide(); // Esconder o menu superior

        Button btnJogar = findViewById(R.id.btn_jogar); // Botão jogar
        btnJogar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =  new Intent(MainActivity.this, JogoActivity.class);
                startActivity(intent); // Intent aplicado para prosseguir pra próxima tela
            }
        });
    }
}