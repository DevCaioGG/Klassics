package dev.isagood.caiogg.klassics

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.LongSerializationPolicy
import org.ehcache.Cache
import org.ehcache.CacheManager
import org.ehcache.config.builders.CacheConfigurationBuilder
import org.ehcache.config.builders.CacheManagerBuilder
import org.ehcache.config.builders.ResourcePoolsBuilder
import org.ehcache.config.units.EntryUnit
import java.io.File
import java.util.concurrent.atomic.AtomicLong

class Database(private val uploadDir: File) {

    private val gson: Gson =
        GsonBuilder().disableHtmlEscaping().serializeNulls().setLongSerializationPolicy(LongSerializationPolicy.STRING)
            .create()

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

    private val digitsOnlyRegex = "\\d+".toRegex()
    private val allIds by lazy {
        uploadDir.listFiles { f -> f.extension == "idx" && f.nameWithoutExtension.matches(digitsOnlyRegex) }
            .mapTo(ArrayList()) { it.nameWithoutExtension.toLong() }
    }

    private val biggestId by lazy { AtomicLong(allIds.maxOrNull() ?: 0) }

    private fun listAll(): Sequence<Klassic> = allIds.asSequence().mapNotNull { klassicById(it) }

    fun getAll() = listAll().toList()

    fun klassicById(id: Long): Klassic? {
        val klassic = klassicsCache.get(id)
        if (klassic != null) {
            return klassic
        }

        return try {
            val json = gson.fromJson(File(uploadDir, "$id.idx").readText(), Klassic::class.java)
            klassicsCache.put(id, json)

            json
        } catch (e: Throwable) {
            null
        }
    }

    private fun nextId() = biggestId.incrementAndGet()

    fun addKlassic(title: String, description: String, userId: String, file: File): Long {
        val id = nextId()
        val klassic = Klassic(id, title,description, userId, file.path)

        File(uploadDir, "$id.idx").writeText(gson.toJson(klassic))
        allIds.add(id)

        klassicsCache.put(id, klassic)

        return id
    }
}