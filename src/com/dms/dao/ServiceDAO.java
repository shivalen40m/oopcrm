package com.dms.dao;

import com.dms.model.Service;
import com.dms.database.Database;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceDAO {
    
    public void addService(Service service) throws SQLException {
        String sql = "INSERT INTO services (vehicle_id, customer_id, description, cost, service_date) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, service.getVehicleId());
            stmt.setInt(2, service.getCustomerId());
            stmt.setString(3, service.getDescription());
            stmt.setDouble(4, service.getCost());
            stmt.setDate(5, service.getServiceDate());
            stmt.executeUpdate();
        }
    }

    public List<Service> getAllServices() throws SQLException {
        List<Service> services = new ArrayList<>();
        String sql = "SELECT * FROM services ORDER BY service_date DESC";
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Service s = new Service();
                s.setId(rs.getInt("id"));
                s.setVehicleId(rs.getInt("vehicle_id"));
                s.setCustomerId(rs.getInt("customer_id"));
                s.setDescription(rs.getString("description"));
                s.setCost(rs.getDouble("cost"));
                s.setServiceDate(rs.getDate("service_date"));
                services.add(s);
            }
        }
        return services;
    }

    public List<Service> getServicesByVehicle(int vehicleId) throws SQLException {
        List<Service> services = new ArrayList<>();
        String sql = "SELECT * FROM services WHERE vehicle_id=? ORDER BY service_date DESC";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, vehicleId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Service s = new Service();
                s.setId(rs.getInt("id"));
                s.setVehicleId(rs.getInt("vehicle_id"));
                s.setCustomerId(rs.getInt("customer_id"));
                s.setDescription(rs.getString("description"));
                s.setCost(rs.getDouble("cost"));
                s.setServiceDate(rs.getDate("service_date"));
                services.add(s);
            }
        }
        return services;
    }
    
    public List<Service> getServicesByCustomer(int customerId) throws SQLException {
        List<Service> services = new ArrayList<>();
        String sql = "SELECT * FROM services WHERE customer_id=? ORDER BY service_date DESC";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, customerId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Service s = new Service();
                s.setId(rs.getInt("id"));
                s.setVehicleId(rs.getInt("vehicle_id"));
                s.setCustomerId(rs.getInt("customer_id"));
                s.setDescription(rs.getString("description"));
                s.setCost(rs.getDouble("cost"));
                s.setServiceDate(rs.getDate("service_date"));
                services.add(s);
            }
        }
        return services;
    }
}
