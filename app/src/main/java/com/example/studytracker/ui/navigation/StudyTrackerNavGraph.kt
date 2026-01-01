package com.example.studytracker.ui.navigation

import android.os.Build
import android.os.Build.VERSION_CODES.TIRAMISU
import androidx.activity.result.ActivityResultLauncher
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.studytracker.ui.screens.AddSessionDestination
import com.example.studytracker.ui.screens.AddSessionScreen
import com.example.studytracker.ui.screens.HomeDestination
import com.example.studytracker.ui.screens.HomeScreen
import com.example.studytracker.ui.screens.SettingsDestination
import com.example.studytracker.ui.screens.SettingsScreen
import com.example.studytracker.ui.screens.StartSessionDestination
import com.example.studytracker.ui.screens.StartSessionScreen
import com.example.studytracker.ui.screens.StudyHistoryScaffold
import com.example.studytracker.ui.screens.StudyHistoryScreen

@RequiresApi(TIRAMISU)
@Composable
fun StudyTrackerNavHost(navController: NavHostController, modifier: Modifier = Modifier,
												launcher: ActivityResultLauncher<String>, importLauncher:
												ActivityResultLauncher<Array<String>>) {
	NavHost(
		navController = navController,
		startDestination = HomeDestination.route,
//		startDestination = SettingsDestination.route,
		modifier = modifier
	) {
		composable(route = HomeDestination.route) {
			HomeScreen(
				onAddSessionScreenClick = {navController.navigate("add_session")},
				onStartSessionScreenClick = {navController.navigate("start_session")},
				onStudyHistoryScreenClick = {navController.navigate("study_history")},
				onSettingsClick = {navController.navigate(SettingsDestination.route)}
			)
		}

		composable(route = AddSessionDestination.route) {
			AddSessionScreen(onBackClick = {navController.navigate(HomeDestination.route)})
		}

		composable(route = StartSessionDestination.route ) {
			StartSessionScreen(onBackClick = {navController.navigate(HomeDestination.route)})
		}

		composable(route = StudyHistoryScreen.route ) {
		StudyHistoryScaffold(onBackClick = {navController.navigate(HomeDestination.route)})
		}

		composable(route = SettingsDestination.route ) {
			SettingsScreen(launcher = launcher, importLauncher = importLauncher, onBackClick =
			{navController.navigate(HomeDestination.route)})
		}
	}

}
