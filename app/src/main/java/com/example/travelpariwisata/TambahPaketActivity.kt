package com.example.travelpariwisata


import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.travelpariwisata.menu.PaketModel
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.UUID

class TambahPaketActivity : AppCompatActivity() {

    lateinit var image: ImageView
    lateinit var storageReference: StorageReference
    companion object {
        const val IMAGE_REQUEST_CODE = 100
    }

    lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tambah_paket)

        // Inisialisasi Firebase Database
        database = FirebaseDatabase.getInstance()

        // Inisialisasi Firebase Storage
        storageReference = FirebaseStorage.getInstance().reference

        supportActionBar?.hide()

        image = findViewById(R.id.imageMenu)
        val textId: EditText = findViewById(R.id.editTextId)
        val textName: EditText = findViewById(R.id.editTextNama)
        val textHarga: EditText = findViewById(R.id.editTextHarga)
        val btnAddImage: Button = findViewById(R.id.buttonAddImage)
        val btnSaveMenu: Button = findViewById(R.id.buttonSaveMenu)

        btnAddImage.setOnClickListener {
            pickImageGallery()
        }
        btnSaveMenu.setOnClickListener {
            val id: String = textId.text.toString().trim()
            val name: String = textName.text.toString().trim()
            val harga: Int = textHarga.text.toString().toInt()
            val bitmapDrawable: BitmapDrawable = image.drawable as BitmapDrawable
            val bitmap: Bitmap = bitmapDrawable.bitmap

            // Menyimpan gambar ke Firebase Storage
            uploadImageToStorage(id, name, harga, bitmap)
        }
    }

    private fun pickImageGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_REQUEST_CODE)
    }

    private fun uploadImageToStorage(id: String, name: String, harga: Int, bitmap: Bitmap) {
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()
        val imageName = "image_${UUID.randomUUID()}.jpg"
        val imageReference = storageReference.child("images/$imageName")
        val uploadTask = imageReference.putBytes(data)

        uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            imageReference.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val downloadUri = task.result
                val imageUrl = downloadUri.toString()

                // Simpan data ke Firebase Realtime Database
                saveDataToRealtimeDatabase(id, name, harga, imageUrl)
            } else {
                // Handle failures
                Toast.makeText(this, "Gagal mengunggah gambar", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveDataToRealtimeDatabase(id: String, name: String, harga: Int, imageUrl: String) {
        val databaseReference = database.getReference("Paket")
        val menuModel = PaketModel(id, name, harga, imageUrl)
        databaseReference.child(id).setValue(menuModel)

        Toast.makeText(this, "Data berhasil disimpan", Toast.LENGTH_SHORT).show()

        val intentMakanan = Intent(this, MainActivity::class.java)
        startActivity(intentMakanan)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_REQUEST_CODE && resultCode == RESULT_OK) {
            val selectedImage: Uri? = data?.data
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedImage)
                image.setImageBitmap(bitmap)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

}
