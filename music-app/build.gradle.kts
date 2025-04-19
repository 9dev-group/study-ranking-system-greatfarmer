plugins {
	kotlin("jvm") version "1.9.25" apply false
	kotlin("plugin.spring") version "1.9.25" apply false
	kotlin("plugin.jpa") version "1.9.25" apply false
	id("org.springframework.boot") version "3.3.10" apply false
	id("io.spring.dependency-management") version "1.1.7" apply false
}

allprojects {
	group = "kr.co"
	version = "0.0.1-SNAPSHOT"

	repositories {
		mavenCentral()
	}
}

subprojects {
	apply(plugin = "org.jetbrains.kotlin.jvm")
	apply(plugin = "org.jetbrains.kotlin.plugin.spring")
	apply(plugin = "org.jetbrains.kotlin.plugin.jpa")
	apply(plugin = "io.spring.dependency-management")

	afterEvaluate {
		dependencies {
			"implementation"("org.jetbrains.kotlin:kotlin-reflect:1.9.25")
			"implementation"("com.fasterxml.jackson.module:jackson-module-kotlin")
			"implementation"("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
		}
	}

	tasks.withType<Test> {
		useJUnitPlatform()
	}
}