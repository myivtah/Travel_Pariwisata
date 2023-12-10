package com.example.travelpariwisata

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.travelpariwisata.menu.PaketModel
import com.google.firebase.database.FirebaseDatabase

class TambahPaketActivity : AppCompatActivity() {

    lateinit var image: ImageView
    companion object {
        val IMAGE_REQUES_CODE = 100
    }

    lateinit var database: FirebaseDatabase

    @SuppressLint("WrongViewCast", "MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tambah_paket)

        // Inisialisasi Firebase Database
        database = FirebaseDatabase.getInstance()

        getSupportActionBar()?.hide()

        image = findViewById(R.id.imageMenu)
        val textId: EditText = findViewById(R.id.textViewIdTambah)
        val textName: EditText = findViewById(R.id.textViewNamaTambah)
        val textHarga: EditText = findViewById(R.id.textViewHarga)
        val btnAddImage: Button = findViewById(R.id.buttonAddImage)
        val btnSaveMenu: Button = findViewById(R.id.buttonSaveMenu)

        btnAddImage.setOnClickListener {
            pickImageGalery()
        }
        btnSaveMenu.setOnClickListener {
            val id: String = textId.text.toString().trim()
            val name: String = textName.text.toString().trim()
            val harga: Int = textHarga.text.toString().toInt()
            val bitmapDrawable: BitmapDrawable = image.drawable as BitmapDrawable
            val bitmap: Bitmap = bitmapDrawable.bitmap

            val menuModel = PaketModel(id, name, harga, bitmap)

            // Simpan data ke Firebase Realtime Database
            val databaseReference = database.getReference("Paket")
            databaseReference.child(id).setValue(menuModel)

            val intentMakanan = Intent(this, MainActivity::class.java)
            startActivity(intentMakanan)
        }
    }

    private fun pickImageGalery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/"
        startActivityForResult(intent, IMAGE_REQUES_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_REQUES_CODE && resultCode == RESULT_OK) {
            image.setImageURI(data?.data)
        }
    }
}
