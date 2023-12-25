package com.example.travelpariwisata

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

class PaymentActivity : AppCompatActivity() {

    private var lastNumericKey = 0
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
        val pesanan: Pesanan? = intent.getParcelableExtra("pesanan")

        if (pesanan != null) {
            kirimDataPesananKeDatabase(pesanan)
        } else {
            Toast.makeText(this, "Data pesanan tidak valid", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun updateChangeMoney() {
        val pesanan: Pesanan? = intent.getParcelableExtra("pesanan")
        val editTextCash: EditText = findViewById(R.id.editTextCash)
        val textViewKembali: TextView = findViewById(R.id.textViewKembali)

        if (pesanan != null) {
            val cashInput = editTextCash.text.toString().toDoubleOrNull()

            if (cashInput != null) {
                val changeMoney = (cashInput - pesanan.harga).toInt()
                textViewKembali.text = "Rp.$changeMoney"
            } else {
                textViewKembali.text = "Change Money: Invalid Input"
            }
        } else {
            Toast.makeText(this, "Data pesanan tidak valid", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun kirimDataPesananKeDatabase(pesanan: Pesanan) {
        val databaseRef = FirebaseDatabase.getInstance().getReference("pesanan")

        lastNumericKey++

        val pesananKey = lastNumericKey.toString()

        databaseRef.child(pesananKey).setValue(pesanan)
            .addOnSuccessListener {
                Toast.makeText(this, "Pembelian berhasil", Toast.LENGTH_SHORT).show()
                val idTransaksi = pesanan.IdTransaksi
                hapusDataTransaksi(idTransaksi)
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Gagal membuat pembelian", Toast.LENGTH_SHORT).show()
            }
    }

    private fun hapusDataTransaksi(idTransaksi: String) {
        transaksiRef.child(idTransaksi).removeValue()
    }
}
