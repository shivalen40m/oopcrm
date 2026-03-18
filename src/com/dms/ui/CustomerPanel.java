package com.dms.ui;

import com.dms.dao.CustomerDAO;
import com.dms.dao.SaleDAO;
import com.dms.dao.ServiceDAO;
import com.dms.model.Customer;
import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class CustomerPanel extends JPanel {
    private JTable customerTable;
    private DefaultTableModel tableModel;
    private CustomerDAO customerDAO;
    private SaleDAO saleDAO;
    private ServiceDAO serviceDAO;
    private JTextField searchField;

    public CustomerPanel() {
        customerDAO = new CustomerDAO();
        saleDAO = new SaleDAO();
        serviceDAO = new ServiceDAO();
        setLayout(new BorderLayout());

        JLabel title = new JLabel("Customer Management", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        //add(title, BorderLayout.NORTH);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Search:"));
        searchField = new JTextField(20);
        searchField.addActionListener(e -> searchCustomers());
        searchPanel.add(searchField);
        JButton searchButton = new JButton("Search");
        searchButton.addActionListener(e -> searchCustomers());
        searchPanel.add(searchButton);
        //add(searchPanel, BorderLayout.NORTH);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(title, BorderLayout.NORTH);
        topPanel.add(searchPanel, BorderLayout.SOUTH);
        add(topPanel, BorderLayout.NORTH);

        String[] columns = {"ID", "Name", "Email", "Phone", "Address", "Active"};
        tableModel = new DefaultTableModel(columns, 0);
        customerTable = new JTable(tableModel);
        add(new JScrollPane(customerTable), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Add Customer");
        JButton editButton = new JButton("Edit Customer");
        JButton deleteButton = new JButton("Delete (Soft)");
        JButton viewDetailsButton = new JButton("View Details");
        JButton refreshButton = new JButton("Refresh");

        addButton.addActionListener(e -> showAddDialog());
        editButton.addActionListener(e -> showEditDialog());
        deleteButton.addActionListener(e -> softDeleteCustomer());
        viewDetailsButton.addActionListener(e -> showCustomerDetails());
        refreshButton.addActionListener(e -> loadCustomers());

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(viewDetailsButton);
        buttonPanel.add(refreshButton);
        add(buttonPanel, BorderLayout.SOUTH);

        loadCustomers();
    }

    public void openAddDialog() {
        showAddDialog();
    }

    private void loadCustomers() {
        tableModel.setRowCount(0);
        try {
            for (Customer c : customerDAO.getAllCustomers()) {
                tableModel.addRow(new Object[]{c.getId(), c.getName(), c.getEmail(), 
                    c.getPhone(), c.getAddress(), c.isActive()});
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void searchCustomers() {
        String searchTerm = searchField.getText().toLowerCase();
        tableModel.setRowCount(0);
        try {
            for (Customer c : customerDAO.getAllCustomers()) {
                if (c.getName().toLowerCase().contains(searchTerm) || 
                    c.getEmail().toLowerCase().contains(searchTerm) ||
                    c.getPhone().contains(searchTerm)) {
                    tableModel.addRow(new Object[]{c.getId(), c.getName(), c.getEmail(), 
                        c.getPhone(), c.getAddress(), c.isActive()});
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void showAddDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add Customer", true);
        dialog.setLayout(new GridLayout(5, 2, 10, 10));
        dialog.setSize(400, 250);

        JTextField nameField = new JTextField();
        JTextField emailField = new JTextField();
        JTextField phoneField = new JTextField();
        JTextField addressField = new JTextField();

        dialog.add(new JLabel("Name:"));
        dialog.add(nameField);
        dialog.add(new JLabel("Email:"));
        dialog.add(emailField);
        dialog.add(new JLabel("Phone:"));
        dialog.add(phoneField);
        dialog.add(new JLabel("Address:"));
        dialog.add(addressField);

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> {
            try {
                Customer customer = new Customer();
                customer.setName(nameField.getText());
                customer.setEmail(emailField.getText());
                customer.setPhone(phoneField.getText());
                customer.setAddress(addressField.getText());
                customer.setActive(true);
                customerDAO.addCustomer(customer);
                loadCustomers();
                dialog.dispose();
                JOptionPane.showMessageDialog(this, "Customer added successfully!");
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
        int row = customerTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a customer to edit");
            return;
        }

        int id = (int) tableModel.getValueAt(row, 0);
        String name = (String) tableModel.getValueAt(row, 1);
        String email = (String) tableModel.getValueAt(row, 2);
        String phone = (String) tableModel.getValueAt(row, 3);
        String address = (String) tableModel.getValueAt(row, 4);

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Edit Customer", true);
        dialog.setLayout(new GridLayout(5, 2, 10, 10));
        dialog.setSize(400, 250);

        JTextField nameField = new JTextField(name);
        JTextField emailField = new JTextField(email);
        JTextField phoneField = new JTextField(phone);
        JTextField addressField = new JTextField(address);

        dialog.add(new JLabel("Name:"));
        dialog.add(nameField);
        dialog.add(new JLabel("Email:"));
        dialog.add(emailField);
        dialog.add(new JLabel("Phone:"));
        dialog.add(phoneField);
        dialog.add(new JLabel("Address:"));
        dialog.add(addressField);

        JButton saveButton = new JButton("Update");
        saveButton.addActionListener(e -> {
            try {
                Customer customer = new Customer();
                customer.setId(id);
                customer.setName(nameField.getText());
                customer.setEmail(emailField.getText());
                customer.setPhone(phoneField.getText());
                customer.setAddress(addressField.getText());
                customerDAO.updateCustomer(customer);
                loadCustomers();
                dialog.dispose();
                JOptionPane.showMessageDialog(this, "Customer updated successfully!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage());
            }
        });

        dialog.add(saveButton);
        dialog.add(new JButton("Cancel") {{ addActionListener(e -> dialog.dispose()); }});
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void softDeleteCustomer() {
        int row = customerTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a customer to delete");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to deactivate this customer?", 
            "Confirm Delete", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            int id = (int) tableModel.getValueAt(row, 0);
            try {
                customerDAO.deleteCustomer(id);
                loadCustomers();
                JOptionPane.showMessageDialog(this, "Customer deactivated successfully!");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            }
        }
    }

    private void showCustomerDetails() {
        int row = customerTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a customer to view details");
            return;
        }

        int customerId = (int) tableModel.getValueAt(row, 0);
        String name = (String) tableModel.getValueAt(row, 1);

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), 
            "Customer Details - " + name, true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(600, 400);

        JTabbedPane tabbedPane = new JTabbedPane();

        JPanel infoPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        infoPanel.add(new JLabel("ID:"));
        infoPanel.add(new JLabel(String.valueOf(tableModel.getValueAt(row, 0))));
        infoPanel.add(new JLabel("Name:"));
        infoPanel.add(new JLabel((String) tableModel.getValueAt(row, 1)));
        infoPanel.add(new JLabel("Email:"));
        infoPanel.add(new JLabel((String) tableModel.getValueAt(row, 2)));
        infoPanel.add(new JLabel("Phone:"));
        infoPanel.add(new JLabel((String) tableModel.getValueAt(row, 3)));
        infoPanel.add(new JLabel("Address:"));
        infoPanel.add(new JLabel((String) tableModel.getValueAt(row, 4)));
        tabbedPane.addTab("Info", infoPanel);

        JPanel purchasePanel = new JPanel(new BorderLayout());
        String[] purchaseCols = {"Sale ID", "Vehicle ID", "Sale Date", "Price"};
        DefaultTableModel purchaseModel = new DefaultTableModel(purchaseCols, 0);
        JTable purchaseTable = new JTable(purchaseModel);
        try {
            for (var sale : saleDAO.getSalesByCustomer(customerId)) {
                purchaseModel.addRow(new Object[]{sale.getId(), sale.getVehicleId(), 
                    sale.getSaleDate(), sale.getSalePrice()});
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(dialog, "Error loading purchases: " + e.getMessage());
        }
        purchasePanel.add(new JScrollPane(purchaseTable), BorderLayout.CENTER);
        tabbedPane.addTab("Purchase History", purchasePanel);

        JPanel servicePanel = new JPanel(new BorderLayout());
        String[] serviceCols = {"Service ID", "Vehicle ID", "Description", "Cost", "Date"};
        DefaultTableModel serviceModel = new DefaultTableModel(serviceCols, 0);
        JTable serviceTable = new JTable(serviceModel);
        try {
            for (var service : serviceDAO.getServicesByCustomer(customerId)) {
                serviceModel.addRow(new Object[]{service.getId(), service.getVehicleId(), 
                    service.getDescription(), service.getCost(), service.getServiceDate()});
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(dialog, "Error loading services: " + e.getMessage());
        }
        servicePanel.add(new JScrollPane(serviceTable), BorderLayout.CENTER);
        tabbedPane.addTab("Service History", servicePanel);

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
