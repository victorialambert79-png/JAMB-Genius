package com.example.data.dao

import androidx.room.*
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM user_profiles WHERE id = :id LIMIT 1")
    fun getUserProfile(id: String = "primary_student"): Flow<UserProfile?>

    @Query("SELECT * FROM user_profiles WHERE id = :id LIMIT 1")
    suspend fun getUserProfileDirect(id: String = "primary_student"): UserProfile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProfile(profile: UserProfile)

    @Query("UPDATE user_profiles SET xp = xp + :xpEarned WHERE id = :id")
    suspend fun addXp(xpEarned: Int, id: String = "primary_student")

    @Query("UPDATE user_profiles SET subscriptionTier = :tier, subscriptionExpiryMillis = :expiry WHERE id = :id")
    suspend fun updateSubscription(tier: String, expiry: Long, id: String = "primary_student")

    @Query("UPDATE user_profiles SET completedQuestionsCount = completedQuestionsCount + :count WHERE id = :id")
    suspend fun incrementQuestionsCount(count: Int = 1, id: String = "primary_student")

    @Query("UPDATE user_profiles SET cbtMocksTaken = cbtMocksTaken + 1 WHERE id = :id")
    suspend fun incrementMockCount(id: String = "primary_student")
}

@Dao
interface SubjectDao {
    @Query("SELECT * FROM subjects ORDER BY isCompulsory DESC, name ASC")
    fun getAllSubjects(): Flow<List<Subject>>

    @Query("SELECT * FROM subjects WHERE isEnrolled = 1 ORDER BY isCompulsory DESC, name ASC")
    fun getEnrolledSubjects(): Flow<List<Subject>>

    @Query("SELECT * FROM subjects WHERE id = :id LIMIT 1")
    fun getSubjectById(id: String): Flow<Subject?>

    @Query("SELECT * FROM subjects WHERE id = :id LIMIT 1")
    suspend fun getSubjectByIdDirect(id: String): Subject?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubjects(subjects: List<Subject>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubject(subject: Subject)

    @Update
    suspend fun updateSubject(subject: Subject)

    @Query("DELETE FROM subjects WHERE id = :id")
    suspend fun deleteSubject(id: String)

    @Query("UPDATE subjects SET isEnrolled = :enrolled WHERE id = :id")
    suspend fun setEnrollment(id: String, enrolled: Boolean)
}

@Dao
interface TopicDao {
    @Query("SELECT * FROM topics WHERE subjectId = :subjectId ORDER BY orderIndex ASC")
    fun getTopicsForSubject(subjectId: String): Flow<List<Topic>>

    @Query("SELECT * FROM topics WHERE id = :id LIMIT 1")
    fun getTopicById(id: String): Flow<Topic?>

    @Query("SELECT * FROM topics WHERE id = :id LIMIT 1")
    suspend fun getTopicByIdDirect(id: String): Topic?

    @Query("SELECT * FROM topics")
    fun getAllTopics(): Flow<List<Topic>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTopics(topics: List<Topic>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTopic(topic: Topic)

    @Update
    suspend fun updateTopic(topic: Topic)

    @Query("DELETE FROM topics WHERE id = :id")
    suspend fun deleteTopic(id: String)

    @Query("UPDATE topics SET isCompleted = :completed, masteryLevel = :mastery WHERE id = :id")
    suspend fun updateTopicProgress(id: String, completed: Boolean, mastery: Int)
}

@Dao
interface SubtopicDao {
    @Query("SELECT * FROM subtopics WHERE topicId = :topicId ORDER BY orderIndex ASC")
    fun getSubtopicsForTopic(topicId: String): Flow<List<Subtopic>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubtopics(subtopics: List<Subtopic>)
}

@Dao
interface LearningObjectiveDao {
    @Query("SELECT * FROM learning_objectives WHERE topicId = :topicId")
    fun getObjectivesForTopic(topicId: String): Flow<List<LearningObjective>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertObjectives(objectives: List<LearningObjective>)

    @Query("UPDATE learning_objectives SET isMet = :isMet WHERE id = :id")
    suspend fun updateObjectiveStatus(id: String, isMet: Boolean)
}

@Dao
interface LessonDao {
    @Query("SELECT * FROM lessons WHERE topicId = :topicId LIMIT 1")
    fun getLessonForTopic(topicId: String): Flow<Lesson?>

    @Query("SELECT * FROM lessons WHERE topicId = :topicId LIMIT 1")
    suspend fun getLessonForTopicDirect(topicId: String): Lesson?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLessons(lessons: List<Lesson>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLesson(lesson: Lesson)

    @Query("UPDATE lessons SET isCompleted = :isCompleted WHERE id = :id")
    suspend fun setLessonCompleted(id: String, isCompleted: Boolean)
}

@Dao
interface QuestionDao {
    @Query("SELECT * FROM questions WHERE subjectId = :subjectId")
    fun getQuestionsForSubject(subjectId: String): Flow<List<Question>>

    @Query("SELECT * FROM questions WHERE topicId = :topicId")
    fun getQuestionsForTopic(topicId: String): Flow<List<Question>>

    @Query("SELECT * FROM questions WHERE subjectId = :subjectId AND difficulty = :difficulty")
    fun getQuestionsByDifficulty(subjectId: String, difficulty: String): Flow<List<Question>>

    @Query("SELECT * FROM questions WHERE id = :id LIMIT 1")
    suspend fun getQuestionById(id: String): Question?

    @Query("SELECT * FROM questions")
    fun getAllQuestions(): Flow<List<Question>>

    @Query("SELECT * FROM questions WHERE subjectId = :subjectId ORDER BY RANDOM() LIMIT :limit")
    suspend fun getRandomQuestionsForSubject(subjectId: String, limit: Int): List<Question>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestions(questions: List<Question>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestion(question: Question)

    @Query("DELETE FROM questions WHERE id = :id")
    suspend fun deleteQuestion(id: String)
}

@Dao
interface QuestionAttemptDao {
    @Query("SELECT * FROM question_attempts ORDER BY attemptedAt DESC LIMIT 100")
    fun getRecentAttempts(): Flow<List<QuestionAttempt>>

    @Query("SELECT COUNT(*) FROM question_attempts WHERE isCorrect = 1")
    fun getCorrectAttemptsCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM question_attempts")
    fun getTotalAttemptsCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttempt(attempt: QuestionAttempt)
}

@Dao
interface CbtMockDao {
    @Query("SELECT * FROM cbt_mock_results ORDER BY completedAt DESC")
    fun getAllMockResults(): Flow<List<CbtMockResult>>

    @Query("SELECT * FROM cbt_mock_results WHERE id = :id LIMIT 1")
    fun getMockResultById(id: Long): Flow<CbtMockResult?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMockResult(result: CbtMockResult): Long
}

@Dao
interface MistakeDao {
    @Query("SELECT * FROM mistake_records WHERE resolved = 0 ORDER BY mistakeCount DESC, lastMistakeAt DESC")
    fun getUnresolvedMistakes(): Flow<List<MistakeRecord>>

    @Query("SELECT * FROM mistake_records WHERE questionId = :questionId LIMIT 1")
    suspend fun getMistakeByQuestionId(questionId: String): MistakeRecord?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMistake(mistake: MistakeRecord)

    @Query("UPDATE mistake_records SET resolved = 1 WHERE id = :id")
    suspend fun resolveMistake(id: Long)
}

@Dao
interface StudyPlanDao {
    @Query("SELECT * FROM study_plan_tasks ORDER BY isCompleted ASC, priority DESC")
    fun getStudyTasks(): Flow<List<StudyPlanTask>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTasks(tasks: List<StudyPlanTask>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: StudyPlanTask)

    @Query("UPDATE study_plan_tasks SET isCompleted = :completed WHERE id = :id")
    suspend fun setTaskCompleted(id: String, completed: Boolean)

    @Query("DELETE FROM study_plan_tasks WHERE id = :id")
    suspend fun deleteTask(id: String)
}

@Dao
interface AchievementDao {
    @Query("SELECT * FROM achievements ORDER BY isUnlocked DESC, xpReward DESC")
    fun getAllAchievements(): Flow<List<Achievement>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAchievements(achievements: List<Achievement>)

    @Query("UPDATE achievements SET isUnlocked = 1, unlockedAt = :unlockedAt WHERE id = :id")
    suspend fun unlockAchievement(id: String, unlockedAt: Long = System.currentTimeMillis())
}

@Dao
interface AiUsageDao {
    @Query("SELECT * FROM ai_usage_records WHERE dateString = :date LIMIT 1")
    suspend fun getUsageForDate(date: String): AiUsageRecord?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateUsage(usage: AiUsageRecord)
}

@Dao
interface PaymentDao {
    @Query("SELECT * FROM payment_transactions ORDER BY createdAt DESC")
    fun getAllTransactions(): Flow<List<PaymentTransaction>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: PaymentTransaction)
}

@Dao
interface AiChatDao {
    @Query("SELECT * FROM ai_chat_messages WHERE (:topicId = '' OR topicId = :topicId) ORDER BY timestampMillis ASC")
    fun getMessagesForTopic(topicId: String): Flow<List<AiChatMessage>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: AiChatMessage)

    @Query("DELETE FROM ai_chat_messages")
    suspend fun clearHistory()
}
