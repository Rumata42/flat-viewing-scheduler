import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import java.time.LocalDateTime
import java.time.format.DateTimeParseException

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    routing {
        route("/flat/{id}") {
            get("/slots") {
                val id = call.parameters["id"]!!.toLong()
                val flat = Flats.getFlat(id)
                if (flat == null) {
                    call.respond(HttpStatusCode.NotFound)
                } else {
                    call.respondText(
                        flat.getSlots()
                            .map { (dateTime, slot) -> "{\"dateTime\": \"$dateTime\", \"tenantId\": ${slot.tenantId}, \"status\": \"${slot.status}\"}" }
                            .toString(),
                        ContentType.Application.Json
                    )
                }
            }
            post("/reserve") {
                processSlot(call) { flat, dateTime, tenantId ->
                    flat.reserveSlot(dateTime, tenantId)
                }
            }
            post("/accept") {
                processSlot(call) { flat, dateTime, tenantId ->
                    flat.acceptSlot(dateTime, tenantId)
                }
            }
            post("/reject") {
                processSlot(call) { flat, dateTime, tenantId ->
                    flat.rejectSlot(dateTime, tenantId)
                }
            }
        }
    }
}

private suspend fun processSlot(call: ApplicationCall, consumer: (Flat, LocalDateTime, Long) -> Unit) {
    val id = call.parameters["id"]!!.toLong()
    val flat = Flats.getFlat(id)
    if (flat == null) {
        call.respond(HttpStatusCode.NotFound)
    } else {
        val tenantId = call.parameters["tenantId"]?.toLong()
        val dateTime = call.parameters["dateTime"]
        if (tenantId == null || dateTime == null) {
            call.respond(HttpStatusCode.BadRequest)
        } else {
            try {
                consumer(flat, LocalDateTime.parse(dateTime), tenantId)
                call.respond(HttpStatusCode.OK)
            } catch (e: DateTimeParseException) {
                call.respond(HttpStatusCode.BadRequest, "Incorrect dateTime format")
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest, e.message ?: "")
            } catch (e: InsufficientPermissionsException) {
                call.respond(HttpStatusCode.Forbidden, e.message)
            }
        }
    }
}