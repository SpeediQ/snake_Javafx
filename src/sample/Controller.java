package sample;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class Controller {


    @FXML
    private Button btn1;

    @FXML
    private Button btn2;


    @FXML
    void initialize() {

        btn1.setOnAction(actionEvent -> {

            try {
                handleButtonClick();
            } catch (Exception exception) {
                exception.printStackTrace();
            }


        });
        btn2.setOnAction(actionEvent -> {

            try {
                handleButtonClick2();
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        });

    }

    public void handleButtonClick() throws Exception {

        new Game().start(new Stage());

    }

    public void handleButtonClick2() throws Exception {

        new Game("noWalls").start(new Stage());


    }


}
