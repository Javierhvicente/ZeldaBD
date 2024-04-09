package org.example

import org.example.repositories.Personajes.PersonajesRepository
import org.example.services.cache.personajes.PersonajesCache
import org.example.services.personajes.PersonajeServiceImpl
import org.example.services.storage.StoragePersonajesCsv
import org.example.services.storage.StoragePersonajesJson

fun main() {
    val personajesService = PersonajeServiceImpl(
        storageCsv = StoragePersonajesCsv(),
        storageJson = StoragePersonajesJson(),
        personajesRepository = PersonajesRepository(),
        personajesCache = PersonajesCache()
    )
    val personajes = personajesService.loadFromCsv()
    personajes.forEach {
        personajesService.save(it)
    }
    personajesService.findAll().forEach {
        println(it)
    }
}