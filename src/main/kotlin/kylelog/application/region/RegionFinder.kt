package kylelog.application.region

interface RegionFinder {
    fun find(regionId: Long): Region
}

data class Region(
    val regionId: Long,
    val name: String,
)