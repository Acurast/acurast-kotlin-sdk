dependencies {
    implementation(project(":codec"))

    // Kotlin
    implementation(kotlin("stdlib"))
    implementation(Dependencies.json)
    implementation(Dependencies.gson)


    // Test
    testImplementation(Dependencies.kotlinTest)
    testImplementation(Dependencies.junit)
    testImplementation(Dependencies.mockk)
}