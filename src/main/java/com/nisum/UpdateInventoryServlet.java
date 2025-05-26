package com.nisum;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;


public class UpdateInventoryServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String sku = request.getParameter("SKU");
        String quantityStr = request.getParameter("Quantity");
        String orderAllocatedQtyStr = request.getParameter("OrderAllocatedQty");
        String orderReservedQtyStr = request.getParameter("OrderReservedQty");
        String isCancelledStr = request.getParameter("IsCancelled");

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        try (Connection conn = DBUtil.getConnection()) {
            String sql = "UPDATE ProductInventory SET Quantity = ?, OrderAllocatedQty = ?, OrderReservedQty = ?, IsCancelled = ? WHERE SKU = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, Integer.parseInt(quantityStr));
            ps.setInt(2, Integer.parseInt(orderAllocatedQtyStr));
            ps.setInt(3, Integer.parseInt(orderReservedQtyStr));
            ps.setBoolean(4, Boolean.parseBoolean(isCancelledStr));
            ps.setString(5, sku);

            int rows = ps.executeUpdate();
            if (rows > 0) {
                out.print("{\"status\":\"success\", \"message\":\"Inventory updated successfully.\"}");
            } else {
                out.print("{\"status\":\"error\", \"message\":\"Failed to update inventory.\"}");
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
