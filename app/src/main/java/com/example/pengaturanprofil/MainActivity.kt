package com.example.pengaturanprofil

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.core.widget.doOnTextChanged

class MainActivity : AppCompatActivity() {

    companion object {
        private const val NAMA_PREFS = "profil_prefs"

        private const val KEY_NAMA        = "nama"
        private const val KEY_EMAIL       = "email"
        private const val KEY_KOTA        = "kota"
        private const val KEY_BIO         = "bio"
        private const val KEY_AVATAR      = "avatar"
        private const val KEY_GELAP       = "mode_gelap"
        private const val KEY_JUMLAH_BUKA = "jumlah_buka" // KUNCI BARU JUMLAH BUKA

        private val DAFTAR_AVATAR = listOf(
            R.drawable.avatar_1, R.drawable.avatar_2, R.drawable.avatar_3
        )
    }

    private val prefs by lazy { getSharedPreferences(NAMA_PREFS, MODE_PRIVATE) }
    private var indeksAvatar = 0

    private lateinit var layoutUtama: View
    private lateinit var ivAvatar: ImageView
    private lateinit var tvNamaKartu: TextView
    private lateinit var tvEmailKartu: TextView
    private lateinit var tvKotaKartu: TextView
    private lateinit var tvJumlahBuka: TextView // VIEW BARU UNTUK TAMPILAN JUMLAH BUKA
    private lateinit var etNama: EditText
    private lateinit var etEmail: EditText
    private lateinit var etKota: EditText
    private lateinit var etBio: EditText
    private lateinit var swGelap: SwitchCompat

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 1) Inisialisasi semua view
        layoutUtama  = findViewById(R.id.layoutUtama)
        ivAvatar     = findViewById(R.id.ivAvatar)
        tvNamaKartu  = findViewById(R.id.tvNamaKartu)
        tvEmailKartu = findViewById(R.id.tvEmailKartu)
        tvKotaKartu  = findViewById(R.id.tvKotaKartu)
        tvJumlahBuka = findViewById(R.id.tvJumlahBuka) // Inisialisasi tvJumlahBuka
        etNama       = findViewById(R.id.etNama)
        etEmail      = findViewById(R.id.etEmail)
        etKota       = findViewById(R.id.etKota)
        etBio        = findViewById(R.id.etBio)
        swGelap      = findViewById(R.id.swGelap)

        // 2) Aksi tombol
        findViewById<Button>(R.id.btnGantiAvatar).setOnClickListener { gantiAvatar() }
        findViewById<Button>(R.id.btnSimpan).setOnClickListener { simpanProfil() }
        findViewById<Button>(R.id.btnReset).setOnClickListener { hapusSemuaData() }

        // 3) Listener Switch Mode Gelap
        swGelap.setOnCheckedChangeListener { _, isChecked ->
            terapkanModeGelap(isChecked)
        }

        // 4) Update kartu profil real-time
        etNama.doOnTextChanged { _, _, _, _ -> perbaruiKartu() }
        etEmail.doOnTextChanged { _, _, _, _ -> perbaruiKartu() }
        etKota.doOnTextChanged { _, _, _, _ -> perbaruiKartu() }

        // 5) Hitung dan tampilkan jumlah pembukaan aplikasi
        hitungDanTampilkanJumlahBuka()

        // 6) Tampilkan data profil tersimpan
        muatProfil()
    }

    // ================= PENGHITUNG BUKA APLIKASI =================
    private fun hitungDanTampilkanJumlahBuka() {
        // Ambil nilai lama (default 0), lalu tambahkan 1
        val jumlahBukaLama = prefs.getInt(KEY_JUMLAH_BUKA, 0)
        val jumlahBukaBaru = jumlahBukaLama + 1

        // Simpan nilai baru kembali ke SharedPreferences
        prefs.edit().putInt(KEY_JUMLAH_BUKA, jumlahBukaBaru).apply()

        // Teks pesan yang diformat
        val pesan = getString(R.string.pesan_jumlah_buka, jumlahBukaBaru)

        // Tampilkan pesan menggunakan Toast
        Toast.makeText(this, pesan, Toast.LENGTH_SHORT).show()

        // Tampilkan juga teks ke TextView di kartu profil
        tvJumlahBuka.text = pesan
    }

    // ================= MENYIMPAN DATA =================
    private fun simpanProfil() {
        prefs.edit()
            .putString(KEY_NAMA,   etNama.text.toString().trim())
            .putString(KEY_EMAIL,  etEmail.text.toString().trim())
            .putString(KEY_KOTA,   etKota.text.toString().trim())
            .putString(KEY_BIO,    etBio.text.toString().trim())
            .putInt(KEY_AVATAR,    indeksAvatar)
            .putBoolean(KEY_GELAP, swGelap.isChecked)
            .apply()

        Toast.makeText(this, getString(R.string.pesan_tersimpan), Toast.LENGTH_SHORT).show()
    }

    // ================= MEMBACA DATA =================
    private fun muatProfil() {
        val nama  = prefs.getString(KEY_NAMA, "") ?: ""
        val email = prefs.getString(KEY_EMAIL, "") ?: ""
        val kota  = prefs.getString(KEY_KOTA, "") ?: ""
        val bio   = prefs.getString(KEY_BIO, "") ?: ""
        indeksAvatar = prefs.getInt(KEY_AVATAR, 0)
        val gelap  = prefs.getBoolean(KEY_GELAP, false)

        etNama.setText(nama)
        etEmail.setText(email)
        etKota.setText(kota)
        etBio.setText(bio)
        ivAvatar.setImageResource(DAFTAR_AVATAR[indeksAvatar])

        swGelap.isChecked = gelap

        terapkanModeGelap(gelap)
        perbaruiKartu()
    }

    // ================= MENGHAPUS DATA =================
    private fun hapusSemuaData() {
        prefs.edit().clear().apply()
        Toast.makeText(this, getString(R.string.pesan_reset), Toast.LENGTH_SHORT).show()
        muatProfil()
        // Opsi: Hitung ulang dari awal jika dihapus
        hitungDanTampilkanJumlahBuka()
    }

    // ================= TAMPILAN & LOGIKA LOKAL =================
    private fun gantiAvatar() {
        indeksAvatar = (indeksAvatar + 1) % DAFTAR_AVATAR.size
        ivAvatar.setImageResource(DAFTAR_AVATAR[indeksAvatar])
    }

    private fun perbaruiKartu() {
        val nama  = etNama.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val kota  = etKota.text.toString().trim()

        tvNamaKartu.text  = nama.ifEmpty { getString(R.string.nama_default) }
        tvEmailKartu.text = email.ifEmpty { getString(R.string.email_default) }
        tvKotaKartu.text  = kota.ifEmpty { getString(R.string.kota_default) }
    }

    private fun terapkanModeGelap(aktif: Boolean) {
        val latar          = if (aktif) "#121212" else "#FFFFFF"
        val warnaNama      = if (aktif) "#F5F5F5" else "#1B1B1B"
        val warnaEmail     = if (aktif) "#B0B0B0" else "#6B6B6B"
        val warnaKota      = if (aktif) "#9E9E9E" else "#757575"
        val warnaBuka      = if (aktif) "#888888" else "#888888"
        val warnaTeksInput = if (aktif) "#FFFFFF" else "#000000"

        layoutUtama.setBackgroundColor(Color.parseColor(latar))

        tvNamaKartu.setTextColor(Color.parseColor(warnaNama))
        tvEmailKartu.setTextColor(Color.parseColor(warnaEmail))
        tvKotaKartu.setTextColor(Color.parseColor(warnaKota))
        tvJumlahBuka.setTextColor(Color.parseColor(warnaBuka))

        etNama.setTextColor(Color.parseColor(warnaTeksInput))
        etEmail.setTextColor(Color.parseColor(warnaTeksInput))
        etKota.setTextColor(Color.parseColor(warnaTeksInput))
        etBio.setTextColor(Color.parseColor(warnaTeksInput))
        swGelap.setTextColor(Color.parseColor(warnaTeksInput))
    }
}