package com.dms.ui;

import com.dms.dao.VehicleDAO;
import com.dms.model.Vehicle;
import com.dms.util.Utils;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class VehiclePanel extends JPanel {
    private JTable vehicleTable;
    private DefaultTableModel tableModel;
    private VehicleDAO vehicleDAO;
    private JTextField searchField;
    private JComboBox<String> statusFilter;

    public VehiclePanel() {
        vehicleDAO = new VehicleDAO();
        setLayout(new BorderLayout());

        JLabel title = new JLabel("Vehicle Inventory", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        //add(title, BorderLayout.NORTH);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Search:"));
        searchField = new JTextField(20);
        searchField.addActionListener(e -> searchVehicles());
        searchPanel.add(searchField);
        searchPanel.add(new JLabel("Status:"));
        statusFilter = new JComboBox<>(new String[]{"All", "Available", "Sold", "Reserved"});
        searchPanel.add(statusFilter);
        JButton searchButton = new JButton("Search");
        searchButton.addActionListener(e -> searchVehicles());
        searchPanel.add(searchButton);
        //add(searchPanel, BorderLayout.NORTH);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(title, BorderLayout.NORTH);
        topPanel.add(searchPanel, BorderLayout.SOUTH);
        add(topPanel, BorderLayout.NORTH);

        String[] columns = {"ID", "VIN", "Make", "Model", "Year", "Price", "Status"};
        tableModel = new DefaultTableModel(columns, 0);
        vehicleTable = new JTable(tableModel);
        add(new JScrollPane(vehicleTable), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Add Vehicle");
        JButton editButton = new JButton("Edit Vehicle");
        JButton deleteButton = new JButton("Delete Vehicle");
        JButton refreshButton = new JButton("Refresh");

        addButton.addActionListener(e -> showAddDialog());
        editButton.addActionListener(e -> showEditDialog());
        deleteButton.addActionListener(e -> deleteVehicle());
        refreshButton.addActionListener(e -> loadVehicles());

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);
        add(buttonPanel, BorderLayout.SOUTH);

        if (!Utils.currentUser.getRole().equals("ADMIN")) {
            deleteButton.setEnabled(false);
        }

        loadVehicles();
    }

    public void openAddDialog() {
        showAddDialog();
    }

    private void loadVehicles() {
        tableModel.setRowCount(0);
        try {
            for (Vehicle v : vehicleDAO.getAllVehicles()) {
                tableModel.addRow(new Object[]{v.getId(), v.getVin(), v.getMake(), 
                    v.getModel(), v.getYear(), String.format("$%.2f", v.getPrice()), v.getStatus()});
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void searchVehicles() {
        String keyword = searchField.getText().trim();
        String status = (String) statusFilter.getSelectedItem();
        tableModel.setRowCount(0);
        try {
            for (Vehicle v : vehicleDAO.searchVehicles(keyword, status)) {
                tableModel.addRow(new Object[]{v.getId(), v.getVin(), v.getMake(), 
                    v.getModel(), v.getYear(), String.format("$%.2f", v.getPrice()), v.getStatus()});
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void showAddDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add Vehicle", true);
        dialog.setLayout(new GridLayout(7, 2, 10, 10));
        dialog.setSize(400, 300);

        JTextField vinField = new JTextField();
        JTextField makeField = new JTextField();
        JTextField modelField = new JTextField();
        JTextField yearField = new JTextField();
        JTextField priceField = new JTextField();
        JComboBox<String> statusCombo = new JComboBox<>(new String[]{"Available", "Sold", "Reserved"});

        dialog.add(new JLabel("VIN:"));
        dialog.add(vinField);
        dialog.add(new JLabel("Make:"));
        dialog.add(makeField);
        dialog.add(new JLabel("Model:"));
        dialog.add(modelField);
        dialog.add(new JLabel("Year:"));
        dialog.add(yearField);
        dialog.add(new JLabel("Price:"));
        dialog.add(priceField);
        dialog.add(new JLabel("Status:"));
        dialog.add(statusCombo);

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> {
            try {
                Vehicle vehicle = new Vehicle();
                vehicle.setVin(vinField.getText());
                vehicle.setMake(makeField.getText());
                vehicle.setModel(modelField.getText());
                vehicle.setYear(Integer.parseInt(yearField.getText()));
                vehicle.setPrice(Double.parseDouble(priceField.getText()));
                vehicle.setStatus((String) statusCombo.getSelectedItem());
                vehicleDAO.addVehicle(vehicle);
                loadVehicles();
                dialog.dispose();
                JOptionPane.showMessageDialog(this, "Vehicle added successfully!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage());
            }
        });

        dialog.add(saveButton);
        dialog.add(new JButton("Cancel") {{ addActionListener(e -> dialog.dispose()); }});
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void showEditDialog() {
        int row = vehicleTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a vehicle to edit");
            return;
        }

        int id = (int) tableModel.getValueAt(row, 0);
        String vin = (String) tableModel.getValueAt(row, 1);
        String make = (String) tableModel.getValueAt(row, 2);
        String model = (String) tableModel.getValueAt(row, 3);
        int year = (int) tableModel.getValueAt(row, 4);
        String priceStr = (String) tableModel.getValueAt(row, 5);
        double price = Double.parseDouble(priceStr.replace("$", "").replace(",", ""));
        String status = (String) tableModel.getValueAt(row, 6);

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Edit Vehicle", true);
        dialog.setLayout(new GridLayout(7, 2, 10, 10));
        dialog.setSize(400, 300);

        JTextField vinField = new JTextField(vin);
        JTextField makeField = new JTextField(make);
        JTextField modelField = new JTextField(model);
        JTextField yearField = new JTextField(String.valueOf(year));
        JTextField priceField = new JTextField(String.valueOf(price));
        JComboBox<String> statusCombo = new JComboBox<>(new String[]{"Available", "Sold", "Reserved"});
        statusCombo.setSelectedItem(status);

        dialog.add(new JLabel("VIN:"));
        dialog.add(vinField);
        dialog.add(new JLabel("Make:"));
        dialog.add(makeField);
        dialog.add(new JLabel("Model:"));
        dialog.add(modelField);
        dialog.add(new JLabel("Year:"));
        dialog.add(yearField);
        dialog.add(new JLabel("Price:"));
        dialog.add(priceField);
        dialog.add(new JLabel("Status:"));
        dialog.add(statusCombo);

        JButton saveButton = new JButton("Update");
        saveButton.addActionListener(e -> {
            try {
                Vehicle vehicle = new Vehicle();
                vehicle.setId(id);
                vehicle.setVin(vinField.getText());
                vehicle.setMake(makeField.getText());
                vehicle.setModel(modelField.getText());
                vehicle.setYear(Integer.parseInt(yearField.getText()));
                vehicle.setPrice(Double.parseDouble(priceField.getText()));
                vehicle.setStatus((String) statusCombo.getSelectedItem());
                vehicleDAO.updateVehicle(vehicle);
                loadVehicles();
                dialog.dispose();
                JOptionPane.showMessageDialog(this, "Vehicle updated successfully!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage());
            }
        });

        dialog.add(saveButton);
        dialog.add(new JButton("Cancel") {{ addActionListener(e -> dialog.dispose()); }});
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void deleteVehicle() {
        int row = vehicleTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a vehicle to delete");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to delete this vehicle?", 
            "Confirm Delete", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            int id = (int) tableModel.getValueAt(row, 0);
            try {
                vehicleDAO.deleteVehicle(id);
                loadVehicles();
                JOptionPane.showMessageDialog(this, "Vehicle deleted successfully!");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            }
        }
    }
}
