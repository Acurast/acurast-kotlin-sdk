package acurast.codec.extensions

public fun String.pureHexToBa(): ByteArray = ByteArray(this.length / 2) {
    this.substring(it * 2, it * 2 + 2).toInt(16).toByte()
}
public fun String.hexToBa(): ByteArray = removePrefix("0x").pureHexToBa()