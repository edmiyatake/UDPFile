import java.net.*;

public class UDPClient {
    public static void main(String[] args) {
        try (DatagramSocket clientSocket = new DatagramSocket(0);){
            
            byte[] sendData = new byte[1024];
            byte[] receiveData = new byte[1024];
            // max amount of data sent can be 65508

            clientSocket.setSoTimeout(10000);
            String stringSendData = "Hello Server!";
            sendData = stringSendData.getBytes();
            InetAddress serverAddress = InetAddress.getByName("localhost");
            DatagramPacket sendPacket = new DatagramPacket(sendData,sendData.length, serverAddress, 9090);
            clientSocket.send(sendPacket);
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            clientSocket.receive(receivePacket);
            receiveData = receivePacket.getData();
            String stringReceiveData = new String(receiveData);
            System.out.println("From Server: " + stringReceiveData);
            clientSocket.close();
        } 
        catch (Exception e) {
            System.out.println(e.toString());    
        }
    }
}