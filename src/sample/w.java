package sample;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class w {
    static void calW() throws SQLException {
        final Connection connection = DbConnector.getConnection();
        String sql = "SELECT DISTINCT webId FROM wordmail";   //lấy id cái bài viết -> tổng số bài viết
        PreparedStatement statement = connection.prepareStatement(sql);
        ResultSet rs = statement.executeQuery();

        float n = 0;
        while(rs.next()) {
            n++;
        }

        sql = "SELECT id, wordId, tf FROM wordmail";   //lấy từng hàng
        PreparedStatement statement1 = connection.prepareStatement(sql);
        ResultSet rs1 = statement1.executeQuery();

        while(rs1.next()) {
            int wordId = rs1.getInt("wordId");
            sql = "SELECT df FROM words where id = " + wordId;   //lấy df cửa từ đó
            PreparedStatement statement2 = connection.prepareStatement(sql);
            ResultSet rs2 = statement2.executeQuery();

            rs2.next();
            float df = (float) rs2.getInt("df");

            float tf = (float) rs1.getInt("tf");  //lấy tf

            //tính w
            float w = (float) (tf * Math.log10(n/df));

            int id = rs1.getInt("id");
            sql = "UPDATE wordmail SET w = " + w + " where id = " + id;
            PreparedStatement statement3 = connection.prepareStatement(sql);
            statement3.executeUpdate();
        }
    }

}
