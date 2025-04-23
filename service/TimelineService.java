package service;

import model.Post;
import java.io.*;
import java.util.*;

public class TimelineService {
    private static final String FOLLOW_FILE = "data/Follow.txt";
    private static final String POST_FILE = "data/Post.txt";

    public static List<Post> getUserTimeline(int userId, int offset, int limit) {
        List<Post> timeline = new ArrayList<>();
        Set<Integer> followedUsers = getFollowedUsers(userId);

        try (BufferedReader reader = new BufferedReader(new FileReader(POST_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Mengabaikan baris komentar yang diawali dengan "//"
                if (line.trim().startsWith("//")) {
                    continue;
                }

                String[] parts = line.split("\\|");
                if (parts.length == 4) {
                    int postId = Integer.parseInt(parts[0]);
                    int uId = Integer.parseInt(parts[1]);
                    long timestamp = Long.parseLong(parts[2]);
                    String content = parts[3];
                    Post post = new Post(postId, uId, timestamp, content);

                    if (followedUsers.contains(post.getUserID()) || post.getUserID() == userId) {
                        timeline.add(post);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("❌ Gagal membaca data post: " + e.getMessage());
        }

        // Sort timeline berdasarkan timestamp (terbaru di atas)
        timeline.sort((p1, p2) -> Long.compare(p2.getTimestamp(), p1.getTimestamp()));

        // Mengambil data berdasarkan offset dan limit
        int toIndex = Math.min(offset + limit, timeline.size());
        return timeline.subList(offset, toIndex); // Mengambil subset dari timeline
    }

    private static Set<Integer> getFollowedUsers(int userId) {
        Set<Integer> followedUsers = new HashSet<>();
    
        try (BufferedReader reader = new BufferedReader(new FileReader(FOLLOW_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Mengabaikan baris yang merupakan komentar (dimulai dengan "//")
                if (line.trim().startsWith("//")) {
                    continue;
                }
    
                String[] parts = line.split("\\|");
                if (parts.length == 2) {
                    try {
                        int followerId = Integer.parseInt(parts[0]);
                        int followeeId = Integer.parseInt(parts[1]);
                        if (followerId == userId) {
                            followedUsers.add(followeeId);
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("❌ Format data tidak valid di baris: " + line);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("❌ Gagal membaca data follow: " + e.getMessage());
        }
    
        return followedUsers;
    }    
}
