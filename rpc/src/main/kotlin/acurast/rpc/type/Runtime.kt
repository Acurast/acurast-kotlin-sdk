package acurast.rpc.type

import acurast.codec.extensions.*
import java.nio.ByteBuffer

public data class RuntimeMetadataV14(
    val magicNumber: UInt,
    val version: Int,
    var modules: List<Module> = ArrayList()
) {
    public data class Module(
        var name: String = "",
        var calls: List<Call>? = ArrayList(),
        var index: Int = 0
    )

    public data class Call(
        var moduleIndex: Int = 0,
        var callIndex: Int = 0,
        var name: String = "",
        var arguments: List<Argument> = ArrayList(),
        var documentation: List<String> = ArrayList()
    ) {
        public data class Argument(
            var name: String = "",
            var type: String = ""
        )
    }
}

public fun RuntimeMetadataV14.findCall(module: String, call: String) : RuntimeMetadataV14.Call? {
    return this.modules
        .find { it.name == module }?.calls
        ?.find { it.name == call }
}

public fun ByteBuffer.readMetadata(): RuntimeMetadataV14 {
    val metadata = RuntimeMetadataV14(
        readU32(),
        readByte().toInt()
    )

    when (metadata.version) {
        14 -> {
            // Skip type definitions
            System.out.println(readList { readMetadataTypeV14() }.sorted())

            //metadata.modules = readList { readMetadataModuleV14() }
        }
        else -> throw Exception("unsupported metadata version: ${metadata.version}")
    }
    return metadata
}

public fun ByteBuffer.readMetadataModuleV14(): RuntimeMetadataV14.Module {
    return RuntimeMetadataV14.Module(
        name = readString(),
        index = readByte().toInt()
    )
}

public fun ByteBuffer.readMetadataTypeV14(): UInt {
    // ID (u32)
    val id = readU32()
    // Path (Vec<T::String>) https://docs.rs/scale-info/2.0.0/src/scale_info/ty/path.rs.html#61-64
    readOptional { readList { readString() } }

    return id
}
