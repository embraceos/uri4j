plugins {
    `java-library`
}

group = "org.embraceos"
version = "0.0.9"

repositories {
    jcenter()
}

dependencies {
    compileOnly("org.apiguardian:apiguardian-api:1.1.0")
    compileOnly("com.google.code.findbugs:jsr305:3.0.2")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}
