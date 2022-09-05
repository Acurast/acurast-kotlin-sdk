dependencies {
    // Kotlin
    implementation(kotlin("stdlib"))
    implementation(Dependencies.xxHash)

    // Test
    testImplementation(Dependencies.kotlinTest)
    testImplementation(Dependencies.junit)
    testImplementation(Dependencies.mockk)

    testImplementation(Dependencies.bouncyCastle)
}
