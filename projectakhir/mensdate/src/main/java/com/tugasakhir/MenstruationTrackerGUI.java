package com.tugasakhir;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

public class MenstruationTrackerGUI extends JFrame {
    private JTextField tanggalField;
    private JButton simpanButton;
    private JTextArea displayArea;
    private int userId;  // User ID for the logged-in user

    public MenstruationTrackerGUI(int userId) {
        this.userId = userId;  // Store the user ID

        setTitle("Menstruation Tracker");
        setSize(400, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());
        add(panel);

        JLabel labelTanggal = new JLabel("Tanggal Menstruasi (YYYY-MM-DD):");
        panel.add(labelTanggal);

        tanggalField = new JTextField(10);
        panel.add(tanggalField);

        simpanButton = new JButton("Simpan Tanggal");
        panel.add(simpanButton);

        displayArea = new JTextArea(10, 30);
        displayArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(displayArea);
        panel.add(scrollPane);

        simpanButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                simpanTanggalMenstruasi();
                fetchAndDisplayRecords();
            }
        });

        fetchAndDisplayRecords();  // Fetch and display records on startup
    }

    private void simpanTanggalMenstruasi() {
        String tanggalStr = tanggalField.getText();
        try {
            LocalDate tanggal = LocalDate.parse(tanggalStr);
            DatabaseManager.simpanKeDatabase(tanggal, userId);
            JOptionPane.showMessageDialog(this, "Tanggal menstruasi berhasil disimpan: " + tanggal, "Informasi", JOptionPane.INFORMATION_MESSAGE);
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Format tanggal tidak valid. Gunakan format YYYY-MM-DD.", "Kesalahan", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Terjadi kesalahan saat menyimpan ke database: " + ex.getMessage(), "Kesalahan", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void fetchAndDisplayRecords() {
        try {
            List<MenstruationRecord> records = DatabaseManager.getMenstruationRecords(userId);
            displayArea.setText("");
            for (MenstruationRecord record : records) {
                displayArea.append("ID: " + record.getUserId() + ", Name: " + record.getUsername() + ", Date: " + record.getMensDate() + "\n");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Terjadi kesalahan saat mengambil data: " + ex.getMessage(), "Kesalahan", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MenstruationTrackerGUI(1).setVisible(true));  // For testing purposes only
    }
}
