import java.time.LocalDateTime
import java.util.concurrent.ConcurrentHashMap

class Flat(
    val id: Long,
    private val ownerId: Long,
    private val notifications: INotifications
) {

    private val slots = ConcurrentHashMap<LocalDateTime, Slot>()


    fun getSlots(): Map<LocalDateTime, Slot> {
        return slots
    }

    fun reserveSlot(dateTime: LocalDateTime, tenantId: Long) {
        checkDateTime(dateTime)
        val previous = slots.putIfAbsent(dateTime, Slot(tenantId, SlotStatus.REQUESTED))
        if (previous != null) {
            throw IllegalArgumentException("This slot is already reserved")
        }
        notifications.addNotification(tenantId, "New request: flat $id, time: $dateTime", dateTime.minusDays(1))
    }

    fun acceptSlot(dateTime: LocalDateTime, tenantId: Long) {
        checkDateTime(dateTime)
        if (tenantId != ownerId) {
            throw InsufficientPermissionsException("Only owner of the flat can accept the time reservation")
        }
        slots.compute(dateTime) { _, slot ->
            if (slot == null) {
                throw IllegalArgumentException("There is no request for reservation for that time")
            } else if (slot.status != SlotStatus.REQUESTED) {
                throw IllegalArgumentException("That request has been already processed")
            }
            slot.withNewStatus(SlotStatus.ACCEPTED)
        }
        notifications.addNotification(tenantId, "Request: flat $id, time: $dateTime - has been accepted", dateTime)
    }

    fun rejectSlot(dateTime: LocalDateTime, tenantId: Long) {
        checkDateTime(dateTime)
        val slot = slots.compute(dateTime) { _, slot ->
            if (slot == null) {
                throw IllegalArgumentException("There is no request for reservation for that time")
            }
            when (tenantId) {
                ownerId -> {
                    if (slot.status != SlotStatus.REQUESTED) {
                        throw IllegalArgumentException("That request has been already processed")
                    }
                    slot.withNewStatus(SlotStatus.REJECTED)
                }
                slot.tenantId -> {
                    if (slot.status == SlotStatus.REQUESTED) {
                        throw IllegalArgumentException("That request has been already rejected")
                    }
                    null
                }
                else -> {
                    throw InsufficientPermissionsException("Only owner of the flat or the same tenant can reject the time reservation")
                }
            }
        }
        if (tenantId == ownerId) {
            notifications.addNotification(slot!!.tenantId, "Request: flat $id, time: $dateTime - has been rejected", dateTime)
        } else {
            notifications.addNotification(ownerId, "Request: flat $id, time: $dateTime - has been rejected", dateTime)
        }
    }


    private fun checkDateTime(dateTime: LocalDateTime) {
        if (dateTime.hour < 10 || dateTime.hour == 20 && dateTime.minute != 0 || dateTime.hour > 20 || dateTime.minute % 20 != 0 || dateTime.second != 0) {
            throw IllegalArgumentException("Viewing slots are available each 20 minutes from 10:00 to 20:00")
        }
        val now = LocalDateTime.now()
        if (dateTime.isBefore(now) || dateTime.minusDays(7).toLocalDate().isAfter(now.toLocalDate())) {
            throw IllegalArgumentException("Viewing slots are available only for 7 upcoming days")
        }
    }

}