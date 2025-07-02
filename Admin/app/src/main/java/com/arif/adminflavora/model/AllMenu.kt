package com.arif.adminflavora.model

data class AllMenu(
    val foodname: String?=null,
    val foodprice: String?=null,
    val foodimage: String? =null,
    val fooddiscription: String?=null,
    val active : Boolean=true,
    var key : String?=null,
    var maxQuantity: Int = 1

)
