package dev.isagood.caiogg.klassics.dao

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.LongSerializationPolicy
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

    suspend fun getAll() = dbQuery { Klassics.selectAll().map { resultRowToKlassic(it) } }

    suspend fun klassicById(id: Long): Klassic? {
        val klassic = klassicsCache.get(id)
        if (klassic != null) {
            return klassic
        }

        return dbQuery {
            Klassics.select { Klassics.id eq id }.mapNotNull { resultRowToKlassic(it) }.singleOrNull()
        }
    }
    suspend fun addKlassic(title: String, description: String, userId: String, file: File): Long {

        val klassicId = dbQuery {
            Klassics.insert {
                it[Klassics.title] = title
                it[Klassics.description] = description
                it[Klassics.authorId] = authorId
                it[Klassics.fileName] = file.name
            } get Klassics.id
        }


        val klassic = Klassic(
            id = klassicId,
            title = title,
            description = description,
            authorId = userId,
            fileName = file.name
        )
        klassicsCache.put(klassicId, klassic)
        return klassicId
    }

}