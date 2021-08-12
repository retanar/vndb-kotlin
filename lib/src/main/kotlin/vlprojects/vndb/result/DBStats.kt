package vlprojects.vndb.result

/**
 * Global database statistics that are visible in the main menu of the site.
 */
data class DBStats(
    val tags: Int,
    val releases: Int,
    val producers: Int,
    val chars: Int,
    val vn: Int,
    val traits: Int,
)
