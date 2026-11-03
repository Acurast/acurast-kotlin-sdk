dependencies {
    // Kotlin
    implementation(kotlin("stdlib"))

    // XXHash
    implementation(Dependencies.xxHash)

    // Bouncy Castle
    implementation(Dependencies.bouncyCastle)

    // Base 58
    implementation(Dependencies.bitcoinj)

    constraints {
        implementation(Dependencies.guava) {
            because("bitcoinj requests guava 33.4.0, which is affected by CVE-2026-102554")
        }
    }

    // Test
    testImplementation(Dependencies.kotlinTest)
    testImplementation(Dependencies.junit)
    testImplementation(Dependencies.mockk)

    testImplementation(Dependencies.bouncyCastle)
}