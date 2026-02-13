### Dokumentation: Drag-and-Drop mit ScrollPane-Korrektur in JavaFX

In dieser Anleitung erklären wir, wie man ein Objekt (hier ein Bild) innerhalb eines `ScrollPane` verschiebbar macht, ohne dass die Scroll-Position die Koordinaten verfälscht.

#### 1. Das Problem mit den Koordinaten
Wenn wir eine `ScrollPane` verwenden, sehen wir nur einen Ausschnitt eines größeren Bereichs (hier das `AnchorPane`). Wenn wir scrollen, ändern sich die Mauskoordinaten relativ zum Fenster, aber das Objekt muss sich relativ zum großen Hintergrund bewegen.

Würden wir einfach `event.getX()` verwenden, würde das Bild "springen", sobald wir gescrollt haben, weil `getX()` standardmäßig die Position auf dem sichtbaren Teil des Bildes liefert.

#### 2. Die Lösung: `sceneToLocal`
Der Schlüssel zur korrekten Umrechnung liegt in der Methode `sceneToLocal`.

```java
Point2D localPos = anchorPane.sceneToLocal(event.getSceneX(), event.getSceneY());
```

*   **`event.getSceneX()`**: Liefert die Mausposition absolut im Fenster (Scene), egal wie weit wir gescrollt haben.
*   **`anchorPane.sceneToLocal(...)`**: Diese Methode rechnet die Fenster-Position (`Scene`) in die Koordinaten des `AnchorPane` um. Dabei berücksichtigt JavaFX automatisch die aktuelle Scroll-Position des `ScrollPane`.

#### 3. Der Ablauf in zwei Schritten

##### Schritt A: Der Startpunkt (`setOnMousePressed`)
Bevor wir ziehen, müssen wir uns merken, **wo genau** im Bild wir hingeklickt haben. Das ist der `offset`.

```java
imageView.setOnMousePressed(event -> {
    // 1. Fenster-Koordinaten in Hintergrund-Koordinaten umrechnen
    Point2D localPos = anchorPane.sceneToLocal(event.getSceneX(), event.getSceneY());

    // 2. Abstand (Offset) zwischen Maus und oberer linker Ecke des Bildes berechnen
    offsetX = localPos.getX() - AnchorPane.getLeftAnchor(imageView);
    offsetY = localPos.getY() - AnchorPane.getTopAnchor(imageView);
});
```

##### Schritt B: Das Verschieben (`setOnMouseDragged`)
Während wir die Maus bewegen, berechnen wir ständig die neue Position.

```java
imageView.setOnMouseDragged(event -> {
    // 1. Aktuelle Mausposition im Hintergrund ermitteln
    Point2D localPos = anchorPane.sceneToLocal(event.getSceneX(), event.getSceneY());

    // 2. Neue Position berechnen (Mausposition minus Klick-Abstand)
    double newX = localPos.getX() - offsetX;
    double newY = localPos.getY() - offsetY;

    // 3. Begrenzung (Clamping): Verhindert, dass das Bild aus dem Pane geschoben wird
    newX = Math.max(0, Math.min(newX, anchorPane.getPrefWidth() - imageView.getImage().getWidth()));
    newY = Math.max(0, Math.min(newY, anchorPane.getPrefHeight() - imageView.getImage().getHeight()));

    // 4. Bild neu positionieren
    AnchorPane.setLeftAnchor(imageView, newX);
    AnchorPane.setTopAnchor(imageView, newY);
});
```

#### Zusammenfassung für Anfänger
1.  **Scene-Koordinaten** sind wie eine globale Landkarte des Fensters.
2.  **Local-Koordinaten** sind die Koordinaten innerhalb deines "Spielfelds" (`AnchorPane`).
3.  Durch `sceneToLocal` "weiß" das Programm, dass die Maus bei gescrolltem Inhalt eigentlich viel weiter rechts oder unten auf dem Spielfeld ist.
4.  Der **Offset** verhindert, dass das Bild beim Klicken mit der Ecke zur Maus springt – es bleibt genau dort an der Maus "kleben", wo man es angefasst hat.
