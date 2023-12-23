package com.example.travelpariwisata.menu

import com.google.firebase.database.IgnoreExtraProperties
import java.io.Serializable

@IgnoreExtraProperties
class PaketModel(var id: String, var name: String, var harga: Int, var deskripsi: String, var imageUrl: String) :
    Serializable {

    // Konstruktor tambahan untuk Firebase
    @JvmOverloads
    constructor() : this("", "", 0, "", "")
}