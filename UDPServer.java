import java.io.File;
import java.net.*;

public class UDPServer {
    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.out.println("Invalid command: Please re-enter in this format:");
            System.out.println("java UDPFileServer <directory>");
            System.exit(1);
        }

        File directory = new File(args[0]);
        if (!directory.isDirectory()) {
            System.err.println("error: directory does not exist");
            System.exit(1);
        }

        try {
            DatagramSocket socket = new DatagramSocket(9090);
            byte[] receiveData = new byte[1024];
            byte[] sendData = new byte[1024];
            while (true) { 
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                socket.receive(receivePacket);

                String sentence = new String(receivePacket.getData());
                System.out.println("Received: " + sentence); // just making sure data is received

                String stringData = "Hello client"; // message that is getting sent
                sendData = stringData.getBytes();

                InetAddress clientIpAddress = receivePacket.getAddress();
                int clientPort = receivePacket.getPort();
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, clientIpAddress, clientPort);
                socket.send(sendPacket);
                socket.close();
            }
        } 
        catch (Exception e) {
            System.out.println(e.toString());
        }
    }
}