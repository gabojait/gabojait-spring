import java.io.ByteArrayOutputStream

plugins {
	id ("org.springframework.boot") version "2.7.1"
	id ("io.spring.dependency-management") version "1.0.11.RELEASE"
	id ("java")
}

val major = 0
val minor = 1
val patch = 0 // getRevisionCount()

group = "inu-appcenter"
version = "$major.$minor.$patch"
java.sourceCompatibility = JavaVersion.VERSION_11

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation ("org.springframework.boot:spring-boot-starter-data-mongodb")
	implementation ("org.springframework.boot:spring-boot-starter-validation")
	implementation ("org.springframework.boot:spring-boot-starter-web")
	implementation ("org.springframework.boot:spring-boot-starter-security")
	implementation ("org.hibernate.ogm:hibernate-ogm-mongodb:5.4.1.Final")
	implementation ("io.jsonwebtoken:jjwt:0.9.1")
	implementation ("io.springfox:springfox-boot-starter:3.0.0")
	implementation ("io.springfox:springfox-swagger2:2.9.2")
	implementation ("io.springfox:springfox-swagger-ui:2.9.2")
	compileOnly ("org.projectlombok:lombok")
	annotationProcessor ("org.projectlombok:lombok")
	testImplementation ("org.springframework.boot:spring-boot-starter-test")
	testImplementation ("org.springframework.security:spring-security-test")
}

tasks.withType<Test> {
	useJUnitPlatform()
}

/**
 * Docker 이미지 빌드
 */
val dockerUrl: String? by project
val dockerImageName: String? by project
val dockerUsername: String? by project
val dockerPassword: String? by project
val dockerTag: String? by project

tasks.bootBuildImage {
	imageName = "${dockerImageName ?: project.name}:${dockerTag ?: project.version}"

	print("[bootBuildImage] 이미지 이름: $imageName")

	if (dockerUrl != null) {
		isPublish = true
		docker {
			publishRegistry {
				url = dockerUrl
				username = dockerUsername
				password = dockerPassword
			}
		}
	}
}

/**
 * Git 리비전 카운트를 가져와서 버전 카운터로 사용
 */
//fun getRevisionCount(): Int {
//	val byteOut = ByteArrayOutputStream()
//	project.exec {
//		commandLine = "git rev-list --count HEAD".split(" ")
//		standardOutput = byteOut
//	}
//	val output = String(byteOut.toByteArray())
//
//	return Integer.parseInt(output.trim())
//}