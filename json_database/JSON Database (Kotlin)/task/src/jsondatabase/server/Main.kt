package jsondatabase.server

fun main() {
    // receive requests from multiple clients and send a response to each client
    // terminate if client sends exit
    Server().run()
}


