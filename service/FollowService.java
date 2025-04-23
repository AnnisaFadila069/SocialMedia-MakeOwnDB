package service;

import model.Follow;

import java.io.*;
import java.util.*;

public class FollowService {
    private static final String FOLLOW_FILE = "data/Follow.txt";

    // In-memory index: followerId -> set of followeeId
    private static Map<Integer, Set<Integer>> followIndex = new HashMap<>();
    
    private static boolean indexLoaded = false;

    private static void loadFollowIndex() {
        followIndex.clear();
        try (BufferedReader reader = new BufferedReader(new FileReader(FOLLOW_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().startsWith("//")) continue;
                String[] parts = line.split("\\|");
                if (parts.length == 2) {
                    int followerId = Integer.parseInt(parts[0]);
                    int followeeId = Integer.parseInt(parts[1]);
                    followIndex.computeIfAbsent(followerId, k -> new HashSet<>()).add(followeeId);
                }
            }
            indexLoaded = true;
        } catch (IOException e) {
            System.out.println("❌ Gagal memuat index follow: " + e.getMessage());
        }
    }

    public static void follow(int followerId, int followeeId) {
        if (!indexLoaded) loadFollowIndex();

        if (followerId == followeeId) {
            System.out.println("❌ Kamu tidak bisa follow diri sendiri.");
            return;
        }

        if (isFollowing(followerId, followeeId)) {
            System.out.println("⚠️ Kamu sudah follow user ini.");
            return;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FOLLOW_FILE, true))) {
            writer.write(followerId + "|" + followeeId);
            writer.newLine();

            followIndex.computeIfAbsent(followerId, k -> new HashSet<>()).add(followeeId);
            System.out.println("✅ Follow berhasil.");
        } catch (IOException e) {
            System.out.println("❌ Gagal follow: " + e.getMessage());
        }
    }

    public static void unfollow(int followerId, int followeeId) {
        List<Follow> allFollows = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(FOLLOW_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().startsWith("//")) continue;
                String[] parts = line.split("\\|");
                if (parts.length == 2) {
                    int fId = Integer.parseInt(parts[0]);
                    int feId = Integer.parseInt(parts[1]);
                    if (!(fId == followerId && feId == followeeId)) {
                        allFollows.add(new Follow(fId, feId));
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("❌ Gagal membaca data follow: " + e.getMessage());
            return;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FOLLOW_FILE))) {
            for (Follow f : allFollows) {
                writer.write(f.getFollowerID() + "|" + f.getFolloweeID());
                writer.newLine();
            }

            // Update index
            if (followIndex.containsKey(followerId)) {
                followIndex.get(followerId).remove(followeeId);
                if (followIndex.get(followerId).isEmpty()) {
                    followIndex.remove(followerId);
                }
            }

            System.out.println("✅ Unfollow berhasil.");
        } catch (IOException e) {
            System.out.println("❌ Gagal menulis ulang follow: " + e.getMessage());
        }
    }

    public static boolean isFollowing(int followerId, int followeeId) {
        if (!indexLoaded) loadFollowIndex();
        return followIndex.containsKey(followerId) && followIndex.get(followerId).contains(followeeId);
    }

    public static List<Integer> getFollowingList(int userId) {
        if (!indexLoaded) loadFollowIndex();
        Set<Integer> following = followIndex.get(userId);
        return following == null ? new ArrayList<>() : new ArrayList<>(following);
    }
}
