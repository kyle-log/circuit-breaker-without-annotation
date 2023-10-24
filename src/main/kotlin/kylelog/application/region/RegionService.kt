package kylelog.application.region

class RegionService : RegionFinder {

    override fun find(regionId: Long) = when {
        regionId <= 0 -> throw IllegalArgumentException()
        else -> Region(1L, "yeoksam")
    }
}