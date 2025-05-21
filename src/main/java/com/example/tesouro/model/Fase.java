package com.example.tesouro.model;

public class Fase {
    private int numero;
    private String desafio;
    private String resposta;
    private String dica;

    public Fase(int numero, String desafio, String resposta, String dica) {
        this.numero = numero;
        this.desafio = desafio;
        this.resposta = resposta;
        this.dica = dica;
    }

    public int getNumero() { return numero; }
    public String getDesafio() { return desafio; }
    public String getResposta() { return resposta; }
    public String getDica() { return dica; }
}
