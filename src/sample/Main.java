package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import vn.hus.nlp.tokenizer.VietTokenizer;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Spam Checker");
        primaryStage.setScene(new Scene(root, 600, 275));
        primaryStage.show();

    }


    public static void main(String[] args) {
        DbConnector.initialize();
        System.out.println("Connect successfully");
        launch(args);
        //initData();
        DbConnector.close();
        return;
    }

    public static void initData() {
        final Connection connection = DbConnector.getConnection();
        try {
            List<String> stopWords = readStopWords();
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM mail  ORDER BY id LIMIT 10 OFFSET 100");
            ResultSet result = statement.executeQuery();
            int count = 0;
            while (result.next()) {
                count++;
                final int mailId = result.getInt(1);
                final String content = result.getString(2);

                VietTokenizer tokenizer = new VietTokenizer();
                final String[] words = tokenizer.tokenize(content)[0].split(" ");
                System.out.println(count);
                for (int i = 0; i < words.length; i++) {
                    String word = words[i].toLowerCase();
                    if (stopWords.indexOf(word) == -1) {
                        int wordId = DbHelper.getInstance().insertOrUpdateWord(word);
                        DbHelper.getInstance().insertWordMail(mailId, wordId, i);
                    }
                }
                tf.calTf();
                df.calDf();
                w.calW();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static List<String> readStopWords() {
        List<String> stopWords = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader("stopwords.txt"))) {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                stopWords.add(line);
                line = br.readLine();
            }
            return stopWords;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stopWords;
    }
}
