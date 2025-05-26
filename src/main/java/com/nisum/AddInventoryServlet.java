package com.nisum;

import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;

import java.sql.Connection;
import java.sql.PreparedStatement;

@WebServlet("/api/addInventory")
public class AddInventoryServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Read the JSON body
        StringBuilder sb = new StringBuilder();
        BufferedReader reader = request.getReader();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }

        JSONObject json = new JSONObject(sb.toString());

        String sku = json.getString("SKU");
        int productId = json.getInt("ProductID");
        int categoryId = json.getInt("CategoryID");
        String location = json.getString("Location");
        int quantity = json.getInt("Quantity");
        int orderId = json.getInt("OrderID");
        boolean isCancelled = json.getBoolean("IsCancelled");
        int orderAllocatedQty = json.getInt("OrderAllocatedQty");
        int orderReservedQty = json.getInt("OrderReservedQty");

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        try (Connection conn = DBUtil.getConnection()) {
            String sql = "INSERT INTO ProductInventory " +
                    "(SKU, ProductID, CategoryID, Location, Quantity, OrderID, IsCancelled, OrderAllocatedQty, OrderReservedQty) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, sku);
            ps.setInt(2, productId);
            ps.setInt(3, categoryId);
            ps.setString(4, location);
            ps.setInt(5, quantity);
            ps.setInt(6, orderId);
            ps.setBoolean(7, isCancelled);
            ps.setInt(8, orderAllocatedQty);
            ps.setInt(9, orderReservedQty);

            int rows = ps.executeUpdate();
            if (rows > 0) {
                out.print("{\"status\":\"success\", \"message\":\"Inventory added successfully.\"}");
            } else {
                out.print("{\"status\":\"error\", \"message\":\"Failed to add inventory.\"}");
            }

            ps.close();

        } catch (Exception e) {
            e.printStackTrace();
            out.print("{\"status\":\"error\", \"message\":\"" + e.getMessage() + "\"}");
        }

        out.flush();
        out.close();
    }
}
