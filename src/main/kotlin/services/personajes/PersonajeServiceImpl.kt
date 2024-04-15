package org.example.services.personajes

import com.github.michaelbull.result.*
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
        return personajesCache.get(name).mapBoth(
            success = {
                logger.debug { "Personaje encontrado en la cache" }
                Ok(it)
            },
            failure = {
                logger.debug { "Personaje no encontrado en la cache" }
                personajesRepository.getByName(name)
                    ?.let { Ok(it) }
                    ?: Err(PersonajeError.PersonajeNotFound("No se ha encontrado personaje con nombre: $name"))
            }
        )
    }

    override fun update(name: String, item: Personaje): Result<Personaje, PersonajeError> {
        logger.debug { "Actualizando personaje con nombre: $name" }
        return personajeValidator.validate(item).andThen {  p ->
            personajesRepository.update(p.nombre, p)
                ?.let { Ok(it) }
                ?: Err(PersonajeError.PersonajeNotUpdated("No se ha podido actualizar el personaje con nombre: $name"))
        }.andThen {
            personajesCache.put(name, item)
        }
    }

    override fun save(item: Personaje): Result<Personaje, PersonajeError> {
        logger.debug { "Guardando personaje $item" }
        return personajeValidator.validate(item).andThen {
            Ok(personajesRepository.save(it))
        }.andThen { p ->
            personajesCache.put(p.nombre, p )
        }

    }

    override fun delete(name: String): Result<Personaje, PersonajeError> {
        logger.debug { "Borrando personaje con nombre $name" }
        return personajesRepository.delete(name)
            ?.let {
                personajesCache.remove(name)
                Ok(it)
            }
            ?: Err(PersonajeError.PersonajeNotDeleted("No se ha podido borrar el personaje con nombre $name"))
    }
}