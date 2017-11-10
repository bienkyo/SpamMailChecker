package sample;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class df {
    static void calDf() throws SQLException {
        final Connection connection = DbConnector.getConnection();
        String sql = "SELECT id FROM words";
        PreparedStatement statement = connection.prepareStatement(sql);
        ResultSet rs = statement.executeQuery();


        int curWebID = 0;
        while(rs.next()) {
            int df = 0;

            int wordId = rs.getInt("id");
            sql = "SELECT webId, wordId, pos FROM wordmail where wordId = " + wordId;
            PreparedStatement statement1 = connection.prepareStatement(sql);
            ResultSet rs1 = statement1.executeQuery();

            while(rs1.next()) {
                int webId = rs1.getInt("webId");
                if(curWebID != webId) {
                    df++;
                    curWebID = webId;
                }
            }

            curWebID = 0;

            sql = "UPDATE words SET df = " + df + " where id = " + wordId;
            PreparedStatement statement2 = connection.prepareStatement(sql);
            statement2.executeUpdate();
        }
    }
}

