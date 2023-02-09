package dev.isagood.caiogg.klassics

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.http.*
import io.ktor.resources.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import kotlinx.html.*

@Resource("/login")
class Login(val userName: String = "", val password: String = "")

fun Route.login(users: UserHashedTableAuth) {
    val myFormAuthentication = "myFormAuthentication"

    application.install(Authentication) {
        form(myFormAuthentication) {
            userParamName = Login::userName.name
            passwordParamName = Login::password.name
            challenge { call.respondRedirect(call.application.href(Login(it?.name ?: ""))) }
            validate { users.authenticate(it) }
        }
    }

    resource<Login> {

        authenticate(myFormAuthentication) {
            post {
                val principal = call.principal<UserIdPrincipal>()
                call.sessions.set(KlassicSession(principal!!.name))
                call.respondRedirect(application.href(Index()))
            }
        }

        method(HttpMethod.Get) {
            handle<Login> {
                call.respondDefaultHtml(emptyList(), CacheControl.Visibility.Public) {
                    section("h-100 gradient-form") {
                        style = "background-color: #eee;"
                        div("container py-5 h-100") {
                            div("row d-flex justify-content-center align-items-center h-100") {
                                div("col-xl-10") {
                                    div("card rounded-3 text-black") {
                                        div("card-body p-md-5 mx-md-4") {
                                            div("text-center") {
                                                h4("mt-1 mb-5 pb-1") { +"""Somos el equipo Klassics""" }
                                            }
                                            form (
                                                call.application.href(Login()),
                                                encType = FormEncType.applicationXWwwFormUrlEncoded,
                                                method = FormMethod.post
                                            ){
                                                p { +"""Por favor haz login a tu cuenta""" }
                                                div("form-outline mb-4") {
                                                    label("form-label") {
                                                        +"""Usuario"""
                                                    }
                                                    telInput(classes = "form-control") {
                                                        type = InputType.text
                                                        placeholder = "root"
                                                        value = it.userName
                                                        name = Login::userName.name

                                                    }
                                                }
                                                div("form-outline mb-4") {
                                                    label("form-label") {
                                                        +"""Contraseña"""
                                                    }
                                                    passwordInput(classes = "form-control") {
                                                        type = InputType.password
                                                        placeholder = "root"
                                                        value = it.password
                                                        name = Login::password.name
                                                    }
                                                }
                                                div("text-center pt-1 mb-5 pb-1") {
                                                    submitInput(classes = "btn btn-primary btn-block fa-lg gradient-custom-2 mb-3") {
                                                        value = "Log in"

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
}

