package UDP; // Bắt buộc phải thuộc package UDP theo đề bài

import java.io.Serializable; // Implement Serializable để có thể gửi nhận đối tượng qua Stream

public class Customer implements Serializable {
    // Trường dữ liệu bắt buộc từ đề bài
    private static final long serialVersionUID = 20151107L;

    // Các thuộc tính của đối tượng Customer
    private String id;
    private String code;
    private String name;
    private String dayOfBirth;
    private String userName;

    // Hàm khởi tạo đầy đủ các thuộc tính
    public Customer(String id, String code, String name, String dayOfBirth, String userName) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.dayOfBirth = dayOfBirth;
        this.userName = userName;
    }

    // Các hàm Getter và Setter
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDayOfBirth() {
        return dayOfBirth;
    }

    public void setDayOfBirth(String dayOfBirth) {
        this.dayOfBirth = dayOfBirth;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}