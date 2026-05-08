package org.example.demo;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Gameboard extends Application {
    // Grid constants
    private static final int ROWS = 10;
    private static final int COLS = 10;
    private static final int SCENE_WIDTH = 800;
    private static final int SCENE_HEIGHT = 800;

    // Number of lives the player starts with
    private static final int LIVES = 3;

    enum CellType {
        GRASS, PLAYER, PRINCESS, BOMB, WALL
    }

    private CellType[][] matrix = new CellType[ROWS][COLS];

    // Images for each cell type
    private Image grassImg;
    private Image playerImg;
    private Image princessImg;
    private Image bombImg;
    private Image wallImg;

    private Stage stage;
    private GridPane grid;

    // Player position
    private int playerRow = 1;
    private int playerCol = 1;

    // Player lives
    private int lives = LIVES;

    // Stops movement when true
    private boolean gameOver = false;

    @Override
    public void start(Stage stage) {
        this.stage = stage;
        grassImg    = new Image(getClass().getResource("/org/example/demo/gameboard/grass.png").toExternalForm());
        playerImg   = new Image(getClass().getResource("/org/example/demo/gameboard/player.png").toExternalForm());
        princessImg = new Image(getClass().getResource("/org/example/demo/gameboard/princess.png").toExternalForm());
        bombImg     = new Image(getClass().getResource("/org/example/demo/gameboard/bomb.png").toExternalForm());
        wallImg     = new Image(getClass().getResource("/org/example/demo/gameboard/wall.png").toExternalForm());

        initMatrix();
        grid = new GridPane();
        grid.prefWidthProperty().bind(stage.widthProperty());
        grid.prefHeightProperty().bind(stage.heightProperty());
        drawBoard(grid);

        BorderPane root = new BorderPane();
        root.setCenter(grid);

        Scene scene = new Scene(root, SCENE_WIDTH, SCENE_HEIGHT);

        // Move player with arrow keys
        scene.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case DOWN  -> movePlayer(1, 0);
                case RIGHT -> movePlayer(0, 1);
                case LEFT  -> movePlayer(0, -1);
                case UP    -> movePlayer(-1, 0);
            }
        });

        stage.setTitle("Rescue the Princess");
        stage.setScene(scene);
        stage.show();
    }

    private void initMatrix() {
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                matrix[r][c] = CellType.GRASS;
            }
        }

        // Walls on the perimeter
        for (int c = 0; c < COLS; c++) {
            matrix[0][c] = CellType.WALL;
            matrix[ROWS - 1][c] = CellType.WALL;
        }
        for (int r = 0; r < ROWS; r++) {
            matrix[r][0] = CellType.WALL;
            matrix[r][COLS - 1] = CellType.WALL;
        }

        // Player always starts at [1][1]
        matrix[1][1] = CellType.PLAYER;

        // Collect all empty inner cells
        List<int[]> freeCells = new ArrayList<>();
        for (int r = 1; r < ROWS - 1; r++) {
            for (int c = 1; c < COLS - 1; c++) {
                if (matrix[r][c] == CellType.GRASS) {
                    freeCells.add(new int[]{r, c});
                }
            }
        }

        // Shuffle and place princess and bombs in random positions
        Collections.shuffle(freeCells);
        matrix[freeCells.get(0)[0]][freeCells.get(0)[1]] = CellType.PRINCESS;
        matrix[freeCells.get(1)[0]][freeCells.get(1)[1]] = CellType.BOMB;
        matrix[freeCells.get(2)[0]][freeCells.get(2)[1]] = CellType.BOMB;
        matrix[freeCells.get(3)[0]][freeCells.get(3)[1]] = CellType.BOMB;
        matrix[freeCells.get(4)[0]][freeCells.get(4)[1]] = CellType.BOMB;
        matrix[freeCells.get(5)[0]][freeCells.get(5)[1]] = CellType.BOMB;
    }

    private void movePlayer(int dRow, int dCol) {
        // Stop if game is over
        if (gameOver) return;

        int newRow = playerRow + dRow;
        int newCol = playerCol + dCol;

        // Stop if hitting a wall
        if (matrix[newRow][newCol] == CellType.WALL) return;

        // Check what is in the next cell before moving
        boolean foundPrincess = matrix[newRow][newCol] == CellType.PRINCESS;
        boolean foundBomb     = matrix[newRow][newCol] == CellType.BOMB;

        // Move player to new position
        matrix[playerRow][playerCol] = CellType.GRASS;
        playerRow = newRow;
        playerCol = newCol;
        matrix[playerRow][playerCol] = CellType.PLAYER;

        drawBoard(grid);

        // alert if player reach the princess
        if (foundPrincess) {
            gameOver = true;
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Victory");
            alert.setHeaderText(null);
            alert.setContentText("You rescued the princess!");
            alert.showAndWait();
        }

        // Show alert if player hit a bomb
        if (foundBomb) {
            lives--;
            if (lives == 0) gameOver = true;
            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle("Boom");
            alert.setHeaderText(null);
            alert.setContentText("You hit a bomb! Lives left: " + lives);
            alert.showAndWait();
        }
    }

    private void drawBoard(GridPane grid) {
        grid.getChildren().clear();

        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {

                StackPane cell = new StackPane();
                cell.prefWidthProperty().bind(grid.widthProperty().divide(COLS));
                cell.prefHeightProperty().bind(grid.heightProperty().divide(ROWS));
                cell.setStyle("-fx-border-color: black; -fx-background-color: beige;");

                // Grass on bottom
                if (matrix[row][col] != CellType.WALL) {
                    ImageView grassView = new ImageView(grassImg);
                    grassView.fitWidthProperty().bind(stage.widthProperty().divide(COLS));
                    grassView.fitHeightProperty().bind(stage.heightProperty().divide(ROWS));
                    grassView.setPreserveRatio(false);
                    cell.getChildren().add(grassView);
                }

                // Add entity image on top of the cell
                Image entityImage = null;
                if (matrix[row][col] == CellType.PLAYER)        entityImage = playerImg;
                else if (matrix[row][col] == CellType.PRINCESS) entityImage = princessImg;
                else if (matrix[row][col] == CellType.BOMB)     entityImage = bombImg;
                else if (matrix[row][col] == CellType.WALL) {
                    entityImage = wallImg;
                    cell.setStyle("-fx-border-color: black; -fx-background-color: gray;");
                }

                if (entityImage != null) {
                    ImageView entityView = new ImageView(entityImage);
                    entityView.fitWidthProperty().bind(stage.widthProperty().divide(COLS));
                    entityView.fitHeightProperty().bind(stage.heightProperty().divide(ROWS));
                    entityView.setPreserveRatio(false);
                    cell.getChildren().add(entityView);
                }

                grid.add(cell, col, row);
            }
        }
    }
}