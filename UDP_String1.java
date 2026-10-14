import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UDP_String1 {
    public static void main(String[] args) {
        DatagramSocket socket = null;
        try{
            //Khoi tao socket
            socket = new DatagramSocket();
            InetAddress address = InetAddress.getByName("36.50.135.242");

            //Gui du lieu toi server
            String sendData1 = ";B23DCCN238;SZrQO7MR";
            byte[] sendBuffer1 = sendData1.getBytes();
            DatagramPacket sendPacket1 = new DatagramPacket(sendBuffer1, sendBuffer1.length, address, 2208);
            socket.send(sendPacket1);

            //Nhan du lieu tu server
            byte[] receiveBuffer = new byte[2048];
            DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
            socket.receive(receivePacket);
            String receiveData = new String(receivePacket.getData(), 0, receivePacket.getLength()).trim();

            //Phan tich du lieu
            String[] parts = receiveData.split(";");
            String requestId = parts[0];
            String data = parts[1];

            Map<Character, Integer> mp = new HashMap<>();
            int maxFreq = 0;
            for(char c : data.toCharArray()){
                int cnt = mp.getOrDefault(c, 0) + 1;
                mp.put(c,cnt);

                if(cnt > maxFreq){
                    maxFreq = cnt;
                }
            } 

            char mostFreq = ' ';
            for(int i = 0; i < data.length(); i++){
                char c = data.charAt(i);
                if(mp.get(c) == maxFreq){
                    mostFreq = c;
                    break;
                }
            }

            List<Integer> positions = new ArrayList<>();
            for(int i = 0; i < data.length(); i++){
                if(data.charAt(i) == mostFreq){
                    positions.add(i+1);
                }
            }

            StringBuilder posStr = new StringBuilder();
            for(int pos : positions){
                posStr.append(pos).append(",");
            }

            String sendData2 = requestId + ";" + mostFreq + ":" + posStr.toString();

            byte[] sendBuffer2 = sendData2.getBytes();
            DatagramPacket sendPacket2 = new DatagramPacket(sendBuffer2, sendBuffer2.length, address, 2208);
            socket.send(sendPacket2);
            socket.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }    
}
