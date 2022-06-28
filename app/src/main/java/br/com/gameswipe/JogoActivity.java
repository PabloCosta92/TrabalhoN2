package br.com.gameswipe;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Locale;

public class JogoActivity extends AppCompatActivity {
    private static final int PRA_CIMA = 0;
    private static final int PRA_BAIXO = 1;
    private static final int PRA_ESQUERDA = 2;
    private static final int PRA_DIREITA = 3;

    private int posicaoAtual; // Posição do jogo
    private int imagensEncontradas; // Contador de imagem, quando pares feitos
    private int imagemSeleciona01; // Quando a primeira imagem for selecionada
    private int imagemSeleciona02; // Quando a segunda imagem for selecionada
    private long tempoDeJogo; // Salva o tempo que durou o tempo do jogo
    private TextView txt; // Informa a posição que está
    private TextView imgEncontradas; // texto de exibição quando localizados pares
    private ImageView imageView; // Imagem centro do jogo
    private ImagenJogo imagemAtual; // Imagem
    private ArrayList<ImagenJogo> listaImagens;
    private SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss", new Locale("pt", "BR"));  // Formatar data

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jogo);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); // Bloquear rotação da tela

        getSupportActionBar().setTitle("Game Swipe"); // titulo na tela da execução do jogo
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // opção de voltar para o menu

        ConstraintLayout layoutFundo = findViewById(R.id.layoutFundo);  //declaração
        imageView = findViewById(R.id.image_view);
        txt = findViewById(R.id.txt);
        imgEncontradas = findViewById(R.id.img_encontradas);

        layoutFundo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (posicaoAtual == 0) {
                    imageView.setImageDrawable(getDrawable(R.drawable.img_swipe));
                    imageView.setBackgroundColor(Color.WHITE);
                    txt.setText("Localize as imagens\nmovendo a tela");

                } else {
                    imagemAtual = listaImagens.get(posicaoAtual);

                    // se a imagem já foi selecinada ignora
                    if (imagemAtual.isSelecionado()) {
                        Toast.makeText(JogoActivity.this, "Imagem já encontrada!", Toast.LENGTH_SHORT).show();
                        imageView.setImageDrawable(getDrawable(imagemAtual.getResId()));
                    } else {
                        Log.e("teste", "imagemSeleciona01:" + imagemSeleciona01);
                        Log.e("teste", "imagemSeleciona02:" + imagemSeleciona02);

                        // selecao da primeira imagem
                        if (imagemSeleciona01 == 0 || imagemSeleciona01 == posicaoAtual) {
                            imageView.setImageDrawable(getDrawable(imagemAtual.getResId()));
                            imagemAtual.setVisivel(true);
                            imagemSeleciona01 = posicaoAtual;
                        } else {
                            // selecao da segunda imagem
                            imageView.setImageDrawable(getDrawable(imagemAtual.getResId()));
                            imagemAtual.setVisivel(true);
                            imagemSeleciona02 = posicaoAtual;

                            // verifica se as duas imagens selecinadas são iguais
                            ImagenJogo imagem01 = listaImagens.get(imagemSeleciona01);


                            if (imagem01.getResId() == imagemAtual.getResId()) {
                                imagem01.setSelecionado(true);
                                imagemAtual.setSelecionado(true);

                                imagensEncontradas++;
                                imgEncontradas.setText("Imagens encontradas: " + imagensEncontradas);

                                Toast.makeText(JogoActivity.this, "Parabéns você encontrou um par de imagens!", Toast.LENGTH_LONG).show();

                                // limpas as imagens selecionadas
                                imagemSeleciona01 = 0;
                                imagemSeleciona02 = 0;

                                if (imagensEncontradas == 4) {
                                    long tempoAgora = System.currentTimeMillis();
                                    long tempoDecorrigo = tempoAgora - tempoDeJogo;

                                    Calendar calendar = Calendar.getInstance();
                                    calendar.setTimeInMillis(tempoDecorrigo);

                                    String tempo = sdf.format(calendar.getTime());
                                    tempo = tempo.substring(3, 5) + " minuto e " + tempo.substring(6, 8) + " segundos";

                                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(JogoActivity.this);
                                    alertDialogBuilder.setTitle("Parabéns!")
                                            .setMessage("Você encontrou todas as imagens!\nTempo total: " + tempo + ".\n\nJogar novamente?")
                                            .setPositiveButton("Sim", (dialog, id1) -> {
                                                dialog.cancel();
                                                iniciarJogo();
                                            })
                                            .setNegativeButton("Não", (dialog, id12) -> {
                                                onBackPressed();
                                                dialog.cancel();
                                            })
                                            .setCancelable(false)
                                            .show();
                                }
                            } else {
                                Toast.makeText(JogoActivity.this, "As imagens não são iguais!", Toast.LENGTH_SHORT).show();

                                imagem01.setVisivel(false);
                                imagemAtual.setVisivel(false);

                                // limpas as imagens selecionadas
                                imagemSeleciona01 = 0;
                                imagemSeleciona02 = 0;
                            }
                        }
                    }
                }
            }
        });

        layoutFundo.setOnTouchListener(new OnSwipeTouchListener(this) {
            @Override
            public void onSwipeBottom() {
                super.onSwipeBottom();

                alterarPosicao(PRA_BAIXO);
            }

            @Override
            public void onSwipeTop() {
                super.onSwipeTop();

                alterarPosicao(PRA_CIMA);
            }

            @Override
            public void onSwipeLeft() {
                super.onSwipeLeft();

                alterarPosicao(PRA_ESQUERDA);
            }

            @Override
            public void onSwipeRight() {
                super.onSwipeRight();

                alterarPosicao(PRA_DIREITA);
            }
        });

        iniciarJogo();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void iniciarJogo() {
        posicaoAtual = 0;
        imagensEncontradas = 0;
        imagemSeleciona01 = 0;
        imagemSeleciona02 = 0;
        tempoDeJogo = System.currentTimeMillis();

        imageView.setImageDrawable(getDrawable(R.drawable.img_swipe));
        imageView.setBackgroundColor(Color.WHITE);
        txt.setText("Localize as imagens\nmovendo a tela");
        imgEncontradas.setText("Imagens encontradas: " + imagensEncontradas);

        popularLista();
    }

    private void popularLista() {
        listaImagens = new ArrayList<>();
        listaImagens.add(new ImagenJogo(R.drawable.gaara, false, false));
        listaImagens.add(new ImagenJogo(R.drawable.gaara, false, false));
        listaImagens.add(new ImagenJogo(R.drawable.hinata, false, false));
        listaImagens.add(new ImagenJogo(R.drawable.hinata, false, false));
        listaImagens.add(new ImagenJogo(R.drawable.kakashi, false, false));
        listaImagens.add(new ImagenJogo(R.drawable.kakashi, false, false));
        listaImagens.add(new ImagenJogo(R.drawable.naruto, false, false));
        listaImagens.add(new ImagenJogo(R.drawable.naruto, false, false));

        // embaralhar lista
        Collections.shuffle(listaImagens);

        // possição inicial do jogo
        listaImagens.add(0, new ImagenJogo(9999, false, true));

    }

        // Logica da navegação
    private void alterarPosicao(int direcao) {
        switch (posicaoAtual) {
            case 0:
                imageView.setImageDrawable(getDrawable(R.drawable.img_swipe));
                imageView.setBackgroundColor(Color.WHITE);
                txt.setText("Localize as imagens\nmovendo a tela");

                switch (direcao) {
                    case PRA_CIMA:
                        posicaoAtual = 2;
                        txt.setText(String.valueOf(posicaoAtual));

                        imagemAtual = listaImagens.get(posicaoAtual);

                        if (imagemAtual.isVisivel()) {
                            imageView.setImageDrawable(getDrawable(imagemAtual.getResId()));
                        } else {
                            imageView.setImageDrawable(null);
                            imageView.setBackgroundColor(Color.GRAY);
                        }
                        break;
                    case PRA_BAIXO:
                        posicaoAtual = 7;
                        txt.setText(String.valueOf(posicaoAtual));

                        imagemAtual = listaImagens.get(posicaoAtual);

                        if (imagemAtual.isVisivel()) {
                            imageView.setImageDrawable(getDrawable(imagemAtual.getResId()));
                        } else {
                            imageView.setImageDrawable(null);
                            imageView.setBackgroundColor(Color.GRAY);
                        }
                        break;
                    case PRA_ESQUERDA:
                        posicaoAtual = 4;
                        txt.setText(String.valueOf(posicaoAtual));

                        imagemAtual = listaImagens.get(posicaoAtual);

                        if (imagemAtual.isVisivel()) {
                            imageView.setImageDrawable(getDrawable(imagemAtual.getResId()));
                        } else {
                            imageView.setImageDrawable(null);
                            imageView.setBackgroundColor(Color.GRAY);
                        }
                        break;
                    case PRA_DIREITA:
                        posicaoAtual = 5;
                        txt.setText(String.valueOf(posicaoAtual));

                        imagemAtual = listaImagens.get(posicaoAtual);

                        if (imagemAtual.isVisivel()) {
                            imageView.setImageDrawable(getDrawable(imagemAtual.getResId()));
                        } else {
                            imageView.setImageDrawable(null);
                            imageView.setBackgroundColor(Color.GRAY);
                        }
                        break;
                }
                break;
            case 1:
                switch (direcao) {
                    case PRA_CIMA:
                        //Toast.makeText(this, "Nada à cima", Toast.LENGTH_LONG).show();
                        break;
                    case PRA_BAIXO:
                        posicaoAtual = 4;
                        txt.setText(String.valueOf(posicaoAtual));

                        imagemAtual = listaImagens.get(posicaoAtual);

                        if (imagemAtual.isVisivel()) {
                            imageView.setImageDrawable(getDrawable(imagemAtual.getResId()));
                        } else {
                            imageView.setImageDrawable(null);
                            imageView.setBackgroundColor(Color.GRAY);
                        }
                        break;
                    case PRA_ESQUERDA:
                        // Toast.makeText(this, "Nada à esquerda", Toast.LENGTH_LONG).show();
                        break;
                    case PRA_DIREITA:
                        posicaoAtual = 2;
                        txt.setText(String.valueOf(posicaoAtual));

                        imagemAtual = listaImagens.get(posicaoAtual);

                        if (imagemAtual.isVisivel()) {
                            imageView.setImageDrawable(getDrawable(imagemAtual.getResId()));
                        } else {
                            imageView.setImageDrawable(null);
                            imageView.setBackgroundColor(Color.GRAY);
                        }
                        break;
                }
                break;
            case 2:
                switch (direcao) {
                    case PRA_CIMA:
                        // Toast.makeText(this, "Nada à cima", Toast.LENGTH_LONG).show();
                        break;
                    case PRA_BAIXO:
                        posicaoAtual = 0;
                        txt.setText(String.valueOf(posicaoAtual));

                        imageView.setImageDrawable(getDrawable(R.drawable.img_swipe));
                        imageView.setBackgroundColor(Color.WHITE);
                        txt.setText("Localize as imagens\nmovendo a tela");
                        break;
                    case PRA_ESQUERDA:
                        posicaoAtual = 1;
                        txt.setText(String.valueOf(posicaoAtual));

                        imagemAtual = listaImagens.get(posicaoAtual);

                        if (imagemAtual.isVisivel()) {
                            imageView.setImageDrawable(getDrawable(imagemAtual.getResId()));
                        } else {
                            imageView.setImageDrawable(null);
                            imageView.setBackgroundColor(Color.GRAY);
                        }
                        break;
                    case PRA_DIREITA:
                        posicaoAtual = 3;
                        txt.setText(String.valueOf(posicaoAtual));

                        imagemAtual = listaImagens.get(posicaoAtual);

                        if (imagemAtual.isVisivel()) {
                            imageView.setImageDrawable(getDrawable(imagemAtual.getResId()));
                        } else {
                            imageView.setImageDrawable(null);
                            imageView.setBackgroundColor(Color.GRAY);
                        }
                        break;
                }
                break;
            case 3:
                switch (direcao) {
                    case PRA_CIMA:
                        // Toast.makeText(this, "Nada à cima", Toast.LENGTH_LONG).show();
                        break;
                    case PRA_BAIXO:
                        posicaoAtual = 5;
                        txt.setText(String.valueOf(posicaoAtual));

                        imagemAtual = listaImagens.get(posicaoAtual);

                        if (imagemAtual.isVisivel()) {
                            imageView.setImageDrawable(getDrawable(imagemAtual.getResId()));
                        } else {
                            imageView.setImageDrawable(null);
                            imageView.setBackgroundColor(Color.GRAY);
                        }
                        break;
                    case PRA_ESQUERDA:
                        posicaoAtual = 2;
                        txt.setText(String.valueOf(posicaoAtual));

                        imagemAtual = listaImagens.get(posicaoAtual);

                        if (imagemAtual.isVisivel()) {
                            imageView.setImageDrawable(getDrawable(imagemAtual.getResId()));
                        } else {
                            imageView.setImageDrawable(null);
                            imageView.setBackgroundColor(Color.GRAY);
                        }
                        break;
                    case PRA_DIREITA:
                        // Toast.makeText(this, "Nada à direita", Toast.LENGTH_LONG).show();
                        break;
                }
                break;
            case 4:
                switch (direcao) {
                    case PRA_CIMA:
                        posicaoAtual = 1;
                        txt.setText(String.valueOf(posicaoAtual));

                        imagemAtual = listaImagens.get(posicaoAtual);

                        if (imagemAtual.isVisivel()) {
                            imageView.setImageDrawable(getDrawable(imagemAtual.getResId()));
                        } else {
                            imageView.setImageDrawable(null);
                            imageView.setBackgroundColor(Color.GRAY);
                        }
                        break;
                    case PRA_BAIXO:
                        posicaoAtual = 6;
                        txt.setText(String.valueOf(posicaoAtual));

                        imagemAtual = listaImagens.get(posicaoAtual);

                        if (imagemAtual.isVisivel()) {
                            imageView.setImageDrawable(getDrawable(imagemAtual.getResId()));
                        } else {
                            imageView.setImageDrawable(null);
                            imageView.setBackgroundColor(Color.GRAY);
                        }
                        break;
                    case PRA_ESQUERDA:
                        // Toast.makeText(this, "Nada à esquerda", Toast.LENGTH_LONG).show();
                        break;
                    case PRA_DIREITA:
                        posicaoAtual = 0;
                        txt.setText(String.valueOf(posicaoAtual));

                        imageView.setImageDrawable(getDrawable(R.drawable.img_swipe));
                        imageView.setBackgroundColor(Color.WHITE);
                        txt.setText("Localize as imagens\nmovendo a tela");
                        break;
                }
                break;
            case 5:
                switch (direcao) {
                    case PRA_CIMA:
                        posicaoAtual = 3;
                        txt.setText(String.valueOf(posicaoAtual));

                        imagemAtual = listaImagens.get(posicaoAtual);

                        if (imagemAtual.isVisivel()) {
                            imageView.setImageDrawable(getDrawable(imagemAtual.getResId()));
                        } else {
                            imageView.setImageDrawable(null);
                            imageView.setBackgroundColor(Color.GRAY);
                        }
                        break;
                    case PRA_BAIXO:
                        posicaoAtual = 8;
                        txt.setText(String.valueOf(posicaoAtual));

                        imagemAtual = listaImagens.get(posicaoAtual);

                        if (imagemAtual.isVisivel()) {
                            imageView.setImageDrawable(getDrawable(imagemAtual.getResId()));
                        } else {
                            imageView.setImageDrawable(null);
                            imageView.setBackgroundColor(Color.GRAY);
                        }
                        break;
                    case PRA_ESQUERDA:
                        posicaoAtual = 0;
                        txt.setText(String.valueOf(posicaoAtual));

                        imageView.setImageDrawable(getDrawable(R.drawable.img_swipe));
                        imageView.setBackgroundColor(Color.WHITE);
                        txt.setText("Localize as imagens\nmovendo a tela");
                        break;
                    case PRA_DIREITA:
                        // Toast.makeText(this, "Nada à direita", Toast.LENGTH_LONG).show();
                        break;
                }
                break;
            case 6:
                switch (direcao) {
                    case PRA_CIMA:
                        posicaoAtual = 4;
                        txt.setText(String.valueOf(posicaoAtual));

                        imagemAtual = listaImagens.get(posicaoAtual);

                        if (imagemAtual.isVisivel()) {
                            imageView.setImageDrawable(getDrawable(imagemAtual.getResId()));
                        } else {
                            imageView.setImageDrawable(null);
                            imageView.setBackgroundColor(Color.GRAY);
                        }
                        break;
                    case PRA_BAIXO:
                        // Toast.makeText(this, "Nada à baixo", Toast.LENGTH_LONG).show();
                        break;
                    case PRA_ESQUERDA:
                        // Toast.makeText(this, "Nada à esquerda", Toast.LENGTH_LONG).show();
                        break;
                    case PRA_DIREITA:
                        posicaoAtual = 7;
                        txt.setText(String.valueOf(posicaoAtual));

                        imagemAtual = listaImagens.get(posicaoAtual);

                        if (imagemAtual.isVisivel()) {
                            imageView.setImageDrawable(getDrawable(imagemAtual.getResId()));
                        } else {
                            imageView.setImageDrawable(null);
                            imageView.setBackgroundColor(Color.GRAY);
                        }
                        break;
                }
                break;
            case 7:
                switch (direcao) {
                    case PRA_CIMA:
                        posicaoAtual = 0;
                        txt.setText(String.valueOf(posicaoAtual));

                        imageView.setImageDrawable(getDrawable(R.drawable.img_swipe));
                        imageView.setBackgroundColor(Color.WHITE);
                        txt.setText("Localize as imagens\nmovendo a tela");
                        break;
                    case PRA_BAIXO:
                        // Toast.makeText(this, "Nada à baixo", Toast.LENGTH_LONG).show();
                        break;
                    case PRA_ESQUERDA:
                        posicaoAtual = 6;
                        txt.setText(String.valueOf(posicaoAtual));

                        imagemAtual = listaImagens.get(posicaoAtual);

                        if (imagemAtual.isVisivel()) {
                            imageView.setImageDrawable(getDrawable(imagemAtual.getResId()));
                        } else {
                            imageView.setImageDrawable(null);
                            imageView.setBackgroundColor(Color.GRAY);
                        }
                        break;
                    case PRA_DIREITA:
                        posicaoAtual = 8;
                        txt.setText(String.valueOf(posicaoAtual));

                        imagemAtual = listaImagens.get(posicaoAtual);

                        if (imagemAtual.isVisivel()) {
                            imageView.setImageDrawable(getDrawable(imagemAtual.getResId()));
                        } else {
                            imageView.setImageDrawable(null);
                            imageView.setBackgroundColor(Color.GRAY);
                        }
                        break;
                }
                break;
            case 8:
                switch (direcao) {
                    case PRA_CIMA:
                        posicaoAtual = 5;
                        txt.setText(String.valueOf(posicaoAtual));

                        imagemAtual = listaImagens.get(posicaoAtual);

                        if (imagemAtual.isVisivel()) {
                            imageView.setImageDrawable(getDrawable(imagemAtual.getResId()));
                        } else {
                            imageView.setImageDrawable(null);
                            imageView.setBackgroundColor(Color.GRAY);
                        }
                        break;
                    case PRA_BAIXO:
                        //Toast.makeText(this, "Nada à baixo", Toast.LENGTH_LONG).show();
                        break;
                    case PRA_ESQUERDA:
                        posicaoAtual = 7;
                        txt.setText(String.valueOf(posicaoAtual));

                        imagemAtual = listaImagens.get(posicaoAtual);

                        if (imagemAtual.isVisivel()) {
                            imageView.setImageDrawable(getDrawable(imagemAtual.getResId()));
                        } else {
                            imageView.setImageDrawable(null);
                            imageView.setBackgroundColor(Color.GRAY);
                        }
                        break;
                    case PRA_DIREITA:
                        // Toast.makeText(this, "Nada à direita", Toast.LENGTH_LONG).show();
                        break;
                }
                break;
        }
    }
}