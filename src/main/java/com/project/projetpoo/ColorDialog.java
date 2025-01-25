package com.project.projetpoo;

import javafx.geometry.Pos;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
//import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
//import javafx.scene.text.Font;

public class ColorDialog extends Dialog<String> {

    public ColorDialog() {
        setTitle("Choose a Color");
        setHeaderText("Select a color for the Wild card:");

        // Create buttons for each color
        ButtonType redButton = new ButtonType("Red", ButtonBar.ButtonData.OK_DONE);
        ButtonType greenButton = new ButtonType("Green", ButtonBar.ButtonData.OK_DONE);
        ButtonType blueButton = new ButtonType("Blue", ButtonBar.ButtonData.OK_DONE);
        ButtonType yellowButton = new ButtonType("Yellow", ButtonBar.ButtonData.OK_DONE);

        // Add buttons to the dialog
        getDialogPane().getButtonTypes().addAll(redButton, greenButton, blueButton, yellowButton);

        // Set the result converter to return the selected color
        setResultConverter(buttonType -> {
            if (buttonType == redButton) {
                return "Red";
            } else if (buttonType == greenButton) {
                return "Green";
            } else if (buttonType == blueButton) {
                return "Blue";
            } else if (buttonType == yellowButton) {
                return "Yellow";
            }
            return null;
        });

        // Add color previews
        HBox colorBox = new HBox(10);
        colorBox.setAlignment(Pos.CENTER);
        colorBox.getChildren().addAll(
                createColorRectangle(Color.RED),
                createColorRectangle(Color.GREEN),
                createColorRectangle(Color.BLUE),
                createColorRectangle(Color.YELLOW)
        );

        getDialogPane().setContent(colorBox);
    }

    private Rectangle createColorRectangle(Color color) {
        Rectangle rect = new Rectangle(50, 50);
        rect.setFill(color);
        rect.setStroke(Color.BLACK);
        rect.setStrokeWidth(2);
        return rect;
    }
}