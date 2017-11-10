package sample;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;
import vn.hus.nlp.tokenizer.VietTokenizer;

import java.io.*;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Controller implements Initializable {
    List<String> stopWords = new ArrayList<>();
    @FXML
    Button button;
    @FXML
    TextArea text;
    int mailId = -1;


    @FXML
    protected void openFileChooser(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
        fileChooser.getExtensionFilters().add(extFilter);
        File file = fileChooser.showOpenDialog(button.getScene().getWindow());
        if (file != null) {
            final String data = readFile(file);
            text.setText(data);

        }
    }

    @FXML
    protected void checkMail(ActionEvent event) {

        final String data = text.getText();
        if (data != null) {
            readStopWords();
            if (mailId == -1) {
                Executors.newSingleThreadExecutor().submit(() -> {
                    System.out.println("MailId: " + mailId);
                    mailId = process(data);
                    Platform.runLater(() -> {
                        try {
                            final boolean isSpam = Classifier.classi(mailId);
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle("Result");
                            String content = isSpam ? "Đây là spam mail" : "Đây không phải là spam mail";
                            alert.setHeaderText(content);
                            alert.show();

                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    });
                });

            }


        }
    }

    private String readFile(File file) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            StringBuffer buffer = new StringBuffer();

            String line;

            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }
            return buffer.toString();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void readStopWords() {
        if (stopWords.size() != 0) {
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader("stopwords.txt"))) {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                stopWords.add(line);
                line = br.readLine();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int process(String data) {
        DbHelper dbHelper = DbHelper.getInstance();

        data = data.trim();
        VietTokenizer tokenizer = new VietTokenizer();
        final String[] words = tokenizer.tokenize(data)[0].split(" ");
        final Connection connection = DbConnector.getConnection();

        try {
            final int mailId = dbHelper.insertMail(data);
            for (int i = 0; i < words.length; i++) {
                String word = words[i].toLowerCase();
                System.out.println(word);
                if (stopWords.indexOf(word) == -1) {
                    int wordId = dbHelper.insertOrUpdateWord(word);
                    dbHelper.insertWordMail(mailId, wordId, i);
                }
            }

            tf.calTf();
            df.calDf();
            w.calW();
            return mailId;
        } catch (SQLException e) {
            Logger lgr = Logger.getLogger(Controller.class.getName());
            lgr.log(Level.WARNING, e.getMessage(), e);
        }
        return -1;

    }


    @Override
    @FXML
    public void initialize(URL location, ResourceBundle resources) {
        text.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                mailId = -1;
            }
        });
    }
}
