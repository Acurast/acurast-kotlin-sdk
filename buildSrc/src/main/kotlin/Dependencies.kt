object Dependencies {
    private object Version {
        const val xxHash = "1.8.0"
        const val json = "20220320"
        const val bouncyCastle = "1.70"
        const val bitcoinj = "0.16.1"
        const val gson = "2.10"

        const val ktor = "2.0.2"

        // Test
        const val junit = "5.+"
        const val mockk = "1.12.2"
    }

    const val xxHash = "org.lz4:lz4-java:${Version.xxHash}"
    const val json = "org.json:json:${Version.json}"
    const val gson = "com.google.code.gson:gson:${Version.gson}"

    const val ktorCore = "io.ktor:ktor-client-core:${Version.ktor}"
    const val ktorCIO = "io.ktor:ktor-client-cio:${Version.ktor}"
    const val ktorClientContentNegotiation = "io.ktor:ktor-client-content-negotiation:${Version.ktor}"
    const val ktorSerializationKotlinxJson = "io.ktor:ktor-serialization-kotlinx-json:${Version.ktor}"
    const val ktorLoggingJvm = "io.ktor:ktor-client-logging-jvm:${Version.ktor}"

    const val bouncyCastle = "org.bouncycastle:bcprov-jdk15on:${Version.bouncyCastle}"
    const val bitcoinj = "org.bitcoinj:bitcoinj-core:${Version.bitcoinj}"

    // Test
    const val kotlinTest = "org.jetbrains.kotlin:kotlin-test:${Build.Kotlin.version}"

    const val junit = "org.junit.jupiter:junit-jupiter:${Version.junit}"
    const val mockk = "io.mockk:mockk:${Version.mockk}"
}
