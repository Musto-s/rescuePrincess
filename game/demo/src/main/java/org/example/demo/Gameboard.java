package org.example.demo;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class Gameboard extends Application {
    // Grid constants
    private static final int ROWS = 10;
    private static final int COLS = 10;
    private static final int SCENE_WIDTH = 800;
    private static final int SCENE_HEIGHT = 800;

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

    @Override
    public void start(Stage stage) {
        this.stage = stage;
        grassImg    = new Image(getClass().getResource("/org/example/demo/gameboard/grass.png").toExternalForm());
        playerImg   = new Image(getClass().getResource("/org/example/demo/gameboard/player.png").toExternalForm());
        princessImg = new Image(getClass().getResource("/org/example/demo/gameboard/princess.png").toExternalForm());
        bombImg     = new Image(getClass().getResource("/org/example/demo/gameboard/bomb.png").toExternalForm());
        wallImg     = new Image(getClass().getResource("/org/example/demo/gameboard/wall.png").toExternalForm());

        initMatrix();
        GridPane grid = new GridPane();
        grid.prefWidthProperty().bind(stage.widthProperty());
        grid.prefHeightProperty().bind(stage.heightProperty());
        drawBoard(grid);

        BorderPane root = new BorderPane();
        root.setCenter(grid);

        Scene scene = new Scene(root, SCENE_WIDTH, SCENE_HEIGHT);

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

        // Sample objects
        matrix[0][0] = CellType.PLAYER;
        matrix[9][9] = CellType.PRINCESS;
        matrix[4][5] = CellType.BOMB;
        matrix[1][1] = CellType.WALL;
        matrix[1][2] = CellType.WALL;
    }

    private void drawBoard(GridPane grid) {
        grid.getChildren().clear();

        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {

                StackPane cell = new StackPane();
                cell.prefWidthProperty().bind(grid.widthProperty().divide(COLS));
                cell.prefHeightProperty().bind(grid.heightProperty().divide(ROWS));
                cell.setStyle("-fx-border-color: black; -fx-background-color: beige;");

                // Grass on botom
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