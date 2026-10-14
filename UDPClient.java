import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import UDP.Customer; // Import lớp Customer thuộc package UDP

public class UDPClient {
    public static void main(String[] args) {
        try {
            // Cấu hình thông tin kết nối UDP theo đề bài
            String serverIP = "36.50.135.242"; // Địa chỉ IP Server
            int serverPort = 2209;             // Cổng kết nối 2209
            String studentCode = "B23DCCN238"; // Thay bằng mã sinh viên của bạn
            String qCode = "6Jecyqpe";         // Thay bằng mã câu hỏi trên màn hình thi

            // Khởi tạo Socket UDP và lấy địa chỉ InetAddress của Server
            DatagramSocket socket = new DatagramSocket();
            InetAddress address = InetAddress.getByName(serverIP);

            // =========================================================================
            // Bước 1: Gửi thông điệp định dạng ";studentCode;qCode"
            // =========================================================================
            String msgA = ";" + studentCode + ";" + qCode; // Tạo chuỗi yêu cầu
            byte[] sendBufferA = msgA.getBytes(); // Chuyển chuỗi thành mảng byte

            // Tạo gói tin UDP gửi đến Server
            DatagramPacket sendPacketA = new DatagramPacket(sendBufferA, sendBufferA.length, address, serverPort);
            socket.send(sendPacketA); // Gửi gói tin đi

            // =========================================================================
            // Bước 2: Nhận gói tin từ Server (08 byte đầu chứa requestId, còn lại chứa Customer)
            // =========================================================================
            byte[] receiveBuffer = new byte[2048]; // Mảng đệm hứng dữ liệu phản hồi
            DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
            socket.receive(receivePacket); // Chờ và nhận gói tin từ server

            byte[] receivedData = receivePacket.getData(); // Lấy toàn bộ dữ liệu byte nhận được

            // Tách 08 byte đầu làm requestId
            byte[] requestIdBytes = new byte[8];
            System.arraycopy(receivedData, 0, requestIdBytes, 0, 8); // Coppy 8 byte đầu
            String requestId = new String(requestIdBytes); // Chuyển 8 byte thành chuỗi

            // Tách các byte còn lại chứa đối tượng Customer (Deserialization)
            int customerDataLength = receivePacket.getLength() - 8; // Đếm độ dài phần dữ liệu Customer
            byte[] customerBytes = new byte[customerDataLength];
            System.arraycopy(receivedData, 8, customerBytes, 0, customerDataLength); // Coppy phần còn lại

            // Giải mã mảng byte thành đối tượng Customer
            ByteArrayInputStream bais = new ByteArrayInputStream(customerBytes);
            ObjectInputStream ois = new ObjectInputStream(bais);
            Customer customer = (Customer) ois.readObject(); // Đọc đối tượng Customer

            // =========================================================================
            // Bước 3: Xử lý chuẩn hóa các thông tin của Customer theo yêu cầu
            // =========================================================================
            
            // --- a. Chuẩn hóa Tên (Ví dụ: "nguyen van hai duong" -> "DUONG, Nguyen Van Hai") ---
            String rawName = customer.getName().trim();
            String[] words = rawName.split("\\s+"); // Tách tên thành mảng các từ
            
            String lastName = words[words.length - 1].toUpperCase(); // Lấy tên cuối cùng và VIẾT HOA
            
            StringBuilder middleAndFirstName = new StringBuilder(); // Chuỗi chứa Họ và Tên đệm
            for (int i = 0; i < words.length - 1; i++) {
                if (!words[i].isEmpty()) {
                    // Viết hoa chữ cái đầu, các chữ sau viết thường
                    String word = words[i].substring(0, 1).toUpperCase() + words[i].substring(1).toLowerCase();
                    middleAndFirstName.append(word).append(" ");
                }
            }
            // Ghép lại thành định dạng: "TÊN_IN_HOA, Họ_Và_Đệm"
            String formattedName = lastName + ", " + middleAndFirstName.toString().trim();
            customer.setName(formattedName); // Cập nhật lại vào thuộc tính name

            // --- b. Chuyển Ngày sinh từ mm-dd-yyyy sang dd/mm/yyyy ---
            // Ví dụ: "10-11-2012" -> "11/10/2012"
            String rawDob = customer.getDayOfBirth().trim();
            String[] dobParts = rawDob.split("-"); // Tách ngày tháng năm phân cách bởi dấu '-'
            String month = dobParts[0]; // Tháng (mm)
            String day = dobParts[1];   // Ngày (dd)
            String year = dobParts[2];  // Năm (yyyy)
            
            String formattedDob = day + "/" + month + "/" + year; // Đổi thứ tự thành dd/mm/yyyy
            customer.setDayOfBirth(formattedDob); // Cập nhật lại vào thuộc tính dayOfBirth

            // --- c. Tạo userName từ các chữ cái in thường sinh tự động từ họ tên ---
            // Ví dụ: "nguyen van hai duong" -> "nvhduong"
            String mainNameLower = words[words.length - 1].toLowerCase(); // Tên chính viết thường
            StringBuilder initials = new StringBuilder(); // Các chữ cái đầu của Họ và Tên đệm
            for (int i = 0; i < words.length - 1; i++) {
                if (!words[i].isEmpty()) {
                    initials.append(words[i].substring(0, 1).toLowerCase()); // Lấy chữ đầu viết thường
                }
            }
            String generatedUserName = initials.toString() + mainNameLower; // Kết hợp chữ cái đầu + tên
            customer.setUserName(generatedUserName); // Cập nhật lại vào thuộc tính userName

            // =========================================================================
            // Bước 4: Đóng gói và gửi lại đối tượng Customer đã sửa đổi lên Server
            // =========================================================================
            // Mã hóa đối tượng Customer thành mảng byte
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(customer);
            oos.flush();
            byte[] updatedCustomerBytes = baos.toByteArray(); // Byte đối tượng Customer

            // Tạo mảng byte tổng hợp: 8 byte requestId + các byte của Customer
            byte[] finalSendBuffer = new byte[8 + updatedCustomerBytes.length];
            System.arraycopy(requestIdBytes, 0, finalSendBuffer, 0, 8); // Nối 8 byte requestId ở đầu
            System.arraycopy(updatedCustomerBytes, 0, finalSendBuffer, 8, updatedCustomerBytes.length); // Nối byte Customer phía sau

            // Gửi gói tin dữ liệu hoàn chỉnh lên Server
            DatagramPacket finalSendPacket = new DatagramPacket(finalSendBuffer, finalSendBuffer.length, address, serverPort);
            socket.send(finalSendPacket); // Thực hiện gửi gói tin

            // =========================================================================
            // Bước 5: Đóng socket và kết thúc
            // =========================================================================
            socket.close(); // Đóng socket UDP
            System.out.println("Đã xử lý và gửi dữ liệu thành công!");

        } catch (Exception e) {
            e.printStackTrace(); // In thông báo lỗi nếu có
        }
    }
}