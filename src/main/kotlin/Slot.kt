class Slot (
        val tenantId: Long,
        val status: SlotStatus
) {
    fun withNewStatus(newStatus: SlotStatus): Slot {
        return Slot(tenantId, newStatus)
    }
}