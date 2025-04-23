package service;

import model.Comment;

import java.io.*;
import java.util.*;

public class CommentService {
    private static final String COMMENT_FILE = "data/Comment.txt";
    private static final String COMMENT_INDEX_FILE = "data/CommentIndex.txt";

    // Menambahkan komentar dan memperbarui index
    public static void addComment(int postId, int userId, String content) {
        long timestamp = System.currentTimeMillis();
        int commentId = generateUniqueCommentId();
        Comment comment = new Comment(commentId, postId, userId, timestamp, content);

        try (RandomAccessFile file = new RandomAccessFile(COMMENT_FILE, "rw")) {
            long offset = file.length(); // posisi untuk index
            file.seek(offset);
            comment.writeToRandomAccessFile(file);
            updateIndex(postId, offset);
            System.out.println("💬 Komentar berhasil ditambahkan.");
        } catch (IOException e) {
            System.out.println("❌ Gagal menulis komentar: " + e.getMessage());
        }
    }

    // Update atau tambah index baru
    private static void updateIndex(int postId, long offset) {
        Map<Integer, List<Long>> indexMap = loadIndex();

        indexMap.computeIfAbsent(postId, k -> new ArrayList<>()).add(offset);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(COMMENT_INDEX_FILE))) {
            for (Map.Entry<Integer, List<Long>> entry : indexMap.entrySet()) {
                StringBuilder sb = new StringBuilder();
                sb.append(entry.getKey()).append("|");
                List<Long> offsets = entry.getValue();
                for (int i = 0; i < offsets.size(); i++) {
                    sb.append(offsets.get(i));
                    if (i != offsets.size() - 1) sb.append(",");
                }
                writer.write(sb.toString());
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("❌ Gagal memperbarui index: " + e.getMessage());
        }
    }

    // Load index dari file
    private static Map<Integer, List<Long>> loadIndex() {
        Map<Integer, List<Long>> indexMap = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(COMMENT_INDEX_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length == 2) {
                    int postId = Integer.parseInt(parts[0]);
                    String[] offsets = parts[1].split(",");
                    List<Long> offsetList = new ArrayList<>();
                    for (String off : offsets) {
                        offsetList.add(Long.parseLong(off));
                    }
                    indexMap.put(postId, offsetList);
                }
            }
        } catch (IOException e) {
            // Tidak masalah jika file index belum ada
        }
        return indexMap;
    }

    // Gunakan index untuk ambil komentar berdasarkan postId
    public static List<Comment> getCommentsByPost(int postId) {
        List<Comment> comments = new ArrayList<>();
        Map<Integer, List<Long>> indexMap = loadIndex();

        if (!indexMap.containsKey(postId)) {
            return comments;
        }

        try (RandomAccessFile file = new RandomAccessFile(COMMENT_FILE, "r")) {
            for (long offset : indexMap.get(postId)) {
                file.seek(offset);
                String line = file.readLine();
                if (line != null) {
                    String[] parts = line.split("\\|");
                    if (parts.length == 5) {
                        int commentId = Integer.parseInt(parts[0]);
                        int pId = Integer.parseInt(parts[1]);
                        int userId = Integer.parseInt(parts[2]);
                        long timestamp = Long.parseLong(parts[3]);
                        String content = parts[4];
                        comments.add(new Comment(commentId, pId, userId, timestamp, content));
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("❌ Gagal membaca komentar dari index: " + e.getMessage());
        }
        return comments;
    }

    public static int generateUniqueCommentId() {
        int id = 1;
        try (RandomAccessFile file = new RandomAccessFile(COMMENT_FILE, "r")) {
            String line;
            while ((line = file.readLine()) != null) {
                if (line.trim().isEmpty() || line.startsWith("//")) continue;
                String[] parts = line.split("\\|");
                if (parts.length >= 1) {
                    try {
                        int currentId = Integer.parseInt(parts[0]);
                        if (currentId >= id) {
                            id = currentId + 1;
                        }
                    } catch (NumberFormatException e) {
                        // skip
                    }
                }
            }
        } catch (IOException e) {
            // skip
        }
        return id;
    }
}
