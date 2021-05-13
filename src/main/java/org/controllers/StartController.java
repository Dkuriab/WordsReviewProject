package org.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class StartController {
    private boolean isInput;

    @FXML
    private Button closeButton;

    @FXML
    private Rectangle filePutArea;

    @FXML
    private Button choseFileButton;

    @FXML
    private Button start_button;

    @FXML
    private Button collapse_button;

    @FXML
    private Text saved_text;


    @FXML
    void FileAreaDragDropped(DragEvent event) {
        Dragboard db = event.getDragboard();
        File input = db.getFiles().get(0);

        if (input != null) {
            copy(input);
            filePutArea.setVisible(false);
            choseFileButton.setDisable(true);
        } else {
            System.out.println("File finding error");
        }
    }

    @FXML
    void FileAreaDragOver(DragEvent event) {
        if (event.getDragboard().hasFiles()) {
            event.acceptTransferModes(TransferMode.ANY);
        }
    }

    public void copy(File source) {
        try {
            File directory = new File(System.getenv("APPDATA") + "/WordsReview");
            if (!directory.exists()) {
                boolean created = directory.mkdirs();
                if (!created) {
                    System.out.println("could not create file in %appdata%");
                }
            }

            File Out = new File(directory.getAbsolutePath() + "/kwords.txt");
            boolean isOpened = Out.createNewFile();
            if (!isOpened) {
                System.out.println("could not create file kwords.txtx");
            }

            BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(Out, true), StandardCharsets.UTF_8));
            BufferedReader knowledge = new BufferedReader(new InputStreamReader(new FileInputStream(source), StandardCharsets.UTF_8));


            String cur;
            while ((cur = knowledge.readLine()) != null) {
                wr.write(cur);
                wr.newLine();
            }
            wr.close();
            isInput = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        saved_text.setVisible(true);
    }

    private double x, y;

    @FXML
    void dragged(MouseEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setX(event.getScreenX() - x);
        stage.setY(event.getScreenY() - y);
    }

    @FXML
    void pressed(MouseEvent event) {
        x = event.getX();
        y = event.getY();
    }

    @FXML
    void initialize() {
        closeButton.setOnAction(event -> {
            Stage stage = (Stage) closeButton.getScene().getWindow();
            stage.close();
        });
        start_button.setOnAction(event -> {
            try {
                if (!isInput) {
                    File dir = new File(System.getenv("APPDATA") + "/WordsReview");
                    boolean created = dir.mkdirs();
                    if (!created) {
                        System.out.println("could not create file in %appdata%");
                    }
                    File Out = new File(System.getenv("APPDATA") + "/WordsReview/kwords.txt");
                    boolean isOpened = Out.createNewFile();
                    if (!isOpened) {
                        System.out.println("not opened");
                    }
                }

                Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                Scene scene = new Scene(root, 600, 400, Color.TRANSPARENT);
                stage.hide();
                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {
                System.out.println("Fail to change scene");
                e.printStackTrace();
            }
        });

        collapse_button.setOnAction(event -> {
            Stage stage = (Stage) collapse_button.getScene().getWindow();
            stage.setIconified(true);
        });

        choseFileButton.setOnAction(event -> {
            FileChooser fc = new FileChooser();
            File input = fc.showOpenDialog(null);
            if (input != null) {
                copy(input);
                filePutArea.setVisible(false);
                choseFileButton.setDisable(true);
            } else {
                System.out.println("File finding error");
            }
        });
    }

}
