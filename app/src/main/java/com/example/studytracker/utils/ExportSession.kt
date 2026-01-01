package com.example.studytracker.utils

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.studytracker.StudyTrackerApplication
import com.example.studytracker.data.SessionDao
import com.google.gson.Gson
import kotlinx.coroutines.flow.stateIn
import java.io.File




suspend fun saveJsonToUri(uri: Uri, context: Context, jsonString: String) {
	//		val jsonString = sessionToJson(context)

	Log.d("testshit", jsonString)

	try {
		context.contentResolver.openOutputStream(uri)?.use { outputStream ->
			outputStream.write(jsonString.toByteArray())
		}
		Toast.makeText(context, "Export successful!", Toast.LENGTH_SHORT).show()
	} catch (e: Exception) {
		e.printStackTrace()
		Toast.makeText(context, "Export failed: ${e.message}", Toast.LENGTH_SHORT).show()
	}
}
