package dev.isagood.caiogg.klassics

import dev.isagood.caiogg.klassics.dao.DAOFacade
import dev.isagood.caiogg.klassics.dao.DAOFacadeImpl
import dev.isagood.caiogg.klassics.dao.DatabaseFactory
import dev.isagood.caiogg.klassics.models.Users
import dev.isagood.caiogg.klassics.routes.*
import io.ktor.http.*
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.plugins.callloging.*
import io.ktor.server.plugins.compression.*
import io.ktor.server.plugins.conditionalheaders.*
import io.ktor.server.plugins.defaultheaders.*
import io.ktor.server.plugins.partialcontent.*
import io.ktor.server.resources.*
import io.ktor.server.resources.Resources
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import io.ktor.util.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.selectAll
import java.io.File
import java.io.IOException
import java.util.*

@Resource("/klassic/{id}")
class KlassicStream(val id: Long)

@Resource("/klassic/page/{id}")
class KlassicPage(val id: Long)

data class KlassicSession(val userId: String)

fun Application.main() {

    install(DefaultHeaders)
    install(CallLogging)
    install(Resources)
    install(ConditionalHeaders)
    install(PartialContent)
    install(Compression) {
        default()
        excludeContentType(ContentType.Image.Any)
    }

    val klassicConfig = environment.config.config("klassic")
    val sessionCookieConfig = klassicConfig.config("session.cookie")
    val key: String = sessionCookieConfig.property("key").getString()
    val sessionKey = hex(key)

    val uploadDirPath: String = klassicConfig.property("upload.dir").getString()
    val uploadDir = File(uploadDirPath)
    if (!uploadDir.mkdirs() && !uploadDir.exists()) {
        throw IOException("Failed to create directory ${uploadDir.absolutePath}")
    }

    DatabaseFactory.init()
    val dao: DAOFacade = DAOFacadeImpl()

    val usersList = runBlocking {
        //Get Hash run: echo -n caio | openssl dgst -binary -sha256 | openssl base64
        dao.newUser("root", "root")
        dao.newUser("caio", "caio")
        dao.newUser("dani", "dani")
        dao.users()
    }

    val digestFunction = getDigestFunction("SHA-256") { "ktor${it.length}" }

    val users = UserHashedTableAuth(
        digester = digestFunction,
        table = usersList.associate {
            it.username to digestFunction(it.password)
        }
    )



    install(Sessions) {
        cookie<KlassicSession>("SESSION") {
            transform(SessionTransportTransformerMessageAuthentication(sessionKey))
        }
    }

    routing {
        login(users)
        upload(dao, uploadDir)
        klassics(dao)
        styles()
        scripts()
    }

}
