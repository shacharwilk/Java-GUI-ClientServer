package HomeWork03;

import java.awt.GridLayout;
import java.io.*;
import java.net.*;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class GUIClient 
{
    private Socket socket;
    private PrintWriter writer;
    private BufferedReader reader;

    public static void main(String[] args) 
    {
        SwingUtilities.invokeLater(GUIClient::new);
    }

    public GUIClient()
    {
        connectToServer();
        createGUI();
    }

    private void connectToServer() 
    {
        try 
        {
            socket = new Socket("localhost", 9999);
            writer = new PrintWriter(socket.getOutputStream(), true);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        }
        catch (IOException e) 
        {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private void createGUI() 
    {
        JFrame frame = new JFrame("Client GUI");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);

        JPanel panel = new JPanel(new GridLayout(6, 2));

        JLabel businessNameLabel = new JLabel("Business Name:");
        JTextField businessNameField = new JTextField();

        JLabel businessIdLabel = new JLabel("Business ID:");
        JTextField businessIdField = new JTextField();

        JLabel itemLabel = new JLabel("Item:");
        String[] items = {"sun glasses", "belt", "scarf"};
        JComboBox<String> itemBox = new JComboBox<>(items);

        JLabel quantityLabel = new JLabel("Quantity:");
        JTextField quantityField = new JTextField();

        JButton sendButton = new JButton("Send");
        JButton disconnectButton = new JButton("Disconnect");
        disconnectButton.setEnabled(false);

        JLabel messageLabel = new JLabel("");

        panel.add(businessNameLabel);
        panel.add(businessNameField);
        panel.add(businessIdLabel);
        panel.add(businessIdField);
        panel.add(itemLabel);
        panel.add(itemBox);
        panel.add(quantityLabel);
        panel.add(quantityField);
        panel.add(sendButton);
        panel.add(disconnectButton);
        panel.add(messageLabel);

        frame.add(panel);
        frame.setVisible(true);

        sendButton.addActionListener(e -> 
        {
            String businessName = businessNameField.getText();
            String businessId = businessIdField.getText();
            String item = (String) itemBox.getSelectedItem();
            String quantity = quantityField.getText();

            if (businessName.isEmpty() || businessId.isEmpty() || quantity.isEmpty())
            {
                messageLabel.setText("Error: All fields must be filled.");
                return;
            }

            if (!businessId.matches("\\d{5}"))
            {
                messageLabel.setText("Error: Business ID must be exactly 5 digits.");
                return;
            }
            try 
            {
                int businessIdInt = Integer.parseInt(businessId);
                int quantityInt = Integer.parseInt(quantity);

                if (businessIdInt < 0 || quantityInt <= 0)
                {
                    messageLabel.setText("Error: Business ID and quantity must be positive.");
                    return;
                }

                String request = String.format("businessName=%s;businessId=%d;item=%s;quantity=%d",businessName, businessIdInt, item, quantityInt); 
                writer.println(request);

                int responseCode = Integer.parseInt(reader.readLine());
                switch (responseCode) 
                {
                    case 100:
                        messageLabel.setText("Sent successfully! Send more items or disconnect.");
                        disconnectButton.setEnabled(true);
                        break;
                    case 200:
                        messageLabel.setText("Error: Missing values.");
                        break;
                    case 201:
                        messageLabel.setText("Error: Business name mismatch.");
                        break;
                    case 202:
                        messageLabel.setText("Error: Invalid data.");
                        break;
                    default:
                        messageLabel.setText("Error: Unknown response.");
                }
            }
            catch (NumberFormatException ex) 
            {
                messageLabel.setText("Error: Business ID and quantity must be numeric.");
            }
            catch (Exception ex) 
            {
                messageLabel.setText("Error: Invalid input.");
            }
        });

        disconnectButton.addActionListener(e -> 
        {
            try 
            {
                socket.close();
                frame.dispose();
            }
            catch (IOException ex) 
            {
                ex.printStackTrace();
            }
        });
    }
}

