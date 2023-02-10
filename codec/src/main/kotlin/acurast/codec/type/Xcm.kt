package acurast.codec.type

import acurast.codec.extensions.*
import java.io.UnsupportedEncodingException
import java.math.BigInteger
import java.nio.ByteBuffer

public data class AssetId(public val kind: Kind): ToU8a {
    private var location: MultiLocation? = null
    private var bytes: ByteArray? = null

    public constructor(location: MultiLocation) : this(Kind.Concrete) {
        this.location = location
    }

    public constructor(bytes: ByteArray) : this(Kind.Abstract) {
        this.bytes = bytes
    }

    /**
     * XCM asset ID kinds.
     */
    public enum class Kind(public val id: Int): ToU8a {
        Concrete(0),
        Abstract(1);

        override fun toU8a(): ByteArray = id.toByte().toU8a()
    }

    override fun toU8a(): ByteArray {
        return kind.toU8a() + if (kind == Kind.Concrete) getConcrete().toU8a() else getAbstract().toU8a()
    }

    public companion object {
        public fun read(buffer: ByteBuffer): AssetId {
            return when (buffer.readByte().toInt()) {
                Kind.Concrete.id -> AssetId(MultiLocation.read(buffer))
                Kind.Abstract.id -> AssetId(buffer.readByteArray())
                else -> throw UnsupportedEncodingException()
            }
        }
    }

    public fun getConcrete() : MultiLocation {
        if (kind != Kind.Concrete && location == null) {
            throw Exception("The asset is not Concrete.")
        }
        return location!!
    }


    public fun getAbstract() : ByteArray {
        if (kind != Kind.Abstract && bytes == null) {
            throw Exception("The asset is not Abstract.")
        }
        return bytes!!
    }
}

public data class JunctionV1(public val kind: Kind): ToU8a {
    private var parachain: Int? = null
    private var palletInstance: Int? = null
    private var generalIndex: BigInteger? = null
    private var generalKey: List<Int> = emptyList()

    /**
     * Junction kinds.
     */
    public enum class Kind(public val id: Int): ToU8a {
        Parachain(0),
        AccountId32(1),
        AccountIndex64(2),
        AccountKey20(3),
        PalletInstance(4),
        GeneralIndex(5),
        GeneralKey(6),
        OnlyChild(7),
        Plurality(8);

        override fun toU8a(): ByteArray {
            return id.toByte().toU8a()
        }
    }

    override fun toU8a(): ByteArray {
        return kind.toU8a() + when (kind) {
            Kind.Parachain -> getParachain().toLong().toCompactU8a()
            Kind.PalletInstance -> getPalletInstance().toByte().toU8a()
            Kind.GeneralIndex -> getGeneralIndex().toCompactU8a()
            else -> throw UnsupportedEncodingException()
        }
    }

    public companion object {
        public fun read(buffer: ByteBuffer): JunctionV1 {
            return when (val kind = buffer.readByte().toInt()) {
                Kind.Parachain.id -> JunctionV1(Kind.Parachain)
                    .setParachain(buffer.readCompactInteger())
                Kind.AccountId32.id -> JunctionV1(Kind.AccountId32) // TODO
                Kind.AccountIndex64.id -> JunctionV1(Kind.AccountIndex64) // TODO
                Kind.AccountKey20.id -> JunctionV1(Kind.AccountKey20) // TODO
                Kind.PalletInstance.id -> JunctionV1(Kind.PalletInstance)
                    .setPalletInstance(buffer.readByte().toInt())
                Kind.GeneralIndex.id -> JunctionV1(Kind.GeneralIndex)
                    .setGeneralIndex(buffer.readCompactU128())
                Kind.GeneralKey.id -> JunctionV1(Kind.GeneralKey)
                    .setGeneralKey(TODO())
                Kind.OnlyChild.id -> JunctionV1(Kind.OnlyChild)
                Kind.Plurality.id -> JunctionV1(Kind.Plurality) // TODO
                else -> throw UnsupportedEncodingException("Unknown JunctionV1 kind $kind.")
            }
        }
    }

    public fun getParachain() : Int {
        if (kind != Kind.Parachain) {
            throw NoSuchFieldException("Junction is not of kind: Parachain")
        }
        return parachain!!
    }

    public fun setParachain(parachain: Int) : JunctionV1 {
        this.parachain = parachain
        return this
    }

    public fun getPalletInstance() : Int {
        if (kind != Kind.PalletInstance) {
            throw NoSuchFieldException("Junction is not of kind: PalletInstance")
        }
        return palletInstance!!
    }

    public fun setPalletInstance(palletInstance: Int) : JunctionV1 {
        this.palletInstance = palletInstance
        return this
    }

    public fun getGeneralIndex() : BigInteger {
        if (kind != Kind.GeneralIndex) {
            throw NoSuchFieldException("Junction is not of kind: GeneralIndex")
        }
        return generalIndex!!
    }

    public fun setGeneralIndex(generalIndex: BigInteger) : JunctionV1 {
        this.generalIndex = generalIndex
        return this
    }

    public fun getGeneralKey() : List<Int> {
        if (kind != Kind.GeneralKey) {
            throw NoSuchFieldException("Junction is not of kind: GeneralKey")
        }
        return generalKey
    }

    public fun setGeneralKey(generalKey: List<Int>) : JunctionV1 {
        this.generalKey = generalKey
        return this
    }
}

public data class JunctionsV1(public val kind: Kind, public val junctions: List<JunctionV1>): ToU8a {
    /**
     * Junctions kinds.
     */
    public enum class Kind(public val id: Int): ToU8a {
        Here(0),
        X1(1),
        X2(2),
        X3(3),
        X4(4),
        X5(5),
        X6(6),
        X7(7),
        X8(8);

        override fun toU8a(): ByteArray {
            return id.toByte().toU8a()
        }
    }

    override fun toU8a(): ByteArray {
        return kind.toU8a() + junctions.toU8a(withSize = false)
    }

    public companion object {
        public fun read(buffer: ByteBuffer): JunctionsV1 {
            return when (buffer.readByte().toInt()) {
                Kind.Here.id -> JunctionsV1(Kind.Here, listOf())
                Kind.X1.id -> JunctionsV1(Kind.X1, listOf(JunctionV1.read(buffer)))
                Kind.X2.id -> JunctionsV1(Kind.X2, listOf(JunctionV1.read(buffer), JunctionV1.read(buffer)))
                Kind.X3.id -> JunctionsV1(
                    Kind.X3,
                    listOf(
                        JunctionV1.read(buffer),
                        JunctionV1.read(buffer),
                        JunctionV1.read(buffer)
                    )
                )
                Kind.X4.id -> JunctionsV1(
                    Kind.X4,
                    listOf(
                        JunctionV1.read(buffer),
                        JunctionV1.read(buffer),
                        JunctionV1.read(buffer),
                        JunctionV1.read(buffer)
                    )
                )
                Kind.X5.id -> JunctionsV1(
                    Kind.X5,
                    listOf(
                        JunctionV1.read(buffer),
                        JunctionV1.read(buffer),
                        JunctionV1.read(buffer),
                        JunctionV1.read(buffer),
                        JunctionV1.read(buffer)
                    )
                )
                Kind.X6.id -> JunctionsV1(
                    Kind.X6,
                    listOf(
                        JunctionV1.read(buffer),
                        JunctionV1.read(buffer),
                        JunctionV1.read(buffer),
                        JunctionV1.read(buffer),
                        JunctionV1.read(buffer),
                        JunctionV1.read(buffer)
                    )
                )
                Kind.X7.id -> JunctionsV1(
                    Kind.X7,
                    listOf(
                        JunctionV1.read(buffer),
                        JunctionV1.read(buffer),
                        JunctionV1.read(buffer),
                        JunctionV1.read(buffer),
                        JunctionV1.read(buffer),
                        JunctionV1.read(buffer),
                        JunctionV1.read(buffer)
                    )
                )
                Kind.X8.id -> JunctionsV1(
                    Kind.X8,
                    listOf(
                        JunctionV1.read(buffer),
                        JunctionV1.read(buffer),
                        JunctionV1.read(buffer),
                        JunctionV1.read(buffer),
                        JunctionV1.read(buffer),
                        JunctionV1.read(buffer),
                        JunctionV1.read(buffer),
                        JunctionV1.read(buffer)
                    )
                )
                else -> throw UnsupportedEncodingException()
            }
        }
    }
}

public data class MultiLocation(
    public val parents: Int,
    public val interior: JunctionsV1,
): ToU8a {
    public companion object {
        public fun read(buffer: ByteBuffer): MultiLocation {
            return MultiLocation(
                parents = buffer.readByte().toInt(),
                interior = JunctionsV1.read(buffer)
            )
        }
    }

    override fun toU8a(): ByteArray {
        return parents.toByte().toU8a() + interior.toU8a()
    }
}

public class Fungibility {
    public enum class Kind(public val id: Int) {
        Fungible(0),
        NonFungible(1),
    }

    public data class V1(
        public val kind: Kind,
        public val amount: BigInteger,
    ) {
        public companion object {
            public fun read(buffer: ByteBuffer): V1 {
                return when(buffer.readByte().toInt()) {
                    Kind.Fungible.id -> V1(
                        kind = Kind.Fungible,
                        amount = buffer.readCompactU128()
                    )
                    Kind.NonFungible.id -> V1(
                        kind = Kind.Fungible,
                        amount = BigInteger.ONE // TODO: We only support fungible assets for now
                    )
                    else -> throw UnsupportedEncodingException()
                }
            }
        }
    }
}

public data class AssetInstanceV1(public val kind: Kind)  {

    public enum class Kind(public val id: Int) {
        Undefined(0),
        Index(1),
        Array4(2),
        Array8(3),
        Array16(4),
        Array32(5),
        Blob(6)
    }

    public companion object {
        public fun read(buffer: ByteBuffer): AssetInstanceV1 {
            val kind =  when(buffer.readByte().toInt()) {
                Kind.Undefined.id -> Kind.Undefined
                Kind.Index.id -> Kind.Index
                Kind.Array4.id -> Kind.Array4
                Kind.Array8.id -> Kind.Array8
                Kind.Array16.id -> Kind.Array16
                Kind.Array32.id -> Kind.Array32
                Kind.Blob.id -> Kind.Blob
                else -> throw UnsupportedEncodingException()
            }
            return AssetInstanceV1(
                kind
            )
        }
    }
}

public data class MultiAssetV1(
    public val id: AssetId,
    public val fungibility: Fungibility.V1,
) {
    public companion object {
        public fun read(buffer: ByteBuffer): MultiAssetV1 {
            return MultiAssetV1(
                AssetId.read(buffer),
                Fungibility.V1.read(buffer)
            )
        }
    }
}