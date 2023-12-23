package com.example.travelpariwisata

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.travelpariwisata.menu.PaketModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class TransaksiActivity : AppCompatActivity() {

    private lateinit var editTextNamaPemesan: EditText
    private lateinit var editTextNoIdPemesan: EditText
    private lateinit var editTextNoTelPemesan: EditText
    private lateinit var editTextAlamatPemesan: EditText

    // Inisialisasi Firebase
    private lateinit var database: FirebaseDatabase
    private lateinit var transaksiRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transaksi)

        // Inisialisasi EditText dan tombol
        editTextNamaPemesan = findViewById(R.id.editTextNamaPemesan)
        editTextNoIdPemesan = findViewById(R.id.editTextNoIdPemesan)
        editTextNoTelPemesan = findViewById(R.id.editTextNoTelPemesan)
        editTextAlamatPemesan = findViewById(R.id.editTextAlamatPemesan)

        // Inisialisasi Firebase
        database = FirebaseDatabase.getInstance()
        transaksiRef = database.getReference("transaksi")

        // Mendapatkan data yang dikirim dari DetailFragment
        val paketModel = intent.getSerializableExtra("paketModel") as PaketModel?

        // Lakukan sesuatu dengan data paketModel di sini
        paketModel?.let {
            // Mengonfirmasi eksekusi uploadToDatabase
            findViewById<View>(R.id.buttonOrderNow).setOnClickListener { view ->
                uploadToDatabase(it)
            }
        }
    }

    private fun uploadToDatabase(paketModel: PaketModel) {
        // Mendapatkan id_trans terakhir dari database
        transaksiRef.child("last_id").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val lastId = snapshot.getValue(Long::class.java) ?: 0
                val idTrans = lastId + 1

                // Update id_trans terakhir di database
                transaksiRef.child("last_id").setValue(idTrans)

                // Ambil data dari formulir
                val namaPemesan = editTextNamaPemesan.text.toString()
                val noIdPemesan = editTextNoIdPemesan.text.toString()
                val noTelPemesan = editTextNoTelPemesan.text.toString()
                val alamatPemesan = editTextAlamatPemesan.text.toString()

                val paket = paketModel.name // Ganti dengan sesuai nama properti di PaketModel
                val harga = paketModel.harga // Ganti dengan sesuai nama properti di PaketModel
                val deskripsi = paketModel.deskripsi // Ganti dengan sesuai nama properti di PaketModel

                // Mendapatkan timestamp saat ini dengan zona waktu Jakarta
                val calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Jakarta"))
                val timestamp = calendar.timeInMillis

                // Konversi timestamp ke dalam format tanggal yang diinginkan
                val dateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault())
                val formattedDate = dateFormat.format(Date(timestamp))

                // Upload data ke tabel transaksi
                val transaksiData = HashMap<String, Any>()
                transaksiData["id_trans"] = idTrans
                transaksiData["NamaPemesan"] = namaPemesan
                transaksiData["NoIdPemesan"] = noIdPemesan
                transaksiData["NoTelpPemesan"] = noTelPemesan
                transaksiData["AlamatPemesan"] = alamatPemesan
                transaksiData["Paket"] = paket
                transaksiData["Harga"] = harga
                transaksiData["Deskripsi"] = deskripsi
                transaksiData["TanggalTransaksi"] = formattedDate

                // Upload data ke Firebase Realtime Database
                transaksiRef.child(idTrans.toString()).setValue(transaksiData)

                // Menampilkan pesan bahwa data berhasil di-upload
                Toast.makeText(this@TransaksiActivity, "Order berhasil di-submit!", Toast.LENGTH_SHORT).show()

                // Kembali ke MainActivity setelah berhasil submit
                val intent = Intent(this@TransaksiActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
                Toast.makeText(this@TransaksiActivity, "Gagal mendapatkan id_trans terakhir.", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
