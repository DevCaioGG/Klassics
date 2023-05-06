package dev.isagood.caiogg.klassics.dao

import dev.isagood.caiogg.klassics.dao.DatabaseFactory.dbQuery
import dev.isagood.caiogg.klassics.models.Klassic
import dev.isagood.caiogg.klassics.models.Klassics
import org.ehcache.Cache
import org.ehcache.CacheManager
import org.ehcache.config.builders.CacheConfigurationBuilder
import org.ehcache.config.builders.CacheManagerBuilder
import org.ehcache.config.builders.ResourcePoolsBuilder
import org.ehcache.config.units.EntryUnit
import org.jetbrains.exposed.sql.*
import java.io.File
import dev.isagood.caiogg.klassics.dao.DatabaseFactory.resultRowToKlassic
import dev.isagood.caiogg.klassics.dao.DatabaseFactory.resultRowToUser
import dev.isagood.caiogg.klassics.models.User
import dev.isagood.caiogg.klassics.models.Users

class DAOFacadeImpl : DAOFacade {

    private val cacheManager: CacheManager = CacheManagerBuilder.newCacheManagerBuilder().build(true)

    @Suppress("UNCHECKED_CAST")
    private val klassicsCache: Cache<Long, Klassic> = cacheManager.createCache(
        "klassics", CacheConfigurationBuilder.newCacheConfigurationBuilder(
            Class.forName("java.lang.Long") as Class<Long>,
            Klassic::class.java,
            ResourcePoolsBuilder.newResourcePoolsBuilder()
                .heap(10, EntryUnit.ENTRIES)
        )
    )

    override suspend fun getAll() = dbQuery { Klassics.selectAll().map { resultRowToKlassic(it) } }

    override suspend fun klassicById(id: Long): Klassic? {
        val klassic = klassicsCache.get(id)
        if (klassic != null) {
            return klassic
        }

        return dbQuery {
            Klassics.select { Klassics.id eq id }.mapNotNull { resultRowToKlassic(it) }.singleOrNull()
        }
    }
    override suspend fun addKlassic(title: String, description: String, userId: String, file: File): Long {

        val klassicId = dbQuery {
            Klassics.insert {
                it[Klassics.title] = title
                it[Klassics.description] = description
                it[Klassics.authorId] = userId
                it[Klassics.fileName] = file.path
            } get Klassics.id
        }


        val klassic = Klassic(
            id = klassicId,
            title = title,
            description = description,
            authorId = userId,
            fileName = file.path
        )
        klassicsCache.put(klassicId, klassic)
        return klassicId
    }

    override suspend fun newUser(username: String, password: String): Long = dbQuery {
        Users.insert {
            it[Users.username] = username
            it[Users.password] = password
        } get Users.id
    }

    override suspend fun userExists(username: String): Boolean = dbQuery {
        Users.select { Users.username eq username }.count() > 0
    }

    override suspend fun users(): List<User> = dbQuery {
        Users.selectAll().map { resultRowToUser(it) }
    }
}