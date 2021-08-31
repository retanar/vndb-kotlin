package vlprojects.vndb

import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import vlprojects.vndb.parameters.*
import vlprojects.vndb.result.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class VndbTest {
    private val vndb = Vndb()

    @BeforeAll
    fun loginTest() = runBlocking {
        val result = vndb.login()
        assert(result is Result.Success) { result.error() }
    }

    @Test
    fun dbstatsTest() = runBlocking {
        val result = vndb.getDBStats()
        val stats = result.success()
        assert(stats.vn > 0)
        assert(stats.chars > 0)
        assert(stats.producers > 0)
        assert(stats.releases > 0)
        assert(stats.tags > 0)
        assert(stats.traits > 0)
    }

    @Test
    fun ever17Test() = runBlocking {
        val results = vndb.getVisualNovel(filter = "id" eq 17)
        assert(results is Result.Success<GetResults<VisualNovel>>)
        val data = results.success()

        assertEquals(data.num, 1)
        assertEquals(data.items.size, 1)

        val ever17 = data.items.single()
        assert(ever17.title.startsWith("Ever17", ignoreCase = true))
        assertEquals(ever17.id, 17)
        assertEquals(ever17.origLanguage.single(), "ja")
        assertEquals(ever17.length, 4)
        assertNotNull(ever17.image)
        assertNotNull(ever17.imageFlags.sexual)
        assertNotNull(ever17.imageFlags.violence)
    }

    @Test
    fun quoteTest() = runBlocking {
        val result = vndb.getQuote()
        assert(result is Result.Success)
        val data = result.success()
        assert(data.num == 1)
        val quote = data.items.single()
        assert(quote.id > 0)
        println(quote)
    }

    @Test
    fun parseMixedArray() = runBlocking {
        val result = vndb.getCharacter(filter = "vn" eq 2002, options = Options(results = 15))
        val items = result.success().items
        val item = items.first()
        assertNotNull(item.vns)

        assertNotNull(item.getVNSpoilerLevel().first(), "Null when getting VN's spoiler level")
    }
}
