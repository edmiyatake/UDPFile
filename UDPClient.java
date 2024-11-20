import java.net.*;

public class UDPClient {
    public static void main(String[] args) {
        try (DatagramSocket clientSocket = new DatagramSocket()) {
            byte[] sendData;
            byte[] receiveData = new byte[1024];

            InetAddress serverAddress = InetAddress.getByName("localhost");

            boolean keepRunning = true;
            while (keepRunning) {
                // Step 1: Read command from user
                System.out.print("Enter command (index, get <filename>, exit): ");
                byte[] userInput = new byte[1024];
                int bytesRead = System.in.read(userInput);

                // Remove extra bytes from the input and send the command
                String command = new String(userInput, 0, bytesRead).trim();
                sendData = command.getBytes();
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddress, 9090);
                clientSocket.send(sendPacket);

                // Step 2: Receive response from server
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                clientSocket.receive(receivePacket);

                String response = new String(receivePacket.getData(), 0, receivePacket.getLength());
                System.out.println("From Server:\n" + response);

                if (command.equalsIgnoreCase("exit")) {
                    keepRunning = false;
                }
            }

        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }
}
