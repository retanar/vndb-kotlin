package vlprojects.vndb

import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import io.ktor.network.selector.ActorSelectorManager
import io.ktor.network.sockets.*
import io.ktor.network.tls.tls
import io.ktor.utils.io.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import vlprojects.vndb.parameters.Filter
import vlprojects.vndb.parameters.Options
import vlprojects.vndb.result.*
import java.net.InetSocketAddress

private const val host = "api.vndb.org"

//private const val TCP_PORT = 19534
private const val TLS_PORT = 19535

// End Of Transmission character
private const val EOT: Byte = 0x4
private const val SPACE: Byte = 0x20

/**
 * Main class for connecting to the [vndb api](https://api.vndb.org). Before issuing any commands, this client needs to
 * establish the connection by using [connect] method and login by using [login] method (which also calls [connect]).
 */
class VNDBService {
    private lateinit var socket: Socket
    private lateinit var outputStream: ByteWriteChannel
    private lateinit var inputStream: ByteReadChannel

    //    private val scope = CoroutineScope(Dispatchers.IO)
    private val networkMutex = Mutex()

    private val gson by lazy {
        GsonBuilder().run {
            this.create()
        }
    }

    /**
     * Establishes a connection with the vndb server. Always called as a part of the [login] method, so in the most
     * cases there is no need to call this method directly.
     *
     * @return `true` if this connection was established succesfully; `false` if this connection was closed right after
     * opening.
     */
    suspend fun connect(): Boolean {
        if (::socket.isInitialized && !socket.isClosed)
            return true
        socket = aSocket(ActorSelectorManager(Dispatchers.IO)).tcp()
            .connect(InetSocketAddress(host, TLS_PORT)).tls(Dispatchers.IO)
        outputStream = socket.openWriteChannel(autoFlush = false)
        inputStream = socket.openReadChannel()
        return !socket.isClosed
    }

    private suspend fun sendCommand(command: String, vararg args: String) {
        outputStream.writeStringUtf8(command)
        args.forEach { outputStream.appendString(it) }
        outputStream.flushWithEOT()
    }

    private suspend fun ByteWriteChannel.appendString(s: String) {
        this.writeByte(SPACE)
        this.writeStringUtf8(s)
    }

    private suspend fun ByteWriteChannel.flushWithEOT() {
        this.writeByte(EOT)
        this.flush()
    }

    private suspend fun getResponse(): Response {
        val resp = inputStream.readUntilDelimiter().decodeToString().split(' ', limit = 2)
//        println("DEBUG: $resp")
        return Response(resp[0], resp.getOrElse(1) { "" })
    }

    private suspend fun ByteReadChannel.readUntilDelimiter(delimiter: Byte = EOT): ByteArray {
        awaitContent()
        val data = ArrayList<Byte>(availableForRead)

        var byte: Byte
        while (true) {
            byte = this.readByte()
            if (byte == delimiter || byte == (-1).toByte()) break
            data.add(byte)
        }

        return data.toByteArray()
    }

    /**
     * Base method for any interaction with the server. Method is synchronized by using Mutex.
     *
     * @param command name of the command that is sent to the server.
     * @param args 0 or more arguments that need to be sent with the command.
     * @return [Response] object that contains response name and json from the server.
     */
    suspend fun sendCommandWithResponse(command: String, vararg args: String): Response {
        networkMutex.withLock {
            sendCommand(command, *args)
            return getResponse()
        }
    }

    /**
     * Acts similarly to [sendCommandWithResponse] but used for "get" commands. Used in various other methods like:
     * [getVN], [getCharacter], [getQuote].
     */
    suspend fun sendGetCommandWithResponse(
        type: String,
        flags: Array<String>,
        filter: String,
        options: String = ""
    ): Response {
        return sendCommandWithResponse("get", type, flags.joinToString(","), filter, options)
    }

    /**
     * Sends "login" command to the vndb server without credentials. This method needs to be called before sending any
     * other commands to the server.
     *
     * @return [Result.Success] on successful login;
     *
     * [Result.Error] on unsuccessful login or if this connection was already logged in.
     */
    suspend fun login(): Result<Unit> {
        connect()
        val json = "{\"protocol\":1,\"client\":\"vlprojects7764_test\",\"clientver\":0.1}"
        sendCommand("login", json)
        return getResponse().parse("ok")
    }

    /**
     * @param flags used to get specific information about visual novels. For now, the only supported flag by this
     * library is `basic`. Existing flags: basic, details, anime, relation, tags, stats, screens, staff.
     * @param filter filtering is possible on these fields: id, title, original, firstchar, released, platforms,
     * languages, orig_lang, search, tags.
     * @param options sorting is possible on the following fields: id, title, released, popularity, rating, votecount.
     */
    suspend fun getVN(
        flags: Array<String> = arrayOf("basic"),
        filter: Filter = Filter.DEFAULT,
        options: Options = Options()
    ): Result<GetResults<VNBasic>> {
        val response = sendGetCommandWithResponse("vn", flags, filter.toString(), options.toString())
        val results = response.parse<GetResults<VNBasic>>("results")
        return results
    }

    /**
     * Used to get random or specific quote(s).
     *
     * @param filter  allows filtering only on "id" field, which takes a single integer or an array of integers.
     * @param options by default set to 1 result. Sorting is possible on the 'id' and the pseudo 'random' field (default).
     * @return 1 (default) or more random [Quote(s)][Quote].
     */
    suspend fun getQuote(
        filter: Filter = Filter.DEFAULT,
        options: Options = Options(results = 1)
    ): Result<GetResults<Quote>> {
        val response = sendGetCommandWithResponse("quote", arrayOf("basic"), filter.toString(), options.toString())
        return response.parse("results")
    }

    /**
     * @param flags used to get specific information about characters. For now, the only supported flags by this
     * library are `basic`, `details`, `vns`. Existing flags: basic, details, meas, traits, vns, voiced, instances.
     * @param filter filtering is possible on these fields: id, name, original, search, vn, traits.
     * @param options sorting is possible on the following fields: id, name.
     */
    suspend fun getCharacter(
        flags: Array<String> = arrayOf("basic", "details", "vns"),
        filter: Filter = Filter.DEFAULT,
        options: Options = Options()
    ): Result<GetResults<Character>> {
        val response = sendGetCommandWithResponse("character", flags, filter.toString(), options.toString())
        val results = response.parse<GetResults<Character>>("results")
        return results
    }

    /**
     * This method returns the global vndb statistics that are visible in the main menu of the site.
     */
    suspend fun getDBStats(): Result<DBStats> {
        sendCommand("dbstats")
        val response = getResponse()
        val result = response.parse<DBStats>("dbstats")
        return result
    }

    private inline fun <reified T> Response.parse(command: String): Result<T> = when (this.name) {
        "error" -> Result.Error(this.json)
        command -> {
            val type = object : TypeToken<T>() {}.type
            val stats: T = gson.fromJson(this.json, type)
            Result.Success(stats)
        }
        else -> throw IllegalArgumentException("Expected command \"$command\" but was \"${this.name}\".")
    }
}