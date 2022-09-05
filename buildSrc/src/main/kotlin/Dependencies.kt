object Dependencies {
    private object Version {
        const val xxHash = "1.8.0"

        const val bouncyCastle = "1.70"

        // Test
        const val junit = "5.+"
        const val mockk = "1.12.2"
    }

    const val xxHash = "org.lz4:lz4-java:${Version.xxHash}"

    const val bouncyCastle = "org.bouncycastle:bcprov-jdk15on:${Version.bouncyCastle}"

    // Test
    const val kotlinTest = "org.jetbrains.kotlin:kotlin-test:${Build.Kotlin.version}"

    const val junit = "org.junit.jupiter:junit-jupiter:${Version.junit}"
    const val mockk = "io.mockk:mockk:${Version.mockk}"
}
