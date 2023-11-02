package cocomo.application.user

class UserService : UserFinder {

    override fun find(userId: Long): User = when {
        userId <= 0 -> throw IllegalArgumentException()
        else -> User(1L, "kyle")
    }
}