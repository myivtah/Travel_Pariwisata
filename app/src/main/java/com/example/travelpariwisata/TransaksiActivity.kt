package com.example.travelpariwisata

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.travelpariwisata.menu.PaketModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
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
    private lateinit var editTextJumlahPeserta : EditText

    private lateinit var database: FirebaseDatabase
    private lateinit var transaksiRef: DatabaseReference
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transaksi)

        editTextNamaPemesan = findViewById(R.id.editTextNamaPemesan)
        editTextNoIdPemesan = findViewById(R.id.editTextNoIdPemesan)
        editTextNoTelPemesan = findViewById(R.id.editTextNoTelPemesan)
        editTextAlamatPemesan = findViewById(R.id.editTextAlamatPemesan)
        editTextJumlahPeserta = findViewById(R.id.editTextJumlahPeserta)

        database = FirebaseDatabase.getInstance()
        transaksiRef = database.getReference("transaksi")
        auth = FirebaseAuth.getInstance()

        val paketModel = intent.getSerializableExtra("paketModel") as PaketModel?

        paketModel?.let {
            findViewById<View>(R.id.buttonOrderNow).setOnClickListener { view ->
                uploadToDatabase(it)
            }
        }
    }

    private fun uploadToDatabase(paketModel: PaketModel) {
        transaksiRef.child("last_id").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val lastId = snapshot.getValue(Long::class.java) ?: 0
                val idTrans = lastId + 1

                transaksiRef.child("last_id").setValue(idTrans)

                val namaPemesan = editTextNamaPemesan.text.toString()
                val noIdPemesan = editTextNoIdPemesan.text.toString()
                val noTelPemesan = editTextNoTelPemesan.text.toString()
                val alamatPemesan = editTextAlamatPemesan.text.toString()
                val jumlahPeserta = editTextJumlahPeserta.text.toString()

                if (namaPemesan.isBlank() || noIdPemesan.isBlank() || noTelPemesan.isBlank() || alamatPemesan.isBlank()) {
                    Toast.makeText(this@TransaksiActivity, "Semua kolom harus diisi!", Toast.LENGTH_SHORT).show()
                    return
                }

                val currentUser: FirebaseUser? = auth.currentUser
                val email: String = currentUser?.email ?: ""
                val userId: String = currentUser?.uid ?: ""

                val paket = paketModel.name
                val harga = paketModel.harga
                val deskripsi = paketModel.deskripsi

                val calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Jakarta"))
                val timestamp = calendar.timeInMillis

                val dateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault())
                val formattedDate = dateFormat.format(Date(timestamp))

                val transaksiData = HashMap<String, Any>()
                transaksiData["id_trans"] = idTrans
                transaksiData["NamaPemesan"] = namaPemesan
                transaksiData["NoIdPemesan"] = noIdPemesan
                transaksiData["NoTelpPemesan"] = noTelPemesan
                transaksiData["AlamatPemesan"] = alamatPemesan
                transaksiData["JumlahPeserta"] = jumlahPeserta
                transaksiData["Paket"] = paket
                transaksiData["Harga"] = harga
                transaksiData["Deskripsi"] = deskripsi
                transaksiData["TanggalTransaksi"] = formattedDate
                transaksiData["EmailPemesan"] = email
                transaksiData["UserIdPemesan"] = userId

                transaksiRef.child(idTrans.toString()).setValue(transaksiData)
                    .addOnSuccessListener {
                        Toast.makeText(this@TransaksiActivity, "Order berhasil di-submit!", Toast.LENGTH_SHORT).show()

                        val intent = Intent(this@TransaksiActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this@TransaksiActivity, "Gagal mengupload data.", Toast.LENGTH_SHORT).show()
                    }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@TransaksiActivity, "Gagal mendapatkan id_trans terakhir.", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
