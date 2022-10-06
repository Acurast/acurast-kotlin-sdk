dependencies {
    implementation(project(":codec"))

    // Kotlin
    implementation(kotlin("stdlib"))
    implementation(Dependencies.json)


    // Test
    testImplementation(Dependencies.kotlinTest)
    testImplementation(Dependencies.junit)
    testImplementation(Dependencies.mockk)
}