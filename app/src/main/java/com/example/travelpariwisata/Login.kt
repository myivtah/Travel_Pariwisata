package com.example.travelpariwisata

import java.util.Objects

class Login {
    var username = ""
    var password = ""

    fun loginCheck(): Boolean {
        if (Objects.equals(username, "miftah") && Objects.equals(password, "amikom")) {
            return true
        } else {
            return false
        }
    }
}