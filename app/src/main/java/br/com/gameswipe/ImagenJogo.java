package br.com.gameswipe;

public class ImagenJogo {
    private int resId; // id da imagem
    private boolean selecionado; // resultado de quando encontrado o par
    private boolean visivel;  // resultado de pares prontos

    public ImagenJogo(int redId, boolean selecionado, boolean visivel) {
        this.resId = redId;
        this.selecionado = selecionado;
        this.visivel = visivel;
    }

    public int getResId() {
        return resId;
    }

    public void setResId(int resId) {
        this.resId = resId;
    }

    public boolean isSelecionado() {
        return selecionado;
    }

    public void setSelecionado(boolean selecionado) {
        this.selecionado = selecionado;
    }

    public boolean isVisivel() {
        return visivel;
    }

    public void setVisivel(boolean visivel) {
        this.visivel = visivel;
    }
}
