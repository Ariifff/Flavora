package com.arif.adminflavora.model

data class PendingOrder(
    val userId: String? = null,
    val userName: String? = null,
    val totalPrice: Double? = null,
    val deliveryAddress: String? = null,
    val timestamp: Long? = null,
    val items: List<OrderItem>? = null,
    var key: String? = null,  // save push key
    var status: String? = null
)
