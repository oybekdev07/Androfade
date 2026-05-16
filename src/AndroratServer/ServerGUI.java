import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ServerGUI extends JFrame {
    
    private JTextArea logArea;
    private JList<String> clientList;
    private DefaultListModel<String> clientModel;
    private JButton sendSMSBtn, getGPSBtn, makeCallBtn;

    public ServerGUI() {
        setTitle("AndroFade Server");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout());

        // Top panel - Clients
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createTitledBorder("Connected Clients"));
        
        clientModel = new DefaultListModel<>();
        clientList = new JList<>(clientModel);
        topPanel.add(new JScrollPane(clientList), BorderLayout.CENTER);
        mainPanel.add(topPanel, BorderLayout.NORTH);

        // Center panel - Log
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBorder(BorderFactory.createTitledBorder("Server Log"));
        
        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        centerPanel.add(new JScrollPane(logArea), BorderLayout.CENTER);
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // Bottom panel - Buttons
        JPanel bottomPanel = new JPanel(new FlowLayout());
        
        sendSMSBtn = new JButton("Send SMS");
        getGPSBtn = new JButton("Get GPS");
        makeCallBtn = new JButton("Make Call");
        
        bottomPanel.add(sendSMSBtn);
        bottomPanel.add(getGPSBtn);
        bottomPanel.add(makeCallBtn);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);
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
                clientModel.addElement(handler.getDeviceInfo() + " (" + handler.getIMEI() + ")");
            }
        }
    }

    public void show() {
        setVisible(true);
    }
}
