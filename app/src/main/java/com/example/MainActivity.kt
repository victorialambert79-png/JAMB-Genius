package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.data.AppDatabase
import com.example.data.repository.JambRepository
import com.example.ui.components.JambBottomNavigation
import com.example.ui.components.JambTopAppBar
import com.example.ui.navigation.NavRoutes
import com.example.ui.screens.*
import com.example.ui.theme.JambGeniusTheme

class MainActivity : ComponentActivity() {

    private lateinit var database: AppDatabase
    private lateinit var repository: JambRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        database = AppDatabase.getDatabase(this, lifecycleScope)
        repository = JambRepository(database)

        setContent {
            JambGeniusTheme {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route ?: NavRoutes.LANDING

                val userProfile by repository.userProfile.collectAsState(initial = null)

                val showBottomNav = currentRoute in listOf(
                    NavRoutes.DASHBOARD,
                    NavRoutes.SUBJECTS,
                    "ai_tutor",
                    "cbt_mock",
                    NavRoutes.PROFILE
                ) || currentRoute.startsWith("ai_tutor?")

                val showTopBar = currentRoute in listOf(
                    NavRoutes.DASHBOARD,
                    NavRoutes.SUBJECTS,
                    NavRoutes.PROFILE,
                    "mistakes",
                    "study_planner"
                )

                val topBarTitle = when {
                    currentRoute == NavRoutes.DASHBOARD -> "JAMB Genius"
                    currentRoute == NavRoutes.SUBJECTS -> "Official IBASS Syllabus"
                    currentRoute == NavRoutes.PROFILE -> "Student Profile"
                    currentRoute == "mistakes" -> "Mistake Analyzer"
                    currentRoute == "study_planner" -> "Study Schedule"
                    else -> "JAMB Genius"
                }

                val topBarSubtitle = when {
                    currentRoute == NavRoutes.DASHBOARD -> "UTME Preparation Platform"
                    currentRoute == NavRoutes.SUBJECTS -> "Verified JAMB Curriculum"
                    currentRoute == NavRoutes.PROFILE -> "Badges & Performance"
                    currentRoute == "mistakes" -> "High-Yield Error Fixes"
                    currentRoute == "study_planner" -> "Adaptive Revision Pacing"
                    else -> null
                }

                Scaffold(
                    topBar = {
                        if (showTopBar) {
                            JambTopAppBar(
                                title = topBarTitle,
                                subtitle = topBarSubtitle,
                                showBackButton = currentRoute in listOf("mistakes", "study_planner"),
                                onBackClick = { navController.popBackStack() },
                                userProfile = userProfile,
                                onUpgradeClick = { navController.navigate(NavRoutes.PRO_UPGRADE) },
                                onAdminClick = { navController.navigate(NavRoutes.ADMIN) }
                            )
                        }
                    },
                    bottomBar = {
                        if (showBottomNav) {
                            JambBottomNavigation(
                                currentRoute = currentRoute,
                                onNavigate = { route ->
                                    navController.navigate(route) {
                                        popUpTo(NavRoutes.DASHBOARD) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            )
                        }
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = NavRoutes.LANDING,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        // 1. Landing Screen
                        composable(NavRoutes.LANDING) {
                            LandingScreen(
                                onStartFree = { navController.navigate(NavRoutes.ONBOARDING) },
                                onLoginClick = { navController.navigate(NavRoutes.AUTH) },
                                onAdminClick = { navController.navigate(NavRoutes.ADMIN) }
                            )
                        }

                        // 2. Auth Screen
                        composable(NavRoutes.AUTH) {
                            AuthScreen(
                                onAuthSuccess = { navController.navigate(NavRoutes.DASHBOARD) },
                                onSkipToOnboarding = { navController.navigate(NavRoutes.ONBOARDING) },
                                onBackToLanding = { navController.popBackStack() }
                            )
                        }

                        // 3. Onboarding Flow
                        composable(NavRoutes.ONBOARDING) {
                            OnboardingScreen(
                                repository = repository,
                                onComplete = {
                                    navController.navigate(NavRoutes.DASHBOARD) {
                                        popUpTo(NavRoutes.LANDING) { inclusive = true }
                                    }
                                }
                            )
                        }

                        // 4. Student Dashboard
                        composable(NavRoutes.DASHBOARD) {
                            DashboardScreen(
                                repository = repository,
                                onNavigateToSubject = { subjId -> navController.navigate(NavRoutes.curriculum(subjId)) },
                                onNavigateToLesson = { topicId -> navController.navigate(NavRoutes.lesson(topicId)) },
                                onNavigateToAiTutor = { sId, tId -> navController.navigate(NavRoutes.aiTutor(sId, tId)) },
                                onNavigateToPractice = { sId, tId -> navController.navigate(NavRoutes.practice(sId, tId)) },
                                onNavigateToUnderstandingCheck = { topicId -> navController.navigate(NavRoutes.understandingCheck(topicId)) },
                                onNavigateToCbtMock = { navController.navigate(NavRoutes.CBT_MOCK) },
                                onNavigateToMistakes = { navController.navigate(NavRoutes.MISTAKES) },
                                onNavigateToStudyPlanner = { navController.navigate(NavRoutes.STUDY_PLANNER) },
                                onUpgradeClick = { navController.navigate(NavRoutes.PRO_UPGRADE) }
                            )
                        }

                        // 5. Subjects & Syllabus List
                        composable(NavRoutes.SUBJECTS) {
                            SubjectsListScreen(
                                repository = repository,
                                onSubjectClick = { subjId -> navController.navigate(NavRoutes.curriculum(subjId)) }
                            )
                        }

                        // 6. Subject Curriculum (Topics, Subtopics & Objectives)
                        composable(
                            route = NavRoutes.CURRICULUM,
                            arguments = listOf(navArgument("subjectId") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val subjectId = backStackEntry.arguments?.getString("subjectId") ?: "subj_bio"
                            CurriculumScreen(
                                subjectId = subjectId,
                                repository = repository,
                                onTopicLessonClick = { topicId -> navController.navigate(NavRoutes.lesson(topicId)) },
                                onTopicPracticeClick = { sId, tId -> navController.navigate(NavRoutes.practice(sId, tId)) },
                                onTopicAiTutorClick = { sId, tId -> navController.navigate(NavRoutes.aiTutor(sId, tId)) },
                                onTopicQuizClick = { topicId -> navController.navigate(NavRoutes.understandingCheck(topicId)) }
                            )
                        }

                        // 7. Verified Syllabus Lesson Reader
                        composable(
                            route = NavRoutes.LESSON,
                            arguments = listOf(navArgument("topicId") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val topicId = backStackEntry.arguments?.getString("topicId") ?: "bio_topic_1"
                            LessonScreen(
                                topicId = topicId,
                                repository = repository,
                                onNavigateToAiTutor = { sId, tId -> navController.navigate(NavRoutes.aiTutor(sId, tId)) },
                                onNavigateToQuiz = { tId -> navController.navigate(NavRoutes.understandingCheck(tId)) },
                                onFinishLesson = { navController.popBackStack() }
                            )
                        }

                        // 8. AI Personal Tutor Chat
                        composable(
                            route = NavRoutes.AI_TUTOR,
                            arguments = listOf(
                                navArgument("subjectId") { type = NavType.StringType; defaultValue = "" },
                                navArgument("topicId") { type = NavType.StringType; defaultValue = "" }
                            )
                        ) { backStackEntry ->
                            val subjectId = backStackEntry.arguments?.getString("subjectId") ?: ""
                            val topicId = backStackEntry.arguments?.getString("topicId") ?: ""
                            AiTutorScreen(
                                subjectId = subjectId,
                                topicId = topicId,
                                repository = repository,
                                onUpgradeClick = { navController.navigate(NavRoutes.PRO_UPGRADE) }
                            )
                        }

                        // 9. Practice Engine
                        composable(
                            route = NavRoutes.PRACTICE,
                            arguments = listOf(
                                navArgument("subjectId") { type = NavType.StringType; defaultValue = "" },
                                navArgument("topicId") { type = NavType.StringType; defaultValue = "" }
                            )
                        ) { backStackEntry ->
                            val subjectId = backStackEntry.arguments?.getString("subjectId") ?: ""
                            val topicId = backStackEntry.arguments?.getString("topicId") ?: ""
                            PracticeScreen(
                                subjectId = subjectId,
                                topicId = topicId,
                                repository = repository,
                                onNavigateToAiTutor = { sId, tId -> navController.navigate(NavRoutes.aiTutor(sId, tId)) }
                            )
                        }

                        // 10. Understanding Check Quiz
                        composable(
                            route = NavRoutes.UNDERSTANDING_CHECK,
                            arguments = listOf(navArgument("topicId") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val topicId = backStackEntry.arguments?.getString("topicId") ?: "bio_topic_1"
                            UnderstandingCheckScreen(
                                topicId = topicId,
                                repository = repository,
                                onNavigateBackToCurriculum = { navController.popBackStack() },
                                onNavigateToAiTutor = { sId, tId -> navController.navigate(NavRoutes.aiTutor(sId, tId)) }
                            )
                        }

                        // 11. Mistake Analyzer
                        composable(NavRoutes.MISTAKES) {
                            MistakesScreen(
                                repository = repository,
                                onNavigateToAiTutor = { sId, tId -> navController.navigate(NavRoutes.aiTutor(sId, tId)) },
                                onNavigateToPractice = { sId, tId -> navController.navigate(NavRoutes.practice(sId, tId)) }
                            )
                        }

                        // 12. CBT Mock Examination
                        composable(NavRoutes.CBT_MOCK) {
                            CbtMockScreen(
                                repository = repository,
                                onExamSubmitted = { mockId ->
                                    navController.navigate(NavRoutes.cbtResults(mockId)) {
                                        popUpTo(NavRoutes.CBT_MOCK) { inclusive = true }
                                    }
                                },
                                onExitExam = { navController.popBackStack() }
                            )
                        }

                        // 13. CBT Results & Diagnostic Report
                        composable(
                            route = NavRoutes.CBT_RESULTS,
                            arguments = listOf(navArgument("resultId") { type = NavType.LongType })
                        ) { backStackEntry ->
                            val resultId = backStackEntry.arguments?.getLong("resultId") ?: 1L
                            CbtResultsScreen(
                                resultId = resultId,
                                repository = repository,
                                onNavigateToMistakes = { navController.navigate(NavRoutes.MISTAKES) },
                                onNavigateToDashboard = {
                                    navController.navigate(NavRoutes.DASHBOARD) {
                                        popUpTo(NavRoutes.DASHBOARD) { inclusive = true }
                                    }
                                },
                                onNavigateToAiTutor = { sId, tId -> navController.navigate(NavRoutes.aiTutor(sId, tId)) }
                            )
                        }

                        // 14. Study Planner
                        composable(NavRoutes.STUDY_PLANNER) {
                            StudyPlannerScreen(
                                repository = repository,
                                onNavigateToLesson = { topicId -> navController.navigate(NavRoutes.lesson(topicId)) },
                                onNavigateToPractice = { sId, tId -> navController.navigate(NavRoutes.practice(sId, tId)) },
                                onNavigateToQuiz = { topicId -> navController.navigate(NavRoutes.understandingCheck(topicId)) }
                            )
                        }

                        // 15. Pro Upgrade Screen
                        composable(NavRoutes.PRO_UPGRADE) {
                            ProUpgradeScreen(
                                repository = repository,
                                onSuccess = { navController.navigate(NavRoutes.DASHBOARD) },
                                onBackClick = { navController.popBackStack() }
                            )
                        }

                        // 16. Profile Screen
                        composable(NavRoutes.PROFILE) {
                            ProfileScreen(
                                repository = repository,
                                onUpgradeClick = { navController.navigate(NavRoutes.PRO_UPGRADE) },
                                onAdminClick = { navController.navigate(NavRoutes.ADMIN) },
                                onLogoutClick = {
                                    navController.navigate(NavRoutes.LANDING) {
                                        popUpTo(0) { inclusive = true }
                                    }
                                }
                            )
                        }

                        // 17. Admin Dashboard
                        composable(NavRoutes.ADMIN) {
                            AdminScreen(
                                repository = repository,
                                onBackClick = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}
