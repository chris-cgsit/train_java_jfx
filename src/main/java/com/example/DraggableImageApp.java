package com.example;

import javafx.application.Application;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class DraggableImageApp extends Application {

    private double offsetX;
    private double offsetY;

    @Override
    public void start(Stage stage) {
        AnchorPane anchorPane = new AnchorPane();
        anchorPane.setPrefSize(2000, 2000);
        anchorPane.setStyle("-fx-background-color: #f0f0f0;");

        ImageView imageView = new ImageView(createColorRect(64, 64, Color.DODGERBLUE));
        AnchorPane.setLeftAnchor(imageView, 100.0);
        AnchorPane.setTopAnchor(imageView, 100.0);
        anchorPane.getChildren().add(imageView);

        imageView.setOnMousePressed(event -> {
            /**
             * Wandelt Scene-Koordinaten (Fenster) in lokale Koordinaten (AnchorPane) um.
             * Berücksichtigt automatisch die Scroll-Position.
             */
            Point2D localPos = anchorPane.sceneToLocal(event.getSceneX(), event.getSceneY());
            
            // Berechnet den Klick-Abstand (Offset) zum Bildrand, um Sprünge zu vermeiden.
            offsetX = localPos.getX() - AnchorPane.getLeftAnchor(imageView);
            offsetY = localPos.getY() - AnchorPane.getTopAnchor(imageView);
            event.consume();
        });

        imageView.setOnMouseDragged(event -> {
            // Aktuelle Mausposition im Hintergrund (AnchorPane) ermitteln.
            Point2D localPos = anchorPane.sceneToLocal(event.getSceneX(), event.getSceneY());
            
            // Neue Position unter Abzug des Offsets berechnen.
            double newX = localPos.getX() - offsetX;
            double newY = localPos.getY() - offsetY;

            // Begrenzung: Verhindert das Verschieben aus dem sichtbaren Bereich (Clamping).
            newX = Math.max(0, Math.min(newX, anchorPane.getPrefWidth() - imageView.getImage().getWidth()));
            newY = Math.max(0, Math.min(newY, anchorPane.getPrefHeight() - imageView.getImage().getHeight()));

            AnchorPane.setLeftAnchor(imageView, newX);
            AnchorPane.setTopAnchor(imageView, newY);
            event.consume();
        });

        ScrollPane scrollPane = new ScrollPane(anchorPane);
        scrollPane.setPannable(true);

        Scene scene = new Scene(scrollPane, 800, 600);
        stage.setTitle("Draggable Image in ScrollPane");
        stage.setScene(scene);
        stage.show();
    }

    private WritableImage createColorRect(int width, int height, Color color) {
        WritableImage image = new WritableImage(width, height);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                image.getPixelWriter().setColor(x, y, color);
            }
        }
        return image;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
