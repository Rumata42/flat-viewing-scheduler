import org.junit.Before
import org.junit.Test
import java.time.LocalDate
import kotlin.test.assertEquals
import kotlin.test.assertNull

class FlatTest {

    private val ownerId = 10L
    private val newTenantId = 20L
    private val localDate = LocalDate.now()
    private val correctDateTime = localDate.plusDays(5).atTime(12, 0)

    private lateinit var flat: Flat

    @Before
    fun beforeEach() {
        flat = Flat(1, ownerId)
    }

    /*------------------reserve------------------*/

    @Test
    fun test_reserve_successful() {
        val dateTime1 = localDate.plusDays(1).atTime(10, 0)
        val dateTime2 = localDate.plusDays(2).atTime(12, 20)
        val dateTime3 = localDate.plusDays(4).atTime(16, 40)
        val dateTime4 = localDate.plusDays(7).atTime(20, 0)
        flat.reserveSlot(dateTime1, newTenantId)
        flat.reserveSlot(dateTime2, newTenantId)
        flat.reserveSlot(dateTime3, newTenantId)
        flat.reserveSlot(dateTime4, newTenantId)

        assertEquals(4, flat.getSlots().size)
        flat.getSlots().values.forEach {
            assertEquals(SlotStatus.REQUESTED, it.status)
            assertEquals(newTenantId, it.tenantId)
        }
    }

    @Test(expected = IllegalArgumentException::class)
    fun test_reserve_incorrectMinutes() {
        val dateTime = localDate.plusDays(1).atTime(12, 15)
        flat.reserveSlot(dateTime, newTenantId)
    }

    @Test(expected = IllegalArgumentException::class)
    fun test_reserve_incorrectHourBefore() {
        val dateTime = localDate.plusDays(1).atTime(9, 40)
        flat.reserveSlot(dateTime, newTenantId)
    }

    @Test(expected = IllegalArgumentException::class)
    fun test_reserve_incorrectHourAfter() {
        val dateTime = localDate.plusDays(1).atTime(20, 20)
        flat.reserveSlot(dateTime, newTenantId)
    }

    @Test(expected = IllegalArgumentException::class)
    fun test_reserve_dayAfter() {
        val dateTime = localDate.plusDays(8).atTime(12, 0)
        flat.reserveSlot(dateTime, newTenantId)
    }

    /*------------------accept------------------*/

    @Test
    fun test_accept_successful() {
        flat.reserveSlot(correctDateTime, newTenantId)
        flat.acceptSlot(correctDateTime, ownerId)

        flat.getSlots()[correctDateTime].apply {
            assertEquals(SlotStatus.ACCEPTED, this?.status)
            assertEquals(newTenantId, this?.tenantId)
        }
    }

    @Test(expected = IllegalArgumentException::class)
    fun test_accept_slotIsAbsent() {
        flat.acceptSlot(correctDateTime, ownerId)
    }

    @Test(expected = InsufficientPermissionsException::class)
    fun test_accept_incorrectPermissions() {
        flat.reserveSlot(correctDateTime, newTenantId)
        flat.acceptSlot(correctDateTime, newTenantId)
    }

    @Test(expected = IllegalArgumentException::class)
    fun test_accept_incorrectSlotStatus() {
        flat.reserveSlot(correctDateTime, newTenantId)
        flat.acceptSlot(correctDateTime, ownerId)
        flat.acceptSlot(correctDateTime, ownerId)
    }

    /*------------------reject------------------*/

    @Test
    fun test_reject_successfulByOwner() {
        flat.reserveSlot(correctDateTime, newTenantId)
        flat.rejectSlot(correctDateTime, ownerId)

        flat.getSlots()[correctDateTime].apply {
            assertEquals(SlotStatus.REJECTED, this?.status)
        }
    }

    @Test
    fun test_reject_successfulByTenant() {
        flat.reserveSlot(correctDateTime, newTenantId)
        flat.acceptSlot(correctDateTime, ownerId)
        flat.rejectSlot(correctDateTime, newTenantId)

        assertNull(flat.getSlots()[correctDateTime])
    }

    @Test(expected = IllegalArgumentException::class)
    fun test_reject_slotIsAbsent() {
        flat.rejectSlot(correctDateTime, ownerId)
    }

    @Test(expected = InsufficientPermissionsException::class)
    fun test_reject_incorrectPermissions() {
        flat.reserveSlot(correctDateTime, newTenantId)
        flat.acceptSlot(correctDateTime, 30)
    }

    @Test(expected = IllegalArgumentException::class)
    fun test_reject_incorrectSlotStatus() {
        flat.reserveSlot(correctDateTime, newTenantId)
        flat.acceptSlot(correctDateTime, ownerId)
        flat.rejectSlot(correctDateTime, ownerId)
    }
}