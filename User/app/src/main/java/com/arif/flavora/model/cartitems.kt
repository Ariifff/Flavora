package com.arif.flavora.model

data class cartitems(
    val foodName: String? = null,
    val foodPrice: String? = null,
    val foodImage: String? = null,
    var foodquantity : Int = 1,
    var key: String? = null // 🔑 Add this to store Firebase push key
)
