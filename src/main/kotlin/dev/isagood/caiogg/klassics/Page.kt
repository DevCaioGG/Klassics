package dev.isagood.caiogg.klassics

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

//                            svg("") {
//                               attributes["width"] = "55"
//                                attributes["height"]  = "55"
//                                attributes["x"]  = "0"
//                                attributes["y"]  = "0"
//                                attributes["viewbox"]  = "0 0 60 60"
//                                attributes["g"] = {
//                                    attributes["path"] = {
//                                        attributes["d"] = "M59.3 34.628c.028-.032.056-.065.067-.08a2.988 2.988 0 0 0-.981-4.5A9.711 9.711 0 0 0 54 29c-.336 0-.669.022-1 .055V27a3 3 0 0 0-3-3H33.086l-.895-3.4A1.992 1.992 0 0 0 33 19v-1a3 3 0 0 0-3-3H12a5.006 5.006 0 0 0-5 5v6.062a3.01 3.01 0 0 0-.762.274A2.987 2.987 0 0 0 .775 27.7l-.76 7.6a2.986 2.986 0 0 0 .662 2.2 2.948 2.948 0 0 0 1.985 1.08A2.4 2.4 0 0 0 3 38.6a2.988 2.988 0 0 0 2.334-1.117A3 3 0 0 0 7 38h.09a5.994 5.994 0 1 0 11.82 2h29.18a6 6 0 1 0 10.556-4.791 2.858 2.858 0 0 0 .654-.581ZM50 26a1 1 0 0 1 1 1v2.469a10.01 10.01 0 0 0-4.679 3.121A3.714 3.714 0 0 1 43.5 34h-7.782l-2.105-8Zm-35 0v-4a1 1 0 0 1 1-1h6v5Zm9-5h6.229l1.316 5H24Zm-12-4h18a1 1 0 0 1 1 1v1H16a3 3 0 0 0-3 3v4H9v-6a3 3 0 0 1 3-3ZM3 36.6h-.1a1 1 0 0 1-.895-1.1l.761-7.6a.979.979 0 0 1 1.094-.893 1 1 0 0 1 .9 1.055 3 3 0 0 0-.141.64l-.6 6a2.981 2.981 0 0 0 .011.668L4 35.7a.993.993 0 0 1-1 .9ZM13 43a4 4 0 1 1 4-4 4 4 0 0 1-4 4Zm0-10a6 6 0 0 0-5.188 3H7a1 1 0 0 1-1-1.1l.6-6a.994.994 0 0 1 1-.9h24.471l2.177 8.272A4.961 4.961 0 0 0 35.1 38H18.91A6.006 6.006 0 0 0 13 33Zm35.09 5h-9.006a3 3 0 0 1-2.818-2H43.5a5.7 5.7 0 0 0 4.356-2.128A8 8 0 0 1 54 31a7.689 7.689 0 0 1 3.475.832 1.009 1.009 0 0 1 .316 1.481.924.924 0 0 1-1.086.339A5.98 5.98 0 0 0 48.09 38ZM54 43a4 4 0 1 1 4-4 4 4 0 0 1-4 4Z"
//                                        attributes["fill"] = "#ffffff"
//                                        attributes["data-original"] = "#ffffff"
//
//                                    }
//                                }
//                            }
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


