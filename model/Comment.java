package model;

import java.io.IOException;
import java.io.RandomAccessFile;

public class Comment {
    private int commentID;
    private int postID;
    private int userID;
    private long timestamp;
    private String content;

    // Constructor
    public Comment(int commentID, int postID, int userID, long timestamp, String content) {
        this.commentID = commentID;
        this.postID = postID;
        this.userID = userID;
        this.timestamp = timestamp;
        this.content = content;
    }

    // Method untuk menulis komentar ke file menggunakan RandomAccessFile
    public void writeToRandomAccessFile(RandomAccessFile file) throws IOException {
        String data = commentID + "|" + postID + "|" + userID + "|" + timestamp + "|" + content + "\n";
        file.writeBytes(data);
    }

    // Method untuk membaca komentar dari file menggunakan RandomAccessFile
    public static Comment readFromRandomAccessFile(RandomAccessFile file) throws IOException {
        String line = file.readLine();
        if (line == null) {
            return null;
        }
        String[] parts = line.split("\\|");
        int commentID = Integer.parseInt(parts[0]);
        int postID = Integer.parseInt(parts[1]);
        int userID = Integer.parseInt(parts[2]);
        long timestamp = Long.parseLong(parts[3]);
        String content = parts[4];
        return new Comment(commentID, postID, userID, timestamp, content);
    }

    // Getter methods
    public int getCommentID() { return commentID; }
    public int getPostID() { return postID; }
    public int getUserID() { return userID; }
    public long getTimestamp() { return timestamp; }
    public String getContent() { return content; }

    // Override toString method
    @Override
    public String toString() {
        return commentID + "|" + postID + "|" + userID + "|" + timestamp + "|" + content;
    }
}
