package vlprojects.vndb.result

import com.google.gson.JsonElement

class Character(
    val id: Int,
    val name: String,
    val original: String?,
    val gender: String?,
    val birthday: Array<Int?>,
    val description: String?,
    val age: Int?,
    val image: String?,
    val vns: Array<Array<JsonElement>>,
) {
    fun getVNSpoilerLevel() = vns.map { vnInfo ->
        Pair(vnInfo[0].asInt, vnInfo[2].asInt)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Character

        if (id != other.id) return false
        if (name != other.name) return false
        if (original != other.original) return false
        if (gender != other.gender) return false
        if (!birthday.contentEquals(other.birthday)) return false
        if (description != other.description) return false
        if (age != other.age) return false
        if (image != other.image) return false
        if (!vns.contentDeepEquals(other.vns)) return false

        return true
    }

    override fun hashCode(): Int = id
}