package acurast.rpc.utils

import org.json.JSONObject

internal const val JSON_RPC_KEY_RESULT = "result"

/**
 * JSONObject.optString(key) actually never returns `null`:
 * - if the key is not present in the object, the method returns an empty string `""`
 * - if the key is present but has no value, i.e. the value is `null`, the method returns the string `"null"`
 *
 * This behavior is counterintuitive and can bring various unexpected effects.
 * `JSONObject.nullableOptString` tries to mitigate the issue by checking whether the value actually exists before
 * trying to access and return it, and returning `null` if it doesn't.
 */
internal fun JSONObject.nullableOptString(key: String): String? =
    if (has(key) && !isNull(key)) getString(key) else null