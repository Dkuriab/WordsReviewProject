package org.controllers;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.Border;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import wordReview.Events;
import wordReview.WordFormater;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class MainController {
    private Events wr = null;
    public File knowl = null;
    public File input = null;

    @FXML
    public Button Yes;
    public Button No;
    public Button ahead_button;
    public Button back_button;
    public Button add_words;
    public Button vocabulary;
    public Button clear;
    public Button choseFileButton;
    public Button closeButton;
    public Button finish_button;
    public Button collapse_button;
    public Button settings_button;
    public ToggleButton translate_mode;
    public ToggleButton dictionary_mode;

    @FXML
    public TextField TextWords;
    @FXML
    public Text drop_file_here_text;
    @FXML
    public Text file_name_text;
    @FXML
    public Text translate_field;
    @FXML
    public Text working_on_text;
    @FXML
    public Text label;
    @FXML
    public Text counter_text;

    @FXML
    public ProgressBar working_on_progress_bar;
    @FXML
    public ProgressBar progress_bar;
    @FXML
    public Hyperlink subscene_hyperlink;

    @FXML
    public Pane main_pane;
    @FXML
    public Pane settings_pane;
    @FXML
    public Pane working_on_pane;
    @FXML
    public Rectangle filePutArea;

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

    public void startWR() {
        if (wr != null) {
            wr.close();
        }

        filePutArea.setVisible(false);
        drop_file_here_text.setVisible(false);

        Yes.setVisible(true);
        No.setVisible(true);
        finish_button.setVisible(true);
        ahead_button.setVisible(true);
        back_button.setVisible(true);
        progress_bar.setVisible(true);

        choseFileButton.setText("Choose another");
        file_name_text.setText(input.getName());
        wr = new Events(TextWords);
    }

    @FXML
    void FileAreaDragDropped(DragEvent event) {
        Dragboard db = event.getDragboard();
        File input = db.getFiles().get(0);

        if (input != null && knowl != null) {
            this.input = input;
            startWR();
        } else {
            System.out.println("File finding error");
        }
    }

    @FXML
    void FileAreaDragExited() {
        if (input == null) {
            drop_file_here_text.setVisible(true);
        }
    }

    @FXML
    void FileAreaDragOver(DragEvent event) {
        if (event.getDragboard().hasFiles()) {
            event.acceptTransferModes(TransferMode.ANY);
            drop_file_here_text.setVisible(false);
        }
    }

    @FXML
    void initialize() {
        ahead_button.setVisible(false);
        back_button.setVisible(false);
        progress_bar.setProgress(0);
        working_on_progress_bar.setProgress(0);
        progress_bar.setVisible(false);
        finish_button.setVisible(false);
        settings_pane.setVisible(false);
        translate_mode.setSelected(false);
        dictionary_mode.setSelected(false);
        working_on_pane.setVisible(false);

        knowl = new File(System.getenv("APPDATA") + "/WordsReview/kwords.txt");
        if (!knowl.exists()) {
            System.out.println("knowl doesn't exist");
        }
        Yes.setVisible(false);
        No.setVisible(false);

        main_pane.setOnKeyPressed(keyEvent -> {

            if (wr != null) {
                if (keyEvent.getCode() == KeyCode.A) {
                    wr.go(true);
                } else if (keyEvent.getCode() == KeyCode.D) {
                    wr.go(false);
                } else if (keyEvent.getCode() == KeyCode.RIGHT) {
                    wr.ahead();
                } else if (keyEvent.getCode() == KeyCode.LEFT) {
                    wr.back();
                }
            }
        });

        choseFileButton.setOnAction(actionEvent -> {
            FileChooser fc = new FileChooser();
            File input = fc.showOpenDialog(null);

            if (input != null && knowl != null) {
                this.input = input;
                startWR();
            } else {
                System.out.println("File finding error");
            }
        });

        closeButton.setOnAction(actionEvent -> {
            Stage stage = (Stage) closeButton.getScene().getWindow();

            if (knowl != null) {
                new WordFormater(knowl).rewrite();
            }

            stage.close();
        });

        collapse_button.setOnAction(actionEvent -> {
            Stage stage = (Stage) collapse_button.getScene().getWindow();
            stage.setIconified(true);
        });

        Yes.setOnAction(event -> {
            if (wr != null) {
                translate_field.setText("");
                wr.go(true);
            }
        });

        No.setOnAction(event -> {
            if (wr != null) {
                translate_field.setText("");
                wr.go(false);
            }
        });

        back_button.setOnAction(event -> {
            if (wr != null) {
                back_button.setOpacity(0.8);
                translate_field.setText("");
                wr.back();
                back_button.setOpacity(0.21);
            }
        });

        ahead_button.setOnAction(event -> {
            if (wr != null) {
                ahead_button.setOpacity(0.8);
                translate_field.setText("");
                wr.ahead();
                ahead_button.setOpacity(0.21);
            }
        });

        finish_button.setOnAction(event -> {
            try {
                DirectoryChooser dc = new DirectoryChooser();
                dc.setTitle("Choose directory for saving unknown words");
                String dir = dc.showDialog(null).getAbsolutePath();

                String name = input.getName();
                int i = name.length() - 1;

                while (i > 0) {
                    if (name.charAt(i) == '.') {
                        break;
                    }
                    i--;
                }

                name = i == 0 ? (name + "Checked") : (name.substring(0, i) + "Checked");

                File Out = new File(dir + "\\" + name + ".txt");
                try {
                    final boolean opened = Out.createNewFile();
                    if (!opened) {
                        Out = new File(dir + "\\" + name + "(1).txt");
                        System.out.println("could not create file for finish");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                String workingOn = "Working on ";
                if (dictionary_mode.isSelected()) {
                    if (translate_mode.isSelected()) {
                        workingOn += "definitions and translate...";
                    } else {
                        workingOn += "definitions...";
                    }
                } else {
                    if (translate_mode.isSelected()) {
                        workingOn += "translate...";
                    } else {
                        workingOn += "writing...";
                    }
                }

                working_on_text.setText(workingOn);
                wr.finish(Out, translate_mode.isSelected(), dictionary_mode.isSelected(), working_on_progress_bar, working_on_pane);
            } catch (NullPointerException e) {
                System.out.println("User didn't choose dir");
            }
        });

        settings_button.setOnAction(event -> {
            if (settings_pane.isVisible()) {
                label.setText("WordsReview");
                settings_pane.setVisible(false);
            } else {
                label.setText("Settings");
                settings_pane.setVisible(true);
            }
        });

        translate_mode.setOnAction(event -> {
            if (translate_mode.isSelected()) {
                translate_mode.setStyle("-fx-background-color:#e78d27; -fx-background-radius: 5; -fx-border-radius: 5");
            } else {
                translate_mode.setStyle("-fx-background-color:#b79a79; -fx-background-radius: 5; -fx-border-radius: 5");
            }
        });

        dictionary_mode.setOnAction(event -> {
            if (dictionary_mode.isSelected()) {
                dictionary_mode.setStyle("-fx-background-color:#e78d27; -fx-background-radius: 5; -fx-border-radius: 5");
            } else {
                dictionary_mode.setStyle("-fx-background-color:#b79a79; -fx-background-radius: 5; -fx-border-radius: 5");
            }
        });

        vocabulary.setOnAction(event -> {
            try {
                java.awt.Desktop.getDesktop().open(knowl);
            } catch (IOException e) {
                System.out.println("Can't open vocabulary");
            }
        });


        clear.setOnAction(e -> {

            Label secondLabel = new Label("No, you don't want to delete words, I am your own god");
            StackPane secondaryLayout = new StackPane();
            secondaryLayout.getChildren().add(secondLabel);

            Scene secondScene = new Scene(secondaryLayout, 400, 100);
            Stage newWindow = new Stage();
            newWindow.setTitle("Second Stage");
            newWindow.setScene(secondScene);
            newWindow.initModality(Modality.WINDOW_MODAL);

            Stage stage = (Stage) clear.getScene().getWindow();
            newWindow.initOwner(stage);
            newWindow.setX(stage.getX() + 200);
            newWindow.setY(stage.getY() + 100);
            newWindow.show();
        });

        subscene_hyperlink.setBorder(Border.EMPTY);
        subscene_hyperlink.setPadding(new Insets(4, 0, 4, 0));
        subscene_hyperlink.setOnAction(event -> {
            try {
                java.awt.Desktop.getDesktop().browse(new URI("https://subscene.com/"));
            } catch (IOException | URISyntaxException e) {
                e.printStackTrace();
            }
        });
    }
}
