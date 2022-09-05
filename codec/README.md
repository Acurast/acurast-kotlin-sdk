# Acurast Kotlin SDK: Codec

[![stable](https://img.shields.io/github/v/tag/acurast/acurast-kotlin-sdk?label=stable&sort=semver)](https://github.com/acurast/acurast-kotlin-sdk/releases)
[![latest](https://img.shields.io/github/v/tag/acurast/acurast-kotlin-sdk?color=orange&include_prereleases&label=latest)](https://github.com/acurast/acurast-kotlin-sdk/releases)
[![release](https://img.shields.io/jitpack/v/github/acurast/acurast-kotlin-sdk)](https://jitpack.io/#acurast/acurast-kotlin-sdk)
[![license](https://img.shields.io/github/license/acurast/acurast-kotlin-sdk)](https://github.com/acurast/acurast-kotlin-sdk/blob/main/LICENSE)

## Setup

To add `:codec` into your project:

1. Make sure the [JitPack](https://jitpack.io/) repository is included in your root `build.gradle` file:

#### Groovy

```groovy
allprojects {
  repositories {
    ...
    maven { url 'https://jitpack.io' }
  }
}
```

#### Kotlin

```kotlin
allprojects {
  repositories {
    ...
    maven("https://jitpack.io")
  }
}
```

2. Add the dependency:

#### Groovy

```groovy
dependencies {
  def acurast_sdk_version = "0.0.1"

  implementation "com.github.acurast.acurast-kotlin-sdk:codec:$acurast_sdk_version"
}
```

#### Kotlin

```kotlin
dependencies {
    val acurastSdkVersion = "0.0.1"

    implementation("com.github.acurast.acurast-kotlin-sdk:codec:$acurastSdkVersion")
}
```
