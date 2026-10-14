import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;


public class UDP_DataString2 {
    public static void main(String[] args) {
        String serverAddress = "36.50.135.242";
        int serverPort = 2207;
        String qCode = "sMlwZzLc";
        String studentCode = "B23DCCN238";

        DatagramSocket socket = null;

        try{
            //Khoi tao socket
            socket = new DatagramSocket();
            InetAddress address = InetAddress.getByName(serverAddress);

            //Gui ma sinh vien
            String sendData1 = ";" + studentCode + ";" + qCode;
            byte[] sendBuffer1 = sendData1.getBytes();
            DatagramPacket sendPacket1 = new DatagramPacket(sendBuffer1, sendBuffer1.length, address, serverPort);
            socket.send(sendPacket1);

            //Nhan phan hoi
            byte[] receiveBuffer = new byte[2048];
            DatagramPacket receivePacket = new DatagramPacket(receiveBuffer,receiveBuffer.length);
            socket.receive(receivePacket);
            String receivePacketData = new String(receivePacket.getData(), 0, receivePacket.getLength()).trim();

            String[] parts = receivePacketData.split(";");
            String requestId = parts[0];

            int max = Integer.MIN_VALUE;
            int min = Integer.MAX_VALUE;
            if(parts.length > 1 && !parts[1].isEmpty()){
                String[] numStr = parts[1].split(",");
                for(String num : numStr){
                    int x = Integer.parseInt(num.trim());
                    if(x > max) max = x;
                    if(x < min) min = x;
                }
            }

            //Gui chuoi
            String sendData2 = requestId + ";" + max + "," + min;
            byte[] sendBuffer2 = sendData2.getBytes();
            DatagramPacket sendPacket2 = new DatagramPacket(sendBuffer2, sendBuffer2.length, address, serverPort);
            socket.send(sendPacket2);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        finally{
            if(socket != null && !socket.isClosed()){
                socket.close();
            }
        }

    }    
}
