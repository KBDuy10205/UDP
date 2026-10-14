import java.net.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
public class UDP_DataString {
    public static void main(String[] args) {
        String serverAddress = "36.50.135.242";
        int serverPort = 2207;

        String studentCode = "B23DCCN238";
        String qCode = "PMcpVo3I";

        DatagramSocket socket = null;

        try{
            //Khoi tao DatagramSocket de nhan/gui du lieu
            socket = new DatagramSocket();
            InetAddress address = InetAddress.getByName(serverAddress);

            //Gui ma sinh vien va ma cau hoi
            String sendData = ";" + studentCode + ";" + qCode;
            byte[] sendBuffer = sendData.getBytes();
            DatagramPacket sendPacket = new DatagramPacket(sendBuffer, sendBuffer.length, address, serverPort);
            socket.send(sendPacket);

            //Nhan thong diep tu server
            byte[] receiveBuffer = new byte[2048];
            DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
            socket.receive(receivePacket);

            String receiveData = new String(receivePacket.getData(), 0, receivePacket.getLength()).trim();

            //Tach thong diep
            String[] parts = receiveData.split(";");
            String requestId = parts[0];
            int n = Integer.parseInt(parts[1]);

            //Lay danh sach cac so
            Set<Integer> numbers = new HashSet<>();
            if(parts.length > 2 && !parts[2].isEmpty()){
                String[] numStr = parts[2].split(",");
                for(String Str: numStr){
                    numbers.add(Integer.parseInt(Str));
                }
            }

            //Tim gia tri thieu
            List<String> missingNumbers = new ArrayList<>();
            for(int i=1; i<=n; i++){
                if(!numbers.contains(i)){
                    missingNumbers.add(String.valueOf(i));
                }
            }

            //Noi gia tri thieu
            String missingNumbersStr = String.join(",", missingNumbers);

            //Gui chuoi
            String sendData2 = requestId + ";" + missingNumbersStr;
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
