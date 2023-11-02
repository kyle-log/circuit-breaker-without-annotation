package cocomo.application.user

interface UserFinder {
    fun find(userId: Long): User
}

data class User(
    val userId: Long,
    val name: String,
)