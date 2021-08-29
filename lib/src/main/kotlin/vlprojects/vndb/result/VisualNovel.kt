package vlprojects.vndb.result

import com.google.gson.annotations.SerializedName

/**
 * Information about a VisualNovel that is returned by "basic" and "details" flags.
 */
data class VisualNovel(
    val id: Int,
    val title: String,
    val original: String?,
    val released: String?,
    @SerializedName("orig_lang")
    val origLanguage: Array<String>,
    val languages: Array<String>,
    val platforms: Array<String>,
    val aliases: String?,
    val length: Int?,
    val description: String?,
    val image: String?,
    @SerializedName("image_flagging")
    val imageFlags: ImageFlagging,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as VisualNovel

        if (id != other.id) return false
        if (title != other.title) return false
        if (original != other.original) return false
        if (released != other.released) return false
        if (!origLanguage.contentEquals(other.origLanguage)) return false
        if (!languages.contentEquals(other.languages)) return false
        if (!platforms.contentEquals(other.platforms)) return false
        if (aliases != other.aliases) return false
        if (length != other.length) return false
        if (description != other.description) return false
        if (image != other.image) return false
        if (imageFlags != other.imageFlags) return false

        return true
    }

    override fun hashCode(): Int = id

}

data class ImageFlagging(
    val votecount: Int = 0,
    @SerializedName("sexual_avg")
    val sexual: Double? = null,
    @SerializedName("violence_avg")
    val violence: Double? = null,
)