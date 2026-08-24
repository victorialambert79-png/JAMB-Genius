package com.example.data.repository

import com.example.data.AppDatabase
import com.example.data.ai.AiTutorService
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.*

class JambRepository(
    private val db: AppDatabase,
    private val aiService: AiTutorService = AiTutorService()
) {
    // User Profile
    val userProfile: Flow<UserProfile?> = db.userDao().getUserProfile()

    suspend fun updateProfile(profile: UserProfile) {
        db.userDao().insertOrUpdateProfile(profile)
    }

    suspend fun addXp(amount: Int) {
        db.userDao().addXp(amount)
    }

    // Subjects
    val allSubjects: Flow<List<Subject>> = db.subjectDao().getAllSubjects()
    val enrolledSubjects: Flow<List<Subject>> = db.subjectDao().getEnrolledSubjects()

    fun getSubjectById(id: String): Flow<Subject?> = db.subjectDao().getSubjectById(id)
    suspend fun getSubjectByIdDirect(id: String): Subject? = db.subjectDao().getSubjectByIdDirect(id)

    suspend fun toggleSubjectEnrollment(subjectId: String, enrolled: Boolean) {
        db.subjectDao().setEnrollment(subjectId, enrolled)
    }

    suspend fun saveSubject(subject: Subject) {
        db.subjectDao().insertSubject(subject)
    }

    suspend fun deleteSubject(id: String) {
        db.subjectDao().deleteSubject(id)
    }

    // Topics & Lessons
    fun getTopicsForSubject(subjectId: String): Flow<List<Topic>> = db.topicDao().getTopicsForSubject(subjectId)
    fun getTopicById(id: String): Flow<Topic?> = db.topicDao().getTopicById(id)
    suspend fun getTopicByIdDirect(id: String): Topic? = db.topicDao().getTopicByIdDirect(id)

    suspend fun saveTopic(topic: Topic) {
        db.topicDao().insertTopic(topic)
    }

    suspend fun deleteTopic(id: String) {
        db.topicDao().deleteTopic(id)
    }

    fun getObjectivesForTopic(topicId: String): Flow<List<LearningObjective>> =
        db.learningObjectiveDao().getObjectivesForTopic(topicId)

    fun getLessonForTopic(topicId: String): Flow<Lesson?> = db.lessonDao().getLessonForTopic(topicId)
    suspend fun getLessonForTopicDirect(topicId: String): Lesson? = db.lessonDao().getLessonForTopicDirect(topicId)

    suspend fun saveLesson(lesson: Lesson) {
        db.lessonDao().insertLesson(lesson)
    }

    suspend fun markLessonCompleted(topicId: String) {
        db.lessonDao().setLessonCompleted(topicId, true)
        db.topicDao().updateTopicProgress(topicId, completed = true, mastery = 85)
        addXp(25)
    }

    // Questions & Practice
    fun getQuestionsForTopic(topicId: String): Flow<List<Question>> = db.questionDao().getQuestionsForTopic(topicId)
    fun getQuestionsForSubject(subjectId: String): Flow<List<Question>> = db.questionDao().getQuestionsForSubject(subjectId)
    val allQuestions: Flow<List<Question>> = db.questionDao().getAllQuestions()

    suspend fun saveQuestion(question: Question) {
        db.questionDao().insertQuestion(question)
    }

    suspend fun deleteQuestion(id: String) {
        db.questionDao().deleteQuestion(id)
    }

    suspend fun getRandomQuestionsForSubject(subjectId: String, limit: Int): List<Question> =
        db.questionDao().getRandomQuestionsForSubject(subjectId, limit)

    suspend fun recordQuestionAttempt(
        questionId: String,
        subjectId: String,
        topicId: String,
        selectedOption: String,
        isCorrect: Boolean,
        timeSpentSecs: Int
    ) {
        db.questionAttemptDao().insertAttempt(
            QuestionAttempt(
                questionId = questionId,
                subjectId = subjectId,
                topicId = topicId,
                selectedOption = selectedOption,
                isCorrect = isCorrect,
                timeSpentSecs = timeSpentSecs
            )
        )
        db.userDao().incrementQuestionsCount(1)
        if (isCorrect) {
            addXp(10)
        } else {
            // Track mistake
            val existing = db.mistakeDao().getMistakeByQuestionId(questionId)
            if (existing != null) {
                db.mistakeDao().insertMistake(
                    existing.copy(
                        mistakeCount = existing.mistakeCount + 1,
                        lastMistakeAt = System.currentTimeMillis(),
                        resolved = false
                    )
                )
            } else {
                db.mistakeDao().insertMistake(
                    MistakeRecord(
                        subjectId = subjectId,
                        topicId = topicId,
                        questionId = questionId,
                        mistakeCount = 1,
                        lastMistakeAt = System.currentTimeMillis(),
                        resolved = false
                    )
                )
            }
        }
    }

    // Mistakes & Weak Areas
    val unresolvedMistakes: Flow<List<MistakeRecord>> = db.mistakeDao().getUnresolvedMistakes()

    suspend fun resolveMistake(id: Long) {
        db.mistakeDao().resolveMistake(id)
    }

    // CBT Mock Exams
    val allMockResults: Flow<List<CbtMockResult>> = db.cbtMockDao().getAllMockResults()
    fun getMockResultById(id: Long): Flow<CbtMockResult?> = db.cbtMockDao().getMockResultById(id)

    suspend fun submitCbtMock(
        examName: String,
        subjectIdsCsv: String,
        totalScore: Int,
        maxScore: Int = 400,
        timeUsedSecs: Int,
        totalQuestions: Int,
        correctAnswersCount: Int,
        subjectsBreakdownJson: String,
        weakTopicsJson: String,
        strongTopicsJson: String,
        recommendationsJson: String
    ): Long {
        val result = CbtMockResult(
            examName = examName,
            subjectIdsCsv = subjectIdsCsv,
            totalScore = totalScore,
            maxScore = maxScore,
            timeUsedSecs = timeUsedSecs,
            totalQuestions = totalQuestions,
            correctAnswersCount = correctAnswersCount,
            subjectsBreakdownJson = subjectsBreakdownJson,
            weakTopicsJson = weakTopicsJson,
            strongTopicsJson = strongTopicsJson,
            recommendationsJson = recommendationsJson
        )
        val id = db.cbtMockDao().insertMockResult(result)
        db.userDao().incrementMockCount()
        addXp(100)
        return id
    }

    // Study Plan
    val studyPlanTasks: Flow<List<StudyPlanTask>> = db.studyPlanDao().getStudyTasks()

    suspend fun setTaskCompleted(taskId: String, completed: Boolean) {
        db.studyPlanDao().setTaskCompleted(taskId, completed)
        if (completed) addXp(15)
    }

    suspend fun generateAdaptiveStudyPlan(profile: UserProfile, enrolledSubjects: List<Subject>) {
        val tasks = mutableListOf<StudyPlanTask>()
        enrolledSubjects.forEachIndexed { index, subject ->
            val topics = db.topicDao().getTopicsForSubject(subject.id)
            tasks.add(
                StudyPlanTask(
                    id = "adapt_task_${subject.id}_${System.currentTimeMillis()}_$index",
                    subjectId = subject.id,
                    topicId = "${subject.id}_topic_1",
                    title = "${subject.name}: Daily High-Yield Syllabus Drill",
                    taskType = if (index % 2 == 0) "LEARN" else "PRACTICE",
                    durationMins = profile.dailyStudyTimeMins / maxOf(1, enrolledSubjects.size),
                    isCompleted = false,
                    scheduledDate = "Today",
                    priority = if (subject.confidenceLevel <= 2) "High" else "Medium"
                )
            )
        }
        db.studyPlanDao().insertTasks(tasks)
    }

    // Achievements & Gamification
    val achievements: Flow<List<Achievement>> = db.achievementDao().getAllAchievements()

    // AI Chat & Tutor
    fun getChatMessagesForTopic(topicId: String): Flow<List<AiChatMessage>> =
        db.aiChatDao().getMessagesForTopic(topicId)

    suspend fun askAiTutor(
        userQuery: String,
        subjectName: String,
        topicName: String,
        lessonContext: String,
        actionType: String = "CHAT",
        topicId: String = ""
    ): String {
        // Save user message
        db.aiChatDao().insertMessage(
            AiChatMessage(
                subjectId = "",
                topicId = topicId,
                senderRole = "user",
                messageText = userQuery,
                suggestedAction = actionType
            )
        )

        // Check Daily Usage
        val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val profile = db.userDao().getUserProfileDirect()
        val isPro = profile?.subscriptionTier == "pro"

        val usage = db.aiUsageDao().getUsageForDate(todayStr) ?: AiUsageRecord(dateString = todayStr, queriesCount = 0)
        if (!isPro && usage.queriesCount >= usage.maxLimit) {
            val limitMessage = "You have reached your daily Free limit of 5 AI Tutor questions. Upgrade to JAMB Genius PRO for unlimited AI explanations, CBT mock predictions, and full step-by-step guidance!"
            db.aiChatDao().insertMessage(
                AiChatMessage(
                    subjectId = "",
                    topicId = topicId,
                    senderRole = "assistant",
                    messageText = limitMessage,
                    suggestedAction = "UPGRADE_PRO"
                )
            )
            return limitMessage
        }

        // Increment usage
        db.aiUsageDao().insertOrUpdateUsage(usage.copy(queriesCount = usage.queriesCount + 1))

        val answer = aiService.askTutor(userQuery, subjectName, topicName, lessonContext, actionType)

        db.aiChatDao().insertMessage(
            AiChatMessage(
                subjectId = "",
                topicId = topicId,
                senderRole = "assistant",
                messageText = answer,
                suggestedAction = actionType
            )
        )
        return answer
    }

    suspend fun clearChatHistory() {
        db.aiChatDao().clearHistory()
    }

    // Subscriptions & Payment Architecture
    val transactions: Flow<List<PaymentTransaction>> = db.paymentDao().getAllTransactions()

    suspend fun upgradeToPro(planName: String, amountNgn: Int, gateway: String): String {
        val reference = "JG_${System.currentTimeMillis()}_${(1000..9999).random()}"
        val transaction = PaymentTransaction(
            reference = reference,
            planName = planName,
            amountNgn = amountNgn,
            gateway = gateway,
            status = "SUCCESS",
            createdAt = System.currentTimeMillis()
        )
        db.paymentDao().insertTransaction(transaction)

        val expiryDurationMillis = if (planName.contains("Season", ignoreCase = true)) {
            180L * 24 * 60 * 60 * 1000 // 6 months
        } else {
            30L * 24 * 60 * 60 * 1000 // 1 month
        }

        db.userDao().updateSubscription(
            tier = "pro",
            expiry = System.currentTimeMillis() + expiryDurationMillis
        )
        addXp(500)
        return reference
    }
}
