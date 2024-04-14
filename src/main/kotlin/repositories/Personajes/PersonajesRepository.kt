package org.example.repositories.Personajes

import org.example.dto.PersonajeDto
import org.example.mappers.toPersonaje
import org.example.mappers.toPersonajeDto
import org.example.models.Personaje
import org.example.repositories.crud.CrudRepository
import org.example.services.database.DatabaseManager
import org.lighthousegames.logging.logging
import java.sql.ResultSet

private val logger = logging()
class PersonajesRepository: CrudRepository<Personaje, String> {

    private fun ResultSet.toPersonaje(): Personaje{
        return PersonajeDto(
            nombre = getString("nombre"),
            tipo = getString("tipo"),
            habilidad = getString("habilidad"),
            ataque = getInt("ataque"),
            edad = getInt("edad"),
            arma = getString("arma"),
            isDeleted = getBoolean("is_Deleted")
        ).toPersonaje()
    }
    override fun findAll(): List<Personaje> {
        logger.debug { "Buscando todos los personajes" }
        val result = mutableListOf<Personaje>()
        DatabaseManager.use { db ->
            val sql = "SELECT * FROM personajes"
            val stmt = db.connection?.prepareStatement(sql)!!
            val res = stmt.executeQuery()
            while (res.next()) {
                result.add(res.toPersonaje())
            }
        }
        return result
    }

    override fun delete(nombre: String, logical: Boolean): Personaje? {
        logger.debug { "Borrando personaje con nombre: $nombre" }
        val result: PersonajeDto = this.getByName(nombre)?.toPersonajeDto() ?: return null
        DatabaseManager.use { db ->
            val sql = "DELETE FROM personajes WHERE nombre = ?"
            val stmt = db.connection?.prepareStatement(sql)!!.apply {
                setString(1, nombre)
            }
            val rs = stmt.executeUpdate()
            if(rs > 0){
                result.copy(isDeleted = true)
            }
        }
        return result.toPersonaje()
    }

    override fun update(name: String, item: Personaje): Personaje? {
        logger.debug { "Actualizando personaje $item" }
        var result: PersonajeDto = this.getByName(name)?.toPersonajeDto() ?: return null
        DatabaseManager.use { db ->
            val sql = "UPDATE personajes SET tipo = ?, habilidad = ?, ataque = ?, edad = ?, arma = ? WHERE nombre = ?"
            val stmt = db.connection?.prepareStatement(sql)!!.apply {
                setString(1, result.nombre)
                setString(2, result.tipo)
                setString(3, result.habilidad)
                setInt(4,result.ataque)
                setInt(5, result.edad)
                setString(6, result.arma)
                setBoolean(7, false)
            }
            val rs = stmt.executeUpdate()
            if (rs > 0){
                result = item.toPersonajeDto().copy(
                    nombre = result.nombre
                )
            }
        }
        return result.toPersonaje()
    }

    override fun save(item: Personaje): Personaje {
        logger.debug { "Guardando personaje $item" }
        var result: PersonajeDto = item.toPersonajeDto()
        DatabaseManager.use { db ->
            val sql = "INSERT INTO personajes (nombre, tipo, habilidad, ataque, edad, arma, is_deleted) VALUES (?, ?, ?, ?, ?, ?, ?)"
            val stmt = db.connection?.prepareStatement(sql)!!.apply {
                setString(1, result.nombre)
                setString(2, result.tipo)
                setString(3, result.habilidad)
                setInt(4,result.ataque)
                setInt(5, result.edad)
                setString(6, result.arma)
                setBoolean(7, false)
            }
            val rs = stmt.executeUpdate()
        }
        return result.toPersonaje()
    }

    override fun getByName(name: String): Personaje? {
        logger.debug { "Obteniendo personaje por nombre: $name" }
        var result: Personaje? = null
        DatabaseManager.use { db ->
            val sql = "SELECT * FROM personajes WHERE nombre = ?"
            val stmt = db.connection?.prepareStatement(sql)!!.apply {
                setString(1, "nombre")
            }
            val rs = stmt.executeQuery()
            if(rs.next()){
                result = rs.toPersonaje()
            }
        }
        return result
    }
}