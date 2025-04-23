package service;

import model.User;
import java.io.*;

public class AuthService {
    private static final String USER_DATA_FILE = "data/User.txt";
    private static final String USER_INDEX_FILE = "data/UserIndex.txt";
    private static final int RECORD_LENGTH = 47; // 5 + 1 + 20 + 1 + 20
    private static User loggedInUser = null;

    public static boolean register(int id, String username, String password) {
        if (isUsernameTaken(username)) {
            System.out.println("❌ Username sudah digunakan.");
            return false;
        }

        try (
                RandomAccessFile raf = new RandomAccessFile(USER_DATA_FILE, "rw");
                BufferedWriter indexWriter = new BufferedWriter(new FileWriter(USER_INDEX_FILE, true))) {
            // Hitung record number
            long recordNumber = raf.length() / RECORD_LENGTH;
            raf.seek(raf.length()); // Pindah ke akhir file

            String formattedId = String.format("%05d", id);
            String formattedUsername = String.format("%-20s", username);
            String formattedPassword = String.format("%-20s", password);

            String record = formattedId + "|" + formattedUsername + "|" + formattedPassword + "\n";
            raf.writeBytes(record);

            // Simpan ke index
            indexWriter.write(username + "|" + recordNumber);
            indexWriter.newLine();

            System.out.println("✅ Registrasi berhasil!");
            return true;
        } catch (IOException e) {
            System.out.println("❌ Gagal menyimpan user: " + e.getMessage());
            return false;
        }
    }

    public static User login(String username, String password) {
        try (BufferedReader indexReader = new BufferedReader(new FileReader(USER_INDEX_FILE));
                RandomAccessFile raf = new RandomAccessFile(USER_DATA_FILE, "r")) {

            String line;
            while ((line = indexReader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length == 2 && parts[0].equals(username)) {
                    int recordNumber = Integer.parseInt(parts[1]);
                    raf.seek(recordNumber * RECORD_LENGTH);
                    byte[] buffer = new byte[RECORD_LENGTH];
                    raf.read(buffer);
                    String record = new String(buffer).trim();

                    String[] fields = record.split("\\|");
                    if (fields.length == 3) {
                        int id = Integer.parseInt(fields[0].trim());
                        String uname = fields[1].trim();
                        String pass = fields[2].trim();

                        if (uname.equals(username) && pass.equals(password)) {
                            loggedInUser = new User(id, uname, pass);
                            System.out.println("✅ Login berhasil!");
                            return loggedInUser;
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("❌ Gagal saat login: " + e.getMessage());
        }
        System.out.println("❌ Username atau password salah.");
        return null;
    }

    public static String getLoggedInUsername() {
        return loggedInUser != null ? loggedInUser.getUsername() : null;
    }

    public static int getLoggedInUserId() {
        return loggedInUser != null ? loggedInUser.getId() : -1;
    }

    public static String getUsernameById(int userId) {
        try (RandomAccessFile raf = new RandomAccessFile(USER_DATA_FILE, "r")) {
            long totalRecords = raf.length() / RECORD_LENGTH;
            for (int i = 0; i < totalRecords; i++) {
                raf.seek(i * RECORD_LENGTH);
                byte[] buffer = new byte[RECORD_LENGTH];
                raf.read(buffer);
                String record = new String(buffer).trim();
                String[] fields = record.split("\\|");
                if (fields.length == 3) {
                    try {
                        int id = Integer.parseInt(fields[0].trim()); // Pastikan ini hanya mengkonversi angka
                        if (id == userId) {
                            return fields[1].trim();
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("❌ Format ID pengguna tidak valid: " + fields[0].trim());
                        return "Unknown User"; // Kembalikan "Unknown User" jika terjadi kesalahan
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("❌ Gagal membaca user: " + e.getMessage());
        }
        return "Unknown User"; // Jika tidak ditemukan
    }

    private static boolean isUsernameTaken(String username) {
        try (BufferedReader indexReader = new BufferedReader(new FileReader(USER_INDEX_FILE))) {
            String line;
            while ((line = indexReader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length == 2 && parts[0].equals(username)) {
                    return true;
                }
            }
        } catch (IOException e) {
            // Optional logging
        }
        return false;
    }
}
