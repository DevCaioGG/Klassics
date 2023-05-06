package dev.isagood.caiogg.klassics.dao

import dev.isagood.caiogg.klassics.models.Klassic
import dev.isagood.caiogg.klassics.models.Klassics
import dev.isagood.caiogg.klassics.models.User
import dev.isagood.caiogg.klassics.models.Users
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File

object DatabaseFactory {
    val data = Database.connect(
        url = "jdbc:h2:file:./build/db" ,
        driver = "org.h2.Driver",
        user = "root",
        password = "root" )
    fun init() {
        transaction(data) {
            SchemaUtils.create(Klassics)
            SchemaUtils.create(Users)
        }
    }

    fun resultRowToKlassic(row: ResultRow) = Klassic(
        id = row[Klassics.id],
        title = row[Klassics.title],
        description = row[Klassics.description],
        authorId = row[Klassics.authorId],
        fileName = row[Klassics.fileName]
    )

    fun resultRowToUser(row: ResultRow) = User(
        id = row[Users.id],
        username = row[Users.username],
        password = row[Users.password]
    )

    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }
}