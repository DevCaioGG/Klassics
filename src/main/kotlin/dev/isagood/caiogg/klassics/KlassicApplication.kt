package dev.isagood.caiogg.klassics

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
    val database = Database(uploadDir)

    val users = UserHashedTableAuth(
        getDigestFunction("SHA-256") { "ktor${it.length}" },
        table = mapOf(
            "root" to Base64.getDecoder().decode("76pc9N9hspQqapj30kCaLJA14O/50ptCg50zCA1oxjA=") // sha256 for "root"
        )
    )

    install(Sessions) {
        cookie<KlassicSession>("SESSION") {
            transform(SessionTransportTransformerMessageAuthentication(sessionKey))
        }
    }

    routing {
        login(users)
        upload(database, uploadDir)
        klassics(database)
        styles()
        scripts()
    }
}
