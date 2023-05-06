package dev.isagood.caiogg.klassics.routes

import dev.isagood.caiogg.klassics.KlassicSession
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.html.HtmlContent
import io.ktor.server.plugins.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.server.sessions.*
import io.ktor.util.date.*
import kotlinx.html.*

@Resource("/")
class Index()

suspend fun ApplicationCall.respondDefaultHtml(
    versions: List<Version>,
    visibility: CacheControl.Visibility,
    title: String = "Klassic",
    block: DIV.() -> Unit
) {
    val content = HtmlContent(HttpStatusCode.OK) {
        val session = sessions.get<KlassicSession>()
        lang = "en"
        head {
            title { +title }
            styleLink(request.origin.run {
                "$scheme://$host:$port${application.href(MainCss())}"
            })

        }
        body {
            header {
                div("collapse bg-dark") {
                    id = "navbarHeader"
                    div("container") {
                        div("row") {
                            div("col-sm-8 col-md-7 py-4") {
                                h4("text-white") { +"""About""" }
                                p("text-white") { +"""Somos una empresa que se especializa en alquilar vehículos antiguos para bodas, eventos corporativos, fotografías, filmaciones y cualquier ocasión especial. Tenemos una amplia variedad de vehículos que van desde clásicos americanos hasta autos europeos de lujo. Cada uno de nuestros vehículos ha sido cuidadosamente restaurado para asegurar que luzcan y funcionen como nuevos. Nos enorgullece ofrecer un servicio excepcional a nuestros clientes y garantizar que sus recuerdos sean únicos y especiales. ¡Deja que nos encarguemos de la transportación en tu próximo evento especial!""" }
                            }
                            div("col-sm-4 offset-md-1 py-4") {
                                h4("text-white") { +"""Contact""" }
                                ul("list-unstyled") {
                                    li {
                                        a(classes = "text-white") {
                                            href = "mailto:caio.ganais.7e5@itb.cat"
                                            +"""Email me"""
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                div("navbar navbar-dark bg-dark shadow-sm") {
                    div("container") {
                        a(classes = "navbar-brand d-flex align-items-center") {
                            href = application.href(Index())

                            strong { +"""ㅤKlassics""" }
                        }
                        button(classes = "navbar-toggler") {
                            attributes["data-bs-toggle"] = "collapse"
                            attributes["data-bs-target"] = "#navbarHeader"
                            attributes["aria-controls"] = "navbarHeader"
                            attributes["aria-expanded"] = "false"
                            attributes["aria-label"] = "Toggle navigation"
                            span("navbar-toggler-icon") {
                            }
                        }
                    }
                }
            }
            main {
                div {
                    block()
                }
            }
            footer("text-muted py-5") {
                div("container") {
                    p("float-end mb-1") {
                        a {
                            href = "#"
                            +"""Vuelve arriba"""
                        }
                    }
                    p("mb-1") { +"""©Klassics""" }
                    p("mb-0") {
                        +"""Programado porㅤ"""
                        a {
                            href = "https://caiogg.is-a-good.dev/"
                            +"""Caio Ganais"""
                        }
                    }
                }
            }

            script(src= "/scripts/main.js"){}
        }
    }
    content.versions = versions
    content.caching = CachingOptions(
        cacheControl = CacheControl.MaxAge(
            3600 * 24 * 7,
            mustRevalidate = true,
            visibility = visibility,
            proxyMaxAgeSeconds = null,
            proxyRevalidate = false
        ),
        expires = (null as? GMTDate?)
    )
    respond(content)
}


