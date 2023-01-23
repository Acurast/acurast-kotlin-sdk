# Acurast Kotlin SDK

[![stable](https://img.shields.io/github/v/tag/acurast/acurast-kotlin-sdk?label=stable&sort=semver)](https://github.com/acurast/acurast-kotlin-sdk/releases)
[![latest](https://img.shields.io/github/v/tag/acurast/acurast-kotlin-sdk?color=orange&include_prereleases&label=latest)](https://github.com/acurast/acurast-kotlin-sdk/releases)
[![release](https://img.shields.io/jitpack/v/github/acurast/acurast-kotlin-sdk)](https://jitpack.io/#acurast/acurast-kotlin-sdk)
[![license](https://img.shields.io/github/license/acurast/acurast-kotlin-sdk)](https://github.com/acurast/acurast-kotlin-sdk/blob/main/LICENSE)

A Kotlin library to interact with the Acurast parachain.

## Setup

To add the SDK into your project:

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

2. Add the dependencies:

#### Groovy
```groovy
dependencies {
    def sdk_version = "0.0.2"

    implementation "com.github.Acurast:acurast-kotlin-sdk:$sdk_version"
}
```

#### Kotlin

```kotlin
dependencies {
    val sdkVersion = "0.0.2"

    implementation "com.github.Acurast:acurast-kotlin-sdk:$sdkVersion"
}
```

## Run tests

```sh
./gradlew test
```


## Credits

Special thanks to these libraries, which were used as reference for developing this SDK.

- https://github.com/NodleCode/substrate-client-kotlin
