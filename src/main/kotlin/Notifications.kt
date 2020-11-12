import java.time.LocalDateTime

object Notifications {

    fun sendNotification(tenantId: Long, message: String) {
        sendNotification(tenantId, message, null)
    }

    fun sendNotification(tenantId: Long, message: String, maxTime: LocalDateTime?) {
        //TODO
    }
}