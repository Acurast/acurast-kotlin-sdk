dependencies {
    // Kotlin
    implementation(kotlin("stdlib"))

    // XXHash
    implementation(Dependencies.xxHash)

    // Bouncy Castle
    implementation(Dependencies.bouncyCastle)

    // Base 58
    implementation(Dependencies.bitcoinj)

    // Test
    testImplementation(Dependencies.kotlinTest)
    testImplementation(Dependencies.junit)
    testImplementation(Dependencies.mockk)

    testImplementation(Dependencies.bouncyCastle)
}