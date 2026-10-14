import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import UDP.Student;

public class UDP_Object1 {
    public static void main(String[] args){
        try{
            String serverIP = "36.50.135.242"; 
            int serverPort = 2209;           
            String studentCode = "B23DCCN238"; 
            String qCode = "HNwyDEYu";

            DatagramSocket socket = new DatagramSocket();
            InetAddress address = InetAddress.getByName(serverIP);

            String sendData = ";" + studentCode + ";" + qCode;
            byte[] sendBuffer = sendData.getBytes();
            DatagramPacket sendPacket = new DatagramPacket(sendBuffer, sendBuffer.length, address, serverPort);
            socket.send(sendPacket);

            byte[] receiveBuffer = new byte[2048];
            DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
            socket.receive(receivePacket);

            byte[] receiveData = receivePacket.getData();

            // Tách 08 byte đầu làm requestId
            byte[] requestIdBytes = new byte[8];
            System.arraycopy(receiveData, 0, requestIdBytes, 0, 8); // Tách 8 byte đầu
            String requestId = new String(requestIdBytes); // Chuyển 8 byte thành chuỗi requestId

            // Tách các byte còn lại chứa đối tượng Student bị băm (deserialization)
            int studentDataLength = receivePacket.getLength() - 8; // Tính độ dài phần byte đối tượng
            byte[] studentBytes = new byte[studentDataLength];
            System.arraycopy(receiveData, 8, studentBytes, 0, studentDataLength); // Tách phần dữ liệu Student

            // Giải mã byte thành đối tượng Student sử dụng ObjectInputStream
            ByteArrayInputStream bais = new ByteArrayInputStream(studentBytes);
            ObjectInputStream ois = new ObjectInputStream(bais);
            Student student = (Student) ois.readObject(); // Nhận đối tượng Student từ mảng byte

            // =========================================================================
            // Câu c: Chuẩn hóa Tên và Tạo Email ptit.edu.vn
            // =========================================================================
            String rawName = student.getName(); // Lấy tên nguyên bản thu được từ server

            // --- 1. Chuẩn hóa tên (Viết hoa chữ cái đầu, các chữ còn lại viết thường) ---
            String[] words = rawName.trim().split("\\s+"); // Tách thành các từ phân cách bởi khoảng trắng
            StringBuilder normalizedName = new StringBuilder();

            for (String word : words) {
                if (!word.isEmpty()) {
                    // Chữ cái đầu hoa, các chữ sau thường
                    String capWord = word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase();
                    normalizedName.append(capWord).append(" "); // Nối từ kèm khoảng trắng
                }
            }
            String finalName = normalizedName.toString().trim(); // Loại bỏ khoảng trắng thừa ở cuối
            student.setName(finalName); // Gán tên đã chuẩn hóa lại cho thuộc tính name

            // --- 2. Tạo email theo công thức: tên + chữ cái đầu họ & tên đệm + @ptit.edu.vn ---
            // Ví dụ: nguyen van tuan nam -> namnvt@ptit.edu.vn
            String mainName = words[words.length - 1].toLowerCase(); // Tên (từ cuối cùng)
            StringBuilder initials = new StringBuilder(); // Lưu ký tự đầu của họ và tên đệm

            for (int i = 0; i < words.length - 1; i++) {
                if (!words[i].isEmpty()) {
                    initials.append(words[i].substring(0, 1).toLowerCase()); // Lấy chữ cái đầu viết thường
                }
            }

            // Nối thành chuỗi email hoàn chỉnh
            String email = mainName + initials.toString() + "@ptit.edu.vn";
            student.setEmail(email); // Gán giá trị vào thuộc tính email của đối tượng Student

            // --- 3. Đóng gói lại dữ liệu gửi lên Server (8 byte requestId + byte đối tượng Student) ---
            // Mã hóa (Serialize) đối tượng Student vừa cập nhật thành mảng byte
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(student);
            oos.flush();
            byte[] updatedStudentBytes = baos.toByteArray(); // Mảng byte đại diện cho đối tượng Student

            // Mảng byte tổng kết quả gửi đi
            byte[] finalSendBuffer = new byte[8 + updatedStudentBytes.length];
            
            // Sao chép 8 byte requestId vào đầu mảng
            System.arraycopy(requestIdBytes, 0, finalSendBuffer, 0, 8);
            // Sao chép dữ liệu byte của đối tượng Student nối tiếp phía sau
            System.arraycopy(updatedStudentBytes, 0, finalSendBuffer, 8, updatedStudentBytes.length);

            // Gửi gói tin dữ liệu hoàn chỉnh lên Server
            DatagramPacket finalSendPacket = new DatagramPacket(finalSendBuffer, finalSendBuffer.length, address, serverPort);
            socket.send(finalSendPacket); // Thực hiện gửi gói tin

            // =========================================================================
            // Câu d: Đóng socket và kết thúc chương trình
            // =========================================================================
            socket.close(); // Đóng socket UDP giải phóng tài nguyên
            System.out.println("Đã gửi dữ liệu thành công và kết thúc chương trình.");
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
}
