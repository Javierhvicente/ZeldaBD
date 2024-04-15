package org.example.services.personajes

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.andThen
import org.example.exceptions.personajes.PersonajeError
import org.example.models.Personaje
import org.example.repositories.Personajes.PersonajesRepository
import org.example.services.cache.personajes.PersonajesCache
import org.example.services.storage.Storage
import org.example.validators.PersonajeValidator
import org.lighthousegames.logging.logging

private val logger = logging()
class PersonajeServiceImpl(
    private val storageCsv: Storage<Personaje>,
    private val storageJson: Storage<Personaje>,
    private val personajesRepository: PersonajesRepository,
    private val personajesCache: PersonajesCache,
    private val personajeValidator: PersonajeValidator
): PersonajesService {
    override fun loadFromCsv(): List<Personaje> {
        logger.debug { "Cargando personajes desde CSV" }
        return storageCsv.load("personajes.csv")
    }

    override fun storeFromCsv(personajes: List<Personaje>) {
        logger.debug { "Guardando personajes en CSV" }
        storageCsv.store(personajes)
    }

    override fun loadFromJsom(): List<Personaje> {
        logger.debug { "Cargando personajes desde JSON" }
        return storageJson.load("personajes-back.json")
    }

    override fun storeFromJsom(personajes: List<Personaje>) {
        logger.debug { "Guardando personajes en JSON" }
        storageJson.store(personajes)
    }

    override fun findAll(): Result<List<Personaje>, PersonajeError> {
        logger.debug { "Buscando todos los personajes " }
        return Ok(personajesRepository.findAll())
    }

    override fun getByName(name: String): Result<Personaje, PersonajeError> {
        logger.debug { "Buscando personaje por nombre: $name" }
        return personajesRepository.getByName(name)
            ?.let { Ok(it) }
            ?: Err(PersonajeError.PersonajeNotFound("No se ha encontrado el personaje con nombre $name"))
    }

    override fun update(name: String, item: Personaje): Result<Personaje, PersonajeError> {
        logger.debug { "Actualizando personaje con nombre: $name" }
        return personajeValidator.validate(item)
            .andThen { Ok(personajesRepository.update(name, item))  }
            .andThen { Ok(personajesCache.put(item.nombre, item)) }
            .andThen { Err(PersonajeError.PersonajeNotFound("No se ha actualizado al personaje con nombre: $name")) }
    }

    override fun save(item: Personaje): Result<Personaje, PersonajeError> {
        logger.debug { "Guardando personaje $item" }
        return personajeValidator.validate(item)
            .andThen { Ok(personajesCache.put(item.nombre, item)) }
            .andThen { Ok(personajesRepository.save(item))}
            .andThen { Err(PersonajeError.PersonajeInvalido("No se ha podido guardar al personaje con nombre ${item.nombre}")) }

    }

    override fun delete(name: String): Result<Personaje, PersonajeError> {
        logger.debug { "Borrando personaje con nombre $name" }
        return personajesRepository.delete(name)
            ?.let { Ok(it).also { personajesCache.remove(name) } }
            ?: Err(PersonajeError.PersonajeNotDeleted("No se ha podido borrar el personaje con nombre $name"))
    }
}