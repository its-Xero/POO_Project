package com.project.projetpoo;
public class Drawtwo extends Card{
    // carte comme +2 wla reverse wla skip
    public Drawtwo(String couleur) {
        super(couleur, "drawtwo"); // Appelle le constructeur de la classe parent
    }

    @Override
    public String toString() {
        return "Card{couleur='" + getCouleur() + "', symbol=" + getSymbol() + '}';
    }
}
