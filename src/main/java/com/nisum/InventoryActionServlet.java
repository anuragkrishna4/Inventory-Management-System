package com.nisum;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;


public class InventoryActionServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        String sku = request.getParameter("SKU");
        String action = request.getParameter("action"); // e.g. cancel, allocate, reserve
        String qtyStr = request.getParameter("quantity");

        if (sku == null || action == null) {
            out.print("{\"status\":\"error\",\"message\":\"SKU and action are required\"}");
            return;
        }

        try (Connection conn = DBUtil.getConnection()) {
            String sql = null;

            switch (action.toLowerCase()) {
                case "cancel":
                    sql = "UPDATE ProductInventory SET IsCancelled = ? WHERE SKU = ?";
                    PreparedStatement cancelStmt = conn.prepareStatement(sql);
                    cancelStmt.setBoolean(1, true);
                    cancelStmt.setString(2, sku);
                    int cancelledRows = cancelStmt.executeUpdate();
                    if (cancelledRows > 0)
                        out.print("{\"status\":\"success\",\"message\":\"Inventory cancelled successfully\"}");
                    else
                        out.print("{\"status\":\"error\",\"message\":\"No inventory found with given SKU\"}");
                    break;

                case "allocate":
                    if (qtyStr == null) {
                        out.print("{\"status\":\"error\",\"message\":\"Quantity is required for allocate action\"}");
                        return;
                    }
                    int qty = Integer.parseInt(qtyStr);
                    sql = "UPDATE ProductInventory SET OrderAllocatedQty = OrderAllocatedQty + ? WHERE SKU = ?";
                    PreparedStatement allocateStmt = conn.prepareStatement(sql);
                    allocateStmt.setInt(1, qty);
                    allocateStmt.setString(2, sku);
                    int allocatedRows = allocateStmt.executeUpdate();
                    if (allocatedRows > 0)
                        out.print("{\"status\":\"success\",\"message\":\"Inventory allocated successfully\"}");
                    else
                        out.print("{\"status\":\"error\",\"message\":\"No inventory found with given SKU\"}");
                    break;

                case "reserve":
                    if (qtyStr == null) {
                        out.print("{\"status\":\"error\",\"message\":\"Quantity is required for reserve action\"}");
                        return;
                    }
                    int reserveQty = Integer.parseInt(qtyStr);
                    sql = "UPDATE ProductInventory SET OrderReservedQty = OrderReservedQty + ? WHERE SKU = ?";
                    PreparedStatement reserveStmt = conn.prepareStatement(sql);
                    reserveStmt.setInt(1, reserveQty);
                    reserveStmt.setString(2, sku);
                    int reservedRows = reserveStmt.executeUpdate();
                    if (reservedRows > 0)
                        out.print("{\"status\":\"success\",\"message\":\"Inventory reserved successfully\"}");
                    else
                        out.print("{\"status\":\"error\",\"message\":\"No inventory found with given SKU\"}");
                    break;

                default:
                    out.print("{\"status\":\"error\",\"message\":\"Unknown action\"}");
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            out.print("{\"status\":\"error\",\"message\":\"" + e.getMessage() + "\"}");
        }
        out.flush();
    }
}
