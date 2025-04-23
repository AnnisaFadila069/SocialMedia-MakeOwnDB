import service.AuthService;
import service.FollowService;
import service.PostService;
import service.CommentService;
import service.TimelineService;
import service.ViewService;
import model.Post;
import model.User;

import java.io.*;
import java.util.*;

public class MainMenu {

    private static Scanner scanner = new Scanner(System.in);
    private static User currentUser = null;

    public static void main(String[] args) {
        showMenu();
    }

    private static void showMenu() {
        while (true) {
            System.out.println("===== Menu Utama =====");
            System.out.println("1. Register");
            System.out.println("2. Login");
            System.out.println("3. Buat Postingan");
            System.out.println("4. Hapus Postingan");
            System.out.println("5. Lihat Timeline");
            System.out.println("6. Follow / Unfollow");
            System.out.println("7. Komentar");
            System.out.println("8. Lihat Detail Postingan");
            System.out.println("9. Keluar");

            int choice = -1;
            boolean validChoice = false;

            while (!validChoice) {
                System.out.print("Pilih opsi: ");
                if (scanner.hasNextInt()) {
                    choice = scanner.nextInt();
                    scanner.nextLine(); // consume newline character
                    if (choice >= 1 && choice <= 9) {
                        validChoice = true;
                    } else {
                        System.out.println("❌ Masukkan angka antara 1 dan 9 sesuai opsi yang tersedia.");
                    }
                } else {
                    System.out.println("❌ Input tidak valid. Harap masukkan angka antara 1 dan 9.");
                    scanner.nextLine(); // consume invalid input
                }
            }

            switch (choice) {
                case 1:
                    register();
                    break;
                case 2:
                    System.out.println("===== Login =====");
                    System.out.print("Username: ");
                    String username = scanner.nextLine();
                    System.out.print("Password: ");
                    String password = scanner.nextLine();
                    currentUser = login(username, password);
                    if (currentUser != null) {
                        // Setelah login berhasil, set currentUser di PostService
                        PostService.setCurrentUser(currentUser);
                        System.out.println("🎉 Login berhasil!");
                    }
                    break;
                case 3:
                    createPost();
                    break;
                case 4:
                    deletePost();
                    break;
                case 5:
                    viewTimeline();
                    break;
                case 6:
                    followUnfollow();
                    break;
                case 7:
                    commentOnPost();
                    break;
                case 8:
                    viewPostWithComments();
                    break;
                case 9:
                    System.out.println("Terima kasih, sampai jumpa!");
                    System.exit(0);
                    break;
                default:
                    System.out.println("❌ Opsi tidak valid."); // Tidak seharusnya muncul dengan validasi ini
            }
        }
    }

    private static void register() {
        System.out.println("===== Register =====");
        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();

        // Pastikan username dan password tidak kosong
        if (username.trim().isEmpty() || password.trim().isEmpty()) {
            System.out.println("❌ Username atau password tidak boleh kosong.");
            return;
        }

        // Generate unique ID
        int userId = generateUniqueUserId();

        boolean success = AuthService.register(userId, username, password);
        if (success) {
            System.out.println("🎉 Register berhasil!");
        } else {
            System.out.println("❌ Username sudah terdaftar.");
        }
    }

    private static int generateUniqueUserId() {
        int id = 1; // ID awal
        try (BufferedReader reader = new BufferedReader(new FileReader("data/User.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Mengabaikan baris komentar yang dimulai dengan // atau #
                if (line.trim().startsWith("//") || line.trim().startsWith("#")) {
                    continue; // Lewati baris komentar
                }

                String[] parts = line.split("\\|");
                if (parts.length == 3) {
                    try {
                        int lastId = Integer.parseInt(parts[0].trim()); // Mengambil ID terakhir
                        id = lastId + 1; // Menambahkan 1 untuk ID baru
                    } catch (NumberFormatException e) {
                        System.out.println("❌ Format ID tidak valid di baris: " + line);
                    }
                } else {
                    System.out.println("❌ Format baris tidak valid: " + line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return id;
    }

    public static User login(String username, String password) {
        // Validasi jika username atau password kosong
        if (username.trim().isEmpty() || password.trim().isEmpty()) {
            System.out.println("❌ Username atau password tidak boleh kosong.");
            return null;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader("data/User.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Mengabaikan baris komentar yang dimulai dengan // atau #
                if (line.trim().startsWith("//") || line.trim().startsWith("#")) {
                    continue; // Lewati baris komentar
                }

                String[] parts = line.split("\\|");
                if (parts.length == 3) {
                    String storedUsername = parts[1].trim();
                    String storedPassword = parts[2].trim();

                    // Membandingkan username dan password yang diberikan dengan yang ada di file
                    if (storedUsername.equals(username) && storedPassword.equals(password)) {
                        // Menemukan username dan password yang cocok
                        int userId = Integer.parseInt(parts[0].trim()); // Mengambil ID
                        currentUser = new User(userId, storedUsername, storedPassword); // Menyimpan user yang login
                        return currentUser; // Mengembalikan user yang cocok
                    }
                } else {
                    System.out.println("❌ Format baris tidak valid: " + line); // Menangani format baris yang salah
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Jika username atau password tidak cocok
        System.out.println("❌ Login gagal. Username atau password salah.");
        return null;
    }

    private static void createPost() {
        if (currentUser == null) {
            System.out.println("❌ Anda harus login terlebih dahulu.");
            return;
        }

        System.out.println("===== Buat Postingan =====");
        System.out.print("Tulis konten: ");
        String content = scanner.nextLine();

        if (content.trim().isEmpty()) {
            System.out.println("❌ Konten tidak boleh kosong.");
            return;
        }

        int postId = PostService.generateUniquePostId(); // Menghasilkan ID unik untuk post
        PostService.createPost(postId, currentUser.getId(), content); // Panggil createPost dengan ID unik
        System.out.println("✔️ Post berhasil dibuat dengan ID: " + postId);
    }

    private static void deletePost() {
        if (currentUser == null) {
            System.out.println("❌ Anda harus login terlebih dahulu.");
            return;
        }

        System.out.println("===== Hapus Postingan =====");
        System.out.print("Masukkan ID postingan yang ingin dihapus: ");
        int postId = scanner.nextInt();
        scanner.nextLine(); // consume newline

        boolean success = PostService.deletePost(postId);

        if (success) {
            System.out.println("🗑️ Postingan berhasil dihapus.");
        } else {
            System.out.println("❌ Gagal menghapus postingan. Mungkin ID tidak valid atau bukan milik Anda.");
        }
    }

    private static int offset = 0; // Menyimpan offset untuk pagination
    private static final int LIMIT = 5; // Menyimpan batas jumlah post yang akan ditampilkan per halaman

    private static void viewTimeline() {
        if (currentUser == null) {
            System.out.println("❌ Anda harus login terlebih dahulu.");
            return;
        }

        System.out.println("===== Lihat Timeline =====");
        List<Post> timeline = TimelineService.getUserTimeline(currentUser.getId(), offset, LIMIT);

        if (timeline.isEmpty()) {
            System.out.println("ℹ️ Tidak ada post untuk ditampilkan.");
        } else {
            // Menampilkan post
            displayPosts(timeline);

            // Cek apakah masih ada post yang tersisa
            while (true) {
                System.out.println("\n🔄 Muat lebih banyak? (y/n)");
                String input = scanner.nextLine();
            
                if (input.equalsIgnoreCase("y")) {
                    timeline = TimelineService.getUserTimeline(currentUser.getId(), offset, LIMIT);
            
                    if (timeline.isEmpty()) {
                        System.out.println("ℹ️ Tidak ada post lagi.");
                        break;  
                    }
            
                    displayPosts(timeline);
            
                    if (timeline.size() < LIMIT) {
                        System.out.println("ℹ️ Tidak ada post lagi untuk dimuat.");
                        break;  // Exit if there are fewer posts than the limit, no more posts to load
                    }
            
                    offset += LIMIT;
                } else {
                    System.out.println("👋 Terima kasih telah melihat timeline!");
                    break; 
                }
            }                        
        }
    }

    private static void displayPosts(List<Post> posts) {
        // Tampilkan setiap post dalam list yang diterima
        for (Post post : posts) {
            // Ambil username berdasarkan userID
            String username = AuthService.getUsernameById(post.getUserID());
            if ("Unknown User".equals(username)) {
                // Jika username tidak ditemukan, skip post ini
                System.out.println("❌ Tidak ditemukan username untuk post ID: " + post.getPostID());
                continue;
            }
    
            // Hitung selisih waktu dan format menjadi "waktu lalu"
            long timeDiffMillis = System.currentTimeMillis() - post.getTimestamp();
            String formattedTime = formatTimeAgo(timeDiffMillis);
    
            // Tampilkan informasi post
            System.out.println("📝 Post ID: " + post.getPostID());
            System.out.println("🕒 " + formattedTime);
            System.out.println("🙋‍♂️ Oleh: @" + username);
            System.out.println("📅 Timestamp: " + new Date(post.getTimestamp()));
            System.out.println("💬 Konten: " + post.getContent());
            System.out.println("---------------------------");
        }
    
        // Update offset
        offset += LIMIT;
    }    

    // Fungsi pembantu untuk mengubah waktu jadi format "x menit yang lalu"
    private static String formatTimeAgo(long millis) {
        long seconds = millis / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;

        if (days > 0)
            return days + " hari yang lalu";
        else if (hours > 0)
            return hours + " jam yang lalu";
        else if (minutes > 0)
            return minutes + " menit yang lalu";
        else
            return "baru saja";
    }

    private static void followUnfollow() {
        if (currentUser == null) {
            System.out.println("❌ Anda harus login terlebih dahulu.");
            return;
        }

        System.out.println("===== Follow / Unfollow =====");
        System.out.print("Masukkan ID atau Username user yang ingin di-follow/unfollow: ");
        String input = scanner.nextLine(); // Mengambil input sebagai string

        int followeeId = -1;

        // Jika input numerik, anggap itu sebagai ID
        try {
            followeeId = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            // Jika bukan angka, anggap itu sebagai username dan cari ID berdasarkan
            // username
            followeeId = getUserIdByUsername(input);
            if (followeeId == -1) {
                System.out.println("❌ Username tidak ditemukan.");
                return;
            }
        }

        int action = -1;

        // Loop sampai pengguna memilih aksi yang valid (1 atau 2)
        while (true) {
            // Menampilkan opsi aksi
            System.out.print("Pilih aksi: 1. Follow  2. Unfollow: ");

            // Cek input aksi
            try {
                action = scanner.nextInt();
                scanner.nextLine(); // consume newline
            } catch (InputMismatchException e) {
                System.out.println("❌ Input tidak valid. Harap pilih antara 1 atau 2.");
                scanner.nextLine(); // Consume invalid input
                continue; // Mengulang pilihan aksi
            }

            // Cek apakah aksi yang dipilih valid
            if (action == 1 || action == 2) {
                break; // Keluar dari loop jika input valid
            } else {
                System.out.println("❌ Pilih 1 untuk Follow atau 2 untuk Unfollow.");
            }
        }

        // Melakukan follow atau unfollow sesuai pilihan
        if (action == 1) {
            FollowService.follow(currentUser.getId(), followeeId);
        } else if (action == 2) {
            FollowService.unfollow(currentUser.getId(), followeeId);
        }
    }

    // Fungsi untuk mendapatkan ID berdasarkan username
    private static int getUserIdByUsername(String username) {
        try (BufferedReader reader = new BufferedReader(new FileReader("data/User.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Abaikan baris yang dimulai dengan "//"
                if (line.trim().startsWith("//")) {
                    continue;
                }

                String[] parts = line.split("\\|");
                if (parts.length == 3 && parts[1].equalsIgnoreCase(username)) {
                    return Integer.parseInt(parts[0]); // Mengembalikan ID berdasarkan username
                }
            }
        } catch (IOException e) {
            System.out.println("❌ Gagal membaca data pengguna: " + e.getMessage());
        }

        return -1; // Kembalikan -1 jika username tidak ditemukan
    }

    private static void commentOnPost() {
        if (currentUser == null) {
            System.out.println("❌ Anda harus login terlebih dahulu.");
            return;
        }

        System.out.println("===== Komentar =====");
        System.out.print("Masukkan ID post yang ingin dikomentari: ");
        int postId = scanner.nextInt();
        scanner.nextLine(); // consume newline
        System.out.print("Konten komentar: ");
        String content = scanner.nextLine();

        CommentService.addComment(postId, currentUser.getId(), content); // Panggil addComment tanpa menyimpan hasil
        System.out.println("✔️ Komentar berhasil ditambahkan.");
    }

    private static void viewPostWithComments() {
        System.out.println("===== Lihat Detail Postingan =====");
        System.out.print("Masukkan ID post yang ingin dilihat: ");
        int postId = scanner.nextInt();
        scanner.nextLine(); // consume newline

        ViewService.viewPostWithComments(postId);
    }
}
