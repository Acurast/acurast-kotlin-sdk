package acurast.rpc.pallet

import acurast.rpc.Rpc
import acurast.rpc.engine.RpcEngine
import com.google.gson.GsonBuilder
import org.json.JSONArray
import org.json.JSONObject

public abstract class PalletRpc(override val defaultEngine: RpcEngine) : Rpc {

    protected inline fun <reified T> JSONObject.toTypedObject(): T = parseJSON(toString())
    protected inline fun <reified T> JSONArray.toTypedList(): List<T> = parseJSON<Array<T>>(toString()).toList()

    protected fun handleError(json: JSONObject): Exception =
        json.optJSONObject("error")?.let { error ->
            Exception(error.optString("message"))
        } ?: Exception("something went wrong")

    protected inline fun <reified R> parseJSON(json: String): R =
        GsonBuilder().create().fromJson(json, R::class.java)
}