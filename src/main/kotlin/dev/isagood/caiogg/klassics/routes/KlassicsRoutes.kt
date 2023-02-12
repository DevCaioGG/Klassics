package dev.isagood.caiogg.klassics.routes

import dev.isagood.caiogg.klassics.*
import dev.isagood.caiogg.klassics.dao.Data
import io.ktor.server.application.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.http.content.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import io.ktor.server.sessions.get
import kotlinx.html.*
import java.io.*

fun Route.klassics(data: Data) {

    get<Index> {
        val session = call.sessions.get<KlassicSession>()
        val klassics = data.getAll()
        val etag =
            klassics.joinToString { "${it.id},${it.title},${it.description}" }.hashCode().toString() + "-" + session?.userId?.hashCode()
        val visibility = if (session == null) CacheControl.Visibility.Public else CacheControl.Visibility.Private

        call.respondDefaultHtml(listOf(EntityTagVersion(etag)), visibility) {
            section("py-5 text-center container") {
                div("row py-lg-5") {
                    div("col-lg-6 col-md-8 mx-auto") {
                        h1("fw-light") { +"""¡Échale un vistazo a nuestros vehículos!""" }
                        p("lead text-muted") { +"""Nuestro catálogo de vehículos es amplio y diverso, ofreciendo a nuestros clientes una amplia selección de opciones para elegir.Todos nuestros vehículos han sido cuidadosamente restaurados para asegurar su belleza y funcionamiento óptimo.""" }
                        p{
                            a(classes = "btn btn-primary my-2") {
                                href = application.href(Index())
                                +"""Cátalogo"""
                            }
                            if (session ==  null){
                                a(classes = "btn btn-secondary my-2") {
                                    href = application.href(Login())
                                    +"""Inicia Sesión"""
                                }
                            } else {
                                a(classes = "btn btn-secondary my-2") {
                                    href = application.href(Upload())
                                    +"""Upload"""
                                }
                            }
                        }
                    }
                }
            }
            div("album py-5 bg-light") {
                div("container") {
                    div("row row-cols-1 row-cols-sm-2 row-cols-md-3 g-3") {
                        klassics.forEach {
                            div("col") {
                                div("card shadow-sm") {
                                    img(classes = "bd-placeholder-img card-img-top") {
                                        src = call.application.href(KlassicStream(it.id))
                                    }
                                    div("card-body") {
                                        p("card-text") { +it.title }
                                        div("d-flex justify-content-between align-items-center") {
                                            div("btn-group") {
                                                a {
                                                    href = application.href(KlassicPage(it.id))
                                                    button(classes = "btn btn-sm btn-outline-secondary") {
                                                        +"""View"""
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
        }
    }

    get<KlassicPage> {
        val klassic = data.klassicById(it.id)

        if (klassic == null) {
            call.respond(HttpStatusCode.NotFound.description("Klassic ${it.id} doesn't exist"))
        } else {
            call.respondDefaultHtml(
                listOf(EntityTagVersion(klassic.hashCode().toString())),
                CacheControl.Visibility.Public
            ) {
                div("vh-100 row justify-content-center align-items-center") {
                    div("card shadow-sm w-50 d-inline-block") {
                        img(classes = "bd-placeholder-img card-img-top mt-3") {
                            src = call.application.href(KlassicStream(it.id))
                        }
                        div("card-body") {
                            p("card-text") { +klassic.title }
                            div("d-flex justify-content-between align-items-center") {
                                p ("card-text"){
                                    +klassic.description
                                }
                                div("btn-group") {
                                    a {
                                        href = application.href(KlassicPage(it.id))
                                        button(classes = "btn btn-sm btn-outline-secondary") {
                                            +"""View"""
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

    get<KlassicStream> {
        val klassic = data.klassicById(it.id)

        if (klassic == null) {
            call.respond(HttpStatusCode.NotFound)
        } else {
            val type = ContentType.fromFilePath(klassic.fileName).first { type -> type.contentType == "image" }
            call.respond(LocalFileContent(File(klassic.fileName), contentType = type))
        }
    }
}
