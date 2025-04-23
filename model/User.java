package model;

import java.io.IOException;
import java.io.RandomAccessFile;

public class User {
    private int id;
    private String username;
    private String password;

    // Panjang maksimal field (dalam karakter)
    public static final int USERNAME_LENGTH = 20;
    public static final int PASSWORD_LENGTH = 20;

    // Panjang record dalam byte (4 byte untuk int ID + 20*2 byte untuk username + 20*2 byte untuk password)
    public static final int RECORD_LENGTH = 4 + (USERNAME_LENGTH + PASSWORD_LENGTH) * 2;

    public User(int id, String username, String password) {
        this.id = id;
        this.username = padRight(username, USERNAME_LENGTH);
        this.password = padRight(password, PASSWORD_LENGTH);
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username.trim(); // buang spasi kosong
    }

    public String getPassword() {
        return password.trim(); // buang spasi kosong
    }

    private static String padRight(String s, int n) {
        return String.format("%-" + n + "s", s); // pad ke kanan dengan spasi
    }

    public void writeToFile(RandomAccessFile raf) throws IOException {
        raf.writeInt(id);
        raf.writeChars(username); 
        raf.writeChars(password); 
    }

    public static User readFromFile(RandomAccessFile raf) throws IOException {
        int id = raf.readInt();
        StringBuilder uname = new StringBuilder();
        for (int i = 0; i < USERNAME_LENGTH; i++) {
            uname.append(raf.readChar());
        }

        StringBuilder pword = new StringBuilder();
        for (int i = 0; i < PASSWORD_LENGTH; i++) {
            pword.append(raf.readChar());
        }

        return new User(id, uname.toString().trim(), pword.toString().trim());
    }

    @Override
    public String toString() {
        return "User{id=" + id + ", username='" + getUsername() + "'}";
    }
}
