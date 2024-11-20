import java.io.*;
import java.net.*;

public class ClientThread extends Thread {
    private final DatagramSocket socket;
    private final DatagramPacket initialPacket;
    private final File directory;

    public ClientThread(DatagramSocket socket, DatagramPacket initialPacket, File directory) {
        this.socket = socket;
        this.initialPacket = initialPacket;
        this.directory = directory;
    }

    @Override
    public void run() {
        InetAddress clientIpAddress = initialPacket.getAddress();
        int clientPort = initialPacket.getPort();
        byte[] sendData;

        try {
            // Send initial response to client
            String welcomeMessage = "Connected to server. Enter commands (index, get <filename>, exit):";
            sendData = welcomeMessage.getBytes();
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, clientIpAddress, clientPort);
            socket.send(sendPacket);

            while (true) {
                // Buffer for incoming client commands
                byte[] receiveData = new byte[1024];
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                socket.receive(receivePacket);

                String command = new String(receivePacket.getData(), 0, receivePacket.getLength()).trim();
                System.out.println("Command from " + clientIpAddress + ":" + clientPort + " -> " + command);

                if (command.equalsIgnoreCase("index")) {
                    // Generate file list
                    StringBuilder fileList = new StringBuilder();
                    for (File file : directory.listFiles()) {
                        if (file.isFile()) {
                            fileList.append(file.getName()).append("\n");
                        }
                    }
                    sendData = fileList.toString().getBytes();

                } else if (command.startsWith("get ")) {
                    // Retrieve the file
                    String fileName = command.substring(4).trim();
                    File file = new File(directory, fileName);

                    if (file.exists() && file.isFile()) {
                        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                            StringBuilder fileContents = new StringBuilder();
                            String line;
                            while ((line = reader.readLine()) != null) {
                                fileContents.append(line).append("\n");
                            }
                            sendData = fileContents.toString().getBytes();
                        } catch (IOException e) {
                            sendData = ("Error reading file: " + e.getMessage()).getBytes();
                        }
                    } else {
                        sendData = "File not found.".getBytes();
                    }

                } else if (command.equalsIgnoreCase("exit")) {
                    sendData = "Goodbye!".getBytes();
                    sendPacket = new DatagramPacket(sendData, sendData.length, clientIpAddress, clientPort);
                    socket.send(sendPacket);
                    System.out.println("Client " + clientIpAddress + ":" + clientPort + " disconnected.");
                    break;

                } else {
                    sendData = "Unknown command. Use 'index', 'get <filename>', or 'exit'.".getBytes();
                }

                // Send response to client
                sendPacket = new DatagramPacket(sendData, sendData.length, clientIpAddress, clientPort);
                socket.send(sendPacket);
            }
        } catch (IOException e) {
            System.out.println("Error handling client " + clientIpAddress + ":" + clientPort + " -> " + e.getMessage());
        }
    }
}
