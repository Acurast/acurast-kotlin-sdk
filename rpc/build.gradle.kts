dependencies {
    implementation(project(":codec"))

    // Kotlin
    implementation(kotlin("stdlib"))
    implementation(Dependencies.json)
    implementation(Dependencies.gson)

    // Ktor
    implementation(Dependencies.ktorCore)
    implementation(Dependencies.ktorCIO)
    implementation(Dependencies.ktorClientContentNegotiation)
    implementation(Dependencies.ktorSerializationKotlinxJson)
    implementation(Dependencies.ktorLoggingJvm)

    // Test
    testImplementation(Dependencies.kotlinTest)
    testImplementation(Dependencies.junit)
    testImplementation(Dependencies.mockk)
}