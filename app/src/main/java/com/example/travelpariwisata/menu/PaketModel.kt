package com.example.travelpariwisata.menu

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
class PaketModel(var id: String, var name: String, var harga: Int, var imageUrl: String){
    // Konstruktor tambahan untuk Firebase
    @JvmOverloads
    constructor() : this("", "", 0, "")
}