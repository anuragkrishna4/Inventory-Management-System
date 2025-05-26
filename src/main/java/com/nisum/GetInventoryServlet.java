package com.nisum;

import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;


public class GetInventoryServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        try (Connection conn = DBUtil.getConnection()) {
            String sql = "SELECT * FROM ProductInventory";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            StringBuilder json = new StringBuilder();
            json.append("[");

            boolean first = true;
            while (rs.next()) {
                if (!first) {
                    json.append(",");
                } else {
                    first = false;
                }
                json.append("{")
                        .append("\"SKU\":\"").append(rs.getString("SKU")).append("\",")
                        .append("\"ProductID\":").append(rs.getInt("ProductID")).append(",")
                        .append("\"CategoryID\":").append(rs.getInt("CategoryID")).append(",")
                        .append("\"Location\":\"").append(rs.getString("Location")).append("\",")
                        .append("\"Quantity\":").append(rs.getInt("Quantity")).append(",")
                        .append("\"OrderID\":").append(rs.getInt("OrderID")).append(",")
                        .append("\"IsCancelled\":").append(rs.getBoolean("IsCancelled")).append(",")
                        .append("\"OrderAllocatedQty\":").append(rs.getInt("OrderAllocatedQty")).append(",")
                        .append("\"OrderReservedQty\":").append(rs.getInt("OrderReservedQty"))
                        .append("}");
            }
            json.append("]");

            out.print(json.toString());

            rs.close();
            stmt.close();

        } catch (Exception e) {
            e.printStackTrace();
            out.print("{\"status\":\"error\", \"message\":\"" + e.getMessage() + "\"}");
        }
        out.flush();
        out.close();
    }
}
