package HomeWork03;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.*;

public class CentralServer 
{
    private final int port = 9999;
    private final Map<Integer, Client> clients = new HashMap<>();
    private final ReentrantLock lock = new ReentrantLock();

    public static void main(String[] args) 
    {
        new CentralServer().startServer();
    }

    public void startServer() 
    {
        ExecutorService executor = Executors.newCachedThreadPool();
        try (ServerSocket serverSocket = new ServerSocket(port)) 
        {
            System.out.println("Server is listening on port " + port);
            while (true) 
            {
                Socket socket = serverSocket.accept();
                executor.submit(() -> handleClient(socket));
            }
        }
        catch (IOException e) 
        {
            e.printStackTrace();
        }
        finally 
        {
            executor.shutdown();
        }
    }

    private void handleClient(Socket socket) 
    {
        try (
            InputStream input = socket.getInputStream();
            OutputStream output = socket.getOutputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            PrintWriter writer = new PrintWriter(output, true)
        )
        {
            String request;
            while ((request = reader.readLine()) != null)
            {
                int responseCode = processRequest(request);
                writer.println(responseCode);
            }
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        }
    }

    private int processRequest(String request) 
    {
        try 
        {
            String[] parts = request.split(";");
            String businessName = parts[0].split("=")[1];
            int businessId = Integer.parseInt(parts[1].split("=")[1]);
            String item = parts[2].split("=")[1];
            int quantity = Integer.parseInt(parts[3].split("=")[1]);

            if (businessName.isEmpty() || businessId <= 0 || item.isEmpty() || quantity <= 0)
            {
                return 200;
            }

            lock.lock();
            try 
            {
                if (clients.containsKey(businessId)) 
                {
                    Client client = clients.get(businessId);
                    if (!client.getBusinessName().equals(businessName)) 
                    {
                        return 201; 
                    }
                    client.updateItem(item, quantity);
                } 
                else 
                {
                    Client newClient = new Client(businessName, businessId);
                    newClient.updateItem(item, quantity);
                    clients.put(businessId, newClient);
                }
            } 
            finally 
            {
                lock.unlock();
            }
            return 100; 
        } 
        catch (Exception e) 
        {
            return 202; 
        }
    }
}

class Client 
{
    private final String businessName;
    private final int businessId;
    private final Map<String, Integer> items;

    public Client(String businessName, int businessId)
    {
        this.businessName = businessName;
        this.businessId = businessId;
        this.items = new HashMap<>();
        items.put("sun glasses", 0);
        items.put("belt", 0);
        items.put("scarf", 0);
    }

    public String getBusinessName() 
    {
        return businessName;
    }

    public void updateItem(String item, int quantity) 
    {
        items.put(item, items.getOrDefault(item, 0) + quantity);
    }

    @Override
    public String toString() 
    {
        return "Client{" + "businessName='" + businessName + '\'' + ", businessId=" + businessId + ", items=" + items + '}';    
    }
}
