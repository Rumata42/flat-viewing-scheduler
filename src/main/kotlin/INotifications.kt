import java.time.LocalDateTime

@FunctionalInterface
fun interface INotifications {

    fun addNotification(tenantId: Long, message: String, maxTime: LocalDateTime)

}