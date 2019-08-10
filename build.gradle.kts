plugins {
    `java-library`
}

group = "org.embraceos"
version = "0.1.0"

repositories {
    jcenter()
}

dependencies {
    compileOnly("org.apiguardian:apiguardian-api:1.1.0")
    compileOnly("com.google.code.findbugs:jsr305:3.0.2")
}