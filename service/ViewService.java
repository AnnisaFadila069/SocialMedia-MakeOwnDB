package service;

import java.util.concurrent.TimeUnit;
import java.io.RandomAccessFile;
import model.Post;
import model.Comment;

import java.io.*;
import java.util.*;

public class ViewService {

    private static final String POST_FILE = "data/Post.txt";
    private static final String COMMENT_FILE = "data/Comment.txt";
    private static final String USER_FILE = "data/User.txt";
    private static final Scanner scanner = new Scanner(System.in);

    // Format waktu relatif
    public static String timeAgo(long timestamp) {
        long diff = System.currentTimeMillis() - timestamp;
        long minutes = TimeUnit.MILLISECONDS.toMinutes(diff);
        if (minutes < 1) return "baru saja";
        else if (minutes == 1) return "1 menit yang lalu";
        else if (minutes < 60) return minutes + " menit yang lalu";

        long hours = TimeUnit.MILLISECONDS.toHours(diff);
        if (hours == 1) return "1 jam yang lalu";
        else if (hours < 24) return hours + " jam yang lalu";

        long days = TimeUnit.MILLISECONDS.toDays(diff);
        if (days == 1) return "kemarin";
        return days + " hari yang lalu";
    }

    // Menampilkan post beserta komentar terbaru
    public static void viewPostWithComments(int postId) {
        Set<Integer> shownCommentIds = new HashSet<>();

        Post post = findPostById(postId);
        if (post == null) {
            System.out.println("❌ Post tidak ditemukan.");
            return;
        }

        displayPostDetail(post);
        displayComments(postId, shownCommentIds);
    }

    // Menemukan post berdasarkan ID
    private static Post findPostById(int postId) {
        try (RandomAccessFile raf = new RandomAccessFile(POST_FILE, "r")) {
            String line;
            while ((line = raf.readLine()) != null) {
                if (line.trim().startsWith("//") || line.trim().isEmpty()) continue;
    
                String[] parts = line.split("\\|");
                if (parts.length == 4) {
                    try {
                        int id = Integer.parseInt(parts[0]);
                        int userId = Integer.parseInt(parts[1]);
                        long timestamp = Long.parseLong(parts[2]);
                        String content = parts[3];
                        if (id == postId) return new Post(id, userId, timestamp, content);
                    } catch (NumberFormatException ignored) {}
                }
            }
        } catch (IOException e) {
            System.out.println("❌ Gagal membaca Post.txt: " + e.getMessage());
        }
        return null;
    }

    // Menampilkan detail post
    private static void displayPostDetail(Post post) {
        System.out.println("📝 Post ID: " + post.getPostID());
        System.out.println("🕒 " + timeAgo(post.getTimestamp()));
        System.out.println("🙋‍♂️ Oleh: @" + getUsernameById(post.getUserID()));
        System.out.println("📅 Timestamp: " + new Date(post.getTimestamp()));
        System.out.println("💬 Konten: " + post.getContent());
        System.out.println("---------------------------");
        System.out.println("💬 Komentar:");
    }

    // Menampilkan 5 komentar terbaru
    private static void displayComments(int postId, Set<Integer> shownCommentIds) {
        List<Comment> allComments = loadCommentsForPost(postId, shownCommentIds);
        if (allComments.isEmpty()) {
            System.out.println("ℹ️ Tidak ada komentar untuk post ini.");
            return;
        }

        allComments.sort((c1, c2) -> Long.compare(c2.getTimestamp(), c1.getTimestamp()));
        Iterator<Comment> iterator = allComments.iterator();
        int displayed = 0;

        while (iterator.hasNext()) {
            while (iterator.hasNext() && displayed < 5) {
                Comment comment = iterator.next();
                shownCommentIds.add(comment.getCommentID());
                displayComment(comment);
                displayed++;
            }

            if (iterator.hasNext()) {
                System.out.print("🔄 Muat lebih banyak komentar? (y/n): ");
                String input = scanner.nextLine();
                if (!input.equalsIgnoreCase("y")) break;
                displayed = 0;
            } else {
                System.out.println("✅ Semua komentar telah ditampilkan.");
            }
        }
    }

    // Membaca komentar dari file
    private static List<Comment> loadCommentsForPost(int postId, Set<Integer> excludeIds) {
        List<Comment> comments = new ArrayList<>();
    
        try (RandomAccessFile raf = new RandomAccessFile(COMMENT_FILE, "r")) {
            String line;
            while ((line = raf.readLine()) != null) {
                if (line.trim().startsWith("//") || line.trim().isEmpty()) continue;
    
                String[] parts = line.split("\\|", 5);
                if (parts.length == 5) {
                    try {
                        int commentId = Integer.parseInt(parts[0]);
                        int commentPostId = Integer.parseInt(parts[1]);
                        int userId = Integer.parseInt(parts[2]);
                        long timestamp = Long.parseLong(parts[3]);
                        String content = parts[4];
    
                        if (commentPostId == postId && !excludeIds.contains(commentId)) {
                            comments.add(new Comment(commentId, commentPostId, userId, timestamp, content));
                        }
                    } catch (NumberFormatException ignored) {}
                }
            }
        } catch (IOException e) {
            System.out.println("❌ Gagal membaca Comment.txt: " + e.getMessage());
        }
    
        return comments;
    }

    // Menampilkan komentar
    private static void displayComment(Comment comment) {
        System.out.println("🗨️ Comment by @" + getUsernameById(comment.getUserID()));
        System.out.println("🕒 " + timeAgo(comment.getTimestamp()));
        System.out.println("📅 Timestamp: " + new Date(comment.getTimestamp()));
        System.out.println("💬 " + comment.getContent());
        System.out.println("---------------------------");
    }

    // Mendapatkan username dari userId
    private static String getUsernameById(int userId) {
        try (RandomAccessFile raf = new RandomAccessFile(USER_FILE, "r")) {
            String line;
            while ((line = raf.readLine()) != null) {
                if (line.trim().startsWith("//")) continue;
    
                String[] parts = line.split("\\|");
                if (parts.length == 3) {
                    try {
                        if (Integer.parseInt(parts[0]) == userId) {
                            return parts[1];
                        }
                    } catch (NumberFormatException ignored) {}
                }
            }
        } catch (IOException e) {
            System.out.println("❌ Gagal membaca User.txt: " + e.getMessage());
        }
    
        return "unknown_user";
    }
}
