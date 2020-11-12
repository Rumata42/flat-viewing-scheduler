import java.util.concurrent.ConcurrentHashMap

object Flats {

    private val flats = ConcurrentHashMap<Long, Flat>()


    fun getFlat(id: Long): Flat? {
        return flats[id]
    }

}