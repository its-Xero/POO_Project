package com.project.projetpoo.gui;

import com.project.projetpoo.Player;
import com.project.projetpoo.Card;
import com.project.projetpoo.Game;
import com.project.projetpoo.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PlayerHandPanel extends JPanel {
    private Player player;
    private final Gamegui game;
    private final UNOGUI gui;
    private JButton drawCardButton;

    public PlayerHandPanel(Player player, Gamegui game, UNOGUI gui) {
        this.player = player;
        this.game = game;
        this.gui = gui;
        setBackground(Color.WHITE);
        setLayout(new FlowLayout(FlowLayout.LEFT));
        initializeComponents();
        updateHand();
    }

    private void initializeComponents() {
        // Add "Draw Card" button
        drawCardButton = new JButton("Draw Card");
        drawCardButton.setFont(new Font("Arial", Font.BOLD, 14));
        drawCardButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                game.getDeck().drawCard(player, 1);
                gui.logMessage(player.getNom() + " draws a card.");
                gui.updateGameState();
            }
        });
        add(drawCardButton);
    }

    private ImageIcon loadCardImage(Card card) {
        String imagePath = getCardImagePath(card); // Get the image path for the card
        try {
            // Use ClassLoader to load the image from the resources folder
            java.net.URL imageUrl = getClass().getClassLoader().getResource(imagePath);
            if (imageUrl != null) {
                ImageIcon icon = new ImageIcon(imageUrl);
                Image image = icon.getImage();

                // Resize the image to fit the card dimensions
                int width = 80;  // Desired width
                int height = 120; // Desired height
                Image resizedImage = image.getScaledInstance(width, height, Image.SCALE_SMOOTH);

                return new ImageIcon(resizedImage);
            } else {
                System.err.println("Image not found: " + imagePath);
                return null;
            }
        } catch (Exception e) {
            System.err.println("Error loading image: " + imagePath);
            e.printStackTrace();
            return null;
        }
    }

    private String getCardImagePath(Card card) {
        if (card instanceof WildCard) {
            return "images/wild.png";
        } else if (card instanceof WildDrawFourCard) {
            return "images/wild_draw_four.png";
        } else if (card instanceof Skip) {
            return "images/skip_" + card.getCouleur().toLowerCase() + ".png";
        } else if (card instanceof Reverse) {
            return "images/reverse_" + card.getCouleur().toLowerCase() + ".png";
        } else if (card instanceof Drawtwo) {
            return "images/draw_two_" + card.getCouleur().toLowerCase() + ".png";
        } else {
            return "images/" + card.getCouleur().toLowerCase() + "_" + card.getSymbol() + ".png";
        }
    }


    public void updateHand() {
        removeAll(); // Clear the panel before updating
        for (Card card : player.getHand()) {
            ImageIcon icon = loadCardImage(card);
            if (icon != null) {
                JButton cardButton = new JButton(icon);
                cardButton.setPreferredSize(new Dimension(80, 120));
                cardButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (game.isCardPlayable(card, game.getTopCard())) {
                            // Handle special card effect BEFORE playing the card
                            handleSpecialCardEffect(card);

                            // Play the card
                            game.playCard(player, card);
                            gui.logMessage(player.getNom() + " plays " + getCardDisplayName(card));

                            // Check if the player's hand is empty (game over)
                            if (player.getHand().isEmpty()) {
                                gui.checkGameOver();
                            }

                            // Update the GUI
                            gui.updateGameState();
                        } else {
                            JOptionPane.showMessageDialog(PlayerHandPanel.this, "You cannot play this card!", "Invalid Move", JOptionPane.WARNING_MESSAGE);
                        }
                    }
                });
                add(cardButton);
            }
        }
        add(drawCardButton); // Re-add the "Draw Card" button
        revalidate();
        repaint();
    }

    private void handleSpecialCardEffect(Card card) {
        if (card instanceof WildCard) {
            handleWildCard((WildCard) card);
        } else if (card instanceof WildDrawFourCard) {
            handleWildDrawFour((WildDrawFourCard) card);
        } else if (card instanceof Skip) {
            handleSkipCard();
        } else if (card instanceof Reverse) {
            handleReverseCard();
        } else if (card instanceof Drawtwo) {
            handleDrawTwoCard();
        }
    }

    private void handleWildCard(WildCard card) {
        String color = JOptionPane.showInputDialog(this, "Choose a color (red, green, blue, yellow):");
        if (color != null && (color.equalsIgnoreCase("red") || color.equalsIgnoreCase("green") ||
                color.equalsIgnoreCase("blue") || color.equalsIgnoreCase("yellow"))) {
            // Update the card's color BEFORE playing it
            ((WildCard) card).setCouleur(color); // Update the card being played
            gui.logMessage(player.getNom() + " chose " + color + " for the Wild Card.");
        } else {
            JOptionPane.showMessageDialog(this, "Invalid color! Please choose again.", "Error", JOptionPane.ERROR_MESSAGE);
            handleWildCard(card); // Retry
        }
    }

    private void handleWildDrawFour(WildDrawFourCard card) {
        String color = JOptionPane.showInputDialog(this, "Choose a color (red, green, blue, yellow):");
        if (color != null && (color.equalsIgnoreCase("red") || color.equalsIgnoreCase("green") ||
                color.equalsIgnoreCase("blue") || color.equalsIgnoreCase("yellow"))) {
            // Update the card's color BEFORE playing it
            ((WildDrawFourCard) card).setCouleur(color); // Update the card being played
            Player nextPlayer = game.getNextPlayer();
            game.getDeck().drawCard(nextPlayer, 4);
            gui.logMessage(nextPlayer.getNom() + " draws 4 cards!");
            gui.logMessage(player.getNom() + " chose " + color + " for the Wild Draw Four Card.");
        } else {
            JOptionPane.showMessageDialog(this, "Invalid color! Please choose again.", "Error", JOptionPane.ERROR_MESSAGE);
            handleWildDrawFour(card); // Retry
        }
    }

    private void handleSkipCard() {
        gui.logMessage("Next player is skipped!");
        game.moveToNextPlayer();
    }

    private void handleReverseCard() {
        gui.logMessage("Turn order reversed!");
        game.setClockwise(!game.isClockwise());
    }

    private void handleDrawTwoCard() {
        Player nextPlayer = game.getNextPlayer();
        game.getDeck().drawCard(nextPlayer, 2);
        gui.logMessage(nextPlayer.getNom() + " draws 2 cards!");
    }

    // Helper method to get a user-friendly display name for cards
    private String getCardDisplayName(Card card) {
        if (card instanceof WildCard) {
            return "Wild";
        } else if (card instanceof WildDrawFourCard) {
            return "Wild Draw Four";
        } else if (card instanceof Skip) {
            return "Skip (" + card.getCouleur() + ")";
        } else if (card instanceof Reverse) {
            return "Reverse (" + card.getCouleur() + ")";
        } else if (card instanceof Drawtwo) {
            return "Draw Two (" + card.getCouleur() + ")";
        } else {
            return card.getSymbol() + " (" + card.getCouleur() + ")";
        }
    }

    public void setPlayer(Player player) {
        this.player = player; // Update the player
        updateHand(); // Refresh the hand display
    }
}