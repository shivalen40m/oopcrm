package com.dms.ui;

import com.dms.dao.CustomerDAO;
import com.dms.dao.ServiceDAO;
import com.dms.dao.VehicleDAO;
import com.dms.model.Customer;
import com.dms.model.Service;
import com.dms.model.Vehicle;
import java.awt.*;
import java.sql.Date;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

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
        //add(title, BorderLayout.NORTH);

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
        //add(searchPanel, BorderLayout.NORTH);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(title, BorderLayout.NORTH);
        topPanel.add(searchPanel, BorderLayout.SOUTH);
        add(topPanel, BorderLayout.NORTH);

        String[] columns = {"ID", "Vehicle ID", "Customer", "Description", "Cost", "Service Date"};
        tableModel = new DefaultTableModel(columns, 0);
        serviceTable = new JTable(tableModel);
        add(new JScrollPane(serviceTable), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Add Service Record");
        JButton viewButton = new JButton("View Details");
        JButton refreshButton = new JButton("Refresh");

        addButton.addActionListener(e -> showAddDialog());
        viewButton.addActionListener(e -> showDetailsDialog());
        refreshButton.addActionListener(e -> loadServices());

        buttonPanel.add(addButton);
        buttonPanel.add(viewButton);
        buttonPanel.add(refreshButton);
        add(buttonPanel, BorderLayout.SOUTH);

        loadServices();
    }

    public void newAddService() {
        showAddDialog();
    }

    private void loadServices() {
        tableModel.setRowCount(0);
        try {
            for (Service s : serviceDAO.getAllServices()) {
                String customerName = getCustomerName(s.getCustomerId());
                tableModel.addRow(new Object[]{
                    s.getId(),
                    s.getVehicleId(),
                    customerName,
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
                String customerName = getCustomerName(s.getCustomerId());
                tableModel.addRow(new Object[]{
                    s.getId(),
                    s.getVehicleId(),
                    customerName,
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

    private String getCustomerName(int customerId) {
        if (customerId == 0) return "Dealership";
        try {
            for (Customer c : customerDAO.getAllCustomers()) {
                if (c.getId() == customerId) return c.getId() + " - " + c.getName();
            }
        } catch (Exception e) {
            return "Unknown";
        }
        return "Unknown";
    }

    private void showAddDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add Service Record", true);
        dialog.setLayout(new GridLayout(6, 2, 10, 10));
        dialog.setSize(450, 300);

        JComboBox<String> vehicleCombo = new JComboBox<>();
        JComboBox<String> customerCombo = new JComboBox<>();

        try {
            for (Vehicle v : vehicleDAO.getAllVehicles()) {
                vehicleCombo.addItem(v.getId() + " - " + v.getMake() + " " + v.getModel() + " (" + v.getVin() + ")");
            }
            customerCombo.addItem("0 - Dealership (No Customer)");
            for (Customer c : customerDAO.getAllCustomers()) {
                if (c.isActive()) customerCombo.addItem(c.getId() + " - " + c.getName());
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(dialog, "Error loading data: " + e.getMessage());
            return;
        }

        JTextArea descriptionArea = new JTextArea(3, 20);
        JTextField costField = new JTextField();
        JTextField dateField = new JTextField(new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date()));

        dialog.add(new JLabel("Vehicle:")); dialog.add(vehicleCombo);
        dialog.add(new JLabel("Customer:")); dialog.add(customerCombo);
        dialog.add(new JLabel("Description:")); dialog.add(new JScrollPane(descriptionArea));
        dialog.add(new JLabel("Cost:")); dialog.add(costField);
        dialog.add(new JLabel("Service Date (yyyy-MM-dd):")); dialog.add(dateField);

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> {
            try {
                if (vehicleCombo.getSelectedItem() == null) {
                    JOptionPane.showMessageDialog(dialog, "Please select a vehicle");
                    return;
                }
                int vehicleId = Integer.parseInt(vehicleCombo.getSelectedItem().toString().split(" - ")[0]);
                int customerId = Integer.parseInt(customerCombo.getSelectedItem().toString().split(" - ")[0]);

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

    private void showDetailsDialog() {
        int row = serviceTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a service record to view details");
            return;
        }

        int serviceId = (int) tableModel.getValueAt(row, 0);
        int vehicleId = (int) tableModel.getValueAt(row, 1);
        String customer = (String) tableModel.getValueAt(row, 2);
        String description = (String) tableModel.getValueAt(row, 3);
        String cost = (String) tableModel.getValueAt(row, 4);
        Object date = tableModel.getValueAt(row, 5);

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
            "Service Details - #" + serviceId, true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(500, 380);

        JTabbedPane tabbedPane = new JTabbedPane();

        // Info tab
        JPanel infoPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        String vehicleInfo = "ID: " + vehicleId;
        try {
            Vehicle v = vehicleDAO.getVehicleById(vehicleId);
            if (v != null) vehicleInfo = v.getMake() + " " + v.getModel() + " (" + v.getVin() + ")";
        } catch (Exception ignored) {}

        infoPanel.add(new JLabel("Service ID:")); infoPanel.add(new JLabel(String.valueOf(serviceId)));
        infoPanel.add(new JLabel("Vehicle:"));    infoPanel.add(new JLabel(vehicleInfo));
        infoPanel.add(new JLabel("Customer:"));   infoPanel.add(new JLabel(customer));
        infoPanel.add(new JLabel("Cost:"));       infoPanel.add(new JLabel(cost));
        infoPanel.add(new JLabel("Date:"));       infoPanel.add(new JLabel(String.valueOf(date)));
        tabbedPane.addTab("Info", infoPanel);

        // Description tab
        JPanel descPanel = new JPanel(new BorderLayout());
        descPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JTextArea descArea = new JTextArea(description);
        descArea.setEditable(false);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        descPanel.add(new JScrollPane(descArea), BorderLayout.CENTER);
        tabbedPane.addTab("Description", descPanel);

        dialog.add(tabbedPane, BorderLayout.CENTER);

        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dialog.dispose());
        JPanel bottomPanel = new JPanel();
        bottomPanel.add(closeButton);
        dialog.add(bottomPanel, BorderLayout.SOUTH);

        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
}
