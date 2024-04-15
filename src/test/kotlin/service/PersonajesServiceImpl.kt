package service

import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.example.models.Enemigo
import org.example.models.Guerrero
import org.example.models.Personaje
import org.example.services.cache.personajes.PersonajesCache
import org.example.services.storage.StoragePersonajesCsv
import org.example.services.storage.StoragePersonajesJson
import org.example.validators.PersonajeValidator
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import repositories.PersonajesRepository

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
    lateinit var personajesServiceImpl: PersonajesServiceImpl

    @Test
    fun findAll(){
        val personajes: List<Personaje> = listOf(
            Guerrero("PersonajeExtra1","PersonajeExtra",10,30,"Extra1",false),
            Guerrero("PersonajeExtra2","PersonajeExtra",10,20,"Extra2",false),
            Enemigo("PersonajeExtra3","PersonajeExtra",10,30,"Extra3",false),
            Enemigo("PersonajeExtra4","PersonajeExtra",10,40,"Extra4",false)
        )

        every { repo.findAll() } returns personajes
    }
}