package ru.itmo.SOA_Backend_Lab1.Model

class QueryAdditions(
    val table: TablesQueryAdditions,
    val column:String,
    val firstValue:String? = null,
    val secondValue:String? = null,
    val sortType:Boolean = true // asc == true, desc == false
) {
}