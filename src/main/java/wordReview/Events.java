package wordReview;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import org.controllers.MainController;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Events extends MainController {

    public Map<String, Integer> newWords;
    public ListIterator<String> iterator;
    public Set<String> delete = new HashSet<>();
    public BufferedWriter writer;
    public String current;

    public TextField textField;
    public boolean LastPrevNext = true;
    public boolean needsTranslate;
    public boolean needsDefinition;

    public int counter;
    public int all;
    public int done = 0;
    public double defined = 0;

    public Events(TextField textField) {
        this.textField = textField;

        this.textField.setText("Parsing...");

        newWords = new Parser(input, knowl).compare();

        iterator = new ArrayList<>(newWords.keySet()).listIterator();
        counter = newWords.keySet().size();

        System.out.println("counter: " + counter);

        if (counter > 0) {
            current = iterator.next();
            done = 1;
            textField.setText(current);
            counter_text.setText(done + " / " + counter);
        } else {
            textField.setText("No new words");
            Yes.setVisible(false);
            No.setVisible(false);
            progress_bar.setVisible(false);
        }

        try {
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(knowl, true), StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("couldn't open writer (knowledge)");
        }
    }

    public void finish(File output, boolean needsTranslate, boolean needsDefinition, ProgressBar working_on_progress_bar, Pane working_on_pane) {
        close();
        this.working_on_pane = working_on_pane;
        this.working_on_progress_bar = working_on_progress_bar;
        this.needsTranslate = needsTranslate;
        this.needsDefinition = needsDefinition;

        try {
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(output, false), StandardCharsets.UTF_8));
            all = newWords.size();
            defined = 1;
            working_on_pane.setVisible(true);


            for (String word : newWords.keySet()) {
                if (!needsDefinition && !needsTranslate) {
                    out.write(word + " " + newWords.get(word));
                    out.newLine();
                    out.newLine();
                    out.flush();
                } else {
                    new InternetService(word, newWords.get(word), out).start();
                }
            }

            if (!needsDefinition && !needsTranslate) {
                working_on_pane.setVisible(false);
                out.close();
            }
        } catch (IOException e) {
            textField.setText("can't open " + output.getName());
        }
    }

    public void close() {
        try {
            for (String get : delete) {
                newWords.remove(get);
            }

            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(knowl, true), StandardCharsets.UTF_8));
            for (String get : delete) {
                out.write(get + ", ");
            }
            out.flush();
            out.close();

            WordFormater wf = new WordFormater(knowl);
            wf.rewrite();

            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void go(boolean isFamiliar) {
        if (isFamiliar) {
            try {
                writer.write(current + " ");
                writer.flush();
                delete.add(current);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            delete.remove(current);
        }

        if (iterator.hasNext()) {
            reverse();
        } else {
            textField.setText("That is all!");
            Yes.setDisable(true);
            No.setDisable(true);
        }
    }

    public void back() {
        if (iterator.hasPrevious()) {
            if (progress_bar.getProgress() == 1) {
                Yes.setDisable(false);
                No.setDisable(false);
                ahead();
            }
            if (LastPrevNext) {
                iterator.previous();
                LastPrevNext = false;
            }
            if (iterator.hasPrevious()) {
                current = iterator.previous();

                textField.setText(current);
                done--;
                progress_bar.setProgress((done + 0.0) / counter);
                counter_text.setText(done + " / " + counter);
            }
        }
    }

    public void ahead() {
        if (iterator.hasNext()) {
            reverse();
        } else {
            textField.setText("That is all!");
            Yes.setDisable(true);
            No.setDisable(true);
        }
    }

    private void reverse() {
        if (!LastPrevNext) {
            iterator.next();
            LastPrevNext = true;
        }
        current = iterator.next();
        textField.setText(current);
        done++;
        progress_bar.setProgress((done + 0.0) / counter);
        counter_text.setText(done + " / " + counter);
    }


    private class InternetService extends Service<String> {
        public String word;
        public BufferedWriter out;

        private InternetService(String word, int num, BufferedWriter out) {
            this.word = word;
            this.out = out;
            setOnSucceeded(response -> {
                try {
                    out.write(word + " " + num + " ");
                    out.write((String) response.getSource().getValue());
                    out.flush();

                    if (defined == all) {
                        out.close();
                        working_on_pane.setVisible(false);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                if (defined == all) {
                    working_on_pane.setVisible(false);
                }

                working_on_progress_bar.setProgress(Math.abs(defined/all));
            });
        }

        @Override
        protected Task<String> createTask() {
            return new Task<>() {
                @Override
                protected String call() {
                    try {
                        String def = Dictionary.getDefinition(word).getDef();
                        String translate = Translator.getTranslate(word);
                        return needsTranslate && needsDefinition ? (translate + "\n" + def) : (needsTranslate ? translate + "\n" : "\n" + def);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return "";
                }

                @Override
                protected void succeeded() {
                    defined++;
                }

                @Override
                protected void failed() {
                    defined++;
                    getException().printStackTrace();
                }
            };
        }
    }

}