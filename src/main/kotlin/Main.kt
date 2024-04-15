package org.example

import com.github.michaelbull.result.onFailure
import com.github.michaelbull.result.onSuccess
import org.example.models.Enemigo
import org.example.models.Guerrero
import org.example.repositories.Personajes.PersonajesRepository
import org.example.services.cache.personajes.PersonajesCache
import org.example.services.personajes.PersonajeServiceImpl
import org.example.services.storage.StoragePersonajesCsv
import org.example.services.storage.StoragePersonajesJson
import org.example.validators.PersonajeValidator

fun main() {
    val personajesService = PersonajeServiceImpl(
        storageCsv = StoragePersonajesCsv(),
        storageJson = StoragePersonajesJson(),
        personajesRepository = PersonajesRepository(),
        personajesCache = PersonajesCache(),
        personajeValidator = PersonajeValidator()
    )
    val personajesInit = personajesService.loadFromCsv()
    personajesService.storeFromJsom(personajesInit)
    val personajes = personajesService.loadFromJsom()
    personajes.forEach {
        personajesService.save(it)
    }
    personajesService.findAll()
        .onSuccess { personajes ->
            personajes.forEach { println(it) }
        }.onFailure { println("Error: ${it.message}") }

    println("Tres personajes más mayores")
    personajes.sortedByDescending { it.edad }.take(3).forEach { println(it) }
    println()

    println("Agrupar personajes por tipo de arma")
    personajes.groupBy { it.arma }.forEach{ println(it)}
    println()

    println("Personajes menores de edad")
    personajes.filter { it.edad < 18 }.forEach { println(it) }
    println()

    println("Personajes ordenados según su ataque")
    personajes.sortedByDescending { it.ataque }.forEach { println(it) }
    println()

    println("Mostrar a todos los enemigos")
    personajes.filterIsInstance<Enemigo>().forEach { println(it) }
    println()

    println("Dos personajes con el ataque más fuerte")
    personajes.sortedByDescending { it.ataque }.take(2).forEach { println(it) }

}