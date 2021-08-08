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
    /*val vnSpoilerLevel: List<Pair<Int, Int>> by lazy {
        vns.map { vnInfo ->
            Pair(vnInfo[0].asInt, vnInfo[2].asInt)
        }
    }*/

    fun getVNSpoilerLevel() = vns.map { vnInfo ->
        Pair(vnInfo[0].asInt, vnInfo[2].asInt)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as Character
        return id == other.id
    }

    override fun hashCode(): Int = id
}

/*class AnyDeserializer : JsonDeserializer<List<Any>> {
    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): List<Any> {
        return json?.asJsonArray?.map { elem ->
            elem.asString
        } ?: listOf()
    }
}*/
