package com.project.projetpoo;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
//import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import java.io.InputStream;
import java.util.Objects;

//import java.util.Objects;


public class UNOgameApp extends Application {
    private Game game;
    private Label topCardLabel;
    private Label currentPlayerLabel;
    private HBox playerHandBox;
    private VBox gameLogBox;

    public static void main(String[] args){
        launch(args);
    }
    @Override
    public void start(Stage primaryStage) {
        game = new Game();
        primaryStage.setTitle("UNO Game");

        // Root layout
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));

        // Top section: Top card and current player
        topCardLabel = new Label("Top Card: ");
        topCardLabel.setFont(new Font(18));
        currentPlayerLabel = new Label("Current Player: ");
        currentPlayerLabel.setFont(new Font(18));
        HBox topBox = new HBox(20, topCardLabel, currentPlayerLabel);
        topBox.setAlignment(Pos.CENTER);
        root.setTop(topBox);

        // Center section: Player's hand
        playerHandBox = new HBox(10);
        playerHandBox.setAlignment(Pos.CENTER);
        root.setCenter(playerHandBox);

        // Bottom section: Game log
        gameLogBox = new VBox(10);
        gameLogBox.setAlignment(Pos.CENTER_LEFT);
        root.setBottom(gameLogBox);

        // Start game button
        Button startButton = new Button("Start Game");
        startButton.setOnAction(_ -> startGame());
        root.setBottom(new VBox(startButton, gameLogBox));

        // Scene setup
        Scene scene = new Scene(root, 800, 600);

        // Link the CSS file to the scene
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("styles.css")).toExternalForm());

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void startGame(){
        game.startGame();
        updateUI();
    }

    public void updateUI() {
        Player currentPlayer = game.getCurrentPlayer();
        currentPlayerLabel.setText("Current Player: " + currentPlayer.getNom());
        topCardLabel.setText("Top Card: " + game.getTopCard().toString());

        // Display player's hand
        playerHandBox.getChildren().clear();
        for (Card card : currentPlayer.getHand()) {
            System.out.println("Adding card to UI: " + card); // Debugging statement

            Button cardButton = new Button();
            Image cardImage = getCardImage(card);
            if (cardImage == null) {
                System.err.println("Card image is null for: " + card); // Debugging statement
            } else {
                cardButton.setGraphic(new ImageView(cardImage));
            }

            cardButton.setOnAction(_ -> playCard(card));
            playerHandBox.getChildren().add(cardButton);
        }

        // Add game log
        gameLogBox.getChildren().add(new Label("Turn: " + currentPlayer.getNom()));
    }

    public void playCard(Card card){
        Player currentPlayer = game.getCurrentPlayer();
        if (game.isCardPlayable(card, game.getTopCard())) {
            game.playCard(currentPlayer, card);
            handleSpecialCard(card);
            updateUI();
            if (game.isGameOver()) {
                gameLogBox.getChildren().add(new Label(currentPlayer.getNom() + " wins the game!"));
                playerHandBox.getChildren().clear();
            }
        } else {
            gameLogBox.getChildren().add(new Label("Invalid card!"));
        }
    }

    public void handleSpecialCard(Card card) {
        if (card instanceof WildCard) {
            // Show color selection dialog for Wild card
            ColorDialog colorDialog = new ColorDialog();
            colorDialog.showAndWait().ifPresent(color -> {
                game.getTopCard().setCouleur(color);
                gameLogBox.getChildren().add(new Label("Wild card played! Color chosen: " + color));
            });
        } else if (card instanceof WildDrawFourCard) {
            // Show color selection dialog for Wild Draw Four card
            ColorDialog colorDialog = new ColorDialog();
            colorDialog.showAndWait().ifPresent(color -> {
                game.getTopCard().setCouleur(color);
                Player nextPlayer = game.getPlayers().getNextPlayer();
                game.getDeck().drawCard(nextPlayer, 4);
                gameLogBox.getChildren().add(new Label("Wild Draw Four played! Color chosen: " + color));
                gameLogBox.getChildren().add(new Label(nextPlayer.getNom() + " draws 4 cards."));
            });
        } else if (card instanceof Drawtwo) {
            Player nextPlayer = game.getPlayers().getNextPlayer();
            game.getDeck().drawCard(nextPlayer, 2);
            gameLogBox.getChildren().add(new Label("Draw Two played! " + nextPlayer.getNom() + " draws 2 cards."));
        } else if (card instanceof Skip) {
            gameLogBox.getChildren().add(new Label("Skip played! " + game.getPlayers().getNextPlayer().getNom() + " is skipped."));
            game.getPlayers().nextPlayer(); // Skip the next player
        } else if (card instanceof Reverse) {
            game.setClockwise(!game.isClockwise());
            gameLogBox.getChildren().add(new Label("Reverse played! Turn order reversed."));
        }
    }

    public Image getCardImage(Card card) {
        String imagePath = "cards/" + card.getCouleur() + "_" + card.getSymbol() + ".png";
        System.out.println("Loading image: " + imagePath); // Debugging statement

        InputStream imageStream = getClass().getResourceAsStream(imagePath);
        if (imageStream == null) {
            System.err.println("Image not found: " + imagePath); // Debugging statement
            return null; // Return null if the image is not found
        }

        return new Image(imageStream);
    }
}
