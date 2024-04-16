package service

import com.github.michaelbull.result.onSuccess
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.example.exceptions.personajes.PersonajeError
import org.example.models.Enemigo
import org.example.models.Guerrero
import org.example.models.Personaje
import org.example.services.cache.personajes.PersonajesCache
import org.example.services.personajes.PersonajeServiceImpl
import org.example.services.storage.StoragePersonajesCsv
import org.example.services.storage.StoragePersonajesJson
import org.example.validators.PersonajeValidator
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.extension.ExtendWith
import repositories.PersonajesRepository
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@ExtendWith(MockKExtension::class)
class PersonajesServiceImpl {
    @MockK
    private lateinit var storagePersonajesCsv: StoragePersonajesCsv

    @MockK
    private lateinit var storagePersonajesJson: StoragePersonajesJson

    @MockK
    private lateinit var repo: org.example.repositories.Personajes.PersonajesRepository

    @MockK
    lateinit var personajeValidator: PersonajeValidator

    @MockK
    lateinit var personajesCache: PersonajesCache

    @InjectMockKs
    lateinit var personajesServiceImpl: PersonajeServiceImpl

    @Test
    fun findAll(){
        val mockPersonajes: List<Personaje> = listOf(
            Guerrero("PersonajeTest1","PersonajeExtra",10,30,"Extra1",false),
            Guerrero("PersonajeTest2","PersonajeExtra",10,20,"Extra2",false),
            Enemigo("PersonajeTest3","PersonajeExtra",10,30,"Extra3",false),
            Enemigo("PersonajeTest4","PersonajeExtra",10,40,"Extra4",false)
        )

        every { repo.findAll() } returns mockPersonajes
        val res = personajesServiceImpl.findAll().value

        assertAll(
            { assertEquals(4, res.size)},
            { assertEquals("PersonajeTest1", res[0].nombre)}
        )
        verify(exactly = 1) { repo.findAll() }
    }

    @Test
    fun getByName(){
        val mockPersonaje = Guerrero("PersonajeTest1","PersonajeExtra",10,30,"Extra1",false)
        personajesCache.put(mockPersonaje.nombre, mockPersonaje)
        every { repo.getByName(mockPersonaje.nombre) } returns mockPersonaje
        val res = personajesServiceImpl.getByName(mockPersonaje.nombre).value

        assertAll(
            { assertEquals("PersonajeTest1", res.nombre)},
            { assertEquals("PersonajeExtra", res.habilidad)},
            { assertEquals(10, res.ataque)}
        )
        verify(exactly = 1) { repo.getByName(mockPersonaje.nombre) }
    }

    @Test
    fun getByNameNotFound(){
        every { repo.getByName("PersonajeSinNombre") } returns null
        val error = personajesServiceImpl.getByName("PersonajeSinNombre").error
        assertAll(
            { assertTrue { error is PersonajeError.PersonajeNotFound }}
        )
        verify(exactly = 1) { repo.getByName("PersonajeSinNombre") }

    }
}