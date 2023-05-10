package acurast.codec.type

import acurast.codec.extensions.*
import java.io.UnsupportedEncodingException
import java.math.BigInteger
import java.nio.ByteBuffer

public sealed interface AssetId: ToU8a {
    public enum class Tag(public val id: Byte) : ToU8a {
        Concrete(0),
        Abstract(1);

        override fun toU8a(): ByteArray = id.toU8a()
    }

    public sealed interface Kind {
        public val tag: Tag

        public companion object {
            internal val values: List<Kind>
                get() = listOf(
                    Concrete,
                    Abstract
                )
        }
    }

    public data class Concrete(val location: MultiLocation) : AssetId {
        public companion object : Kind {
            override val tag: Tag = Tag.Concrete
        }

        override fun toU8a(): ByteArray = tag.toU8a() + location.toU8a()
    }

    public data class Abstract(val bytes: ByteArray) : AssetId {
        public companion object : Kind {
            override val tag: Tag = Tag.Abstract
        }

        override fun toU8a(): ByteArray = tag.toU8a() + bytes
    }

    public object Decoder {
        public fun read(buffer: ByteBuffer): AssetId {
            return when (val kind = buffer.readByte()) {
                Tag.Concrete.id -> Concrete(MultiLocation.read(buffer))
                Tag.Abstract.id -> Abstract(buffer.readBytes(32))
                else -> throw UnsupportedEncodingException("Unknown AssetId kind: $kind.")
            }
        }
    }
}

public sealed interface JunctionV1: ToU8a {
    public enum class Tag(public val id: Byte) : ToU8a {
        Parachain(0),
        AccountId32(1),
        AccountIndex64(2),
        AccountKey20(3),
        PalletInstance(4),
        GeneralIndex(5),
        GeneralKey(6),
        OnlyChild(7),
        Plurality(8);

        override fun toU8a(): ByteArray = id.toU8a()
    }

    public sealed interface Kind {
        public val tag: Tag

        public companion object {
            internal val values: List<Kind>
                get() = listOf(
                    Parachain,
                    AccountId32,
                    AccountIndex64,
                    AccountKey20,
                    PalletInstance,
                    GeneralIndex,
                    GeneralKey,
                    OnlyChild,
                    Plurality
                )
        }
    }

    public object Decoder {
        public fun read(buffer: ByteBuffer): JunctionV1 {
            return when (val kind = buffer.readByte()) {
                Tag.Parachain.id -> Parachain(buffer.readCompactInteger().toLong())
                Tag.AccountId32.id -> AccountId32(buffer.readByteArray()) // TODO
                Tag.AccountIndex64.id -> AccountIndex64() // TODO
                Tag.AccountKey20.id -> AccountKey20() // TODO
                Tag.PalletInstance.id -> PalletInstance(buffer.readByte())
                Tag.GeneralIndex.id -> GeneralIndex(buffer.readCompactU128())
                Tag.GeneralKey.id -> GeneralKey() // TODO
                Tag.OnlyChild.id -> OnlyChild() // TODO
                Tag.Plurality.id -> Plurality() // TODO
                else -> throw UnsupportedEncodingException("Unknown Junction kind: $kind.")
            }
        }
    }

    public data class Parachain(val parachainId: Long) : JunctionV1 {
        public companion object : Kind {
            override val tag: Tag = Tag.Parachain
        }

        override fun toU8a(): ByteArray = tag.toU8a() + parachainId.toCompactU8a()
    }

    public data class AccountId32(val accountId32: ByteArray) : JunctionV1 {
        public companion object : Kind {
            override val tag: Tag = Tag.AccountId32
        }

        override fun toU8a(): ByteArray = tag.toU8a() + accountId32.toU8a()
    }

    // TODO
    public class AccountIndex64() : JunctionV1 {
        public companion object : Kind {
            override val tag: Tag = Tag.AccountIndex64
        }

        override fun toU8a(): ByteArray = tag.toU8a()
    }

    // TODO
    public class AccountKey20() : JunctionV1 {
        public companion object : Kind {
            override val tag: Tag = Tag.AccountKey20
        }

        override fun toU8a(): ByteArray = tag.toU8a()
    }

    public data class PalletInstance(val palletInstance: Byte) : JunctionV1 {
        public companion object : Kind {
            override val tag: Tag = Tag.PalletInstance
        }

        override fun toU8a(): ByteArray = tag.toU8a() + palletInstance.toU8a()
    }

    public data class GeneralIndex(val generalIndex: BigInteger) : JunctionV1 {
        public companion object : Kind {
            override val tag: Tag = Tag.GeneralIndex
        }

        override fun toU8a(): ByteArray = tag.toU8a() + generalIndex.toCompactU8a()
    }

    // TODO
    public class GeneralKey() : JunctionV1 {
        public companion object : Kind {
            override val tag: Tag = Tag.GeneralKey
        }

        override fun toU8a(): ByteArray = tag.toU8a()
    }

    // TODO
    public class OnlyChild() : JunctionV1 {
        public companion object : Kind {
            override val tag: Tag = Tag.OnlyChild
        }

        override fun toU8a(): ByteArray = tag.toU8a()
    }

    // TODO
    public class Plurality() : JunctionV1 {
        public companion object : Kind {
            override val tag: Tag = Tag.Plurality
        }

        override fun toU8a(): ByteArray = tag.toU8a()
    }
}

public sealed interface JunctionsV1: ToU8a {
    public enum class Tag(public val id: Byte): ToU8a {
        Here(0),
        X1(1),
        X2(2),
        X3(3),
        X4(4),
        X5(5),
        X6(6),
        X7(7),
        X8(8);

        override fun toU8a(): ByteArray = id.toU8a()
    }

    public sealed interface Kind {
        public val tag: Tag

        public companion object {
            internal val values: List<Kind>
                get() = listOf(
                    Here,
                    X1,
                    X2,
                    X3,
                    X4,
                    X5,
                    X6,
                    X7,
                    X8,
                )
        }
    }

    public object Decoder {
        public fun read(buffer: ByteBuffer): JunctionsV1 {
            return when (val tag = buffer.readByte()) {
                Tag.Here.id -> Here()
                Tag.X1.id -> X1(JunctionV1.Decoder.read(buffer))
                Tag.X2.id -> X2(
                    JunctionV1.Decoder.read(buffer),
                    JunctionV1.Decoder.read(buffer),
                )
                Tag.X3.id -> X3(
                    JunctionV1.Decoder.read(buffer),
                    JunctionV1.Decoder.read(buffer),
                    JunctionV1.Decoder.read(buffer),
                )
                Tag.X4.id -> X4(
                    JunctionV1.Decoder.read(buffer),
                    JunctionV1.Decoder.read(buffer),
                    JunctionV1.Decoder.read(buffer),
                    JunctionV1.Decoder.read(buffer),
                )
                Tag.X5.id -> X5(
                    JunctionV1.Decoder.read(buffer),
                    JunctionV1.Decoder.read(buffer),
                    JunctionV1.Decoder.read(buffer),
                    JunctionV1.Decoder.read(buffer),
                    JunctionV1.Decoder.read(buffer),
                )
                Tag.X6.id -> X6(
                    JunctionV1.Decoder.read(buffer),
                    JunctionV1.Decoder.read(buffer),
                    JunctionV1.Decoder.read(buffer),
                    JunctionV1.Decoder.read(buffer),
                    JunctionV1.Decoder.read(buffer),
                    JunctionV1.Decoder.read(buffer),
                )
                Tag.X7.id -> X7(
                    JunctionV1.Decoder.read(buffer),
                    JunctionV1.Decoder.read(buffer),
                    JunctionV1.Decoder.read(buffer),
                    JunctionV1.Decoder.read(buffer),
                    JunctionV1.Decoder.read(buffer),
                    JunctionV1.Decoder.read(buffer),
                    JunctionV1.Decoder.read(buffer),
                )
                Tag.X8.id -> X8(
                    JunctionV1.Decoder.read(buffer),
                    JunctionV1.Decoder.read(buffer),
                    JunctionV1.Decoder.read(buffer),
                    JunctionV1.Decoder.read(buffer),
                    JunctionV1.Decoder.read(buffer),
                    JunctionV1.Decoder.read(buffer),
                    JunctionV1.Decoder.read(buffer),
                    JunctionV1.Decoder.read(buffer),
                )
                else -> throw UnsupportedEncodingException("Unknown junctions tag: $tag")
            }
        }
    }

    public class Here : JunctionsV1 {
        public companion object : Kind {
            override val tag: Tag = Tag.Here
        }

        override fun toU8a(): ByteArray = tag.toU8a()
    }

    public data class X1(val junction1: JunctionV1) : JunctionsV1 {
        private val junctions: List<JunctionV1> = listOf(junction1)

        public companion object : Kind {
            override val tag: Tag = Tag.X1
        }

        override fun toU8a(): ByteArray = tag.toU8a() + junctions.toU8a(withSize = false)
    }

    public data class X2(val junction1: JunctionV1, val junction2: JunctionV1) : JunctionsV1 {
        private val junctions: List<JunctionV1> = listOf(junction1, junction2)

        public companion object : Kind {
            override val tag: Tag = Tag.X2
        }

        override fun toU8a(): ByteArray = tag.toU8a() + junctions.toU8a(withSize = false)
    }

    public data class X3(
        val junction1: JunctionV1,
        val junction2: JunctionV1,
        val junction3: JunctionV1
    ) : JunctionsV1 {
        private val junctions: List<JunctionV1> = listOf(junction1, junction2, junction3)

        public companion object : Kind {
            override val tag: Tag = Tag.X3
        }

        override fun toU8a(): ByteArray = tag.toU8a() + junctions.toU8a(withSize = false)
    }

    public data class X4(
        val junction1: JunctionV1,
        val junction2: JunctionV1,
        val junction3: JunctionV1,
        val junction4: JunctionV1
    ) : JunctionsV1 {
        private val junctions: List<JunctionV1> = listOf(
            junction1,
            junction2,
            junction3,
            junction4
        )

        public companion object : Kind {
            override val tag: Tag = Tag.X4
        }

        override fun toU8a(): ByteArray = tag.toU8a() + junctions.toU8a(withSize = false)
    }

    public data class X5(
        val junction1: JunctionV1,
        val junction2: JunctionV1,
        val junction3: JunctionV1,
        val junction4: JunctionV1,
        val junction5: JunctionV1
    ) : JunctionsV1 {
        private val junctions: List<JunctionV1> = listOf(
            junction1,
            junction2,
            junction3,
            junction4,
            junction5
        )

        public companion object : Kind {
            override val tag: Tag = Tag.X5
        }

        override fun toU8a(): ByteArray = tag.toU8a() + junctions.toU8a(withSize = false)
    }

    public data class X6(
        val junction1: JunctionV1,
        val junction2: JunctionV1,
        val junction3: JunctionV1,
        val junction4: JunctionV1,
        val junction5: JunctionV1,
        val junction6: JunctionV1
    ) : JunctionsV1 {
        private val junctions: List<JunctionV1> = listOf(
            junction1,
            junction2,
            junction3,
            junction4,
            junction5,
            junction6
        )

        public companion object : Kind {
            override val tag: Tag = Tag.X6
        }

        override fun toU8a(): ByteArray = tag.toU8a() + junctions.toU8a(withSize = false)
    }

    public data class X7(
        val junction1: JunctionV1,
        val junction2: JunctionV1,
        val junction3: JunctionV1,
        val junction4: JunctionV1,
        val junction5: JunctionV1,
        val junction6: JunctionV1,
        val junction7: JunctionV1
    ) : JunctionsV1 {
        private val junctions: List<JunctionV1> = listOf(
            junction1,
            junction2,
            junction3,
            junction4,
            junction5,
            junction6,
            junction7
        )

        public companion object : Kind {
            override val tag: Tag = Tag.X7
        }

        override fun toU8a(): ByteArray = tag.toU8a() + junctions.toU8a(withSize = false)
    }

    public data class X8(
        val junction1: JunctionV1,
        val junction2: JunctionV1,
        val junction3: JunctionV1,
        val junction4: JunctionV1,
        val junction5: JunctionV1,
        val junction6: JunctionV1,
        val junction7: JunctionV1,
        val junction8: JunctionV1
    ) : JunctionsV1 {
        private val junctions: List<JunctionV1> = listOf(
            junction1,
            junction2,
            junction3,
            junction4,
            junction5,
            junction6,
            junction7,
            junction8
        )

        public companion object : Kind {
            override val tag: Tag = Tag.X8
        }

        override fun toU8a(): ByteArray = tag.toU8a() + junctions.toU8a(withSize = false)
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
                interior = JunctionsV1.Decoder.read(buffer)
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
                AssetId.Decoder.read(buffer),
                Fungibility.V1.read(buffer)
            )
        }
    }
}