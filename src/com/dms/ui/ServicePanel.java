package com.dms.ui;

import com.dms.dao.ServiceDAO;
import com.dms.dao.VehicleDAO;
import com.dms.dao.CustomerDAO;
import com.dms.model.Service;
import com.dms.model.Vehicle;
import com.dms.model.Customer;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Date;
import java.util.List;

public class ServicePanel extends JPanel {
    private JTable serviceTable;
    private DefaultTableModel tableModel;
    private ServiceDAO serviceDAO;
    private VehicleDAO vehicleDAO;
    private CustomerDAO customerDAO;
    private JTextField searchField;

    public ServicePanel() {
        serviceDAO = new ServiceDAO();
        vehicleDAO = new VehicleDAO();
        customerDAO = new CustomerDAO();
        setLayout(new BorderLayout());

        JLabel title = new JLabel("Service Records", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        add(title, BorderLayout.NORTH);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Search by Vehicle ID:"));
        searchField = new JTextField(15);
        searchField.addActionListener(e -> searchByVehicle());
        searchPanel.add(searchField);
        JButton searchButton = new JButton("Search");
        searchButton.addActionListener(e -> searchByVehicle());
        searchPanel.add(searchButton);
        JButton showAllButton = new JButton("Show All");
        showAllButton.addActionListener(e -> loadServices());
        searchPanel.add(showAllButton);
        add(searchPanel, BorderLayout.NORTH);

        String[] columns = {"ID", "Vehicle ID", "Customer ID", "Description", "Cost", "Service Date"};
        tableModel = new DefaultTableModel(columns, 0);
        serviceTable = new JTable(tableModel);
        add(new JScrollPane(serviceTable), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Add Service Record");
        JButton refreshButton = new JButton("Refresh");

        addButton.addActionListener(e -> showAddDialog());
        refreshButton.addActionListener(e -> loadServices());

        buttonPanel.add(addButton);
        buttonPanel.add(refreshButton);
        add(buttonPanel, BorderLayout.SOUTH);

        loadServices();
    }

    private void loadServices() {
        tableModel.setRowCount(0);
        try {
            for (Service s : serviceDAO.getAllServices()) {
                tableModel.addRow(new Object[]{
                    s.getId(), 
                    s.getVehicleId(), 
                    s.getCustomerId(), 
                    s.getDescription(), 
                    String.format("$%.2f", s.getCost()), 
                    s.getServiceDate()
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void searchByVehicle() {
        String vehicleIdStr = searchField.getText().trim();
        if (vehicleIdStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a Vehicle ID");
            return;
        }

        try {
            int vehicleId = Integer.parseInt(vehicleIdStr);
            tableModel.setRowCount(0);
            for (Service s : serviceDAO.getServicesByVehicle(vehicleId)) {
                tableModel.addRow(new Object[]{
                    s.getId(), 
                    s.getVehicleId(), 
                    s.getCustomerId(), 
                    s.getDescription(), 
                    String.format("$%.2f", s.getCost()), 
                    s.getServiceDate()
                });
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid Vehicle ID");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void showAddDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add Service Record", true);
        dialog.setLayout(new GridLayout(6, 2, 10, 10));
        dialog.setSize(450, 300);

        // Load vehicles and customers for dropdowns
        JComboBox<String> vehicleCombo = new JComboBox<>();
        JComboBox<String> customerCombo = new JComboBox<>();
        
        try {
            List<Vehicle> vehicles = vehicleDAO.getAllVehicles();
            for (Vehicle v : vehicles) {
                vehicleCombo.addItem(v.getId() + " - " + v.getMake() + " " + v.getModel() + " (" + v.getVin() + ")");
            }

            List<Customer> customers = customerDAO.getAllCustomers();
            for (Customer c : customers) {
                if (c.isActive()) {
                    customerCombo.addItem(c.getId() + " - " + c.getName());
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(dialog, "Error loading data: " + e.getMessage());
            return;
        }

        JTextArea descriptionArea = new JTextArea(3, 20);
        JScrollPane descScrollPane = new JScrollPane(descriptionArea);
        JTextField costField = new JTextField();
        JTextField dateField = new JTextField(new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date()));

        dialog.add(new JLabel("Vehicle:"));
        dialog.add(vehicleCombo);
        dialog.add(new JLabel("Customer:"));
        dialog.add(customerCombo);
        dialog.add(new JLabel("Description:"));
        dialog.add(descScrollPane);
        dialog.add(new JLabel("Cost:"));
        dialog.add(costField);
        dialog.add(new JLabel("Service Date (yyyy-MM-dd):"));
        dialog.add(dateField);

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> {
            try {
                if (vehicleCombo.getSelectedItem() == null || customerCombo.getSelectedItem() == null) {
                    JOptionPane.showMessageDialog(dialog, "Please select vehicle and customer");
                    return;
                }

                String vehicleStr = vehicleCombo.getSelectedItem().toString();
                String customerStr = customerCombo.getSelectedItem().toString();
                int vehicleId = Integer.parseInt(vehicleStr.split(" - ")[0]);
                int customerId = Integer.parseInt(customerStr.split(" - ")[0]);

                Service service = new Service();
                service.setVehicleId(vehicleId);
                service.setCustomerId(customerId);
                service.setDescription(descriptionArea.getText());
                service.setCost(Double.parseDouble(costField.getText()));
                service.setServiceDate(Date.valueOf(dateField.getText()));
                
                serviceDAO.addService(service);
                loadServices();
                dialog.dispose();
                JOptionPane.showMessageDialog(this, "Service record added successfully!");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Invalid cost or date format");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage());
            }
        });

        dialog.add(saveButton);
        dialog.add(new JButton("Cancel") {{ addActionListener(e -> dialog.dispose()); }});
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
}
