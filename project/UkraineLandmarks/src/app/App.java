package app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;

public class App extends Application {
    @Override
    public void start(Stage stage){
        try {
            Parent root;
            root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/form.fxml")));
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Визначні місця");
            stage.show();

        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);

    }

}
