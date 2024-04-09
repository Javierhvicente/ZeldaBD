package org.example.models

abstract class Personajes(
    val nombre:String,
    val habilidad:String,
    val ataque:Int,
    val edad:Int,
    val arma:String,
    val isDeleted:Boolean?
) {
}