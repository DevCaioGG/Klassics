package dev.isagood.caiogg.klassics.models

import dev.isagood.caiogg.klassics.models.Klassics.autoIncrement
import org.jetbrains.exposed.sql.Table

object Users : Table(){
    val id = long("id").autoIncrement()
    val username = varchar("username", 128)
    val password = varchar("password", 2048)

    override val primaryKey = PrimaryKey(id)
}