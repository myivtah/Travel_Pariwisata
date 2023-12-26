package com.example.travelpariwisata

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.travelpariwisata.menu.Pesanan
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class PaymentActivity : AppCompatActivity() {

    private val transaksiRef = FirebaseDatabase.getInstance().getReference("transaksi")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)

        val editTextCash: EditText = findViewById(R.id.editTextCash)

        editTextCash.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(editable: Editable?) {
                updateChangeMoney()
            }
        })

        val buttonBuy: Button = findViewById(R.id.buttonBuy)
        buttonBuy.setOnClickListener {
            onBuyButtonClick()
        }
    }

    private fun onBuyButtonClick() {
        val transaksiData: HashMap<String, Any>? = intent.getSerializableExtra("transaksiData") as? HashMap<String, Any>

        if (transaksiData != null) {
            processCashPayment(transaksiData)
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
            finish()
        } else {
            Toast.makeText(this, "Data transaksi tidak valid", Toast.LENGTH_SHORT).show()
            finish()
        }
    }


    private fun updateChangeMoney() {
        val transaksiData: HashMap<String, Any>? = intent.getSerializableExtra("transaksiData") as? HashMap<String, Any>
        val editTextCash: EditText = findViewById(R.id.editTextCash)
        val textViewKembali: TextView = findViewById(R.id.textViewKembali)

        if (transaksiData != null) {
            val cashInput = editTextCash.text.toString().toDoubleOrNull()

            if (cashInput != null) {
                val harga: Double = when (val hargaRaw = transaksiData["Harga"]) {
                    is Long -> hargaRaw.toDouble()
                    is Double -> hargaRaw
                    else -> 0.0
                }
                val peserta: Int = transaksiData["JumlahPeserta"]?.toString()?.toIntOrNull() ?: 0
                val totalHarga = (harga * peserta).toInt()
                val tax = (totalHarga * 0.11).toInt()
                val totalBayar = (totalHarga + tax)

                val changeMoney = (cashInput - totalBayar).toInt()
                textViewKembali.text = "Rp.$changeMoney"
            } else {
                textViewKembali.text = "Change Money: Invalid Input"
            }
        } else {
            Toast.makeText(this, "Data transaksi tidak valid", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun processCashPayment(transaksiData: HashMap<String, Any>) {
        val harga: Double = when (val hargaRaw = transaksiData["Harga"]) {
            is Long -> hargaRaw.toDouble()
            is Double -> hargaRaw
            else -> 0.0
        }
        val peserta: Int = transaksiData["JumlahPeserta"]?.toString()?.toIntOrNull() ?: 0
        val totalHarga = (harga * peserta).toInt()
        val tax = (totalHarga * 0.11).toInt()
        val totalBayar = (totalHarga + tax)


        val resultIntent = Intent()
        resultIntent.putExtra("pesanan", createPesananFromTransaksi(transaksiData, totalBayar))
        setResult(Activity.RESULT_OK, resultIntent)

        kirimDataPesananKeDatabase(transaksiData)

        finish()
    }

    private fun kirimDataPesananKeDatabase(transaksiData: HashMap<String, Any>) {
        val databaseRef = FirebaseDatabase.getInstance().getReference("pesanan")

        val harga: Double = when (val hargaRaw = transaksiData["Harga"]) {
            is Long -> hargaRaw.toDouble()
            is Double -> hargaRaw
            else -> 0.0
        }
        val peserta: Int = transaksiData["JumlahPeserta"]?.toString()?.toIntOrNull() ?: 0
        val totalHarga = (harga * peserta).toInt()
        val tax = (totalHarga * 0.11).toInt()
        val totalBayar = (totalHarga + tax)

        val pesanan = createPesananFromTransaksi(transaksiData, totalBayar)
        databaseRef.child(pesanan.idPesanan).setValue(pesanan)
            .addOnSuccessListener {
                Toast.makeText(this, "Pembelian berhasil", Toast.LENGTH_SHORT).show()
                val idTransaksi = transaksiData["id_trans"].toString()
                hapusDataTransaksi(idTransaksi)
            }
            .addOnFailureListener {
                Toast.makeText(this, "Gagal membuat pembelian", Toast.LENGTH_SHORT).show()
            }
    }

    private fun hapusDataTransaksi(idTransaksi: String) {
        transaksiRef.child(idTransaksi).removeValue()
    }

    private fun getCurrentDateTimeInJakarta(): String {
        val timeZone = TimeZone.getTimeZone("Asia/Jakarta")
        val dateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault())
        dateFormat.timeZone = timeZone
        return dateFormat.format(Date())
    }

    private fun createPesananFromTransaksi(transaksiData: HashMap<String, Any>, totalBayar: Int): Pesanan {
        return Pesanan(
            idPesanan = transaksiData["id_trans"].toString(),
            IdTransaksi = transaksiData["id_trans"].toString(),
            namaPemesan = transaksiData["NamaPemesan"] as String,
            noIdPemesan = transaksiData["NoIdPemesan"] as String,
            noTelpPemesan = transaksiData["NoTelpPemesan"] as String,
            alamatPemesan = transaksiData["AlamatPemesan"] as String,
            paket = transaksiData["Paket"] as String,
            harga = totalBayar,
            deskripsi = transaksiData["Deskripsi"] as String,
            emailPemesan = transaksiData["EmailPemesan"] as String,
            userIdPemesan = transaksiData["UserIdPemesan"] as String,
            tanggalPesanan = getCurrentDateTimeInJakarta()
        )
    }
}