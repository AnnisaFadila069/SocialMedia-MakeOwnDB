package model;

public class Post {
    private int postID;
    private int userID;
    private long timestamp;
    private String content;

    public Post(int postID, int userID, long timestamp, String content) {
        this.postID = postID;
        this.userID = userID;
        this.timestamp = timestamp;
        this.content = content;
    }

    public int getPostID() {
        return postID;
    }

    public int getUserID() {
        return userID;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getContent() {
        return content;
    }

    // Mengubah objek Post menjadi string untuk disimpan ke file teks
    public String toTextLine() {
        return postID + "|" + userID + "|" + timestamp + "|" + content.replace("|", "␟");
        // ␟ digunakan sebagai pengganti jika konten mengandung "|"
    }

    // Membaca Post dari string baris file
    public static Post fromTextLine(String line) {
        String[] parts = line.split("\\|", 4); // hanya split jadi 4 bagian pertama
        if (parts.length != 4) {
            throw new IllegalArgumentException("Format baris tidak valid: " + line);
        }

        int postID = Integer.parseInt(parts[0]);
        int userID = Integer.parseInt(parts[1]);
        long timestamp = Long.parseLong(parts[2]);
        String content = parts[3].replace("␟", "|");

        return new Post(postID, userID, timestamp, content);
    }

    @Override
    public String toString() {
        return "Post{postID=" + postID +
               ", userID=" + userID +
               ", timestamp=" + timestamp +
               ", content='" + content + "'}";
    }
}
