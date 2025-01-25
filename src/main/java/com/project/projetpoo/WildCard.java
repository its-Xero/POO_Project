package com.project.projetpoo;
public class WildCard extends Card {
    public WildCard() {
        super( "none", "wild"); // Pas de couleur par défaut
    }

    @Override
    public void setCouleur(String couleur) {
        super.setCouleur(couleur);
    }

    @Override
    public String getCouleur() {
        return super.getCouleur();
    }

    @Override
    public String toString() {
        return "Card{couleur='" + getCouleur() + "', symbol=" + getSymbol() + '}';
    }
}

