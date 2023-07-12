package jsondatabase.server

import com.google.gson.*
import java.io.File
import java.util.concurrent.locks.ReadWriteLock
import java.util.concurrent.locks.ReentrantReadWriteLock


object Database {
    private const val filepath = "src/jsondatabase/server/data/data.json"
    private var data: JsonObject
    private val lock: ReadWriteLock = ReentrantReadWriteLock()

    init {
        val file = File(filepath)
        data = if (file.exists() && file.readText().isNotEmpty()) {
            val content = file.readText()
            Gson().fromJson(content, JsonObject::class.java)
        } else {
            file.createNewFile()
            JsonObject()
        }
    }

    fun set(key: JsonElement, value: JsonElement): String {
        try {
            lock.writeLock().lock()
            if (key.isJsonPrimitive) {
                data.add(key.asString, value)
            } else {
                val keys = key.asJsonArray
                val toAdd = keys.remove(keys.size() - 1).asString
                val obj = createOnDemand(keys)
                obj.asJsonObject.add(toAdd, value)
            }
            writeToFile()
            return makeResponse(Response.OKAY)
        } finally {
            lock.writeLock().unlock()
        }
    }

    fun delete(key: JsonElement): String {
        try {
            lock.writeLock().lock()
            return if (key.isJsonPrimitive && data.has(key.asString)) {
                data.remove(key.asString)
                writeToFile()
                makeResponse(Response.OKAY)
            } else {
                val keys = key.asJsonArray
                val toRemove = keys.remove(keys.size() - 1).asString
                val obj = getImpl(keys)
                if (obj != null) {
                    obj.asJsonObject.remove(toRemove)
                    writeToFile()
                    makeResponse(Response.OKAY)
                } else {
                    makeResponse(Response.ERROR)
                }
            }
        } finally {
            lock.writeLock().unlock()
        }
    }

    fun get(key: JsonElement): String {
        try {
            lock.readLock().lock()
            return if (key.isJsonPrimitive && data.has(key.asString)) {
                val value = data.get(key.asString)
                makeResponse(Response.VALUE, value.toString())
            } else {
                val value = getImpl(key.asJsonArray)
                if (value != null) {
                    makeResponse(Response.VALUE, value.toString())
                } else {
                    makeResponse(Response.ERROR)
                }
            }
        } finally {
            lock.readLock().unlock()
        }
    }

    private fun getImpl(keys: JsonArray): JsonElement? {
        var dummy: JsonElement = data
        for (key in keys.map { it.asString }) {
            if (dummy.asJsonObject.has(key)) {
                dummy = dummy.asJsonObject.get(key)
            } else {
                return null
            }
        }
        return dummy
    }

    private fun createOnDemand(keys: JsonArray): JsonElement {
        var dummy: JsonElement = data
        for (key in keys.map { it.asString }) {
            if (!dummy.asJsonObject.has(key)) {
                dummy.asJsonObject.add(key, JsonObject())
            }
            dummy = dummy.asJsonObject.get(key)
        }
        return dummy
    }

    private fun writeToFile() {
        File(filepath).writeText(
            GsonBuilder().setPrettyPrinting()
                .create().toJson(data)
        )
    }
}
