package dev.isagood.caiogg.klassics

import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {
    val database = Database.connect(url = "jdbc:h2:file:./build/db" , driver = "org.h2.Driver", user = "root",
        password = "root" )
    fun init() {
        transaction(database) {
            SchemaUtils.create()
        }
    }

    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }
}