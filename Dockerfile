FROM alpine:latest

RUN apk add --no-cache openjdk17 bash

RUN mkdir /build
WORKDIR /build

# copy source
COPY . /build

# clean project
RUN /build/gradlew --project-dir /build clean

# build project
RUN /build/gradlew --project-dir /build assemble

# copy jars
RUN /build/gradlew --project-dir /build zipJars

CMD ["/build/gradlew", "test"]
