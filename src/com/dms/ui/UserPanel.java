package com.dms.ui;

import com.dms.dao.UserDAO;
import com.dms.model.User;
import com.dms.util.Utils;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class UserPanel extends JPanel {
    private JTable userTable;
    private DefaultTableModel tableModel;
    private UserDAO userDAO;

    public UserPanel() {
        userDAO = new UserDAO();
        setLayout(new BorderLayout());

        JLabel title = new JLabel("User Management", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        add(title, BorderLayout.NORTH);

        String[] columns = {"ID", "Username", "Role", "Active"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        userTable = new JTable(tableModel);
        add(new JScrollPane(userTable), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Add User");
        JButton editButton = new JButton("Edit User");
        JButton changePasswordButton = new JButton("Change Password");
        JButton deactivateButton = new JButton("Deactivate User");
        JButton deleteButton = new JButton("Delete User");
        JButton refreshButton = new JButton("Refresh");

        addButton.addActionListener(e -> showAddDialog());
        editButton.addActionListener(e -> showEditDialog());
        changePasswordButton.addActionListener(e -> showChangePasswordDialog());
        deactivateButton.addActionListener(e -> deactivateUser());
        deleteButton.addActionListener(e -> deleteUser());
        refreshButton.addActionListener(e -> loadUsers());

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(changePasswordButton);
        buttonPanel.add(deactivateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);
        add(buttonPanel, BorderLayout.SOUTH);

        loadUsers();
    }

    private void loadUsers() {
        tableModel.setRowCount(0);
        try {
            for (User u : userDAO.getAllUsers()) {
                tableModel.addRow(new Object[]{
                    u.getId(), 
                    u.getUsername(), 
                    u.getRole(), 
                    u.isActive() ? "Yes" : "No"
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void showAddDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add User", true);
        dialog.setLayout(new GridLayout(5, 2, 10, 10));
        dialog.setSize(400, 250);

        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        JPasswordField confirmPasswordField = new JPasswordField();
        JComboBox<String> roleCombo = new JComboBox<>(new String[]{"ADMIN", "SALES", "SERVICE"});

        dialog.add(new JLabel("Username:"));
        dialog.add(usernameField);
        dialog.add(new JLabel("Password:"));
        dialog.add(passwordField);
        dialog.add(new JLabel("Confirm Password:"));
        dialog.add(confirmPasswordField);
        dialog.add(new JLabel("Role:"));
        dialog.add(roleCombo);

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> {
            try {
                String username = usernameField.getText().trim();
                String password = new String(passwordField.getPassword());
                String confirmPassword = new String(confirmPasswordField.getPassword());

                if (username.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Username and password are required");
                    return;
                }

                if (!password.equals(confirmPassword)) {
                    JOptionPane.showMessageDialog(dialog, "Passwords do not match");
                    return;
                }

                User user = new User();
                user.setUsername(username);
                user.setPassword(Utils.hashPassword(password));
                user.setRole((String) roleCombo.getSelectedItem());
                user.setActive(true);
                
                userDAO.addUser(user);
                loadUsers();
                dialog.dispose();
                JOptionPane.showMessageDialog(this, "User added successfully!");
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
        int row = userTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a user to edit");
            return;
        }

        int id = (int) tableModel.getValueAt(row, 0);
        String username = (String) tableModel.getValueAt(row, 1);
        String role = (String) tableModel.getValueAt(row, 2);
        String activeStr = (String) tableModel.getValueAt(row, 3);
        boolean active = activeStr.equals("Yes");

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Edit User", true);
        dialog.setLayout(new GridLayout(4, 2, 10, 10));
        dialog.setSize(400, 200);

        JTextField usernameField = new JTextField(username);
        JComboBox<String> roleCombo = new JComboBox<>(new String[]{"ADMIN", "SALES", "SERVICE"});
        roleCombo.setSelectedItem(role);
        JCheckBox activeCheckbox = new JCheckBox("Active", active);

        dialog.add(new JLabel("Username:"));
        dialog.add(usernameField);
        dialog.add(new JLabel("Role:"));
        dialog.add(roleCombo);
        dialog.add(new JLabel("Status:"));
        dialog.add(activeCheckbox);

        JButton saveButton = new JButton("Update");
        saveButton.addActionListener(e -> {
            try {
                // Get current password from database
                String currentPassword = "";
                for (User u : userDAO.getAllUsers()) {
                    if (u.getId() == id) {
                        currentPassword = u.getPassword();
                        break;
                    }
                }

                User user = new User();
                user.setId(id);
                user.setUsername(usernameField.getText().trim());
                user.setPassword(currentPassword); // Keep existing password
                user.setRole((String) roleCombo.getSelectedItem());
                user.setActive(activeCheckbox.isSelected());
                
                userDAO.updateUser(user);
                loadUsers();
                dialog.dispose();
                JOptionPane.showMessageDialog(this, "User updated successfully!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage());
            }
        });

        dialog.add(saveButton);
        dialog.add(new JButton("Cancel") {{ addActionListener(e -> dialog.dispose()); }});
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void showChangePasswordDialog() {
        int row = userTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a user to change password");
            return;
        }

        int id = (int) tableModel.getValueAt(row, 0);
        String username = (String) tableModel.getValueAt(row, 1);

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Change Password - " + username, true);
        dialog.setLayout(new GridLayout(4, 2, 10, 10));
        dialog.setSize(400, 200);

        JPasswordField newPasswordField = new JPasswordField();
        JPasswordField confirmPasswordField = new JPasswordField();

        dialog.add(new JLabel("New Password:"));
        dialog.add(newPasswordField);
        dialog.add(new JLabel("Confirm Password:"));
        dialog.add(confirmPasswordField);

        JButton saveButton = new JButton("Change Password");
        saveButton.addActionListener(e -> {
            try {
                String newPassword = new String(newPasswordField.getPassword());
                String confirmPassword = new String(confirmPasswordField.getPassword());

                if (newPassword.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Password cannot be empty");
                    return;
                }

                if (!newPassword.equals(confirmPassword)) {
                    JOptionPane.showMessageDialog(dialog, "Passwords do not match");
                    return;
                }

                // Get current user data
                User user = null;
                for (User u : userDAO.getAllUsers()) {
                    if (u.getId() == id) {
                        user = u;
                        break;
                    }
                }

                if (user != null) {
                    user.setPassword(Utils.hashPassword(newPassword));
                    userDAO.updateUser(user);
                    dialog.dispose();
                    JOptionPane.showMessageDialog(this, "Password changed successfully!");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage());
            }
        });

        dialog.add(saveButton);
        dialog.add(new JButton("Cancel") {{ addActionListener(e -> dialog.dispose()); }});
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void deactivateUser() {
        int row = userTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a user to deactivate");
            return;
        }

        int id = (int) tableModel.getValueAt(row, 0);
        String username = (String) tableModel.getValueAt(row, 1);

        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to deactivate user: " + username + "?", 
            "Confirm Deactivate", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                userDAO.deactivateUser(id);
                loadUsers();
                JOptionPane.showMessageDialog(this, "User deactivated successfully!");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            }
        }
    }

    private void deleteUser() {
        int row = userTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a user to delete");
            return;
        }

        int id = (int) tableModel.getValueAt(row, 0);
        String username = (String) tableModel.getValueAt(row, 1);

        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to permanently delete user: " + username + "?\nThis action cannot be undone!", 
            "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                userDAO.deleteUser(id);
                loadUsers();
                JOptionPane.showMessageDialog(this, "User deleted successfully!");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            }
        }
    }
}
