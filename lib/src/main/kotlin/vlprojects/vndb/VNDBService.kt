package vlprojects.vndb

import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import io.ktor.network.selector.ActorSelectorManager
import io.ktor.network.sockets.Socket
import io.ktor.network.sockets.aSocket
import io.ktor.network.sockets.openReadChannel
import io.ktor.network.sockets.openWriteChannel
import io.ktor.network.tls.tls
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.ByteWriteChannel
import io.ktor.utils.io.writeStringUtf8
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

    suspend fun connect() {
        if (::socket.isInitialized) return
        socket = aSocket(ActorSelectorManager(Dispatchers.IO)).tcp()
            .connect(InetSocketAddress(host, TLS_PORT)).tls(Dispatchers.IO)
        outputStream = socket.openWriteChannel(autoFlush = false)
        inputStream = socket.openReadChannel()
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
        return Response(resp[0], resp.getOrNull(1) ?: "")
    }

    private suspend fun ByteReadChannel.readUntilDelimiter(delimiter: Byte = EOT): ByteArray {
        val data = ArrayList<Byte>(availableForRead)

        var byte: Byte = 0
        while (byte != (-1).toByte()) {
            byte = this.readByte()
            if (byte == EOT) break
            data.add(byte)
        }

        return data.takeWhile { it != delimiter }.toByteArray()
    }

    suspend fun sendCommandWithResponse(command: String, vararg args: String): Response {
        networkMutex.withLock {
            sendCommand(command, *args)
            return getResponse()
        }
    }

    suspend fun sendGetCommandWithResponse(
        type: String,
        flags: Array<String>,
        filter: String,
        options: String = ""
    ): Response {
        return sendCommandWithResponse("get", type, flags.joinToString(","), filter, options)
    }

    suspend fun login(): Result<Unit> {
        connect()
        val json = "{\"protocol\":1,\"client\":\"vlprojects7764_test\",\"clientver\":0.1}"
        sendCommand("login", json)
        return getResponse().parse("ok")
    }

    suspend fun getVN(
        flags: Array<String> = arrayOf("basic"),
        filter: Filter = Filter.DEFAULT,
        options: Options = Options()
    ): Result<GetResults<VNBasic>> {
        val response = sendGetCommandWithResponse("vn", flags, filter.toString(), options.toString())
        val results = response.parse<GetResults<VNBasic>>("results")
        return results
    }

    suspend fun getQuote(
        filter: Filter = Filter.DEFAULT,
        options: Options = Options(results = 1)
    ): Result<GetResults<Quote>> {
        val response = sendGetCommandWithResponse("quote", arrayOf("basic"), filter.toString(), options.toString())
        return response.parse("results")
    }

    suspend fun getCharacter(
        flags: Array<String> = arrayOf("basic", "details", "vns"),
        filter: Filter = Filter.DEFAULT,
        options: Options = Options()
    ): Result<GetResults<Character>> {
        val response = sendGetCommandWithResponse("character", flags, filter.toString(), options.toString())
        val results = response.parse<GetResults<Character>>("results")
        return results
    }

    suspend fun getDBStats(): Result<DBStats> {
        sendCommand("dbstats")
        val response = getResponse()
        val result = response.parse<DBStats>("dbstats")
        return result
    }

    // (;°Д°)
    private inline fun <reified T> Response.parse(command: String): Result<T> = when (this.name) {
        "error" -> Result.Error(this.json)
        command -> {
            val type = object : TypeToken<T>() {}.type
            val stats: T = gson.fromJson(this.json, type)
            Result.Success(stats)
        }
        else -> throw IllegalArgumentException("")
    }
}