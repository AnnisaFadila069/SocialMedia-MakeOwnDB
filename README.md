# Social Media Database - Own Implementation

## Deskripsi

Proyek ini adalah implementasi sederhana dari sebuah **sistem sosial media** seperti **X/Twitter** dengan menggunakan **database berbasis teks (plain text)**. Proyek ini dibuat menggunakan bahasa **Java**, dan data disimpan dalam format file teks, terdiri dari beberapa file data yang terpisah.

### Fitur Utama
- Membuat database sendiri menggunakan **plain text (txt)** untuk menyimpan informasi sosial media seperti **postingan**, **komentar**, **pengikut**, dan **pengguna**.
- Proyek ini mengelola data menggunakan struktur file teks dengan format yang sederhana, memungkinkan simulasikan interaksi sosial media seperti membuat post, mengikuti pengguna, serta berkomentar.

## Struktur Folder

Proyek ini terdiri dari beberapa folder utama sebagai berikut:

### 1. **data**
Folder `data` digunakan untuk menyimpan **data mentah** yang digunakan dalam sistem sosial media. Format yang digunakan adalah sebagai berikut:

- **Komentar:**
// <CommentID>|<PostID>|<UserID>|<Timestamp>|<Konten>
Menyimpan data komentar pada sebuah post, dengan `CommentID` yang unik, `PostID` yang terkait, `UserID` dari pengguna yang mengomentari, timestamp, dan konten komentar.

- **Follow:** 
// <FollowerID>|<FolloweeID>
Menyimpan data hubungan follow antar pengguna, dengan `FollowerID` dan `FolloweeID`.

- **Postingan:**
// <PostID>|<UserID>|<Timestamp>|<Konten>
Menyimpan data postingan dari pengguna, dengan `PostID` yang unik, `UserID` dari pembuat postingan, timestamp, dan konten postingan.

- **Pengguna:**
// <ID>|<Username>|<Password>
Menyimpan data pengguna, termasuk ID, username, dan password.

### 2. **service**
Folder `service` berisi layanan-layanan yang menangani logika bisnis dari aplikasi sosial media ini. Beberapa layanan yang ada antara lain:
- **AuthService:** Untuk mengelola pendaftaran dan login pengguna.
- **FollowService:** Untuk mengelola operasi follow dan unfollow antar pengguna.
- **PostService:** Untuk menangani pembuatan, penghapusan, dan penampilan postingan.
- **CommentService:** Untuk mengelola komentar pada postingan.
- **TimelineService:** Untuk mengelola timeline yang berisi postingan dari pengguna yang diikuti.
- **ViewService:** Untuk menampilkan postingan dan komentar terkait.

### 3. **model**
Folder `model` berisi kelas-kelas model yang mendefinisikan struktur data yang digunakan dalam sistem ini. Kelas-kelas ini digunakan untuk representasi objek seperti:
- **Post:** Menyimpan data terkait sebuah postingan (ID, konten, pengirim).
- **Comment:** Menyimpan data terkait komentar pada sebuah postingan (ID, konten, pengirim, timestamp).
- **User:** Menyimpan data pengguna sosial media (ID, username, password).
- **Follow:** Menyimpan data hubungan follow antara pengguna.

### 4. **main menu**
 `main menu` berisi antarmuka pengguna utama yang menghubungkan semua layanan dan model dalam program. Di sini, pengguna bisa melakukan berbagai interaksi seperti:
- Login atau mendaftar.
- Membuat, melihat, dan menghapus postingan.
- Mengikuti atau berhenti mengikuti pengguna lain.
- Melihat timeline dan komentar.
