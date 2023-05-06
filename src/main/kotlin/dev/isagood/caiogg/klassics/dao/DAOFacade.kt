package dev.isagood.caiogg.klassics.dao

import dev.isagood.caiogg.klassics.models.Klassic
import dev.isagood.caiogg.klassics.models.User
import java.io.File

interface DAOFacade {

    suspend fun getAll(): List<Klassic>

    suspend fun klassicById(id: Long): Klassic?

    suspend fun addKlassic(title: String, description: String, authorId: String, file: File): Long

    suspend fun newUser(username: String, password: String): Long

    suspend fun userExists(username: String): Boolean

    suspend fun users(): List<User>

}