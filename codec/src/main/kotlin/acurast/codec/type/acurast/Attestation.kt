package acurast.codec.type.acurast

import acurast.codec.extensions.*
import acurast.codec.type.ToU8a
import acurast.codec.type.UInt16
import acurast.codec.type.UInt32
import acurast.codec.type.UInt64
import acurast.codec.type.UInt8
import acurast.codec.type.acurast.AttestationSecurityLevel.*
import java.io.UnsupportedEncodingException
import java.nio.ByteBuffer

@JvmInline
public value class AttestationChain(val bytes: List<ByteArray>) : ToU8a {
    override fun toU8a(): ByteArray = bytes.size.toLong().toCompactU8a() + bytes
        .fold(byteArrayOf()) { acc, cert -> acc + cert.size.toLong().toCompactU8a() + cert }
}

public data class Attestation(
    public val certIds: List<CertId>,
    public val content: AttestationContent,
    public val validity: AttestationValidity,
) {

    public companion object {
        public fun read(buffer: ByteBuffer): Attestation {
            val certIds = buffer.readList { CertId.read(this) }
            val content = AttestationContent.read(buffer)
            val validity = AttestationValidity.read(buffer)

            return Attestation(certIds, content, validity)
        }
    }
}

public data class CertId(public val issuerName: ByteArray, public val serialNumber: ByteArray) {
    public companion object {
        public fun read(buffer: ByteBuffer): CertId {
            val issuerName = buffer.readByteArray()
            val serialNumber = buffer.readByteArray()

            return CertId(issuerName, serialNumber)
        }
    }
}

public sealed interface AttestationContent {
    public val id: Byte

    public data class KeyDescription(
        public val attestationSecurityLevel: AttestationSecurityLevel,
        public val keyMintSecurityLevel: AttestationSecurityLevel,
        public val softwareEnforced: AuthorizationList,
        public val teeEnforced: AuthorizationList,
    ) : AttestationContent {
        override val id: Byte = ID

        public companion object {
            internal const val ID: Byte = 0

            public fun read(buffer: ByteBuffer): KeyDescription {
                val attestationSecurityLevel = AttestationSecurityLevel.read(buffer)
                val keyMintSecurityLevel = AttestationSecurityLevel.read(buffer)
                val softwareEnforced = AuthorizationList.read(buffer)
                val teeEnforced = AuthorizationList.read(buffer)

                return KeyDescription(attestationSecurityLevel, keyMintSecurityLevel, softwareEnforced, teeEnforced)
            }
        }
    }

    public data class DeviceAttestation(
        public val keyUsageProperties: KeyUsageProperties,
        public val deviceOsInformation: DeviceOsInformation,
        public val nonce: Nonce,
    ) : AttestationContent {
        override val id: Byte = ID

        public companion object {
            internal const val ID: Byte = 1

            public fun read(buffer: ByteBuffer): DeviceAttestation {
                val keyUsageProperties = KeyUsageProperties.read(buffer)
                val deviceOsInformation = DeviceOsInformation.read(buffer)
                val nonce = Nonce.read(buffer)

                return DeviceAttestation(keyUsageProperties, deviceOsInformation, nonce)
            }
        }
    }

    public companion object {
        public fun read(buffer: ByteBuffer): AttestationContent =
            when (val id = buffer.readByte()) {
                KeyDescription.ID -> KeyDescription.read(buffer)
                DeviceAttestation.ID -> DeviceAttestation.read(buffer)
                else -> throw UnsupportedEncodingException("Unknown AttestationContent $id.")
            }
    }
}

public enum class AttestationSecurityLevel(public val id: Byte) {
    Software(0),
    TrustedEnvironment(1),
    StrongBox(2),
    Unknown(3);

    public companion object {
        public fun read(buffer: ByteBuffer): AttestationSecurityLevel =
            when (val id = buffer.readByte()) {
                Software.id -> Software
                TrustedEnvironment.id -> TrustedEnvironment
                StrongBox.id -> StrongBox
                Unknown.id -> Unknown
                else -> throw UnsupportedEncodingException("Unknown AttestationSecurityLevel $id.")
            }
    }
}

public data class AuthorizationList(
    public val purpose: ByteArray?,
    public val algorithm: UInt8?,
    public val keySize: UInt16?,
    public val digest: ByteArray?,
    public val padding: ByteArray?,
    public val ecCurve: UInt8?,
    public val rsaPublicExponent: UInt64?,
    public val mgfDigest: ByteArray?,
    public val rollbackResistance: Boolean?,
    public val earlyBootOnly: Boolean?,
    public val activeDateTime: UInt64?,
    public val originationExpireDateTime: UInt64?,
    public val usageExpireDateTime: UInt64?,
    public val usageCountLimit: UInt64?,
    public val noAuthRequired: Boolean,
    public val userAuthType: UInt8?,
    public val authTimeout: UInt32?,
    public val allowWhileOnBody: Boolean,
    public val trustedUserPresenceRequired: Boolean?,
    public val trustedConfirmationRequired: Boolean?,
    public val unlockedDeviceRequired: Boolean?,
    public val allApplications: Boolean?,
    public val applicationId: ByteArray?,
    public val creationDateTime: UInt64?,
    public val origin: UInt8?,
    public val rootOfTrust: RootOfTrust?,
    public val osVersion: UInt32?,
    public val osPatchLevel: UInt32?,
    public val attestationApplicationId: AttestationApplicationId?,
    public val attestationIdBrand: ByteArray?,
    public val attestationIdDevice: ByteArray?,
    public val attestationIdProduct: ByteArray?,
    public val attestationIdSerial: ByteArray?,
    public val attestationIdImei: ByteArray?,
    public val attestationIdMeid: ByteArray?,
    public val attestationIdManufacturer: ByteArray?,
    public val attestationIdModel: ByteArray?,
    public val vendorPatchLevel: UInt32?,
    public val bootPatchLevel: UInt32?,
    public val deviceUniqueAttestation: Boolean?,
) {
    public companion object {
        public fun read(buffer: ByteBuffer): AuthorizationList {
            val purpose = buffer.readOptional { readByteArray() }
            val algorithm = buffer.readOptional { readU8() }
            val keySize = buffer.readOptional { readU16() }
            val digest = buffer.readOptional { readByteArray() }
            val padding = buffer.readOptional { readByteArray() }
            val ecCurve = buffer.readOptional { readU8() }
            val rsaPublicExponent = buffer.readOptional { readU64() }
            val mgfDigest = buffer.readOptional { readByteArray() }
            val rollbackResistance = buffer.readOptionalBoolean()
            val earlyBootOnly = buffer.readOptionalBoolean()
            val activeDateTime = buffer.readOptional { readU64() }
            val originationExpireDateTime = buffer.readOptional { readU64() }
            val usageExpireDateTime = buffer.readOptional { readU64() }
            val usageCountLimit = buffer.readOptional { readU64() }
            val noAuthRequired = buffer.readBoolean()
            val userAuthType = buffer.readOptional { readU8() }
            val authTimeout = buffer.readOptional { readU32() }
            val allowWhileOnBody = buffer.readBoolean()
            val trustedUserPresenceRequired = buffer.readOptionalBoolean()
            val trustedConfirmationRequired = buffer.readOptionalBoolean()
            val unlockedDeviceRequired = buffer.readOptionalBoolean()
            val allApplications = buffer.readOptionalBoolean()
            val applicationId = buffer.readOptional { readByteArray() }
            val creationDateTime = buffer.readOptional { readU64() }
            val origin = buffer.readOptional { readU8() }
            val rootOfTrust = buffer.readOptional { RootOfTrust.read(this) }
            val osVersion = buffer.readOptional { readU32() }
            val osPatchLevel = buffer.readOptional { readU32() }
            val attestationApplicationId = buffer.readOptional { AttestationApplicationId.read(this) }
            val attestationIdBrand = buffer.readOptional { readByteArray() }
            val attestationIdDevice = buffer.readOptional { readByteArray() }
            val attestationIdProduct = buffer.readOptional { readByteArray() }
            val attestationIdSerial = buffer.readOptional { readByteArray() }
            val attestationIdImei = buffer.readOptional { readByteArray() }
            val attestationIdMeid = buffer.readOptional { readByteArray() }
            val attestationIdManufacturer = buffer.readOptional { readByteArray() }
            val attestationIdModel = buffer.readOptional { readByteArray() }
            val vendorPatchLevel = buffer.readOptional { readU32() }
            val bootPatchLevel = buffer.readOptional { readU32() }
            val deviceUniqueAttestation = buffer.readOptionalBoolean()

            return AuthorizationList(
                purpose,
                algorithm?.let { UInt8(it.toByte()) },
                keySize?.let { UInt16(it.toShort()) },
                digest,
                padding,
                ecCurve?.let { UInt8(it.toByte()) },
                rsaPublicExponent?.let { UInt64(it.toLong()) },
                mgfDigest,
                rollbackResistance,
                earlyBootOnly,
                activeDateTime?.let { UInt64(it.toLong()) },
                originationExpireDateTime?.let { UInt64(it.toLong()) },
                usageExpireDateTime?.let { UInt64(it.toLong()) },
                usageCountLimit?.let { UInt64(it.toLong()) },
                noAuthRequired,
                userAuthType?.let { UInt8(it.toByte()) },
                authTimeout?.let { UInt32(it.toInt()) },
                allowWhileOnBody,
                trustedUserPresenceRequired,
                trustedConfirmationRequired,
                unlockedDeviceRequired,
                allApplications,
                applicationId,
                creationDateTime?.let { UInt64(it.toLong()) },
                origin?.let { UInt8(it.toByte()) },
                rootOfTrust,
                osVersion?.let { UInt32(it.toInt()) },
                osPatchLevel?.let { UInt32(it.toInt()) },
                attestationApplicationId,
                attestationIdBrand,
                attestationIdDevice,
                attestationIdProduct,
                attestationIdSerial,
                attestationIdImei,
                attestationIdMeid,
                attestationIdManufacturer,
                attestationIdModel,
                vendorPatchLevel?.let { UInt32(it.toInt()) },
                bootPatchLevel?.let { UInt32(it.toInt()) },
                deviceUniqueAttestation,
            )
        }
    }
}

public data class RootOfTrust(
    public val verifiedBootKey: ByteArray,
    public val deviceLocked: Boolean,
    public val verifiedBootState: VerifiedBootState,
    public val verifiedBootHash: ByteArray?,
) {
    public companion object {
        public fun read(buffer: ByteBuffer): RootOfTrust {
            val verifiedBootKey = buffer.readByteArray()
            val deviceLocked = buffer.readBoolean()
            val verifiedBootState = VerifiedBootState.read(buffer)
            val verifiedBootHash = buffer.readOptional { readByteArray() }

            return RootOfTrust(verifiedBootKey, deviceLocked, verifiedBootState, verifiedBootHash)
        }
    }
}

public enum class VerifiedBootState(public val id: Byte) {
    Verified(0),
    SelfSigned(1),
    Unverified(2),
    Failed(3);

    public companion object {
        public fun read(buffer: ByteBuffer): VerifiedBootState =
            when (val id = buffer.readByte()) {
                Verified.id -> Verified
                SelfSigned.id -> SelfSigned
                Unverified.id -> Unverified
                Failed.id -> Failed
                else -> throw UnsupportedEncodingException("Unknown VerifiedBootState $id.")
            }
    }
}

public data class AttestationApplicationId(
    public val packageInfos: List<AttestationPackageInfo>,
    public val signatureDigests: List<ByteArray>,
) {
    public companion object {
        public fun read(buffer: ByteBuffer): AttestationApplicationId {
            val packageInfos = buffer.readList { AttestationPackageInfo.read(this) }
            val signatureDigests = buffer.readList { readByteArray() }

            return AttestationApplicationId(packageInfos, signatureDigests)
        }
    }
}

public data class AttestationPackageInfo(public val packageName: ByteArray, public val version: Long) {
    public companion object {
        public fun read(buffer: ByteBuffer): AttestationPackageInfo {
            val packageName = buffer.readByteArray()
            val version = buffer.long

            return AttestationPackageInfo(packageName, version)
        }
    }
}

public data class KeyUsageProperties(
    public val t4: Long?,
    public val t1200: Long?,
    public val t1201: Long?,
    public val t1202: Long?,
    public val t1203: Long?,
    public val t1204: ByteArray?,
    public val t5: ByteArray?,
    public val t1206: Long?,
    public val t1207: Long?,
    public val t1209: Long?,
    public val t1210: Long?,
    public val t1211: Long?,
) {
    public companion object {
        public fun read(buffer: ByteBuffer): KeyUsageProperties {
            val t4 = buffer.readOptional { long }
            val t1200 = buffer.readOptional { long }
            val t1201 = buffer.readOptional { long }
            val t1202 = buffer.readOptional { long }
            val t1203 = buffer.readOptional { long }
            val t1204 = buffer.readOptional { readByteArray() }
            val t5 = buffer.readOptional { readByteArray() }
            val t1206 = buffer.readOptional { long }
            val t1207 = buffer.readOptional { long }
            val t1209 = buffer.readOptional { long }
            val t1210 = buffer.readOptional { long }
            val t1211 = buffer.readOptional { long }

            return KeyUsageProperties(t4, t1200, t1201, t1202, t1203, t1204, t5, t1206, t1207, t1209, t1210, t1211)
        }
    }
}

public data class DeviceOsInformation(
    public val t1400: ByteArray?,
    public val t1104: Long?,
    public val t1403: ByteArray?,
    public val t1420: ByteArray?,
    public val t1026: ByteArray?,
    public val t1029: ByteArray?,
) {
    public companion object {
        public fun read(buffer: ByteBuffer): DeviceOsInformation {
            val t1400 = buffer.readOptional { readByteArray() }
            val t1104 = buffer.readOptional { long }
            val t1403 = buffer.readOptional { readByteArray() }
            val t1420 = buffer.readOptional { readByteArray() }
            val t1026 = buffer.readOptional { readByteArray() }
            val t1029 = buffer.readOptional { readByteArray() }

            return DeviceOsInformation(t1400, t1104, t1403, t1420, t1026, t1029)
        }
    }
}

public data class Nonce(public val nonce: ByteArray?) {
    public companion object {
        public fun read(buffer: ByteBuffer): Nonce {
            val nonce = buffer.readOptional { readByteArray() }

            return Nonce(nonce)
        }
    }
}

public data class AttestationValidity(public val notBefore: UInt64, public val notAfter: UInt64) {
    public companion object {
        public fun read(buffer: ByteBuffer): AttestationValidity {
            val notBefore = UInt64(buffer.readU64().toLong())
            val notAfter = UInt64(buffer.readU64().toLong())

            return AttestationValidity(notBefore, notAfter)
        }
    }
}
