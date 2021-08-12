package vlprojects.vndb.result

import com.google.gson.annotations.SerializedName

/**
 * Information about a VN that is returned by a "basic" flag.
 */
data class VNBasic(
    val id: Int,
    val title: String,
    val original: String?,
    val released: String?,
    @SerializedName("orig_lang")
    val origLanguage: Array<String>,
    val languages: Array<String>,
    val platforms: Array<String>,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as VNBasic

        return id == other.id
    }

    override fun hashCode(): Int = id
}
