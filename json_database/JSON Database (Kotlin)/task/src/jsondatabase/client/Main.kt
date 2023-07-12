package jsondatabase.client

fun main(args: Array<String>) {
    // get a Request as a JSON string
    val jsonRequest = makeJsonString(args)
    // send request to server and receive response
    Client().run(jsonRequest)
}


