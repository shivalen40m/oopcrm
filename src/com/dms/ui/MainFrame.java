package com.dms.ui;

import com.dms.util.Utils;
import java.awt.*;
import javax.swing.*;

public class MainFrame extends JFrame {
    private JPanel contentPanel;

    public MainFrame() {
        setTitle("DMS - Dealership Management System");
        setSize(1200, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Main layout
        setLayout(new BorderLayout());

        // Menu bar
        setJMenuBar(createMenuBar());

        // Top bar
        JPanel topBar = createTopBar();
        add(topBar, BorderLayout.NORTH);

        // Sidebar
        JPanel sidebar = createSidebar();
        add(sidebar, BorderLayout.WEST);

        // Content area
        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(Color.WHITE);
        add(contentPanel, BorderLayout.CENTER);

        // Show dashboard by default
        showPanel(new DashboardPanel());
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        String role = Utils.currentUser.getRole();

        // File Menu
        JMenu fileMenu = new JMenu("File");
        JMenuItem refreshItem = new JMenuItem("Refresh");
        refreshItem.addActionListener(e -> showPanel(new DashboardPanel()));
        fileMenu.add(refreshItem);
        fileMenu.addSeparator();
        JMenuItem logoutItem = new JMenuItem("Logout");
        logoutItem.addActionListener(e -> logout());
        fileMenu.add(logoutItem);
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> System.exit(0));
        fileMenu.add(exitItem);
        menuBar.add(fileMenu);

        // View Menu
        JMenu viewMenu = new JMenu("View");
        JMenuItem dashboardItem = new JMenuItem("Dashboard");
        dashboardItem.addActionListener(e -> handleMenuClick("Dashboard"));
        viewMenu.add(dashboardItem);
        JMenuItem vehiclesItem = new JMenuItem("Vehicles");
        vehiclesItem.addActionListener(e -> handleMenuClick("Vehicles"));
        viewMenu.add(vehiclesItem);
        JMenuItem customersItem = new JMenuItem("Customers");
        customersItem.addActionListener(e -> handleMenuClick("Customers"));
        viewMenu.add(customersItem);
        
        if (role.equals("ADMIN") || role.equals("SALES")) {
            JMenuItem salesItem = new JMenuItem("Sales");
            salesItem.addActionListener(e -> handleMenuClick("Sales"));
            viewMenu.add(salesItem);
        }
        
        if (role.equals("ADMIN") || role.equals("SERVICE")) {
            JMenuItem serviceItem = new JMenuItem("Service");
            serviceItem.addActionListener(e -> handleMenuClick("Service"));
            viewMenu.add(serviceItem);
        }
        
        if (role.equals("ADMIN") || role.equals("SALES")) {
            JMenuItem reportsItem = new JMenuItem("Reports");
            reportsItem.addActionListener(e -> handleMenuClick("Reports"));
            viewMenu.add(reportsItem);
        }
        
        if (role.equals("ADMIN")) {
            viewMenu.addSeparator();
            JMenuItem usersItem = new JMenuItem("User Management");
            usersItem.addActionListener(e -> handleMenuClick("Users"));
            viewMenu.add(usersItem);
        }
        menuBar.add(viewMenu);

        // Quick Actions Menu
        JMenu actionsMenu = new JMenu("Quick Actions");
        
        JMenuItem newCustomerItem = new JMenuItem("New Customer");
        newCustomerItem.setAccelerator(KeyStroke.getKeyStroke("ctrl N"));
        newCustomerItem.addActionListener(e -> showQuickAddCustomer());
        actionsMenu.add(newCustomerItem);
        
        JMenuItem newVehicleItem = new JMenuItem("New Vehicle");
        newVehicleItem.setAccelerator(KeyStroke.getKeyStroke("alt V"));
        newVehicleItem.addActionListener(e -> showQuickAddVehicle());
        actionsMenu.add(newVehicleItem);
        
        if (role.equals("ADMIN") || role.equals("SALES")) {
            JMenuItem newSaleItem = new JMenuItem("New Sale");
            newSaleItem.setAccelerator(KeyStroke.getKeyStroke("alt S"));
            newSaleItem.addActionListener(e -> showQuickNewSale());
            actionsMenu.add(newSaleItem);
        }
        
        if (role.equals("ADMIN") || role.equals("SERVICE")) {
            JMenuItem newServiceItem = new JMenuItem("New Service");
            newServiceItem.setAccelerator(KeyStroke.getKeyStroke("ctrl R"));
            newServiceItem.addActionListener(e -> showQuickNewService());
            actionsMenu.add(newServiceItem);
        }
        
        menuBar.add(actionsMenu);

        // Help Menu
        JMenu helpMenu = new JMenu("Help");
        JMenuItem aboutItem = new JMenuItem("About");
        aboutItem.addActionListener(e -> JOptionPane.showMessageDialog(this,
            "Dealership Management System v1.0\n\nRole: " + Utils.currentUser.getRole() + "\nUser: " + Utils.currentUser.getUsername(),
            "About DMS", JOptionPane.INFORMATION_MESSAGE));
        helpMenu.add(aboutItem);
        menuBar.add(helpMenu);

        return menuBar;
    }

    private JPanel createTopBar() {
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(new Color(52, 58, 64));
        topBar.setPreferredSize(new Dimension(0, 50));

        JLabel titleLabel = new JLabel("  Dealership Management System");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        topBar.add(titleLabel, BorderLayout.WEST);

        JLabel userLabel = new JLabel(Utils.currentUser.getUsername() + " (" + Utils.currentUser.getRole() + ")  ");
        userLabel.setForeground(Color.WHITE);
        topBar.add(userLabel, BorderLayout.EAST);

        return topBar;
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(new Color(33, 37, 41));
        sidebar.setPreferredSize(new Dimension(200, 0));

        String role = Utils.currentUser.getRole();

        addMenuButton(sidebar, "Dashboard", true);
        addMenuButton(sidebar, "Vehicles", true);
        addMenuButton(sidebar, "Customers", true);
        
        if (role.equals("ADMIN") || role.equals("SALES")) {
            addMenuButton(sidebar, "Sales", true);
        }
        
        if (role.equals("ADMIN") || role.equals("SERVICE")) {
            addMenuButton(sidebar, "Service", true);
        }
        /* 
        if (role.equals("ADMIN") || role.equals("SALES")) {
            addMenuButton(sidebar, "Reports", true);
        } */
        
        if (role.equals("ADMIN")) {
            addMenuButton(sidebar, "Users", true);
        }

        sidebar.add(Box.createVerticalGlue());

        JButton logoutButton = createStyledButton("Logout", new Color(220, 53, 69));
        logoutButton.addActionListener(e -> logout());
        sidebar.add(logoutButton);
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));

        return sidebar;
    }

    private void addMenuButton(JPanel sidebar, String text, boolean enabled) {
        JButton button = createStyledButton(text, new Color(52, 58, 64));
        button.setEnabled(enabled);
        button.addActionListener(e -> handleMenuClick(text));
        sidebar.add(button);
        sidebar.add(Box.createRigidArea(new Dimension(0, 5)));
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setMaximumSize(new Dimension(200, 50));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setBackground(bgColor);
        button.setForeground(Color.black);
        button.setFocusPainted(true);
        button.setBorderPainted(true);
        button.setRolloverEnabled(rootPaneCheckingEnabled);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setSelected(false);
        button.setFont(new Font("Arial", Font.PLAIN, 14));
        return button;
    }

    private void handleMenuClick(String menu) {
        switch (menu) {
            case "Dashboard":
                showPanel(new DashboardPanel());
                break;
            case "Vehicles":
                showPanel(new VehiclePanel());
                break;
            case "Customers":
                showPanel(new CustomerPanel());
                break;
            case "Sales":
                showPanel(new SalesPanel());
                break;
            case "Service":
                showPanel(new ServicePanel());
                break;
            case "Reports":
                showPanel(new JLabel("Reports Panel - Coming Soon", SwingConstants.CENTER));
                break;
            case "Users":
                showPanel(new UserPanel());
                break;
        }
    }

    private void showPanel(JComponent panel) {
        contentPanel.removeAll();
        contentPanel.add(panel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to logout?", "Logout", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            Utils.currentUser = null;
            dispose();
            new LoginFrame().setVisible(true);
        }
    }

    private void showQuickAddCustomer() {
        handleMenuClick("Customers");
        SwingUtilities.invokeLater(() -> {
            if (contentPanel.getComponentCount() > 0 && contentPanel.getComponent(0) instanceof CustomerPanel) {
                CustomerPanel panel = (CustomerPanel) contentPanel.getComponent(0);
                panel.openAddDialog();
            }
        });
    }

    private void showQuickAddVehicle() {
        handleMenuClick("Vehicles");
        SwingUtilities.invokeLater(() -> {
            if (contentPanel.getComponentCount() > 0 && contentPanel.getComponent(0) instanceof VehiclePanel) {
                VehiclePanel panel = (VehiclePanel) contentPanel.getComponent(0);
                panel.openAddDialog();
            }
        });
    }

    private void showQuickNewSale() {
        handleMenuClick("Sales");
        SwingUtilities.invokeLater(() -> {
            if (contentPanel.getComponentCount() > 0 && contentPanel.getComponent(0) instanceof SalesPanel) {
                SalesPanel panel = (SalesPanel) contentPanel.getComponent(0);
                panel.openNewSaleDialog();
            }
        });
    }

    private void showQuickNewService() {
        handleMenuClick("Service");
        SwingUtilities.invokeLater(() -> {
            if (contentPanel.getComponentCount() > 0 && contentPanel.getComponent(0) instanceof ServicePanel) {
                ServicePanel panel = (ServicePanel) contentPanel.getComponent(0);
                panel.newAddService();
            }
        });
    }
}
