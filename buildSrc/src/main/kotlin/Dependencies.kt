object Dependencies {
    private object Version {
        const val xxHash = "1.8.0"
        const val json = "20220320"
        const val bouncyCastle = "1.70"
        const val bitcoinj = "0.16.1"
        const val gson = "2.10"

        // Test
        const val junit = "5.+"
        const val mockk = "1.12.2"
    }

    const val xxHash = "org.lz4:lz4-java:${Version.xxHash}"
    const val json = "org.json:json:${Version.json}"
    const val gson = "com.google.code.gson:gson:${Version.gson}"

    const val bouncyCastle = "org.bouncycastle:bcprov-jdk15on:${Version.bouncyCastle}"
    const val bitcoinj = "org.bitcoinj:bitcoinj-core:${Version.bitcoinj}"

    // Test
    const val kotlinTest = "org.jetbrains.kotlin:kotlin-test:${Build.Kotlin.version}"

    const val junit = "org.junit.jupiter:junit-jupiter:${Version.junit}"
    const val mockk = "io.mockk:mockk:${Version.mockk}"
}
