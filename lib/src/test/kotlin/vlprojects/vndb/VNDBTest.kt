package vlprojects.vndb

import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import vlprojects.vndb.parameters.*
import vlprojects.vndb.result.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class VNDBTest {
    private val vndb = VNDBService()

    @BeforeAll
    fun loginTest() = runBlocking {
        val result = vndb.login()
        assert(result is Result.Success) { (result as Result.Error).json }
    }

    @Test
    fun dbstatsTest() = runBlocking {
        val result = vndb.getDBStats()
        assert(result is Result.Success<DBStats>)
        val stats = (result as Result.Success<DBStats>).data
        assert(stats.vn > 0)
    }

    @Test
    fun ever17Test() = runBlocking {
        val results = vndb.getVN(filter = "id" eq 17)
        assert(results is Result.Success<GetResults<VNBasic>>)
        results as Result.Success<GetResults<VNBasic>>
        val data = results.data

        assertEquals(data.num, 1)
        assertEquals(data.items.size, 1)

        val ever17 = data.items.single()
        assertEquals(ever17.id, 17)
        assertEquals(ever17.origLanguage.single(), "ja")
    }

    @Test
    fun quoteTest() = runBlocking {
        val result = vndb.getQuote()
        assert(result is Result.Success)
        result as Result.Success<GetResults<Quote>>
        assert(result.data.num == 1)
        val quote = result.data.items.single()
        assert(quote.id > 0)
        println(quote)
    }

    @Test
    fun parseMixedArray() = runBlocking {
        val result = vndb.getCharacter(filter = "vn" eq 2002, options = Options(results = 4))
        result as Result.Success
        val items = result.data.items
        val item = items.first()
        assertNotNull(item.vns)

        assertNotNull(item.getVNSpoilerLevel().first(), "おはよう、お兄ちゃん")
    }
}
