import java.util.concurrent.ConcurrentHashMap

object Flats {

    private val flats = ConcurrentHashMap<Long, Flat>()

    // Until someone implements CRUD operations for flats
    init {
        flats[1L] = Flat(1L, 10L)
    }


    fun getFlat(id: Long): Flat? {
        return flats[id]
    }

}