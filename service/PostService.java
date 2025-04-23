package service;

import model.Post;
import model.User;

import java.io.RandomAccessFile;
import java.io.*;
import java.util.*;

public class PostService {
    private static final String POST_FILE = "data/Post.txt";
    private static User currentUser;  // Menyimpan pengguna yang sedang login

    public static void setCurrentUser(User user) {
        currentUser = user;
    }
    
    public static void createPost(int postId, int userId, String content) {
        long timestamp = System.currentTimeMillis(); // Waktu saat post dibuat
        Post post = new Post(postId, userId, timestamp, content);

        try (RandomAccessFile file = new RandomAccessFile(POST_FILE, "rw")) {
            // Pindahkan ke akhir file
            file.seek(file.length());
            // Menulis post ke file
            file.writeBytes(post.toTextLine() + System.lineSeparator());
            System.out.println("✅ Post berhasil dibuat.");
        } catch (IOException e) {
            System.out.println("❌ Gagal membuat post: " + e.getMessage());
        }
    }

    public static int generateUniquePostId() {
        int id = 1; // Mulai dari ID 1 jika file kosong
        try (RandomAccessFile file = new RandomAccessFile(POST_FILE, "r")) {
            String line;
            while ((line = file.readLine()) != null) {
                // Mengabaikan komentar setelah "//"
                int commentIndex = line.indexOf("//");
                if (commentIndex != -1) {
                    line = line.substring(0, commentIndex).trim();
                }

                if (!line.isEmpty()) {
                    String[] parts = line.split("\\|");
                    if (parts.length == 4) {
                        int postId = Integer.parseInt(parts[0]);
                        id = Math.max(id, postId + 1);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("❌ Gagal membaca file post: " + e.getMessage());
        }
        return id;
    }

    public static void listUserPosts(int userId) {
        try (RandomAccessFile file = new RandomAccessFile(POST_FILE, "r")) {
            String line;
            boolean found = false;
            while ((line = file.readLine()) != null) {
                // Mengabaikan komentar setelah "//"
                int commentIndex = line.indexOf("//");
                if (commentIndex != -1) {
                    line = line.substring(0, commentIndex).trim();
                }

                if (!line.isEmpty()) {
                    String[] parts = line.split("\\|");
                    if (parts.length == 4) {
                        int postId = Integer.parseInt(parts[0]);
                        int uId = Integer.parseInt(parts[1]);
                        long timestamp = Long.parseLong(parts[2]);
                        String content = parts[3];
                        if (uId == userId) {
                            System.out.println("📝 Post ID: " + postId);
                            System.out.println("📅 Timestamp: " + new Date(timestamp));
                            System.out.println("💬 Konten: " + content);
                            System.out.println("---------------------------");
                            found = true;
                        }
                    }
                }
            }
            if (!found) {
                System.out.println("ℹ️ Belum ada post.");
            }
        } catch (IOException e) {
            System.out.println("❌ Gagal membaca post: " + e.getMessage());
        }
    }
    
    public static List<Post> getAllPosts() {
        List<Post> posts = new ArrayList<>();
        try (RandomAccessFile file = new RandomAccessFile(POST_FILE, "r")) {
            String line;
            while ((line = file.readLine()) != null) {
                // Mengabaikan komentar setelah "//"
                int commentIndex = line.indexOf("//");
                if (commentIndex != -1) {
                    line = line.substring(0, commentIndex).trim();
                }

                if (!line.isEmpty()) {
                    String[] parts = line.split("\\|");
                    if (parts.length == 4) {
                        int postId = Integer.parseInt(parts[0]);
                        int userId = Integer.parseInt(parts[1]);
                        long timestamp = Long.parseLong(parts[2]);
                        String content = parts[3];
                        posts.add(new Post(postId, userId, timestamp, content));
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("❌ Gagal membaca semua post: " + e.getMessage());
        }
        return posts;
    }

    public static boolean deletePost(int postId) {
        List<Post> allPosts = new ArrayList<>();
        boolean deleted = false;

        if (currentUser == null) {
            System.out.println("⚠️ Kamu belum login.");
            return false;
        }

        int loggedInUserId = currentUser.getId();

        try (RandomAccessFile file = new RandomAccessFile(POST_FILE, "r")) {
            String line;
            while ((line = file.readLine()) != null) {
                // Mengabaikan baris komentar
                if (line.trim().startsWith("//")) continue;

                String[] parts = line.split("\\|");
                if (parts.length == 4) {
                    try {
                        int pId = Integer.parseInt(parts[0]);
                        int uId = Integer.parseInt(parts[1]);
                        long timestamp = Long.parseLong(parts[2]);
                        String content = parts[3];

                        if (pId == postId && uId == loggedInUserId) {
                            deleted = true; // Tandai untuk dihapus
                            continue; // Lewatkan penambahan post ini ke list
                        }

                        allPosts.add(new Post(pId, uId, timestamp, content));
                    } catch (NumberFormatException e) {
                        System.out.println("⚠️ Format data tidak valid, melewati baris.");
                        continue;
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("❌ Gagal membaca data post: " + e.getMessage());
            return false;
        }

        try (RandomAccessFile file = new RandomAccessFile(POST_FILE, "rw")) {
            file.setLength(0); // Clear file

            for (Post p : allPosts) {
                file.writeBytes(p.toTextLine() + System.lineSeparator());
            }
        } catch (IOException e) {
            System.out.println("❌ Gagal menulis ulang post: " + e.getMessage());
            return false;
        }

        return deleted;
    }
}
