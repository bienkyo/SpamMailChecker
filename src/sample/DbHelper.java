package sample;

import java.sql.*;

public class DbHelper {
    private static DbHelper sInstance;
    private Connection connection;

    private DbHelper() {
        connection = DbConnector.getConnection();
    }

    public static DbHelper getInstance() {
        if (sInstance == null) {
            sInstance = new DbHelper();
        }
        return sInstance;
    }

    public int insertOrUpdateWord(String word) throws SQLException {
        String sql = "SELECT id FROM words where word = ?";
        PreparedStatement statement1 = connection.prepareStatement(sql);
        statement1.setString(1,word);
        ResultSet resultSet = statement1.executeQuery();

        if (resultSet.next()) {
            return resultSet.getInt(1);
        } else {
            sql = "INSERT INTO words(word) VALUES(?)";
            PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1,word);
            final int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating user failed, no rows affected.");
            }

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
                else {
                    throw new SQLException("Creating user failed, no ID obtained.");
                }
            }

        }
    }

    public int insertMail(String data) throws SQLException {
        String sql = "INSERT INTO mail(content) VALUES(?)";
        PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        statement.setString(1,data);
        final int affectedRows = statement.executeUpdate();
        if (affectedRows == 0) {
            throw new SQLException("Insert mail failed, no rows affected.");
        }

        try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
            if (generatedKeys.next()) {
                System.out.println(generatedKeys.toString());
                return generatedKeys.getInt(1);
            }
            else {
                throw new SQLException("Creating mail failed, no ID obtained.");
            }
        }
    }

    public void insertWordMail(int mailId, int wordId, int pos) throws SQLException {
        String sql = "INSERT INTO wordmail(webId,wordId,pos) VALUES (?,?,?)";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setInt(1,mailId);
        statement.setInt(2,wordId);
        statement.setInt(3,pos);
        statement.executeUpdate();
    }
}
