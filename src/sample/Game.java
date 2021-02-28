package sample;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;


public class Game extends Application {
    private static final int MIN_NUMBER_OF_SQUARES = 3;
    private static final int WIDTH = 800;
    private static final int HEIGHT = WIDTH;
    private static final int ROWS = 20;
    private static final int COLUMNS = ROWS;
    private static final int SQUARE_SIZE = WIDTH / ROWS;
    private static final String toadstool = "/img/toadstool.png";
    private static final String apple = "/img/ic_apple.png";
    private static final int RIGHT = 0;
    private static final int LEFT = 1;
    private static final int UP = 2;
    private static final int DOWN = 3;

    private static final String[] FOODS_IMAGE = new String[]{"/img/ic_orange.png", apple, "/img/ic_cherry.png",
            "/img/ic_berry.png", "/img/ic_coconut_.png", "/img/ic_peach.png", "/img/ic_watermelon.png", "/img/ic_orange.png",
            "/img/ic_pomegranate.png"};

    private static final String[] HEAD_IMAGE = new String[]{"/img/snakeHead_RIGHT.png", "/img/snakeHead_LEFT.png", "/img/snakeHead_UP.png", "/img/snakeHead_DOWN.png"};

    private GraphicsContext gc;
    private List<Point> snakeBody = new ArrayList();
    private Point snakeHead;

    private javafx.scene.image.Image headImage;
    private javafx.scene.image.Image bodyImage = new Image("/img/body.png");
    private javafx.scene.image.Image foodImage;
    private javafx.scene.image.Image addictionImage;

    private int foodX;
    private boolean isAddiction = false;
    private boolean isGenerateAddiction = false;
    private int foodY;
    private int outsideArea = -50;
    private int addictionX = outsideArea;
    private int addictionY = outsideArea;
    private boolean gameOver;
    private int currentDirection = 0;
    private int score = 0;
    private int speed = 5;
    private String gameType = "normal";


    public Game() {
    }

    public Game(String gameType) {
        this.gameType = gameType;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Snake");
        Group root = new Group();
        javafx.scene.canvas.Canvas canvas = new Canvas(WIDTH, HEIGHT);
        root.getChildren().add(canvas);
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();
        gc = canvas.getGraphicsContext2D();

        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                KeyCode code = event.getCode();
                if (code == KeyCode.RIGHT || code == KeyCode.D) {
                    if (!isAddiction) {
                        if (currentDirection != LEFT) {
                            currentDirection = RIGHT;
                        }
                    } else {
                        if (currentDirection != RIGHT) {
                            currentDirection = LEFT;
                        }
                    }
                } else if (code == KeyCode.LEFT || code == KeyCode.A) {
                    if (!isAddiction) {
                        if (currentDirection != RIGHT) {
                            currentDirection = LEFT;
                        }
                    } else {
                        if (currentDirection != LEFT) {
                            currentDirection = RIGHT;
                        }
                    }
                } else if (code == KeyCode.UP || code == KeyCode.W) {
                    if (!isAddiction) {
                        if (currentDirection != DOWN) {
                            currentDirection = UP;
                        }
                    } else {
                        if (currentDirection != UP) {
                            currentDirection = DOWN;
                        }
                    }
                } else if (code == KeyCode.DOWN || code == KeyCode.S) {
                    if (!isAddiction) {
                        if (currentDirection != UP) {
                            currentDirection = DOWN;
                        }
                    } else {
                        if (currentDirection != DOWN) {
                            currentDirection = UP;
                        }
                    }
                }
            }
        });

        for (int i = 0; i < MIN_NUMBER_OF_SQUARES; i++) {
            Point point = new Point(ROWS / 2, ROWS / 2);
            snakeBody.add(point);
        }
        snakeHead = snakeBody.get(0);
        generateFood();

        new AnimationTimer() {
            long lastTick = 0;

            public void handle(long now) {
                if (lastTick == 0) {
                    lastTick = now;
                    run(gc);
                    return;
                }
                int prawo = 1000000000 / speed;
                long lewo = now - lastTick;
                if (lewo > 1000000000 / speed) {
                    lastTick = now;
                    run(gc);
                }
            }

        }.start();

    }

    private void run(GraphicsContext gc) {

        if (gameOver) {
            gc.setFill(javafx.scene.paint.Color.RED);
            gc.setFont(new javafx.scene.text.Font("Digital-7", 70));
            gc.fillText("Game Over", WIDTH / 3.5, HEIGHT / 2);
            return;
        }
        drawBackground(gc);
        drawFood(gc);
        drawAddiction(gc);
        drawSnake(gc);
        drawScore();
        drawSpeed();
        drawWarning();

        for (int i = snakeBody.size() - 1; i >= 1; i--) {
            snakeBody.get(i).x = snakeBody.get(i - 1).x;
            snakeBody.get(i).y = snakeBody.get(i - 1).y;
        }

        switch (currentDirection) {
            case RIGHT:
                moveRight();
                break;
            case LEFT:
                moveLeft();
                break;
            case UP:
                moveUp();
                break;
            case DOWN:
                moveDown();
                break;
        }

        gameOver();
        eatFood();
        eatAddiction();
    }

    private void drawBackground(GraphicsContext gc) {
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLUMNS; j++) {
                if ((i + j) % 2 == 0) {
                    if (!isAddiction)
                        gc.setFill(javafx.scene.paint.Color.web("99ff99"));
                    else
                        gc.setFill(javafx.scene.paint.Color.web("ffebe6"));

                } else {
                    if (!isAddiction)
                        gc.setFill(javafx.scene.paint.Color.web("71d571"));
                    else
                        gc.setFill(javafx.scene.paint.Color.web("ffffff"));

                }
                gc.fillRect(i * SQUARE_SIZE, j * SQUARE_SIZE, SQUARE_SIZE, SQUARE_SIZE);
            }
        }
    }

    private void generateFood() {
        foodX = (int) (Math.random() * ROWS);
        foodY = (int) (Math.random() * COLUMNS);
        try {
            while ((!isPointOutsideSnake(foodX, foodY)) && ((foodX == addictionX) && (foodY == addictionY))) {
                foodX = (int) (Math.random() * ROWS);
                foodY = (int) (Math.random() * COLUMNS);
            }
            int idFood = (int) (Math.random() * FOODS_IMAGE.length);
            foodImage = new Image(FOODS_IMAGE[idFood]);

        } catch (Exception ex) {
            System.out.println("generatedFood exception");
        }
    }

    private void generateAddiction() {
        if (isGenerateAddiction && !gameType.equals("normal")) {

            addictionX = (int) (Math.random() * ROWS);
            addictionY = (int) (Math.random() * COLUMNS);

            try {
                while ((!isPointOutsideSnake(addictionX, addictionY)) && ((foodX == addictionX) && (foodY == addictionY))) {
                    addictionX = (int) (Math.random() * ROWS);
                    addictionY = (int) (Math.random() * COLUMNS);
                }
                addictionImage = new Image(toadstool);

            } catch (Exception ex) {
                System.out.println("generatedAddiction exception");
            }
        }
    }


    private boolean isPointOutsideSnake(int point_X, int point_Y) {
        for (Point snake : snakeBody) {
            if (snake.getX() == point_X && snake.getY() == point_Y) {
                return false;
            }
        }
        return true;
    }


    private void drawFood(GraphicsContext gc) {
        gc.drawImage(foodImage, foodX * SQUARE_SIZE, foodY * SQUARE_SIZE, SQUARE_SIZE, SQUARE_SIZE);
    }

    private void drawAddiction(GraphicsContext gc) {

        gc.drawImage(addictionImage, addictionX * SQUARE_SIZE, addictionY * SQUARE_SIZE, SQUARE_SIZE, SQUARE_SIZE);


    }

    private void drawSnake(GraphicsContext gc) {
        if (currentDirection == 0)
            headImage = new Image(HEAD_IMAGE[0]);
        else if (currentDirection == 1)
            headImage = new Image(HEAD_IMAGE[1]);
        else if (currentDirection == 2)
            headImage = new Image(HEAD_IMAGE[2]);
        else if (currentDirection == 3)
            headImage = new Image(HEAD_IMAGE[3]);
        gc.drawImage(headImage, snakeHead.getX() * SQUARE_SIZE, snakeHead.getY() * SQUARE_SIZE, SQUARE_SIZE, SQUARE_SIZE);


        for (int i = 1; i < snakeBody.size(); i++) {
            gc.drawImage(bodyImage, snakeBody.get(i).getX() * SQUARE_SIZE, snakeBody.get(i).getY() * SQUARE_SIZE, SQUARE_SIZE, SQUARE_SIZE);
        }
    }

    private void moveRight() {
        snakeHead.x++;
        if (!gameType.equals("normal")) {
            if (snakeHead.x == COLUMNS) {
                snakeHead.x = 0;
            }
        }
    }

    private void moveLeft() {
        snakeHead.x--;
        if (!gameType.equals("normal")) {
            if (snakeHead.x < 0) {
                snakeHead.x = COLUMNS - 1;
            }
        }
    }

    private void moveUp() {
        snakeHead.y--;
        if (!gameType.equals("normal")) {
            if (snakeHead.y < 0) {
                snakeHead.y = ROWS - 1;
            }
        }
    }

    private void moveDown() {
        snakeHead.y++;
        if (!gameType.equals("normal")) {
            if (snakeHead.y == ROWS) {
                snakeHead.y = 0;
            }
        }
    }

    public void gameOver() {
        if (gameType.equals("normal") && (snakeHead.x < 0 || snakeHead.y < 0 || snakeHead.x * SQUARE_SIZE >= WIDTH || snakeHead.y * SQUARE_SIZE >= HEIGHT)) {
            gameOver = true;
        }

        //destroy itself
        for (int i = 1; i < snakeBody.size(); i++) {
            if (snakeHead.x == snakeBody.get(i).getX() && snakeHead.getY() == snakeBody.get(i).getY()) {
                gameOver = true;
                break;
            }
        }
    }

    private void eatFood() {
        if (snakeHead.getX() == foodX && snakeHead.getY() == foodY) {
            snakeBody.add(new Point(outsideArea, outsideArea));
            generateFood();

            score += 5;

            isGenerateAddiction = false;
            isAddiction = false;

            if ((speed - 4) < 10) {
                if (score % 10 == 0) {
                    speed++;
                }
            } else {
                if (score % 20 == 0) {
                    speed++;
                }
            }


            if (score % 25 == 0) {
                isGenerateAddiction = true;
                generateAddiction();

            }
        }
    }

    private void eatAddiction() {

        if (snakeHead.getX() == addictionX && snakeHead.getY() == addictionY) {
            snakeBody.add(new Point(outsideArea, outsideArea));
            addictionX = outsideArea;
            addictionY = outsideArea;
            isAddiction = true;
//

        }
    }

    private void drawScore() {
        if (!isAddiction) {
            gc.setFill(javafx.scene.paint.Color.web("396339"));
        } else {
            gc.setFill(Color.RED);

        }
        gc.setFont(new Font("Digital-7", 35));
        gc.fillText("Score: " + score, 10, 35);
    }

    private void drawSpeed() {
        if (!isAddiction) {
            gc.setFill(javafx.scene.paint.Color.web("396339"));
        } else {
            gc.setFill(Color.RED);

        }
        gc.setFont(new Font("Digital-7", 35));
        gc.fillText("Speed: " + (speed - 4), 10, 70);
    }

    private void drawWarning() {

        gc.setFill(Color.RED);


        gc.setFont(new Font("Digital-7", 35));
        if (isAddiction) {
            gc.fillText("You're poisoned", (WIDTH / 3.5 + 75), 35);
        } else {
            gc.fillText("", (WIDTH / 3.5 + 75), 35);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}