package acurast.rpc.pallet

import acurast.rpc.utils.JsonRpc
import acurast.rpc.utils.nullableOptString
import com.google.gson.GsonBuilder
import org.json.JSONArray
import org.json.JSONObject

public abstract class PalletRpc {

    protected inline fun <reified T> JSONObject.toTypedObject(): T = parseJSON(toString())
    protected inline fun <reified T> JSONArray.toTypedList(): List<T> = parseJSON<Array<T>>(toString()).toList()

    protected fun handleError(json: JSONObject): Exception =
        json.optJSONObject(JsonRpc.Key.ERROR)?.let { error ->
            val message = error.optString(JsonRpc.Key.MESSAGE)
            val data = error.nullableOptString(JsonRpc.Key.DATA)
            val exceptionMessage = data?.let { "$message ($it)" } ?: message

            Exception(exceptionMessage)
        } ?: Exception("something went wrong")

    protected inline fun <reified R> parseJSON(json: String): R =
        GsonBuilder().create().fromJson(json, R::class.java)
}