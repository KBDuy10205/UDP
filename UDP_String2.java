import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UDP_String2 {
    public static void main(String[] args) {
        String serverAddress = "36.50.135.242";
        int serverPort = 2208;
        String studentCode = "B23DCCN238";
        String qCode = "2tOASmT3";

        DatagramSocket socket = null;
        try{
            socket = new DatagramSocket();
            InetAddress address = InetAddress.getByName(serverAddress);
            
            String sendData1 = ";" + studentCode + ";" + qCode;
            byte[] sendBuffer1 = sendData1.getBytes();
            DatagramPacket sendPacket1 = new DatagramPacket(sendBuffer1, sendBuffer1.length, address, serverPort);
            socket.send(sendPacket1);

            byte[] receiveBuffer = new byte[2048];
            DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
            socket.receive(receivePacket);
            String receiveData = new String(receivePacket.getData(), 0, receivePacket.getLength()).trim();

            String[] parts = receiveData.split(";");
            String requestId = parts[0];
            String str = parts[1];

            String[] words = str.split("\\s+");
            StringBuilder result = new StringBuilder();

            for(String s : words){
                String vh = s.substring(0,1).toUpperCase() + s.substring(1).toLowerCase();
                result.append(vh).append(" ");
            }

            String sendData2 = requestId + ";" + result.toString().trim();
            byte[] sendBuffer2 = sendData2.getBytes();
            DatagramPacket sendPacket2 = new DatagramPacket(sendBuffer2, sendBuffer2.length, address, serverPort);
            socket.send(sendPacket2);

            socket.close();
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
}
