package org.controllers;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.io.IOException;

public class Main extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    public void start(Stage primaryStage) throws IOException {

        primaryStage.getIcons().add(new Image("file:ico.png"));
        Parent root;
        if ((new File(System.getenv("APPDATA") + "/WordsReview/kwords.txt")).exists()) {
            root = Loader("sample");
        } else {
            root = Loader("start");
        }
        primaryStage.setTitle("WordReview");
        primaryStage.setResizable(false);
        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.setScene(new Scene(root, 600, 400, Color.TRANSPARENT));
        primaryStage.show();
    }

    private Parent Loader(String name) throws IOException {
        return FXMLLoader.load(getClass().getResource(name + ".fxml"));
    }
}
