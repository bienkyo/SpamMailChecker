package sample;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Classifier {

    public static boolean classi(int webId) throws SQLException {
        final Connection connection = DbConnector.getConnection();

        //khởi tạo rate
        float rate1 = 1;   //spam mail  - type = -1
        float rate2 = 1;   //khong phai spam - type = 1


        // tính P(c_i)

        String sql = "SELECT * FROM mail where type = -1";   //lấy số lượng bài loại spam
        PreparedStatement statement = connection.prepareStatement(sql);
        ResultSet rs = statement.executeQuery();
        float c1 = 0;
        while(rs.next()) {
            c1++;
        }

        sql = "SELECT * FROM mail where type = 1";   //lấy số lượng bài loại khong phai spam
        PreparedStatement statement1 = connection.prepareStatement(sql);
        ResultSet rs1 = statement1.executeQuery();
        float c2 = 0;
        while(rs1.next()) {
            c2++;
        }

        float sum = c1 + c2;
        float pc1 = c1 / sum;
        float pc2 = c2 / sum;

        rate1 *= pc1;
        rate2 *= pc2;

        sql = "SELECT * FROM wordmail where webId = " + webId;   //lấy các từ thuộc mail đang xét trong bảng wordmail
        PreparedStatement statement3 = connection.prepareStatement(sql);
        ResultSet rs3 = statement3.executeQuery();
        while(rs3.next()) {
            //tính P(x_pos|c_i)
            int wordId = rs3.getInt("wordId");
            int pos = rs3.getInt("pos");


            //P(x_pos|c_1)
            sql = "SELECT * FROM wordmail WHERE wordId= " + wordId + " and pos = " + pos + " and webId in (select id from mail WHERE type = -1)";   //lấy số lượng bài loại spam
            PreparedStatement statement4 = connection.prepareStatement(sql);
            ResultSet rs4 = statement4.executeQuery();

            float cxpos1 = 0;
            while(rs4.next()) {
                cxpos1++;
            }
            if(cxpos1 != 0) rate1 *= cxpos1 / c1;

            //P(x_pos|c_2)
            sql = "SELECT * FROM wordmail WHERE wordId= " + wordId + " and pos = " + pos + " and webId in (select id from mail WHERE type = 1)";   //lấy số lượng bài loại không phải spam
            PreparedStatement statement5 = connection.prepareStatement(sql);
            ResultSet rs5 = statement5.executeQuery();

            float cxpos2 = 0;
            while(rs5.next()) {
                cxpos2++;
            }
            if(cxpos2 != 0) rate2 *= cxpos2 / c2;
        }

        float max = Math.max(rate1, rate2);
        System.out.println("rate: " + rate1 + " - " + rate2);

        if(max == rate1) {

            System.out.println("đây là spam mail");
            return true;
        }
        else{
            System.out.println("đây không phải spam mail");
            return false;
        }
    }
}