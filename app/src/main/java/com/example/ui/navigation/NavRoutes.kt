package com.example.ui.navigation

object NavRoutes {
    const val LANDING = "landing"
    const val AUTH = "auth"
    const val ONBOARDING = "onboarding"
    const val DASHBOARD = "dashboard"
    const val SUBJECTS = "subjects"
    const val CURRICULUM = "curriculum/{subjectId}"
    const val LESSON = "lesson/{topicId}"
    const val AI_TUTOR = "ai_tutor?subjectId={subjectId}&topicId={topicId}"
    const val PRACTICE = "practice?subjectId={subjectId}&topicId={topicId}"
    const val UNDERSTANDING_CHECK = "understanding_check/{topicId}"
    const val MISTAKES = "mistakes"
    const val CBT_MOCK = "cbt_mock"
    const val CBT_RESULTS = "cbt_results/{resultId}"
    const val STUDY_PLANNER = "study_planner"
    const val PRO_UPGRADE = "pro_upgrade"
    const val PROFILE = "profile"
    const val ADMIN = "admin"

    fun curriculum(subjectId: String) = "curriculum/$subjectId"
    fun lesson(topicId: String) = "lesson/$topicId"
    fun aiTutor(subjectId: String = "", topicId: String = "") = "ai_tutor?subjectId=$subjectId&topicId=$topicId"
    fun practice(subjectId: String = "", topicId: String = "") = "practice?subjectId=$subjectId&topicId=$topicId"
    fun understandingCheck(topicId: String) = "understanding_check/$topicId"
    fun cbtResults(resultId: Long) = "cbt_results/$resultId"
}
