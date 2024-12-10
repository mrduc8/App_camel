package com.dnd.camel.dbconnect;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FirebaseDBConnection {
    private static FirebaseDatabase database;
    private static DatabaseReference databaseReference;
    private static FirebaseDBConnection instance;


    public static final String MESSAGE = "Message";
    public static final String NOTIFICATION = "Notification";
    public static final String FLAIR = "Flair";
    public static final String COMMENT = "Comment";
    public static final String POST = "Post";
    public static final String DB_URL = "https://camel-2a7f2-default-rtdb.asia-southeast1.firebasedatabase.app/";

    // singleton
    private FirebaseDBConnection() {
        initFirebase();
    }

    public static FirebaseDBConnection getInstance() {
        if (instance == null) {
            instance = new FirebaseDBConnection();
        }
        return instance;
    }
    // Hàm này sử dụng để khởi tạo kết nối với Firebase Database
    private void initFirebase() {
        // Kiểm tra xem Firebase đã được khởi tạo chưa
        if (database == null) {
            // Khởi tạo Firebase
            database = FirebaseDatabase.getInstance("https://camel-2a7f2-default-rtdb.asia-southeast1.firebasedatabase.app/");
            // Lấy tham chiếu đến "root" của cơ sở dữ liệu
            databaseReference = database.getReference();
        }
    }

    // Phương thức này trả về tham chiếu đến "root" của cơ sở dữ liệu
    public DatabaseReference getRootReference() {
        return databaseReference;
    }

    // Phương thức này trả về tham chiếu đến một nút cụ thể trong cơ sở dữ liệu
    public DatabaseReference getChildReference(String childName) {
        // Trả về tham chiếu đến nút con có tên là childName
        return databaseReference.child(childName);
    }

    public void setData(String childName, Object data) {
        // Tạo một tham chiếu đến nút con có tên là childName
        DatabaseReference childReference = databaseReference.child(childName);
        // Thêm dữ liệu mới vào nút con
        childReference.push().setValue(data);
    }
    public void setData(DatabaseReference childRef, Object data, DatabaseReference.CompletionListener listener) {
        childRef.setValue(data, listener);
    }

    // Đọc dữ liệu từ cơ sở dữ liệu
    public void readData(String childName, ValueEventListener listener) {
        // Tạo một tham chiếu đến nút con có tên là childName
        DatabaseReference childReference = databaseReference.child(childName);
        // Đọc dữ liệu từ nút con và gán một ValueEventListener để xử lý dữ liệu
        childReference.addListenerForSingleValueEvent(listener);
    }

    // Xóa dữ liệu từ cơ sở dữ liệu
    public void deleteData(String childName) {
        // Tạo một tham chiếu đến nút con có tên là childName và xóa nó
        databaseReference.child(childName).removeValue();
    }
    public void deleteData(DatabaseReference childRef) {
        // Xóa dữ liệu từ nút con
        childRef.removeValue();
    }
}
