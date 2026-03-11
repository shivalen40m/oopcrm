package com.dms.ui;

import com.dms.dao.*;
import com.dms.model.*;
import com.dms.util.Utils;
import java.awt.*;
import java.sql.Date;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class SalesPanel extends JPanel {
    private JTable salesTable;
    private DefaultTableModel tableModel;
    private SaleDAO saleDAO;
    private CustomerDAO customerDAO;
    private VehicleDAO vehicleDAO;

    public SalesPanel() {
        saleDAO = new SaleDAO();
        customerDAO = new CustomerDAO();
        vehicleDAO = new VehicleDAO();
        setLayout(new BorderLayout());

        JLabel title = new JLabel("Sales Management", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        add(title, BorderLayout.NORTH);

        String[] columns = {"Sale ID", "Vehicle ID", "Customer", "Employee ID", "Sale Date", "Price", "Payment"};
        tableModel = new DefaultTableModel(columns, 0);
        salesTable = new JTable(tableModel);
        add(new JScrollPane(salesTable), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton newSaleButton = new JButton("New Sale");
        JButton viewDetailsButton = new JButton("View Details");
        JButton refreshButton = new JButton("Refresh");

        newSaleButton.addActionListener(e -> showNewSaleDialog());
        viewDetailsButton.addActionListener(e -> showSaleDetails());
        refreshButton.addActionListener(e -> loadSales());

        buttonPanel.add(newSaleButton);
        buttonPanel.add(viewDetailsButton);
        buttonPanel.add(refreshButton);
        add(buttonPanel, BorderLayout.SOUTH);

        loadSales();
    }

    private void loadSales() {
        tableModel.setRowCount(0);
        try {
            for (Sale s : saleDAO.getAllSales()) {
                tableModel.addRow(new Object[]{s.getId(), s.getVehicleId(), s.getCustomerId() + " - " + customerDAO.getCustomerById(s.getCustomerId()).getName(),
                    s.getEmployeeId(), s.getSaleDate(), s.getSalePrice(), s.getPaymentMethod()});
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void showNewSaleDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "New Sale", true);
        dialog.setLayout(new GridLayout(6, 2, 10, 10));
        dialog.setSize(500, 300);

        JComboBox<String> customerBox = new JComboBox<>();
        JComboBox<String> vehicleBox = new JComboBox<>();
        JTextField priceField = new JTextField();
        JComboBox<String> paymentBox = new JComboBox<>(new String[]{"Cash", "Credit Card", "Debit Card", "Bank Transfer", "Financing"});

        try {
            for (Customer c : customerDAO.getAllCustomers()) {
                if (c.isActive()) {
                    customerBox.addItem(c.getId() + " - " + c.getName());
                }
            }
            for (Vehicle v : vehicleDAO.getAllVehicles()) {
                if ("Available".equals(v.getStatus())) {
                    vehicleBox.addItem(v.getId() + " - " + v.getMake() + " " + v.getModel() + " (" + v.getYear() + ")");
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(dialog, "Error loading data: " + e.getMessage());
        }

        vehicleBox.addActionListener(e -> {
            try {
                String selected = (String) vehicleBox.getSelectedItem();
                if (selected != null) {
                    int vehicleId = Integer.parseInt(selected.split(" - ")[0]);
                    for (Vehicle v : vehicleDAO.getAllVehicles()) {
                        if (v.getId() == vehicleId) {
                            priceField.setText(String.valueOf(v.getPrice()));
                            break;
                        }
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        dialog.add(new JLabel("Customer:"));
        dialog.add(customerBox);
        dialog.add(new JLabel("Vehicle:"));
        dialog.add(vehicleBox);
        dialog.add(new JLabel("Sale Price:"));
        dialog.add(priceField);
        dialog.add(new JLabel("Payment Method:"));
        dialog.add(paymentBox);

        JButton saveButton = new JButton("Complete Sale");
        saveButton.addActionListener(e -> {
            try {
                String customerStr = (String) customerBox.getSelectedItem();
                String vehicleStr = (String) vehicleBox.getSelectedItem();
                
                if (customerStr == null || vehicleStr == null) {
                    JOptionPane.showMessageDialog(dialog, "Please select customer and vehicle");
                    return;
                }

                int customerId = Integer.parseInt(customerStr.split(" - ")[0]);
                int vehicleId = Integer.parseInt(vehicleStr.split(" - ")[0]);

                Sale sale = new Sale();
                sale.setVehicleId(vehicleId);
                sale.setCustomerId(customerId);
                sale.setEmployeeId(Utils.currentUser.getId());
                sale.setSaleDate(new Date(System.currentTimeMillis()));
                sale.setSalePrice(Double.parseDouble(priceField.getText()));
                sale.setPaymentMethod((String) paymentBox.getSelectedItem());

                saleDAO.createSale(sale);
                loadSales();
                dialog.dispose();
                JOptionPane.showMessageDialog(this, "Sale completed successfully!\nVehicle status updated to Sold.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage());
            }
        });

        dialog.add(saveButton);
        dialog.add(new JButton("Cancel") {{ addActionListener(e -> dialog.dispose()); }});
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void showSaleDetails() {
        int row = salesTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a sale to view details");
            return;
        }

        int saleId = (int) tableModel.getValueAt(row, 0);
        
        try {
            Sale sale = saleDAO.getSaleById(saleId);
            Customer customer = customerDAO.getCustomerById(sale.getCustomerId());
            Vehicle vehicle = vehicleDAO.getVehicleById(sale.getVehicleId());
            String employeename = UserDAO.getUsernameById(sale.getEmployeeId());

            JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Sale Details", true);
            dialog.setLayout(new GridLayout(10, 2, 10, 10));
            dialog.setSize(500, 400);

            dialog.add(new JLabel("Sale ID:"));
            dialog.add(new JLabel(String.valueOf(sale.getId())));
            dialog.add(new JLabel("Sale Date:"));
            dialog.add(new JLabel(String.valueOf(sale.getSaleDate())));
            dialog.add(new JLabel("Customer:"));
            dialog.add(new JLabel(customer.getName() + " (" + customer.getEmail() + ")"));
            dialog.add(new JLabel("Vehicle:"));
            dialog.add(new JLabel(vehicle.getMake() + " " + vehicle.getModel() + " " + vehicle.getYear()));
            dialog.add(new JLabel("VIN:"));
            dialog.add(new JLabel(vehicle.getVin()));
            dialog.add(new JLabel("Sale Price:"));
            dialog.add(new JLabel("$" + String.format("%.2f", sale.getSalePrice())));
            dialog.add(new JLabel("Payment Method:"));
            dialog.add(new JLabel(sale.getPaymentMethod()));
            dialog.add(new JLabel("Employee ID:"));
            dialog.add(new JLabel(String.valueOf(sale.getEmployeeId()) + " - " + employeename));

            JButton closeButton = new JButton("Close");
            closeButton.addActionListener(e -> dialog.dispose());
            dialog.add(new JLabel(""));
            dialog.add(closeButton);

            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }
}
