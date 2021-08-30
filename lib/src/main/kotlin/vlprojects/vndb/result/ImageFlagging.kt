package vlprojects.vndb.result

import com.google.gson.annotations.SerializedName

/**
 * Image flagging summary.
 */
data class ImageFlagging(
    /** Number of flagging votes. If this number is 0 then [sexual] and [violence] variables will both be null */
    val votecount: Int = 0,
    /** Sexual score between 0 (safe) and 2 (explicit), or `null` if no votes have been cast yet. */
    @SerializedName("sexual_avg")
    val sexual: Double? = null,
    /** Violence score between 0 (tame) and 2 (brutal), or `null` if no votes have been cast yet. */
    @SerializedName("violence_avg")
    val violence: Double? = null,
)