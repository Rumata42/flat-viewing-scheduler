import java.lang.RuntimeException

class InsufficientPermissionsException(override val message: String): RuntimeException(message)