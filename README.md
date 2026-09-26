# 🚀 TalentMatchPro — AI-Powered Resume Matching & Candidate Ranking

**TalentMatchPro** is an AI-powered resume screening and candidate matching application built with **Java, Spring Boot, Spring AI, and Groq**.

The project takes a traditional backend-driven resume management system and extends it with Generative AI to automatically analyze resumes against job descriptions.

Instead of simply storing resumes, TalentMatchPro can **understand resume content, compare candidate profiles with job requirements, identify matching and missing skills, calculate a match score, and rank multiple candidates.**

---

## 🎯 What Problem Does It Solve?

Recruiters can receive dozens or hundreds of resumes for a single job opening.

Manually reviewing every resume can be:

* Time-consuming
* Repetitive
* Difficult to scale
* Inconsistent across candidates

TalentMatchPro explores how Generative AI can assist with this process.

The application allows a recruiter to provide a **Job Description** and then analyze candidate resumes using AI.

The system supports **two different resume-matching approaches**.

---

# ⭐ Two Ways of Resume Matching

## 1️⃣ Individual Resume → Job Matching

This mode answers:

> **"How well does this particular resume match this particular job?"**

The application takes:

```text
Job Description
        +
   One Resume
        ↓
   Groq LLM
        ↓
AI Match Analysis
```

The AI evaluates the candidate against the job description and provides information such as:

* Match assessment
* Matched skills
* Missing skills
* Candidate feedback

### Example

```text
Job:
Java Backend Developer

Resume:
Candidate with Java, Spring Boot, JPA and MySQL experience

AI Analysis:
Strong match
Matched Skills:
- Java
- Spring Boot
- JPA

Missing / weaker areas:
- Docker
- Kubernetes
```

This mode is useful when a recruiter wants to investigate **one particular candidate** in detail.

---

# 2️⃣ Bulk Resume → Job Ranking

This is where TalentMatchPro becomes more useful as a screening system.

Instead of sending every resume together in one huge prompt, the application evaluates **each resume independently against the same job description**.

For example, with 8 resumes:

```text
                 Job Description
                       │
        ┌──────────────┼──────────────┐
        ↓              ↓              ↓
     Resume 1       Resume 2       Resume 3
        ↓              ↓              ↓
       Groq           Groq           Groq
        ↓              ↓              ↓
       82%            91%            74%
        │              │              │
        └──────────────┼──────────────┘
                       ↓
              Java-side sorting
                       ↓
               Ranked Candidates
```

The application makes an individual AI evaluation for each resume.

The resulting candidate objects are then sorted by their match score in Java.

Conceptually:

```java
results.sort(
    Comparator.comparingInt(CandidateMatch::getScore)
              .reversed()
);
```

This keeps the **candidate evaluation** and **final ranking logic** under application control rather than asking the LLM to rank a huge collection of resumes in one response.

### Example Result

| Rank | Candidate   | Match Score | Recommendation |
| ---- | ----------- | ----------- | -------------- |
| 1    | Candidate A | 91          | SHORTLIST      |
| 2    | Candidate B | 86          | SHORTLIST      |
| 3    | Candidate C | 74          | REVIEW         |
| 4    | Candidate D | 61          | REVIEW         |

This mode is designed for **candidate screening and comparison across multiple resumes**.

---

# 🧠 Why Process Resumes Separately?

A key design decision in TalentMatchPro is that multiple resumes are **not combined into one enormous prompt**.

Instead:

```text
Job + Resume 1 → AI
Job + Resume 2 → AI
Job + Resume 3 → AI
...
Job + Resume N → AI
```

This approach provides several practical advantages:

### 🔹 Smaller AI requests

Each request contains only the job description and one candidate's resume.

### 🔹 Cleaner structured output

Each candidate can produce an independent result such as:

```json
{
  "score": 85,
  "matchedSkills": [
    "Java",
    "Spring Boot"
  ],
  "missingSkills": [
    "Docker"
  ],
  "recommendation": "SHORTLIST",
  "explanation": "Good match for the role."
}
```

### 🔹 Independent candidate evaluation

One candidate's resume does not need to be included in the same prompt as another candidate's resume.

### 🔹 Application-controlled ranking

The LLM evaluates candidates, while Java performs the final sorting.

This creates a clean separation:

```text
LLM
 ↓
Candidate Evaluation

Java Application
 ↓
Candidate Ranking
```

---

# 🤖 Generative AI Integration

TalentMatchPro uses **Spring AI** to integrate an LLM into the Spring Boot application.

The current implementation uses **Groq** as the AI provider.

The application communicates with Groq through an OpenAI-compatible API interface supported by the Spring AI OpenAI integration.

The architecture is therefore:

```text
Spring Boot
     │
     ↓
Spring AI
     │
     ↓
Groq API
     │
     ↓
LLM
```

The AI is used for actual resume/job analysis rather than simply generating static chatbot responses.

---

# 📄 Resume Processing

TalentMatchPro supports resume documents and extracts their text before sending relevant content to the AI layer.

The project uses **Apache Tika / Spring AI document tooling** for document processing.

The overall pipeline is:

```text
Resume PDF
    ↓
Document Extraction
    ↓
Extracted Resume Text
    ↓
Resume stored in application
    ↓
AI Matching
```

This means the LLM receives the **actual extracted resume content** instead of relying on filenames or manually entered candidate information.

---

# 🔄 End-to-End Matching Flow

The complete workflow looks like this:

```text
                    ┌─────────────────┐
                    │  Job Description │
                    └────────┬────────┘
                             │
                             ↓
                    ┌─────────────────┐
                    │  Spring Boot    │
                    │  Application    │
                    └────────┬────────┘
                             │
                 ┌───────────┴───────────┐
                 ↓                       ↓
          Individual Match        Bulk Ranking
                 ↓                       ↓
             One Resume          Multiple Resumes
                 │                       │
                 └───────────┬───────────┘
                             ↓
                       Spring AI
                             ↓
                          Groq LLM
                             ↓
                    Candidate Analysis
                             ↓
                    Structured Result
                             ↓
                       Java Ranking
```

---

# 🏗️ Architecture

The application follows a layered Spring Boot architecture.

```text
Frontend
   │
   ↓
REST Controllers
   │
   ↓
Service Layer
   │
   ├── Job Service
   ├── Resume Service
   └── AI Matching Service
   │
   ↓
Spring Data JPA
   │
   ↓
H2 Database
```

The AI portion extends the traditional backend architecture:

```text
Resume
   ↓
Text Extraction
   ↓
AI Matching Service
   ↓
Spring AI
   ↓
Groq
   ↓
CandidateMatch
   ↓
Ranking
```

---

# 🛠️ Technology Stack

## Backend

* **Java**
* **Spring Boot 3.2.4**
* **Spring Web**
* **Spring Data JPA**
* **Hibernate**
* **H2 Database**
* **REST APIs**

## AI / GenAI

* **Spring AI 1.0.0-M1**
* **Groq**
* **LLM-based resume analysis**
* **Prompt-based candidate evaluation**
* **Structured JSON candidate results**

## Document Processing

* **Apache Tika**
* PDF/document text extraction

## Frontend

* HTML
* CSS
* JavaScript
* Served through the Spring Boot application

## Development

* IntelliJ IDEA
* Maven
* Git
* GitHub

---

# ✨ Key Features

### 📌 Resume Management

* Upload resumes
* Extract resume text
* Store candidate information
* Retrieve resumes through backend APIs

### 📌 Job Management

* Create and manage job descriptions
* Retrieve job information
* Use job descriptions as the matching criteria

### 📌 AI Resume Matching

Compare a resume against a job description using an LLM.

### 📌 Candidate Ranking

Evaluate multiple resumes independently and produce a ranked candidate list.

### 📌 Skill Analysis

The AI identifies:

* Matched skills
* Missing skills
* Overall match score
* Recommendation
* Explanation

### 📌 Structured AI Output

The bulk-ranking workflow requests a predictable JSON structure which is then mapped into a Java `CandidateMatch` object using Jackson.

### 📌 Java-Controlled Ranking

The AI generates candidate scores, but the application performs the final sorting.

### 📌 Separation of Concerns

The application keeps:

* Resume management
* Job management
* AI analysis
* Candidate ranking

in separate responsibilities.

---

# 🔥 What Makes TalentMatchPro Different?

The main goal of this project was not simply:

> "Connect an LLM to a Spring Boot application."

Instead, it explores how an LLM can become part of an actual backend workflow.

### 1. AI is connected to real application data

The model receives:

```text
Real Job Description
+
Real Extracted Resume
```

rather than answering a generic prompt.

---

### 2. Two matching strategies

TalentMatchPro supports both:

```text
One Resume → One Job
```

and:

```text
Many Resumes → One Job → Ranked Candidates
```

This makes the application useful for both **detailed candidate analysis** and **initial candidate screening**.

---

### 3. Resumes are evaluated independently

Instead of putting all resumes into a single massive prompt, the application performs separate AI evaluations.

This creates a scalable conceptual pipeline:

```text
Resume → AI Evaluation → CandidateMatch
Resume → AI Evaluation → CandidateMatch
Resume → AI Evaluation → CandidateMatch
```

---

### 4. AI + traditional backend logic

The project deliberately combines AI reasoning with deterministic application logic.

```text
AI
 ↓
Understand & Evaluate Resume

Java
 ↓
Store & Sort Results
```

The LLM is responsible for natural-language understanding and evaluation.

Java remains responsible for application-level processing and ranking.

---

### 5. Provider flexibility

The AI integration uses Spring AI's OpenAI-compatible integration while pointing the configuration to Groq.

This keeps the AI provider configuration separate from the core matching service.

The application can therefore be designed around an AI abstraction rather than tightly coupling the matching logic to a specific provider.

---

# 📊 Example Candidate Evaluation

A candidate can produce a result similar to:

```json
{
  "score": 85,
  "matchedSkills": [
    "Java",
    "Spring Boot",
    "JPA"
  ],
  "missingSkills": [
    "Docker"
  ],
  "recommendation": "SHORTLIST",
  "explanation": "Good match for the backend development role."
}
```

The Java application can then attach the result to the original resume:

```text
Candidate ID
Candidate Name
Resume File
AI Score
Matched Skills
Missing Skills
Recommendation
Explanation
```

and use the score for ranking.

---

# 🔐 API Key Configuration

The Groq API key should **never be committed to GitHub**.

Use an environment variable:

```properties
spring.ai.openai.api-key=${GROQ_API_KEY}
```

Then configure:

```text
GROQ_API_KEY
```

in your local environment or deployment platform.

### Never do this:

```properties
spring.ai.openai.api-key=gsk_XXXXXXXXXXXXXXXX
```

Your secret API key should remain outside the repository.

---

# ▶️ Running Locally

## 1. Clone the repository

```bash
git clone https://github.com/<your-username>/TalentMatchPro.git
```

```bash
cd TalentMatchPro
```

## 2. Configure Groq API Key

Set:

```text
GROQ_API_KEY
```

as an environment variable.

## 3. Build the project

```bash
mvn clean install
```

## 4. Run the application

```bash
mvn spring-boot:run
```

Or run the main Spring Boot application directly from IntelliJ IDEA.

## 5. Open the application

```text
http://localhost:8080
```

The application also exposes the H2 console during local development.

---

# 🗄️ Database

The current development setup uses:

```text
H2 In-Memory Database
```

with a database similar to:

```text
jdbc:h2:mem:talentmatchdb
```

This is useful for development and demonstration because the project does not require an external database to get started.

For a production deployment, a persistent database would be more appropriate.

---

# 🚀 Deployment

The application is designed as a Spring Boot web application and can be deployed to a cloud platform such as **Render**.

For deployment, the Groq API key should be configured as a secure environment variable rather than stored in source code.

Typical deployment architecture:

```text
GitHub
   │
   ↓
Render
   │
   ↓
Spring Boot Application
   │
   ├── REST APIs
   ├── Resume Processing
   ├── H2 / Database
   │
   └── Spring AI
          │
          ↓
        Groq
```

> **Note:** The current H2 configuration is in-memory, so database contents are not intended to provide persistent production storage across application restarts.

---

# 📁 High-Level Project Structure

```text
src/
 └── main/
     ├── java/
     │   └── com.codewithhitesh.talentmatchpro/
     │       │
     │       ├── Controllers
     │       │
     │       ├── Services
     │       │   ├── JobService
     │       │   ├── ResumeService
     │       │   └── AiMatchingService
     │       │
     │       ├── Entities / Models
     │       │
     │       ├── Repositories
     │       │
     │       └── TalentmatchproApplication
     │
     └── resources/
         ├── application.properties
         └── static/
             └── index.html
```

---

# 🧪 Current Development Status

The application has been successfully tested locally with:

* Spring Boot startup
* Embedded Tomcat
* H2 database
* Resume loading
* Frontend serving
* Groq API integration
* AI-powered resume matching

The development environment currently loads multiple demonstration resumes for testing the candidate-ranking workflow.

---

# 🔮 Future Improvements

Potential next steps include:

* Persistent PostgreSQL database
* Asynchronous / concurrent resume processing
* Improved structured AI output validation
* More advanced scoring criteria
* Resume/job semantic similarity
* Embeddings and vector search
* RAG-based job/recruitment knowledge
* Authentication and authorization
* Recruiter dashboards
* Candidate analytics
* Production observability
* Rate-limit handling and retry strategies
* Production-grade document storage

---

# 💡 Key Learning Outcomes

This project was built as a practical exploration of combining traditional backend engineering with Generative AI.

It demonstrates concepts including:

* Building REST APIs with Spring Boot
* Database integration with JPA/Hibernate
* Document processing
* PDF text extraction
* Integrating LLMs into backend services
* Prompt engineering
* Structured AI responses
* AI-assisted candidate evaluation
* Multi-candidate processing
* Separating AI reasoning from deterministic business logic
* Environment-based API configuration
* Git/GitHub workflow
* Cloud deployment concepts

---

# 🎯 Project Vision

TalentMatchPro is an exploration of what happens when a traditional resume-management backend is extended with Generative AI.

The core idea is simple:

> **Don't just store resumes. Understand them.**

The application combines:

```text
Java
   +
Spring Boot
   +
Spring Data JPA
   +
Document Processing
   +
Spring AI
   +
Groq
   +
LLM-powered Resume Analysis
   +
Candidate Ranking
```

to create an end-to-end AI-assisted recruitment workflow.

---

## ⭐ Final Takeaway

TalentMatchPro demonstrates a practical pattern for building AI-powered backend applications:

```text
Real Business Data
        ↓
Traditional Backend
        ↓
AI Processing
        ↓
Structured AI Result
        ↓
Application Logic
        ↓
Useful Business Output
```

Rather than treating Generative AI as a standalone chatbot, this project integrates the LLM directly into a real application workflow where it can analyze resumes, compare candidates against job requirements, and assist with candidate screening.

---

### 👨‍💻 Built With

**Java • Spring Boot • Spring AI • Groq • Spring Data JPA • Hibernate • H2 • Apache Tika • REST APIs • HTML • CSS • JavaScript • Maven • Git • GitHub**

---

