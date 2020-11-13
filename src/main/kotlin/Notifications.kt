import org.slf4j.LoggerFactory
import java.time.LocalDateTime
import java.util.concurrent.*
import kotlin.random.Random

object Notifications: INotifications {

    private val log = LoggerFactory.getLogger(this::class.java)

    private val executor = ThreadPoolExecutor(1, 1, 1, TimeUnit.DAYS, PriorityBlockingQueue())

    override fun addNotification(tenantId: Long, message: String, maxTime: LocalDateTime) {
        executor.execute(Notification(tenantId, message, maxTime))
    }


    private fun sendNotification(tenantId: Long, message: String, maxTime: LocalDateTime) {
        //Stub: instead of real sending a job is waiting for 05-5 seconds
        Thread.sleep(Random.nextLong(500, 5000))
        log.info("Send notification for $tenantId: $message")
    }

    private class Notification(
        val tenantId: Long,
        val message: String,
        val maxTime: LocalDateTime
    ): Comparable<Notification>, Runnable {

        override fun compareTo(other: Notification): Int {
            return maxTime.compareTo(other.maxTime)
        }

        override fun run() {
            sendNotification(tenantId, message, maxTime)
        }

    }
}