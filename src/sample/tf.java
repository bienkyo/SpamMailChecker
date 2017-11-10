package sample;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class tf {
    static void calTf() throws SQLException {
        final Connection connection = DbConnector.getConnection();
        String sql = "SELECT DISTINCT webId FROM wordmail";   //lấy id cái bài viết
        PreparedStatement statement = connection.prepareStatement(sql);
        ResultSet rs = statement.executeQuery();

        while(rs.next()) {
            int webId = rs.getInt("webId");
            sql = "SELECT DISTINCT wordId FROM wordmail where webId = " + webId;   //lấy id các từ
            PreparedStatement statement1 = connection.prepareStatement(sql);
            ResultSet rs1 = statement1.executeQuery();

            while(rs1.next()) {
                int wordId = rs1.getInt("wordId");
                sql = "SELECT * FROM wordmail where webId = " + webId + " and wordId = " + wordId;
                PreparedStatement statement2 = connection.prepareStatement(sql);
                ResultSet rs2 = statement2.executeQuery();

                //số lần xuất hiện của từ trong bài viết đó
                int tf = 0;
                while(rs2.next()) {
                    tf++;
                }

                sql = "UPDATE wordmail SET tf = " + tf + " where webId = " + webId + " AND wordId = " + wordId;
                PreparedStatement statement3 = connection.prepareStatement(sql);
                statement3.executeUpdate();
            }
        }
    }
}
