import java.io.*;
import java.net.*;

public class UDPServer {

    private static final int PORT = 9090;

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.out.println("Invalid command: Please re-enter in this format:");
            System.out.println("java UDPServer <directory>");
            System.exit(1);
        }

        File directory = new File(args[0]);
        if (!directory.isDirectory()) {
            System.err.println("Error: Directory does not exist");
            System.exit(1);
        }

        try (DatagramSocket socket = new DatagramSocket(PORT)) {
            System.out.println("Connected to port: " + PORT);
            System.out.println("Starting server...");

            byte[] receiveData = new byte[1024];
            byte[] sendData;

            while (true) {
                // Step 1: Wait for client command
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                socket.receive(receivePacket);

                InetAddress clientIpAddress = receivePacket.getAddress();
                int clientPort = receivePacket.getPort();

                String command = new String(receivePacket.getData(), 0, receivePacket.getLength()).trim();
                System.out.println("Command received: " + command);

                // logic for index
                if (command.equalsIgnoreCase("index")) {
                    // Generate the file list
                    StringBuilder fileList = new StringBuilder();
                    for (File file : directory.listFiles()) {
                        if (file.isFile()) {
                            fileList.append(file.getName()).append("\n");
                        }
                    }
                    sendData = fileList.toString().getBytes();
                } 
                // logic for get
                else if (command.startsWith("get ")) {
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
                        socket.close();
                    }
                }
                // logic for exit 
                else if (command.equalsIgnoreCase("exit")) {
                    // Terminate session
                    sendData = "Goodbye!".getBytes();
                    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, clientIpAddress, clientPort);
                    socket.send(sendPacket);
                    System.out.println("Client disconnected.");
                    socket.close();
                } else {
                    // Respond with an error message for unknown commands
                    String errorMessage = "Unknown command. Use 'index', 'get <filename>', or 'exit'.";
                    sendData = errorMessage.getBytes();
                }

                // Send response to client
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, clientIpAddress, clientPort);
                socket.send(sendPacket);
            }
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }
}
