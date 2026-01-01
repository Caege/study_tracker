plugins {
	alias(libs.plugins.android.application)
	alias(libs.plugins.kotlin.android)
	alias(libs.plugins.kotlin.compose)
	id("com.google.devtools.ksp")
	id("com.google.protobuf") version "0.9.4"
}

android {
	namespace = "com.example.studytracker"
	compileSdk = 35

	defaultConfig {
		applicationId = "com.example.studytracker"
		minSdk = 24
		targetSdk = 35
		versionCode = 1
		versionName = "1.0"

		testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
	}

	buildTypes {
		release {
			isMinifyEnabled = false
			proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
		}
	}
	compileOptions {
		sourceCompatibility = JavaVersion.VERSION_11
		targetCompatibility = JavaVersion.VERSION_11
	}
	kotlinOptions {
		jvmTarget = "11"
	}
	buildFeatures {
		compose = true
	}
}


protobuf {
	protoc {
		artifact = "com.google.protobuf:protoc:3.21.7"
	}

	// Generates the java Protobuf-lite code for the Protobufs in this project
	generateProtoTasks {
		all().forEach { task ->
			task.builtins {
				create("java") {
					option("lite")
				}
			}
		}
	}
}

dependencies {
	// Protobuf
	implementation("com.google.protobuf:protobuf-javalite:3.21.7")
	implementation("com.google.protobuf:protobuf-kotlin-lite:3.21.7")

	implementation(libs.androidx.core.ktx)
	implementation(libs.androidx.lifecycle.runtime.ktx)
	implementation(libs.androidx.activity.compose)
	implementation(platform(libs.androidx.compose.bom))
	implementation(libs.androidx.ui)
	implementation(libs.androidx.ui.graphics)
	implementation(libs.androidx.ui.tooling.preview)
	implementation(libs.androidx.navigation.compose.android)
	implementation("androidx.compose.material3:material3:1.5.0-alpha03")
	implementation("androidx.compose.material:material-icons-extended:1.5.0")
	implementation(libs.androidx.media3.common.ktx)
	// Use the latest stable version

//	implementation(libs.androidx.material3.jvmstubs)

	testImplementation(libs.junit)
	androidTestImplementation(libs.androidx.junit)
	androidTestImplementation(libs.androidx.espresso.core)
	androidTestImplementation(platform(libs.androidx.compose.bom))
	androidTestImplementation(libs.androidx.ui.test.junit4)
	debugImplementation(libs.androidx.ui.tooling)
	debugImplementation(libs.androidx.ui.test.manifest)

	implementation("androidx.datastore:datastore-core:1.0.0")
	implementation("androidx.datastore:datastore:1.0.0")

	implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.2")

	//room dependencies
	val room_version = "2.7.2"

	implementation("androidx.room:room-runtime:$room_version")
	ksp("androidx.room:room-compiler:$room_version")
	implementation("androidx.room:room-ktx:$room_version")  // For Kotlin coroutines support

	implementation("androidx.datastore:datastore-preferences:1.0.0")

	//gson
	implementation("com.google.code.gson:gson:2.10.1")
}