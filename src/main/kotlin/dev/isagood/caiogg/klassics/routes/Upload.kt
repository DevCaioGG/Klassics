package dev.isagood.caiogg.klassics.routes

import dev.isagood.caiogg.klassics.KlassicPage
import dev.isagood.caiogg.klassics.KlassicSession
import dev.isagood.caiogg.klassics.dao.Data
import dev.isagood.caiogg.klassics.routes.Login
import dev.isagood.caiogg.klassics.routes.respondDefaultHtml
import io.ktor.server.application.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.resources.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.resources.post
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import io.ktor.server.sessions.get
import kotlinx.coroutines.*
import kotlinx.html.*
import java.io.*

@Resource("/upload")
class Upload()

fun Route.upload(data: Data, uploadDir: File) {

    get<Upload> {
        val session = call.sessions.get<KlassicSession>()
        if (session == null) {
            call.respondRedirect(application.href(Login))
        } else {
            call.respondDefaultHtml(emptyList(), CacheControl.Visibility.Private) {
                section("vh-100 gradient-custom") {
                    div("container py-5 h-10") {
                        div("row d-flex justify-content-center align-items-center h-10") {
                            div("col-12 col-md-8 col-lg-6 col-xl-5") {
                                div("card bg-dark text-white") {
                                    style = "border-radius: 1rem;"
                                    div("card-body p-5 text-center") {
                                        form (
                                            call.application.href(Upload()),
                                            encType = FormEncType.multipartFormData,
                                            method = FormMethod.post) {
                                            h2("fw-bold mb-2 text-uppercase") { +"""Upload""" }
                                            p("text-white-50 mb-5") { +"""Introduce un Klassic""" }
                                            div("form-outline form-white mb-4") {
                                                label("form-label display-6") {
                                                    htmlFor = "title"; +"Título:"
                                                }
                                                textInput(classes = "form-control form-control-lg") {
                                                    name = "title"; id = "title"
                                                }
                                            }
                                            div("form-outline form-white mb-4") {
                                                label("form-label") {
                                                    htmlFor = "description"; +"Descripción:"
                                                }
                                                textArea(classes = "form-control") {
                                                    name = "description"; id = "description"
                                                    rows = "5"
                                                }
                                            }
                                            label("form-label") {
                                                htmlFor = "formFileMultiple"
                                                +"""Sube tu archivo"""
                                            }
                                            fileInput(classes = "form-control") {
                                                name = "file"
                                            }
                                            submitInput(classes = "btn btn-primary btn-lg btn-block mt-4") {
                                                value = "Upload"
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    post<Upload> {
        val session = call.sessions.get<KlassicSession>()
        if (session == null) {
            call.respond(HttpStatusCode.Forbidden.description("Not logged in"))
        } else {
            val multipart = call.receiveMultipart()
            var title = ""
            var description = ""
            var klassicFile: File? = null

            // Processes each part of the multipart input content of the user
            multipart.forEachPart { part ->
                if (part is PartData.FormItem) {
                    if (part.name == "title") {
                        title = part.value
                    }
                    if (part.name == "description") {
                        description = part.value
                    }
                } else if (part is PartData.FileItem) {
                    val ext = File(part.originalFileName.toString()).extension
                    val file = File(
                        uploadDir,
                        "upload-${System.currentTimeMillis()}-${session.userId.hashCode()}-${title.hashCode()}-${description.hashCode()}.$ext"
                    )

                    part.streamProvider().use { its -> file.outputStream().buffered().use { its.copyToSuspend(it) } }
                    klassicFile = file
                }

                part.dispose()
            }

            val id = data.addKlassic(title,description, session.userId, klassicFile!!)

            call.respondRedirect(application.href(KlassicPage(id)))
        }
    }
}

suspend fun InputStream.copyToSuspend(
    out: OutputStream,
    bufferSize: Int = DEFAULT_BUFFER_SIZE,
    yieldSize: Int = 4 * 1024 * 1024,
    dispatcher: CoroutineDispatcher = Dispatchers.IO
): Long {
    return withContext(dispatcher) {
        val buffer = ByteArray(bufferSize)
        var bytesCopied = 0L
        var bytesAfterYield = 0L
        while (true) {
            val bytes = read(buffer).takeIf { it >= 0 } ?: break
            out.write(buffer, 0, bytes)
            if (bytesAfterYield >= yieldSize) {
                yield()
                bytesAfterYield %= yieldSize
            }
            bytesCopied += bytes
            bytesAfterYield += bytes
        }
        return@withContext bytesCopied
    }
}
