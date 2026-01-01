package com.example.studytracker

import android.os.Build
import androidx.activity.result.ActivityResultLauncher
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.studytracker.ui.navigation.StudyTrackerNavHost


@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun StudyTrackerApp(navController: NavHostController = rememberNavController(), modifier:
Modifier, launcher: ActivityResultLauncher<String>, importLauncher: ActivityResultLauncher<Array<String>>
) {
	StudyTrackerNavHost(navController = navController, modifier = modifier, launcher = launcher,
		importLauncher = importLauncher)
}