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
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.travelpariwisata.menu.PaketModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import java.io.ByteArrayOutputStream

class EditPaketActivity : AppCompatActivity() {

    private lateinit var databaseReference: DatabaseReference
    private lateinit var storageReference: StorageReference

    private lateinit var id: String
    private lateinit var name: String
    private var harga: Int = 0
    private lateinit var deskripsi: String
    private lateinit var imageUrl: String

    private lateinit var textViewIdEdit: TextView
    private lateinit var editTextNamaEdit: EditText
    private lateinit var editTextHargaEdit: EditText
    private lateinit var editTextDeskripsiEdit: EditText
    private lateinit var imageMenuEdit: ImageView
    private lateinit var buttonAddImageEdit: Button
    private lateinit var buttonSaveMenuEdit: Button

    companion object {
        const val IMAGE_REQUEST_CODE = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_paket)

        databaseReference = FirebaseDatabase.getInstance().getReference("Paket")

        storageReference = FirebaseStorage.getInstance().reference

        supportActionBar?.hide()

        textViewIdEdit = findViewById(R.id.textViewIdEdit)
        editTextNamaEdit = findViewById(R.id.editTextNamaEdit)
        editTextHargaEdit = findViewById(R.id.editTextHargaEdit)
        editTextDeskripsiEdit = findViewById(R.id.editTextDeskripsiEdit)
        imageMenuEdit = findViewById(R.id.imageMenuEdit)
        buttonAddImageEdit = findViewById(R.id.buttonAddImageEdit)
        buttonSaveMenuEdit = findViewById(R.id.buttonSaveMenuEdit)

        val intent = intent
        if (intent != null && intent.hasExtra("paket_id")) {
            id = intent.getStringExtra("paket_id") ?: ""
            loadDataToEdit(id)
        }

        buttonAddImageEdit.setOnClickListener {
            pickImageGallery()
        }

        buttonSaveMenuEdit.setOnClickListener {
            saveEditedDataToDatabase()
        }
    }

    private fun loadDataToEdit(paketId: String) {
        databaseReference.child(paketId).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val paket = snapshot.getValue(PaketModel::class.java)
                if (paket != null) {
                    // Set data ke view
                    textViewIdEdit.text = paket.id
                    editTextNamaEdit.setText(paket.name)
                    editTextHargaEdit.setText(paket.harga.toString())
                    editTextDeskripsiEdit.setText(paket.deskripsi)
                    imageUrl = paket.imageUrl

                    Glide.with(this@EditPaketActivity)
                        .load(paket.imageUrl)
                        .into(imageMenuEdit)
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    private fun pickImageGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_REQUEST_CODE)
    }

    private fun saveEditedDataToDatabase() {
        name = editTextNamaEdit.text.toString().trim()
        harga = editTextHargaEdit.text.toString().toInt()
        deskripsi = editTextDeskripsiEdit.text.toString().trim()

        if (imageMenuEdit.drawable != null) {
            uploadImageToStorage(id, name, harga, deskripsi, (imageMenuEdit.drawable as BitmapDrawable).bitmap)
        } else {
            // Jika pengguna tidak memilih gambar baru, hanya perbarui data lainnya
            updateDataInDatabase(id, name, harga, deskripsi, imageUrl)
        }
    }

    private fun uploadImageToStorage(id: String, name: String, harga: Int, deskripsi: String, bitmap: Bitmap) {
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        val timestamp = System.currentTimeMillis()
        val imageName = "image_${id}_$timestamp.jpg"

        val imageReference = storageReference.child("images/$imageName")
        val uploadTask: UploadTask = imageReference.putBytes(data)

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
                val newImageUrl = downloadUri.toString()

                if (imageUrl.isNotEmpty()) {
                    deleteImageFromStorage(imageUrl) { success ->
                        if (success) {
                            updateDataInDatabase(id, name, harga, deskripsi, newImageUrl)
                        } else {
                            Toast.makeText(
                                this,
                                "Gagal menghapus gambar lama",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                } else {
                    updateDataInDatabase(id, name, harga, deskripsi, newImageUrl)
                }
            } else {
                val errorMessage = task.exception?.message ?: "Unknown error"
                Toast.makeText(this, "Gagal mengunggah gambar: $errorMessage", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun deleteImageFromStorage(imageUrl: String, callback: (Boolean) -> Unit) {
        val storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl)
        storageReference.delete()
            .addOnSuccessListener {
                callback(true)
            }
            .addOnFailureListener {
                callback(false)
            }
    }

    private fun updateDataInDatabase(id: String, name: String, harga: Int, deskripsi: String, imageUrl: String) {
        val menuModel = PaketModel(id, name, harga, deskripsi, imageUrl)
        databaseReference.child(id).setValue(menuModel)

        Toast.makeText(this, "Data berhasil diperbarui", Toast.LENGTH_SHORT).show()

        finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_REQUEST_CODE && resultCode == RESULT_OK) {
            val selectedImage: Uri? = data?.data
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedImage)
                imageMenuEdit.setImageBitmap(bitmap)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
