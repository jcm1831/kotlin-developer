package jsondatabase.server

import com.google.gson.Gson
import jsondatabase.client.Request
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.IOException
import java.net.InetAddress
import java.net.ServerSocket
import java.net.Socket
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

private const val SERVER_ADDRESS = "127.0.0.1"
private const val SERVER_PORT = 23456

class Server {
    private val executor: ExecutorService = Executors.newFixedThreadPool(8)

    fun run() {
        println("Server started!")
        try {
            ServerSocket(
                SERVER_PORT,
                50,
                InetAddress.getByName(SERVER_ADDRESS)
            ).use { server ->
                while (!executor.isShutdown) {
                    executor.submit {
                        handleRequest(server.accept())
                    }
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun handleRequest(socket: Socket) {
        try {
            DataInputStream(socket.getInputStream()).use { input ->
                DataOutputStream(socket.getOutputStream()).use { output ->
                    val request =
                        Gson().fromJson(input.readUTF(), Request::class.java)

                    if (request.type == "exit") {
                        output.writeUTF(makeResponse(Response.OKAY))
                        executor.shutdownNow()
                    } else {
                        val response =
                            when (request.type) {
                                "set" -> Database.set(
                                    request.key!!,
                                    request.value!!
                                )

                                "get" -> {
                                    Database.get(request.key!!)
                                }

                                "delete" -> Database.delete(request.key!!)
                                else -> "Unknown action!"
                            }
                        output.writeUTF(response)
                    }
                    socket.close()
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}