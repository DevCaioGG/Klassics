package dev.isagood.caiogg.klassics.models

import org.jetbrains.exposed.sql.Table

object Klassics : Table(){
    val id = long("id").autoIncrement()
    val title = varchar("title", 128)
    val description = varchar("description", 2048)
    val authorId = varchar("authorId", 128)
    val fileName = varchar("fileName", 2000)

    override val primaryKey = PrimaryKey(id)
}