package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.dao.*
import com.example.data.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        UserProfile::class,
        Subject::class,
        Topic::class,
        Subtopic::class,
        LearningObjective::class,
        Lesson::class,
        Question::class,
        QuestionAttempt::class,
        QuizAttempt::class,
        CbtMockResult::class,
        MistakeRecord::class,
        StudyPlanTask::class,
        Achievement::class,
        AiUsageRecord::class,
        PaymentTransaction::class,
        AiChatMessage::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun subjectDao(): SubjectDao
    abstract fun topicDao(): TopicDao
    abstract fun subtopicDao(): SubtopicDao
    abstract fun learningObjectiveDao(): LearningObjectiveDao
    abstract fun lessonDao(): LessonDao
    abstract fun questionDao(): QuestionDao
    abstract fun questionAttemptDao(): QuestionAttemptDao
    abstract fun cbtMockDao(): CbtMockDao
    abstract fun mistakeDao(): MistakeDao
    abstract fun studyPlanDao(): StudyPlanDao
    abstract fun achievementDao(): AchievementDao
    abstract fun aiUsageDao(): AiUsageDao
    abstract fun paymentDao(): PaymentDao
    abstract fun aiChatDao(): AiChatDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "jamb_genius_database"
                )
                    .addCallback(DatabaseCallback(scope))
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialData(database)
                    }
                }
            }
        }

        suspend fun populateInitialData(db: AppDatabase) {
            // 1. Initial User Profile
            val profile = UserProfile(
                id = "primary_student",
                name = "Chinedu Okafor",
                targetScore = 320,
                examDateMillis = System.currentTimeMillis() + (58L * 24 * 60 * 60 * 1000), // in 58 days
                dailyStudyTimeMins = 90,
                learningStyle = "Interactive AI Tutor & CBT Practice",
                subscriptionTier = "free",
                subscriptionExpiryMillis = 0L,
                xp = 680,
                streakDays = 4,
                completedQuestionsCount = 92,
                cbtMocksTaken = 2,
                isAdmin = false
            )
            db.userDao().insertOrUpdateProfile(profile)

            // 2. Subjects (Verified Nigerian JAMB subjects)
            val subjects = listOf(
                Subject(
                    id = "subj_eng",
                    name = "Use of English",
                    code = "ENG",
                    category = "General",
                    description = "Mandatory for all candidates: Comprehension, Lexis, Structure, and Oral Forms.",
                    iconName = "menu_book",
                    colorHex = "#2563EB",
                    isCompulsory = true,
                    isEnrolled = true,
                    confidenceLevel = 4,
                    officialIbassUrl = "https://ibass.jamb.gov.ng/assets/uploads/Use-of-English.pdf"
                ),
                Subject(
                    id = "subj_bio",
                    name = "Biology",
                    code = "BIO",
                    category = "Science",
                    description = "Cell structure, Form & Function, Ecology, Genetics, and Organic Evolution.",
                    iconName = "biotech",
                    colorHex = "#059669",
                    isCompulsory = false,
                    isEnrolled = true,
                    confidenceLevel = 3,
                    officialIbassUrl = "https://ibass.jamb.gov.ng/assets/uploads/Biology.pdf"
                ),
                Subject(
                    id = "subj_chem",
                    name = "Chemistry",
                    code = "CHM",
                    category = "Science",
                    description = "Atomic structure, Chemical bonding, Stoichiometry, Organic chemistry, and Energetics.",
                    iconName = "science",
                    colorHex = "#D97706",
                    isCompulsory = false,
                    isEnrolled = true,
                    confidenceLevel = 3,
                    officialIbassUrl = "https://ibass.jamb.gov.ng/assets/uploads/Chemistry.pdf"
                ),
                Subject(
                    id = "subj_phy",
                    name = "Physics",
                    code = "PHY",
                    category = "Science",
                    description = "Mechanics, Heat, Waves, Electricity, Magnetism, and Modern Physics.",
                    iconName = "bolt",
                    colorHex = "#7C3AED",
                    isCompulsory = false,
                    isEnrolled = true,
                    confidenceLevel = 2,
                    officialIbassUrl = "https://ibass.jamb.gov.ng/assets/uploads/Physics.pdf"
                ),
                Subject(
                    id = "subj_math",
                    name = "Mathematics",
                    code = "MTH",
                    category = "General",
                    description = "Number bases, Algebra, Geometry, Trigonometry, Calculus, and Statistics.",
                    iconName = "calculate",
                    colorHex = "#0284C7",
                    isCompulsory = false,
                    isEnrolled = false,
                    confidenceLevel = 3,
                    officialIbassUrl = "https://ibass.jamb.gov.ng"
                ),
                Subject(
                    id = "subj_econ",
                    name = "Economics",
                    code = "ECN",
                    category = "Commercial",
                    description = "Basic economic problems, Theory of consumer behaviour, Production, National income, and Money.",
                    iconName = "trending_up",
                    colorHex = "#0D9488",
                    isCompulsory = false,
                    isEnrolled = false,
                    confidenceLevel = 4,
                    officialIbassUrl = "https://ibass.jamb.gov.ng/assets/uploads/Economics.pdf"
                ),
                Subject(
                    id = "subj_gov",
                    name = "Government",
                    code = "GOV",
                    category = "Arts",
                    description = "Political theory, Constitutional development, Nigerian pre-colonial systems, and Foreign policy.",
                    iconName = "account_balance",
                    colorHex = "#DC2626",
                    isCompulsory = false,
                    isEnrolled = false,
                    confidenceLevel = 4,
                    officialIbassUrl = "https://ibass.jamb.gov.ng/assets/uploads/Government.pdf"
                ),
                Subject(
                    id = "subj_lit",
                    name = "Literature in English",
                    code = "LIT",
                    category = "Arts",
                    description = "African drama, African poetry, Non-African prose, and Literary devices.",
                    iconName = "auto_stories",
                    colorHex = "#9333EA",
                    isCompulsory = false,
                    isEnrolled = false,
                    confidenceLevel = 3,
                    officialIbassUrl = "https://ibass.jamb.gov.ng/assets/uploads/Literature-in-English.pdf"
                ),
                Subject(
                    id = "subj_com",
                    name = "Commerce",
                    code = "COM",
                    category = "Commercial",
                    description = "Trade, Aids to trade, Business units, Banking, and Stock exchange.",
                    iconName = "storefront",
                    colorHex = "#EA580C",
                    isCompulsory = false,
                    isEnrolled = false,
                    confidenceLevel = 3,
                    officialIbassUrl = "https://ibass.jamb.gov.ng"
                ),
                Subject(
                    id = "subj_acc",
                    name = "Principles of Accounts",
                    code = "ACC",
                    category = "Commercial",
                    description = "Double entry, Trial balance, Final accounts, Partnership, and Company accounts.",
                    iconName = "receipt_long",
                    colorHex = "#475569",
                    isCompulsory = false,
                    isEnrolled = false,
                    confidenceLevel = 3,
                    officialIbassUrl = "https://ibass.jamb.gov.ng"
                ),
                Subject(
                    id = "subj_agr",
                    name = "Agricultural Science",
                    code = "AGR",
                    category = "Science",
                    description = "Soil science, Crop production, Animal husbandry, and Agricultural economics.",
                    iconName = "agriculture",
                    colorHex = "#15803D",
                    isCompulsory = false,
                    isEnrolled = false,
                    confidenceLevel = 3,
                    officialIbassUrl = "https://ibass.jamb.gov.ng"
                ),
                Subject(
                    id = "subj_crs",
                    name = "Christian Religious Studies",
                    code = "CRS",
                    category = "Arts",
                    description = "Early life of Jesus, The early church, Discipleship, and Moral themes.",
                    iconName = "menu_book",
                    colorHex = "#4F46E5",
                    isCompulsory = false,
                    isEnrolled = false,
                    confidenceLevel = 4,
                    officialIbassUrl = "https://ibass.jamb.gov.ng"
                ),
                Subject(
                    id = "subj_irs",
                    name = "Islamic Religious Studies",
                    code = "IRS",
                    category = "Arts",
                    description = "Tawhid, Fiqh, Quranic studies, Hadith, and Islamic history.",
                    iconName = "menu_book",
                    colorHex = "#047857",
                    isCompulsory = false,
                    isEnrolled = false,
                    confidenceLevel = 4,
                    officialIbassUrl = "https://ibass.jamb.gov.ng"
                ),
                Subject(
                    id = "subj_csc",
                    name = "Computer Studies",
                    code = "CSC",
                    category = "Science",
                    description = "Computer hardware, Software, Programming concepts, Networking, and Information systems.",
                    iconName = "computer",
                    colorHex = "#0284C7",
                    isCompulsory = false,
                    isEnrolled = false,
                    confidenceLevel = 4,
                    officialIbassUrl = "https://ibass.jamb.gov.ng"
                ),
                Subject(
                    id = "subj_geo",
                    name = "Geography",
                    code = "GEO",
                    category = "Arts",
                    description = "Physical geography, Human geography, Regional geography of Nigeria, and Map reading.",
                    iconName = "public",
                    colorHex = "#0891B2",
                    isCompulsory = false,
                    isEnrolled = false,
                    confidenceLevel = 3,
                    officialIbassUrl = "https://ibass.jamb.gov.ng"
                )
            )
            db.subjectDao().insertSubjects(subjects)

            // 3. Topics for Subjects
            val topics = listOf(
                // Biology Topics (JAMB IBASS Syllabus)
                Topic(
                    id = "bio_topic_1",
                    subjectId = "subj_bio",
                    title = "Cell Structure and Organization of Life",
                    orderIndex = 1,
                    summary = "Cell theory, structures of plant and animal cells, cell organelles, and organization of living things.",
                    isCompleted = true,
                    masteryLevel = 78,
                    difficulty = "Standard"
                ),
                Topic(
                    id = "bio_topic_2",
                    subjectId = "subj_bio",
                    title = "Plant and Animal Nutrition (Photosynthesis & Digestion)",
                    orderIndex = 2,
                    summary = "Autotrophic vs heterotrophic nutrition, light and dark reactions of photosynthesis, enzymes and human digestive system.",
                    isCompleted = false,
                    masteryLevel = 62,
                    difficulty = "High-Yield"
                ),
                Topic(
                    id = "bio_topic_3",
                    subjectId = "subj_bio",
                    title = "Genetics, Heredity and Variation",
                    orderIndex = 3,
                    summary = "Mendelian inheritance, monohybrid and dihybrid crosses, sex-linked traits (hemophilia, color blindness), ABO blood groups.",
                    isCompleted = false,
                    masteryLevel = 45,
                    difficulty = "Advanced"
                ),
                Topic(
                    id = "bio_topic_4",
                    subjectId = "subj_bio",
                    title = "Ecology: Ecosystems, Energy Flow & Nutrient Cycles",
                    orderIndex = 4,
                    summary = "Abiotic and biotic factors, food chains/webs, pyramid of biomass, carbon and nitrogen cycles.",
                    isCompleted = false,
                    masteryLevel = 55,
                    difficulty = "Standard"
                ),

                // Chemistry Topics (JAMB IBASS Syllabus)
                Topic(
                    id = "chem_topic_1",
                    subjectId = "subj_chem",
                    title = "Separation of Mixtures and Purification of Substances",
                    orderIndex = 1,
                    summary = "Physical and chemical changes, filtration, crystallization, fractional distillation, chromatography, and purity criteria.",
                    isCompleted = true,
                    masteryLevel = 84,
                    difficulty = "Standard"
                ),
                Topic(
                    id = "chem_topic_2",
                    subjectId = "subj_chem",
                    title = "Chemical Bonding and Atomic Structure",
                    orderIndex = 2,
                    summary = "Electronic configuration, ionic, covalent, dative/coordinate, hydrogen and metallic bonding, electronegativity.",
                    isCompleted = false,
                    masteryLevel = 58,
                    difficulty = "High-Yield"
                ),
                Topic(
                    id = "chem_topic_3",
                    subjectId = "subj_chem",
                    title = "Acids, Bases, Salts and Redox Reactions",
                    orderIndex = 3,
                    summary = "Arrhenius, Bronsted-Lowry & Lewis definitions, pH calculations, neutralization, oxidation numbers, balancing redox equations.",
                    isCompleted = false,
                    masteryLevel = 38,
                    difficulty = "Advanced"
                ),
                Topic(
                    id = "chem_topic_4",
                    subjectId = "subj_chem",
                    title = "Hydrocarbons and Organic Chemistry",
                    orderIndex = 4,
                    summary = "Alkanes, alkenes, alkynes, benzene, functional groups, isomerism, IUPAC nomenclature, and petroleum refining.",
                    isCompleted = false,
                    masteryLevel = 50,
                    difficulty = "High-Yield"
                ),

                // Use of English Topics (JAMB IBASS Syllabus)
                Topic(
                    id = "eng_topic_1",
                    subjectId = "subj_eng",
                    title = "Comprehension and Summary Techniques",
                    orderIndex = 1,
                    summary = "Inferential reading, tone, mood, figurative language detection, identifying main ideas and summary precision.",
                    isCompleted = true,
                    masteryLevel = 72,
                    difficulty = "High-Yield"
                ),
                Topic(
                    id = "eng_topic_2",
                    subjectId = "subj_eng",
                    title = "Lexis and Structure: Concord, Prepositions and Tenses",
                    orderIndex = 2,
                    summary = "Subject-verb agreement (concord), faulty parallelism, modifier placement, modal verbs, and phrasal verbs.",
                    isCompleted = false,
                    masteryLevel = 60,
                    difficulty = "Standard"
                ),
                Topic(
                    id = "eng_topic_3",
                    subjectId = "subj_eng",
                    title = "Oral Forms: Vowels, Consonants, Rhymes and Stress",
                    orderIndex = 3,
                    summary = "Monophthongs, diphthongs, silent letters, rhyming pairs, primary syllable stress, and emphatic sentence stress.",
                    isCompleted = false,
                    masteryLevel = 42,
                    difficulty = "Advanced"
                ),

                // Physics Topics (JAMB IBASS Syllabus)
                Topic(
                    id = "phy_topic_1",
                    subjectId = "subj_phy",
                    title = "Mechanics: Motion, Newton's Laws and Projectiles",
                    orderIndex = 1,
                    summary = "Scalar and vector quantities, equations of uniform acceleration, Newton's 3 laws, momentum conservation, projectile trajectory.",
                    isCompleted = false,
                    masteryLevel = 48,
                    difficulty = "High-Yield"
                ),
                Topic(
                    id = "phy_topic_2",
                    subjectId = "subj_phy",
                    title = "Heat Energy, Gas Laws and Thermodynamics",
                    orderIndex = 2,
                    summary = "Specific heat capacity, latent heat, Boyle's law, Charles's law, general gas equation (PV/T = k), expansion of solids.",
                    isCompleted = false,
                    masteryLevel = 52,
                    difficulty = "Standard"
                ),
                Topic(
                    id = "phy_topic_3",
                    subjectId = "subj_phy",
                    title = "Current Electricity, Ohm's Law and DC Circuits",
                    orderIndex = 3,
                    summary = "Electromotive force, internal resistance, resistors in series/parallel, Kirchhoff's laws, electrical energy and power.",
                    isCompleted = false,
                    masteryLevel = 35,
                    difficulty = "Advanced"
                )
            )
            db.topicDao().insertTopics(topics)

            // 4. Subtopics & Learning Objectives
            val subtopics = listOf(
                Subtopic("sub_bio_1", "bio_topic_1", "The Cell Theory & Microscopy", 1),
                Subtopic("sub_bio_2", "bio_topic_1", "Ultrastructure of Plant vs Animal Cells", 2),
                Subtopic("sub_bio_3", "bio_topic_1", "Levels of Organization (Cells, Tissues, Organs)", 3),
                Subtopic("sub_chem_1", "chem_topic_2", "Atomic Orbitals and Electron Shells", 1),
                Subtopic("sub_chem_2", "chem_topic_2", "Types of Chemical Bonds (Ionic, Covalent, Coordinate)", 2),
                Subtopic("sub_eng_1", "eng_topic_2", "Rules of Grammatical Concord", 1),
                Subtopic("sub_eng_2", "eng_topic_2", "Tenses and Sequence of Tenses", 2)
            )
            db.subtopicDao().insertSubtopics(subtopics)

            val objectives = listOf(
                LearningObjective("obj_bio_1", "bio_topic_1", "State the tenets of the cell theory by Schleiden, Schwann, and Virchow.", true),
                LearningObjective("obj_bio_2", "bio_topic_1", "Distinguish between eukaryotic and prokaryotic cell structures.", true),
                LearningObjective("obj_bio_3", "bio_topic_1", "Explain the functions of ribosomes, mitochondria, chloroplasts, and Golgi apparatus.", false),
                LearningObjective("obj_bio_4", "bio_topic_1", "Differentiate between plant cells and animal cells under a light microscope.", true),
                LearningObjective("obj_chem_1", "chem_topic_2", "Explain why noble gases possess exceptional chemical stability.", true),
                LearningObjective("obj_chem_2", "chem_topic_2", "Predict bond types based on electronegativity differences.", false),
                LearningObjective("obj_chem_3", "chem_topic_2", "Distinguish between inter-molecular and intra-molecular forces.", false),
                LearningObjective("obj_eng_1", "eng_topic_2", "Apply the rule of proximity with either...or / neither...nor subjects.", true),
                LearningObjective("obj_eng_2", "eng_topic_2", "Identify parenthetical phrases and avoid false agreement errors in JAMB questions.", false)
            )
            db.learningObjectiveDao().insertObjectives(objectives)

            // 5. Lessons
            val lessons = listOf(
                Lesson(
                    id = "les_bio_1",
                    topicId = "bio_topic_1",
                    title = "Mastering Cell Structure & Organelle Functions",
                    contentMarkdown = """
# Cell Structure and Organization of Life

### 1. The Historical Foundations
The cell is the basic structural, functional, and biological unit of all known living organisms. The **Cell Theory** established by Matthias Schleiden, Theodor Schwann (1839), and Rudolf Virchow (1855) asserts:
1. All living organisms are composed of one or more cells.
2. The cell is the fundamental unit of structure and function in living things.
3. All cells arise from pre-existing cells through cell division.

---

### 2. Plant Cells vs Animal Cells (High-Yield JAMB Focus)
| Feature | Plant Cell | Animal Cell |
|---|---|---|
| **Cell Wall** | Present (Cellulose) | Absent |
| **Plastids (Chloroplasts)** | Present (in green photosynthetic cells) | Absent |
| **Vacuole** | Large, permanent, central sap vacuole | Small, temporary, or absent |
| **Centrioles** | Absent (in higher plants) | Present (active during mitosis) |
| **Food Storage** | Starch granules | Glycogen granules |

---

### 3. Key Organelles & Their Exact Roles
- **Nucleus**: Houses chromatin (DNA) and controls metabolic operations.
- **Mitochondria**: 'Powerhouse' of the cell; site of aerobic cellular respiration yielding ATP via the Krebs cycle and electron transport chain.
- **Ribosomes**: Sites of protein synthesis (translation).
- **Endoplasmic Reticulum (ER)**:
  - *Rough ER*: Studded with ribosomes; transports newly synthesised proteins.
  - *Smooth ER*: Synthesizes lipids, steroids, and detoxifies drugs.
- **Golgi Apparatus**: Modifies, packages, and secretes cellular products (glycoproteins).
- **Lysosomes**: Contain hydrolytic digestive enzymes for intracellular digestion and autolysis.
                    """.trimIndent(),
                    examples = """
### Worked JAMB Example:
**Question:** Which organelle would be found in unusually high numbers in human active liver cells and cardiac muscle cells?
**Answer:** *Mitochondria*, because active liver and cardiac muscles require continuous, massive quantities of ATP energy to sustain involuntary contraction and metabolic detoxification.
                    """.trimIndent(),
                    keyPoints = "• Mitochondria have cristae (folded inner membrane) to maximize surface area for ATP synthesis.\n• Plant cell walls are fully permeable, while cell membranes are selectively permeable.\n• Centrioles form spindle fibers during animal cell division.",
                    readTimeMins = 7,
                    isCompleted = true
                ),
                Lesson(
                    id = "les_chem_2",
                    topicId = "chem_topic_2",
                    title = "Chemical Bonding: Ionic, Covalent & Coordinate",
                    contentMarkdown = """
# Chemical Bonding and Molecular Architecture

### 1. Why Do Atoms Bond?
Atoms react to attain the stable octet (or duplet) valence electron configuration characteristic of noble gases (Group 0/18), which represent minimal energy states.

---

### 2. Ionic (Electrovalent) Bonding
- **Mechanism**: Complete transfer of one or more valence electrons from an electropositive metallic atom to an electronegative non-metal.
- **Formation**: Forms positive cations and negative anions held by strong electrostatic forces.
- **Properties**:
  - High melting and boiling points.
  - Soluble in polar solvents (e.g. water), insoluble in non-polar solvents.
  - Conduct electricity when molten or in aqueous solution (free mobile ions).

---

### 3. Covalent & Coordinate (Dative) Bonding
- **Covalent Bond**: Mutual sharing of pairs of electrons between non-metallic atoms.
- **Coordinate (Dative) Bond**: A shared pair of electrons where **both electrons originate from one atom alone** (the donor atom possesses a lone pair, such as in NH₄⁺ or H₃O⁺).
                    """.trimIndent(),
                    examples = """
### Worked JAMB Example:
**Question:** In the formation of the ammonium ion (NH₄⁺) from ammonia (NH₃) and a hydrogen ion (H⁺), what type of bond is formed?
**Answer:** *Coordinate (Dative) Covalent Bond*, because the nitrogen atom in NH₃ donates its lone pair of electrons to the electron-deficient H⁺ ion.
                    """.trimIndent(),
                    keyPoints = "• Ionic bonds involve electrostatic attraction between ions.\n• Coordinate bonds require a donor with a lone pair and an acceptor with an empty orbital.\n• Hydrogen bonding explains the abnormally high boiling point of water compared to H₂S.",
                    readTimeMins = 8,
                    isCompleted = false
                ),
                Lesson(
                    id = "les_eng_2",
                    topicId = "eng_topic_2",
                    title = "Grammatical Concord: The Ultimate JAMB Rules",
                    contentMarkdown = """
# Mastering Concord in JAMB Use of English

### 1. The Rule of Proximity (Either...or / Neither...nor)
When subjects are connected by *either...or*, *neither...nor*, or *not only...but also*, the verb agrees with the **nearest subject**.
- *Example:* Neither the teacher nor the **students were** present.
- *Example:* Either the students or the **teacher was** in the hall.

---

### 2. Parenthetical Phrases & Quasi-Concord
Phrases such as *as well as*, *together with*, *along with*, *in addition to*, *accompanied by* do NOT alter the number of the subject. The verb agrees strictly with the **first subject**.
- *Example:* The **Principal**, as well as his vice-principals, **was** commended. (Singular verb 'was' agrees with 'Principal').

---

### 3. Indefinite Pronouns
Words like *each, everyone, everybody, someone, nobody, either, neither* are grammatically singular and take singular verbs.
- *Example:* Each of the candidates **has** received an admission slip.
                    """.trimIndent(),
                    examples = """
### Worked JAMB Question:
**Question:** The senator, together with his aides, ______ arriving tomorrow.
A) are  B) is  C) were  D) have been
**Correct Answer:** **B (is)**. The phrase 'together with his aides' is parenthetical; the true grammatical subject is the singular 'The senator'.
                    """.trimIndent(),
                    keyPoints = "• 'As well as' does not make a singular subject plural.\n• Proximity rule applies only to disjunctive coordinators (or/nor).\n• 'A number of' takes plural verb, while 'The number of' takes singular verb.",
                    readTimeMins = 6,
                    isCompleted = false
                )
            )
            db.lessonDao().insertLessons(lessons)

            // 6. Practice Questions (Authentic JAMB Curriculum Standards)
            val questions = listOf(
                Question(
                    id = "q_bio_1",
                    subjectId = "subj_bio",
                    topicId = "bio_topic_1",
                    questionText = "Which of the following cellular components is found in both prokaryotic (bacterial) and eukaryotic plant cells?",
                    optionA = "Nuclear membrane",
                    optionB = "Ribosomes",
                    optionC = "Mitochondria",
                    optionD = "Endoplasmic reticulum",
                    correctOption = "B",
                    explanation = "Ribosomes (70S in prokaryotes, 80S in eukaryotes) are non-membrane bound organelles responsible for protein synthesis and are present in all living cells, including bacteria and plants.",
                    difficulty = "JAMB-Standard",
                    yearMetadata = "JAMB Syllabus Practice Model"
                ),
                Question(
                    id = "q_bio_2",
                    subjectId = "subj_bio",
                    topicId = "bio_topic_1",
                    questionText = "The organelle responsible for hydrolytic digestion and cellular autolysis during programmed cell death is the:",
                    optionA = "Golgi body",
                    optionB = "Lysosome",
                    optionC = "Centrosome",
                    optionD = "Peroxisome",
                    correctOption = "B",
                    explanation = "Lysosomes contain powerful hydrolytic enzymes that break down waste materials, cellular debris, and foreign invaders, and trigger autolysis when ruptured.",
                    difficulty = "Basic",
                    yearMetadata = "JAMB Syllabus Practice Model"
                ),
                Question(
                    id = "q_bio_3",
                    subjectId = "subj_bio",
                    topicId = "bio_topic_1",
                    questionText = "If a plant cell with a water potential of -400 kPa is placed in a sucrose solution with a water potential of -800 kPa, the cell will:",
                    optionA = "Become turgid as water rushes in",
                    optionB = "Undergo plasmolysis as water leaves the cell",
                    optionC = "Burst due to excessive endosmosis",
                    optionD = "Remain unchanged in volume",
                    correctOption = "B",
                    explanation = "Water moves from a region of higher (less negative) water potential (-400 kPa) to lower water potential (-800 kPa). The cell loses water, causing the protoplast to shrink away from the cell wall (plasmolysis).",
                    difficulty = "Application",
                    yearMetadata = "JAMB Syllabus Practice Model"
                ),
                Question(
                    id = "q_chem_1",
                    subjectId = "subj_chem",
                    topicId = "chem_topic_2",
                    questionText = "The high boiling point of water (100°C) relative to hydrogen sulfide (H₂S, -60°C) is primarily attributed to:",
                    optionA = "Covalent bonding within the H₂O molecule",
                    optionB = "Intermolecular hydrogen bonding in water",
                    optionC = "Ionic dissociation of water molecules",
                    optionD = "Higher molecular mass of oxygen compared to sulfur",
                    correctOption = "B",
                    explanation = "Oxygen is highly electronegative, creating strong dipole-dipole hydrogen bonds between water molecules. Substantial thermal energy is required to break these intermolecular hydrogen bonds.",
                    difficulty = "JAMB-Standard",
                    yearMetadata = "JAMB Chemistry IBASS Model"
                ),
                Question(
                    id = "q_chem_2",
                    subjectId = "subj_chem",
                    topicId = "chem_topic_2",
                    questionText = "Which species contains a coordinate (dative) covalent bond?",
                    optionA = "CH₄ (Methane)",
                    optionB = "NaCl (Sodium chloride)",
                    optionC = "NH₄⁺ (Ammonium ion)",
                    optionD = "Cl₂ (Chlorine gas)",
                    correctOption = "C",
                    explanation = "In NH₄⁺, nitrogen donates its lone pair of electrons to a proton (H⁺) which contributes no electrons to the bond, forming a coordinate dative covalent bond.",
                    difficulty = "JAMB-Standard",
                    yearMetadata = "JAMB Chemistry IBASS Model"
                ),
                Question(
                    id = "q_eng_1",
                    subjectId = "subj_eng",
                    topicId = "eng_topic_2",
                    questionText = "Neither the mathematics teacher nor the students ______ happy with the examination timetable.",
                    optionA = "was",
                    optionB = "were",
                    optionC = "is",
                    optionD = "are being",
                    correctOption = "B",
                    explanation = "According to the rule of proximity for 'neither...nor', the verb must agree with the nearest subject. Since 'students' is plural, the past tense plural verb 'were' is correct.",
                    difficulty = "JAMB-Standard",
                    yearMetadata = "JAMB English IBASS Model"
                ),
                Question(
                    id = "q_eng_2",
                    subjectId = "subj_eng",
                    topicId = "eng_topic_2",
                    questionText = "The Governor, accompanied by three commissioners and several advisers, ______ just arrived at the state banquet hall.",
                    optionA = "have",
                    optionB = "has",
                    optionC = "are",
                    optionD = "were",
                    correctOption = "B",
                    explanation = "Phrases introduced by 'accompanied by' are parenthetical and do not affect grammatical number. The singular subject 'The Governor' takes the singular auxiliary verb 'has'.",
                    difficulty = "JAMB-Standard",
                    yearMetadata = "JAMB English IBASS Model"
                ),
                Question(
                    id = "q_phy_1",
                    subjectId = "subj_phy",
                    topicId = "phy_topic_1",
                    questionText = "A car traveling at 20 m/s decelerates uniformly to rest over a distance of 50 m. The magnitude of the deceleration is:",
                    optionA = "2.0 m/s²",
                    optionB = "4.0 m/s²",
                    optionC = "8.0 m/s²",
                    optionD = "10.0 m/s²",
                    correctOption = "B",
                    explanation = "Using the equation v² = u² + 2as: 0 = (20)² + 2(a)(50) => 0 = 400 + 100a => 100a = -400 => a = -4.0 m/s². The deceleration magnitude is 4.0 m/s².",
                    difficulty = "JAMB-Standard",
                    yearMetadata = "JAMB Physics IBASS Model"
                )
            )
            db.questionDao().insertQuestions(questions)

            // 7. Initial Mistakes (for Mistake Analyzer)
            val mistakes = listOf(
                MistakeRecord(
                    id = 1,
                    subjectId = "subj_chem",
                    topicId = "chem_topic_3",
                    questionId = "q_chem_mock_err1",
                    mistakeCount = 3,
                    lastMistakeAt = System.currentTimeMillis() - (12 * 60 * 60 * 1000),
                    resolved = false
                ),
                MistakeRecord(
                    id = 2,
                    subjectId = "subj_phy",
                    topicId = "phy_topic_3",
                    questionId = "q_phy_mock_err2",
                    mistakeCount = 2,
                    lastMistakeAt = System.currentTimeMillis() - (24 * 60 * 60 * 1000),
                    resolved = false
                )
            )
            for (m in mistakes) {
                db.mistakeDao().insertMistake(m)
            }

            // 8. Initial Study Plan Tasks (Today's Study Plan)
            val tasks = listOf(
                StudyPlanTask(
                    id = "task_today_1",
                    subjectId = "subj_bio",
                    topicId = "bio_topic_1",
                    title = "Biology: Cell Structure & Organelles",
                    taskType = "LEARN",
                    durationMins = 25,
                    isCompleted = true,
                    scheduledDate = "Today",
                    priority = "High"
                ),
                StudyPlanTask(
                    id = "task_today_2",
                    subjectId = "subj_chem",
                    topicId = "chem_topic_2",
                    title = "Chemistry: Chemical Bonding Practice",
                    taskType = "PRACTICE",
                    durationMins = 30,
                    isCompleted = false,
                    scheduledDate = "Today",
                    priority = "High"
                ),
                StudyPlanTask(
                    id = "task_today_3",
                    subjectId = "subj_eng",
                    topicId = "eng_topic_2",
                    title = "English: Concord & Grammatical Structure Quiz",
                    taskType = "QUIZ",
                    durationMins = 20,
                    isCompleted = false,
                    scheduledDate = "Today",
                    priority = "Medium"
                ),
                StudyPlanTask(
                    id = "task_today_4",
                    subjectId = "subj_phy",
                    topicId = "phy_topic_1",
                    title = "Physics: Equations of Uniform Motion Drill",
                    taskType = "REVISE",
                    durationMins = 25,
                    isCompleted = false,
                    scheduledDate = "Today",
                    priority = "High"
                )
            )
            db.studyPlanDao().insertTasks(tasks)

            // 9. Achievements
            val achievements = listOf(
                Achievement(
                    id = "ach_1",
                    badgeKey = "streak_7",
                    title = "7-Day Study Streak",
                    description = "Consistently study on JAMB Genius for 7 consecutive days.",
                    iconName = "local_fire_department",
                    xpReward = 200,
                    isUnlocked = false
                ),
                Achievement(
                    id = "ach_2",
                    badgeKey = "first_mock",
                    title = "CBT Pioneer",
                    description = "Completed your first official JAMB CBT timed mock examination.",
                    iconName = "military_tech",
                    xpReward = 150,
                    isUnlocked = true,
                    unlockedAt = System.currentTimeMillis() - (2 * 24 * 60 * 60 * 1000)
                ),
                Achievement(
                    id = "ach_3",
                    badgeKey = "questions_100",
                    title = "Centurion Scholar",
                    description = "Successfully answered 100 authentic JAMB practice questions.",
                    iconName = "verified",
                    xpReward = 250,
                    isUnlocked = false
                ),
                Achievement(
                    id = "ach_4",
                    badgeKey = "bio_master",
                    title = "Biology Ace",
                    description = "Attained 80%+ mastery across all Biology syllabus topics.",
                    iconName = "biotech",
                    xpReward = 300,
                    isUnlocked = false
                ),
                Achievement(
                    id = "ach_5",
                    badgeKey = "ai_tutor_user",
                    title = "Inquisitive Mind",
                    description = "Engaged in 10 deep pedagogical sessions with the AI Personal Tutor.",
                    iconName = "smart_toy",
                    xpReward = 100,
                    isUnlocked = true,
                    unlockedAt = System.currentTimeMillis() - (1 * 24 * 60 * 60 * 1000)
                )
            )
            db.achievementDao().insertAchievements(achievements)

            // 10. Sample CBT Mock Result
            val mockResult = CbtMockResult(
                id = 1,
                examName = "JAMB Diagnostic CBT Mock #1",
                subjectIdsCsv = "subj_eng,subj_bio,subj_chem,subj_phy",
                totalScore = 278,
                maxScore = 400,
                timeUsedSecs = 6120, // ~1 hr 42 mins
                totalQuestions = 180,
                correctAnswersCount = 125,
                subjectsBreakdownJson = """[{"name":"Use of English","score":68,"max":100},{"name":"Biology","score":74,"max":100},{"name":"Chemistry","score":68,"max":100},{"name":"Physics","score":68,"max":100}]""",
                weakTopicsJson = """["Acids, Bases & Redox Reactions","DC Current Electricity","Stress & Phonology"]""",
                strongTopicsJson = """["Cell Structure & Organization","Separation of Mixtures","Comprehension Inference"]""",
                recommendationsJson = """["Dedicate 25 mins daily to Redox Reaction balancing","Review Ohm's Law and DC Circuit Kirchhoff rules","Practice 15 Oral English stress questions daily"]"""
            )
            db.cbtMockDao().insertMockResult(mockResult)
        }
    }
}
