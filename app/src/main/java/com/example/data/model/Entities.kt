package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profiles")
data class UserProfile(
    @PrimaryKey val id: String = "primary_student",
    val name: String = "Chinedu Okafor",
    val targetScore: Int = 320,
    val examDateMillis: Long = System.currentTimeMillis() + (60L * 24 * 60 * 60 * 1000), // ~60 days default
    val dailyStudyTimeMins: Int = 90,
    val learningStyle: String = "AI Tutor & CBT Practice",
    val subscriptionTier: String = "free", // "free", "pro", "expired", "cancelled"
    val subscriptionExpiryMillis: Long = 0L,
    val xp: Int = 450,
    val streakDays: Int = 4,
    val lastStudyDate: String = "",
    val completedQuestionsCount: Int = 85,
    val cbtMocksTaken: Int = 2,
    val isAdmin: Boolean = false
)

@Entity(tableName = "subjects")
data class Subject(
    @PrimaryKey val id: String,
    val name: String,
    val code: String,
    val category: String, // "Science", "Commercial", "Arts", "General"
    val description: String,
    val iconName: String,
    val colorHex: String,
    val isCompulsory: Boolean = false,
    val isEnrolled: Boolean = false,
    val confidenceLevel: Int = 3, // 1 to 5
    val officialIbassUrl: String = "https://ibass.jamb.gov.ng"
)

@Entity(tableName = "topics")
data class Topic(
    @PrimaryKey val id: String,
    val subjectId: String,
    val title: String,
    val orderIndex: Int,
    val summary: String,
    val isCompleted: Boolean = false,
    val masteryLevel: Int = 0, // 0 to 100
    val difficulty: String = "Standard" // "Standard", "High-Yield", "Advanced"
)

@Entity(tableName = "subtopics")
data class Subtopic(
    @PrimaryKey val id: String,
    val topicId: String,
    val title: String,
    val orderIndex: Int
)

@Entity(tableName = "learning_objectives")
data class LearningObjective(
    @PrimaryKey val id: String,
    val topicId: String,
    val objectiveText: String,
    val isMet: Boolean = false
)

@Entity(tableName = "lessons")
data class Lesson(
    @PrimaryKey val id: String,
    val topicId: String,
    val title: String,
    val contentMarkdown: String,
    val examples: String,
    val keyPoints: String,
    val readTimeMins: Int = 8,
    val isCompleted: Boolean = false
)

@Entity(tableName = "questions")
data class Question(
    @PrimaryKey val id: String,
    val subjectId: String,
    val topicId: String,
    val questionText: String,
    val optionA: String,
    val optionB: String,
    val optionC: String,
    val optionD: String,
    val correctOption: String, // "A", "B", "C", "D"
    val explanation: String,
    val difficulty: String = "JAMB-Standard", // "Basic", "Application", "JAMB-Standard", "Challenge"
    val yearMetadata: String = "JAMB Past Curriculum Model",
    val isAuthorized: Boolean = true
)

@Entity(tableName = "question_attempts")
data class QuestionAttempt(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val questionId: String,
    val subjectId: String,
    val topicId: String,
    val selectedOption: String,
    val isCorrect: Boolean,
    val timeSpentSecs: Int,
    val attemptedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "quiz_attempts")
data class QuizAttempt(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val subjectId: String,
    val topicId: String,
    val totalQuestions: Int,
    val score: Int,
    val passed: Boolean,
    val completedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "cbt_mock_results")
data class CbtMockResult(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val examName: String,
    val subjectIdsCsv: String,
    val totalScore: Int,
    val maxScore: Int = 400,
    val timeUsedSecs: Int,
    val totalQuestions: Int,
    val correctAnswersCount: Int,
    val subjectsBreakdownJson: String, // JSON or formatted text
    val weakTopicsJson: String,
    val strongTopicsJson: String,
    val recommendationsJson: String,
    val completedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "mistake_records")
data class MistakeRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val subjectId: String,
    val topicId: String,
    val questionId: String,
    val mistakeCount: Int = 1,
    val lastMistakeAt: Long = System.currentTimeMillis(),
    val resolved: Boolean = false
)

@Entity(tableName = "study_plan_tasks")
data class StudyPlanTask(
    @PrimaryKey val id: String,
    val subjectId: String,
    val topicId: String,
    val title: String,
    val taskType: String, // "LEARN", "PRACTICE", "QUIZ", "REVISE"
    val durationMins: Int = 30,
    val isCompleted: Boolean = false,
    val scheduledDate: String, // "YYYY-MM-DD" or "Today"
    val priority: String = "High" // "High", "Medium", "Normal"
)

@Entity(tableName = "achievements")
data class Achievement(
    @PrimaryKey val id: String,
    val badgeKey: String,
    val title: String,
    val description: String,
    val iconName: String,
    val xpReward: Int = 100,
    val isUnlocked: Boolean = false,
    val unlockedAt: Long = 0L
)

@Entity(tableName = "ai_usage_records")
data class AiUsageRecord(
    @PrimaryKey val dateString: String,
    val queriesCount: Int = 0,
    val maxLimit: Int = 5
)

@Entity(tableName = "payment_transactions")
data class PaymentTransaction(
    @PrimaryKey val reference: String,
    val planName: String,
    val amountNgn: Int,
    val gateway: String, // "Paystack", "Flutterwave"
    val status: String, // "SUCCESS", "PENDING", "FAILED"
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "ai_chat_messages")
data class AiChatMessage(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val subjectId: String = "",
    val topicId: String = "",
    val senderRole: String, // "user", "assistant"
    val messageText: String,
    val timestampMillis: Long = System.currentTimeMillis(),
    val suggestedAction: String = ""
)
