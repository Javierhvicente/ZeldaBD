package org.example.services.personajes

import com.github.michaelbull.result.Result
import org.example.exceptions.personajes.PersonajeError
import org.example.models.Personaje

interface PersonajesService {
    fun loadFromCsv():List<Personaje>
    fun storeFromCsv(personajes: List<Personaje>)
    fun loadFromJsom():List<Personaje>
    fun storeFromJsom(personajes: List<Personaje>)
    fun findAll(): Result<List<Personaje>, PersonajeError>
    fun getByName(name: String): Result<Personaje, PersonajeError>
    fun update(name: String, item: Personaje): Result<Personaje, PersonajeError>
    fun save(item: Personaje):Result<Personaje, PersonajeError>
    fun delete(name: String, logical: Boolean = false): Result<Personaje, PersonajeError>
}