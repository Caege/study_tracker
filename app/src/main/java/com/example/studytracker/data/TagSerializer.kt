package com.example.studytracker.data

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.example.studytracker.Tags
import com.google.protobuf.InvalidProtocolBufferException

import java.io.InputStream
import java.io.OutputStream

object UserPreferencesSerializer : Serializer<Tags> {
	override val defaultValue: Tags = Tags.getDefaultInstance()
	override suspend fun readFrom(input: InputStream): Tags {
		try {
			return Tags.parseFrom(input)
		} catch (exception: InvalidProtocolBufferException) {
			throw CorruptionException("Cannot read proto.", exception)
		}
	}

	override suspend fun writeTo(t: Tags, output: OutputStream) = t.writeTo(output)
}