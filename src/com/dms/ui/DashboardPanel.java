package com.dms.ui;

import com.dms.database.Database;
import com.dms.util.Utils;
import java.awt.*;
import javax.swing.*;

public class DashboardPanel extends JPanel {
    
    public DashboardPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(248, 249, 250));

        // Title
        JPanel titlePanel = new JPanel(new BorderLayout());
        //JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titlePanel.setBackground(new Color(248, 249, 250));
        JLabel titleLabel = new JLabel("Dashboard", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titlePanel.add(titleLabel);
        add(titlePanel, BorderLayout.NORTH);

        // Stats cards
        JPanel statsPanel = new JPanel(new GridLayout(2, 2, 20, 20));
        statsPanel.setBackground(new Color(248, 249, 250));
        statsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        int vehicleCount = Database.getAllVehicles().size();
        int customerCount = Database.getAllCustomers().size();
        int salesCount = Database.getAllSales().size();
        double totalRevenue = Database.getTotalRevenue();
        statsPanel.add(createStatCard("Total Vehicles", String.valueOf(vehicleCount), new Color(0, 123, 255)));
        statsPanel.add(createStatCard("Total Customers", String.valueOf(customerCount), new Color(40, 167, 69)));
        statsPanel.add(createStatCard("Total Sales", String.valueOf(salesCount), new Color(255, 193, 7)));
        statsPanel.add(createStatCard("Total Revenue", String.valueOf(totalRevenue), new Color(220, 53, 69)));

        add(statsPanel, BorderLayout.CENTER);

        // Welcome message
        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(new Color(248, 249, 250));
        JLabel welcomeLabel = new JLabel("Welcome, " + Utils.currentUser.getUsername() + "!");
        welcomeLabel.setFont(new Font("Arial", Font.ITALIC, 14));
        welcomeLabel.setForeground(Color.GRAY);
        bottomPanel.add(welcomeLabel);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private JPanel createStatCard(String title, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(222, 226, 230), 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        titleLabel.setForeground(Color.GRAY);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 32));
        valueLabel.setForeground(color);

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);

        return card;
    }
}
