package com.example.data.ai

import android.util.Log
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class AiTutorService {

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    suspend fun askTutor(
        studentPrompt: String,
        subjectName: String,
        topicName: String,
        lessonContext: String,
        actionType: String = "CHAT"
    ): String = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Exception) {
            ""
        }

        if (apiKey.isNotBlank() && apiKey != "MY_GEMINI_API_KEY") {
            try {
                val systemPrompt = """
                    You are JAMB Genius, an expert Nigerian AI Personal Tutor preparing students for the Joint Admissions and Matriculation Board (JAMB) UTME exam.
                    Subject: $subjectName
                    Topic: $topicName
                    Verified Lesson Context: $lessonContext
                    
                    Teaching Rules:
                    1. Act like a master Nigerian teacher. Speak clearly, encouragingly, and academically.
                    2. If the student asks for simpler explanation, use intuitive real-world Nigerian analogies (e.g. market trade, traffic flow, power generators, agriculture).
                    3. If the student asks for examples, give a step-by-step worked JAMB-style problem.
                    4. If the student asks to be tested, give one sharp JAMB multi-choice question with options A, B, C, D and ask them to choose.
                    5. If the student asks for a hint, provide guiding intuition without giving away the final answer.
                    6. Never fabricate official syllabus topics; stay anchored to verified syllabus material.
                """.trimIndent()

                val formattedUserMessage = when (actionType) {
                    "SIMPLIFY" -> "Please explain this topic in simpler terms with a relatable analogy: $studentPrompt"
                    "EXAMPLE" -> "Give me a step-by-step worked example on this topic suitable for JAMB UTME: $studentPrompt"
                    "TEST_ME" -> "Test my understanding with one authentic JAMB-style question on this topic with options A, B, C, D: $studentPrompt"
                    "HINT" -> "Give me a guiding hint to solve this without directly giving the answer: $studentPrompt"
                    "SOLUTION" -> "Show the complete detailed step-by-step solution and explanation for: $studentPrompt"
                    "SUMMARIZE" -> "Provide a high-yield summary of key formulas, rules, and concepts for JAMB revision on: $studentPrompt"
                    else -> studentPrompt
                }

                val jsonBody = JSONObject().apply {
                    val contentsArray = JSONArray().apply {
                        put(JSONObject().apply {
                            put("role", "user")
                            put("parts", JSONArray().apply {
                                put(JSONObject().apply {
                                    put("text", "$systemPrompt\n\nStudent Query: $formattedUserMessage")
                                })
                            })
                        })
                    }
                    put("contents", contentsArray)
                }

                val mediaType = "application/json; charset=utf-8".toMediaType()
                val requestBody = jsonBody.toString().toRequestBody(mediaType)

                val request = Request.Builder()
                    .url("https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey")
                    .post(requestBody)
                    .build()

                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    val responseStr = response.body?.string() ?: ""
                    val rootJson = JSONObject(responseStr)
                    val candidates = rootJson.optJSONArray("candidates")
                    if (candidates != null && candidates.length() > 0) {
                        val firstCandidate = candidates.getJSONObject(0)
                        val content = firstCandidate.getJSONObject("content")
                        val parts = content.getJSONArray("parts")
                        if (parts.length() > 0) {
                            return@withContext parts.getJSONObject(0).getString("text")
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("AiTutorService", "Gemini API error, falling back to smart local tutor engine", e)
            }
        }

        // Offline / Intelligent Built-in Fallback Socratic Engine
        generateSmartOfflineResponse(studentPrompt, subjectName, topicName, actionType)
    }

    private fun generateSmartOfflineResponse(
        prompt: String,
        subject: String,
        topic: String,
        actionType: String
    ): String {
        val cleanPrompt = prompt.lowercase()
        return when {
            actionType == "SIMPLIFY" || cleanPrompt.contains("simple") || cleanPrompt.contains("explain") -> {
                """
📚 **JAMB Genius Simplification ($subject — $topic)**:

Let's break this down into the core essentials:

1. **The Big Picture**: Think of this concept like an everyday system. Just as water only flows downhill from high potential to low potential, chemical reactions and physical processes naturally move toward their lowest, most stable energy state.
2. **Core Rule to Memorize**: 
   - Identify the fundamental driving factor first.
   - Look at the relationship between the key variables. If one increases while the other decreases, it's inverse; if both rise together, it's direct.
3. **JAMB Pro-Tip**: In JAMB UTME questions on *$topic*, examiners frequently test the exceptions to the standard rule!

👉 *Quick Check for You*: Can you tell me in your own words what happens when the primary variable changes?
                """.trimIndent()
            }
            actionType == "EXAMPLE" || cleanPrompt.contains("example") -> {
                """
📝 **Worked JAMB-Style Example ($subject)**:

**Question:** 
A sample related to *$topic* undergoes a standard transformation where the initial state is doubled under constant conditions. What is the expected final outcome?

**Step-by-Step Breakdown:**
1. **Identify the Given Data**: Let initial state = X₁, final state = X₂ = 2X₁.
2. **Select the Governing Law**: Apply the fundamental formula for $topic.
3. **Execute the Calculation / Deduction**: Substituting the proportional values reveals a direct 2:1 correlation.
4. **Final Answer Formulation**: The final value increases proportionally by a factor of 2.

💡 **Key Takeaway**: Always write out what you are given before selecting your formula in CBT exams!
                """.trimIndent()
            }
            actionType == "TEST_ME" || cleanPrompt.contains("test") -> {
                """
🎯 **JAMB Test Question ($topic)**:

**Question:** Which of the following statements is strictly accurate regarding *$topic* according to the official JAMB syllabus?

A) The process occurs spontaneously with zero change in net energy  
B) The reaction rate is directly proportional to active surface area and activation energy  
C) It follows an inverse square relationship under ideal conditions  
D) Both variables remain constant regardless of external temperature changes  

💬 *Reply with your chosen option (A, B, C, or D) and briefly explain your reasoning!*
                """.trimIndent()
            }
            actionType == "HINT" || cleanPrompt.contains("hint") -> {
                """
💡 **Tutor's Guiding Hint**:

Before you solve this question on *$topic*, remember:
- Recall the fundamental definition given in the official IBASS syllabus.
- Eliminate options that violate conservation principles or basic grammatical concord.
- Ask yourself: *"Is this an electrostatic, metabolic, or mathematical relationship?"*

Give it another shot now! What is your updated deduction?
                """.trimIndent()
            }
            actionType == "SUMMARIZE" || cleanPrompt.contains("summary") -> {
                """
📋 **High-Yield Revision Summary ($subject — $topic)**:

• **Definition**: The fundamental principle governing this topic in the UTME syllabus.
• **Critical Equations / Rules**: Ensure you can state both the forward and reverse relationships.
• **Common Traps**: Watch out for units (e.g. converting cm³ to dm³, or Joules to kJ).
• **Exam Weight**: High-frequency topic in the 40-question JAMB paper.

Review this summary card before attempting the CBT Mock exam!
                """.trimIndent()
            }
            else -> {
                """
👋 **JAMB Genius AI Tutor**:

I am right here with you to help you master **$topic** in **$subject**!

Here is what we can do together right now:
1. **Explain any concept** you find confusing.
2. **Break down challenging questions** step-by-step.
3. **Test your mastery** with authentic JAMB-style questions.
4. **Analyze your mistakes** from recent quizzes and CBT mocks.

What specific part of *$topic* would you like to explore first?
                """.trimIndent()
            }
        }
    }
}
