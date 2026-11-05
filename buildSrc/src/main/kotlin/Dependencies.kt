object Dependencies {
    private object Version {
        const val xxHash = "1.12.1"
        const val json = "20260814"
        const val bouncyCastle = "1.86"
        const val bitcoinj = "0.17.1"
        const val guava = "33.8.0-android"
        const val gson = "2.10"

        // Test
        const val junit = "5.+"
        const val mockk = "1.12.2"
        const val coroutines = "1.7.3"
    }

    const val xxHash = "at.yawk.lz4:lz4-java:${Version.xxHash}"
    const val json = "org.json:json:${Version.json}"
    const val gson = "com.google.code.gson:gson:${Version.gson}"

    const val bouncyCastle = "org.bouncycastle:bcprov-jdk15to18:${Version.bouncyCastle}"
    const val bitcoinj = "org.bitcoinj:bitcoinj-core:${Version.bitcoinj}"
    const val guava = "com.google.guava:guava:${Version.guava}"

    // Test
    const val kotlinTest = "org.jetbrains.kotlin:kotlin-test:${Build.Kotlin.version}"

    const val junit = "org.junit.jupiter:junit-jupiter:${Version.junit}"
    const val mockk = "io.mockk:mockk:${Version.mockk}"
    const val coroutines = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Version.coroutines}"
}
