package jsondatabase.client

import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.IOException
import java.net.InetAddress
import java.net.Socket

const val SERVER_ADDRESS = "127.0.0.1"
const val SERVER_PORT = 23456

class Client {
    fun run(request : String) {
        println("Client started!")
        try {
            Socket(
                InetAddress.getByName(SERVER_ADDRESS),
                SERVER_PORT
            ).use { socket ->
                DataOutputStream(socket.getOutputStream()).use { output ->
                    DataInputStream(socket.getInputStream()).use { input ->
                        output.writeUTF(request)
                        println("Sent: $request")
                        println("Received: ${input.readUTF()}")
                    }
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}