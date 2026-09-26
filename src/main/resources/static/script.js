```javascript
// TalentMatchPro frontend

// ============================================================
// API BASE URL
// ============================================================
// IMPORTANT:
// Leave this empty.
// The frontend and Spring Boot backend are deployed together,
// so the browser will automatically use the current domain.
//
// Local:
// http://localhost:8080/api/...
//
// Render:
// https://ai-talentmatch-pro.onrender.com/api/...
//
// This avoids hard-coded localhost URLs in production.
const API_BASE = "";

// This file intentionally contains plain JavaScript only.
// No external JavaScript library is required.

const resumeForm = document.getElementById("resumeForm");
const jobForm = document.getElementById("jobForm");
const result = document.getElementById("result");

const resumeStatus = document.getElementById("resumeStatus");
const jobStatus = document.getElementById("jobStatus");
const rankingStatus = document.getElementById("rankingStatus");
const globalStatus = document.getElementById("globalStatus");

const uploadResumeBtn = document.getElementById("uploadResumeBtn");
const createJobBtn = document.getElementById("createJobBtn");
const matchBtn = document.getElementById("matchBtn");
const rankBtn = document.getElementById("rankBtn");

// ============================================================
// PAGE LOAD
// ============================================================

document.addEventListener("DOMContentLoaded", async () => {
    await Promise.all([loadResumes(), loadJobs()]);
});

// ============================================================
// COMMON HELPERS
// ============================================================

function setStatus(element, message, type = "info") {
    element.textContent = message;
    element.className = `status ${type}`;
}

function hideStatus(element) {
    element.className = "status hidden";
    element.textContent = "";
}

function setButtonLoading(button, loading, normalText) {
    button.disabled = loading;
    button.textContent = loading ? "Please wait..." : normalText;
}

async function readError(response) {
    const text = await response.text();

    if (!text) {
        return `HTTP ${response.status} ${response.statusText}`;
    }

    try {
        const data = JSON.parse(text);
        return data.message || data.error || text;
    } catch (_) {
        return text;
    }
}

function escapeHtml(value) {
    return String(value ?? "")
        .replace(/&/g, "&amp;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;")
        .replace(/"/g, "&quot;")
        .replace(/'/g, "&#039;");
}

// ============================================================
// 1. UPLOAD RESUME
// POST /api/resumes/upload
// ============================================================

resumeForm.addEventListener("submit", async (event) => {
    event.preventDefault();

    const fileInput = document.getElementById("resumeFile");
    const candidateName = document.getElementById("candidateName").value.trim();
    const file = fileInput.files[0];

    if (!candidateName) {
        setStatus(resumeStatus, "Please enter the candidate name.", "error");
        return;
    }

    if (!file) {
        setStatus(resumeStatus, "Please select a resume file.", "error");
        return;
    }

    const formData = new FormData();
    formData.append("file", file);
    formData.append("candidateName", candidateName);

    hideStatus(resumeStatus);
    setButtonLoading(uploadResumeBtn, true, "Upload Resume");

    try {
        const response = await fetch(`${API_BASE}/api/resumes/upload`, {
            method: "POST",
            body: formData
        });

        if (!response.ok) {
            throw new Error(await readError(response));
        }

        const resume = await response.json();

        setStatus(
            resumeStatus,
            `Resume uploaded successfully. Resume ID: ${resume.resumeId}`,
            "success"
        );

        setStatus(
            globalStatus,
            "Resume uploaded successfully. It is now available for AI matching.",
            "success"
        );

        resumeForm.reset();
        await loadResumes();

    } catch (error) {
        console.error("Resume upload error:", error);
        setStatus(resumeStatus, `Upload failed: ${error.message}`, "error");
        setStatus(globalStatus, "Resume upload failed. Check the error message.", "error");
    } finally {
        setButtonLoading(uploadResumeBtn, false, "Upload Resume");
    }
});

// ============================================================
// 2. CREATE JOB
// POST /api/jobs
// ============================================================

jobForm.addEventListener("submit", async (event) => {
    event.preventDefault();

    const experienceValue = Number(document.getElementById("experience").value);

    if (Number.isNaN(experienceValue) || experienceValue < 0) {
        setStatus(jobStatus, "Experience must be 0 or greater.", "error");
        return;
    }

    const job = {
        title: document.getElementById("title").value.trim(),
        companyName: document.getElementById("companyName").value.trim(),
        description: document.getElementById("description").value.trim(),
        industry: document.getElementById("industry").value,
        role: document.getElementById("role").value,
        experience: experienceValue
    };

    hideStatus(jobStatus);
    setButtonLoading(createJobBtn, true, "Create Job");

    try {
        const response = await fetch(`${API_BASE}/api/jobs`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(job)
        });

        if (!response.ok) {
            throw new Error(await readError(response));
        }

        const savedJob = await response.json();

        setStatus(
            jobStatus,
            `Job created successfully. Job ID: ${savedJob.jobId}`,
            "success"
        );

        setStatus(
            globalStatus,
            "Job created successfully. You can now run AI matching.",
            "success"
        );

        jobForm.reset();
        document.getElementById("experience").value = "0";
        await loadJobs();

    } catch (error) {
        console.error("Job creation error:", error);
        setStatus(jobStatus, `Job creation failed: ${error.message}`, "error");
        setStatus(globalStatus, "Job creation failed. Check the error message.", "error");
    } finally {
        setButtonLoading(createJobBtn, false, "Create Job");
    }
});

// ============================================================
// 3. LOAD ALL RESUMES
// GET /api/resumes
// ============================================================

async function loadResumes() {
    const container = document.getElementById("resumeList");

    try {
        const response = await fetch(`${API_BASE}/api/resumes`);

        if (!response.ok) {
            throw new Error(await readError(response));
        }

        const resumes = await response.json();

        updateResumeDropdowns(resumes);
        displayResumeDatabase(resumes);

    } catch (error) {
        console.error("Could not load resumes:", error);
        container.innerHTML = `<div class="status error">Could not load resumes: ${escapeHtml(error.message)}</div>`;
    }
}

// ============================================================
// 4. LOAD ALL JOBS
// GET /api/jobs
// ============================================================

async function loadJobs() {
    const container = document.getElementById("jobList");

    try {
        const response = await fetch(`${API_BASE}/api/jobs`);

        if (!response.ok) {
            throw new Error(await readError(response));
        }

        const jobs = await response.json();

        updateJobDropdowns(jobs);
        displayJobDatabase(jobs);

    } catch (error) {
        console.error("Could not load jobs:", error);
        container.innerHTML = `<div class="status error">Could not load jobs: ${escapeHtml(error.message)}</div>`;
    }
}

// ============================================================
// 5. RESUME DROPDOWNS
// ============================================================

function updateResumeDropdowns(resumes) {
    const select = document.getElementById("individualResumeSelect");

    select.innerHTML = '<option value="">Select Resume</option>';

    resumes.forEach((resume) => {
        const option = document.createElement("option");
        option.value = resume.resumeId;
        option.textContent = `${resume.candidateName || "Unnamed candidate"} (ID: ${resume.resumeId})`;
        select.appendChild(option);
    });
}

// ============================================================
// 6. JOB DROPDOWNS
// ============================================================

function updateJobDropdowns(jobs) {
    const individualSelect = document.getElementById("individualJobSelect");
    const rankingSelect = document.getElementById("rankingJobSelect");

    individualSelect.innerHTML = '<option value="">Select Job</option>';
    rankingSelect.innerHTML = '<option value="">Select Job</option>';

    jobs.forEach((job) => {
        const text = `${job.title || "Untitled job"} - ${job.companyName || "Unknown company"} (ID: ${job.jobId})`;

        const option1 = document.createElement("option");
        option1.value = job.jobId;
        option1.textContent = text;
        individualSelect.appendChild(option1);

        const option2 = document.createElement("option");
        option2.value = job.jobId;
        option2.textContent = text;
        rankingSelect.appendChild(option2);
    });
}

// ============================================================
// 7. DISPLAY RESUME DATABASE
// ============================================================

function displayResumeDatabase(resumes) {
    const container = document.getElementById("resumeList");

    if (!resumes || resumes.length === 0) {
        container.innerHTML = '<p class="muted">No resumes uploaded yet.</p>';
        return;
    }

    container.innerHTML = "";

    resumes.forEach((resume) => {
        const item = document.createElement("div");
        item.className = "resume-item";

        item.innerHTML = `
            <div class="item-main">
                <strong>${escapeHtml(resume.candidateName || "Unnamed candidate")}</strong>
                <div class="item-meta">${escapeHtml(resume.fileName || "Resume file")}</div>
            </div>
            <span class="id-badge">Resume ID ${escapeHtml(resume.resumeId)}</span>
        `;

        container.appendChild(item);
    });
}

// ============================================================
// 8. DISPLAY JOB DATABASE
// ============================================================

function displayJobDatabase(jobs) {
    const container = document.getElementById("jobList");

    if (!jobs || jobs.length === 0) {
        container.innerHTML = '<p class="muted">No jobs created yet.</p>';
        return;
    }

    container.innerHTML = "";

    jobs.forEach((job) => {
        const item = document.createElement("div");
        item.className = "job-item";

        item.innerHTML = `
            <div class="item-main">
                <strong>${escapeHtml(job.title || "Untitled job")}</strong>
                <div class="item-meta">
                    ${escapeHtml(job.companyName || "Unknown company")} •
                    ${escapeHtml(job.industry || "N/A")} •
                    ${escapeHtml(job.role || "N/A")} •
                    ${escapeHtml(job.experience)} years
                </div>
            </div>
            <div class="job-actions">
                <span class="id-badge">Job ID ${escapeHtml(job.jobId)}</span>
                <button type="button" class="delete-btn" data-job-id="${escapeHtml(job.jobId)}">Delete</button>
            </div>
        `;

        item.querySelector(".delete-btn").addEventListener("click", () => deleteJob(job.jobId));
        container.appendChild(item);
    });
}

// ============================================================
// 9. DELETE JOB
// DELETE /api/jobs/{id}
// ============================================================

async function deleteJob(jobId) {
    const confirmed = window.confirm(`Delete Job ID ${jobId}?`);

    if (!confirmed) {
        return;
    }

    try {
        const response = await fetch(`${API_BASE}/api/jobs/${jobId}`, {
            method: "DELETE"
        });

        if (!response.ok) {
            throw new Error(await readError(response));
        }

        setStatus(globalStatus, `Job ${jobId} deleted successfully.`, "success");
        result.textContent = "Your AI analysis will appear here.";
        await loadJobs();

    } catch (error) {
        console.error("Delete job error:", error);
        setStatus(globalStatus, `Could not delete job: ${error.message}`, "error");
    }
}

// ============================================================
// 10. REFRESH BUTTONS
// ============================================================

document.getElementById("refreshResumesBtn").addEventListener("click", async () => {
    await loadResumes();
});

document.getElementById("refreshJobsBtn").addEventListener("click", async () => {
    await loadJobs();
});

// ============================================================
// 11. INDIVIDUAL AI MATCH
// POST /api/match?jobId=...&resumeId=...
// ============================================================

matchBtn.addEventListener("click", async () => {
    const resumeId = document.getElementById("individualResumeSelect").value;
    const jobId = document.getElementById("individualJobSelect").value;

    if (!resumeId || !jobId) {
        result.textContent = "Please select both a resume and a job.";
        return;
    }

    result.textContent = "AI is analyzing the resume... Please wait.";
    setButtonLoading(matchBtn, true, "Analyze Resume with AI");

    try {
        const response = await fetch(`${API_BASE}/api/match?jobId=${encodeURIComponent(jobId)}&resumeId=${encodeURIComponent(resumeId)}`, {
            method: "POST"
        });

        if (!response.ok) {
            throw new Error(await readError(response));
        }

        const text = await response.text();
        result.textContent = text || "AI returned an empty response.";
        setStatus(globalStatus, "Individual AI matching completed.", "success");

    } catch (error) {
        console.error("Matching error:", error);
        result.textContent = `Matching failed: ${error.message}`;
        setStatus(globalStatus, "Individual AI matching failed. Check your Groq configuration.", "error");
    } finally {
        setButtonLoading(matchBtn, false, "Analyze Resume with AI");
    }
});

// ============================================================
// 12. CANDIDATE RANKING
// POST /api/match/rank?jobId=...
// ============================================================

rankBtn.addEventListener("click", async () => {
    const jobId = document.getElementById("rankingJobSelect").value;
    const rankingResults = document.getElementById("rankingResults");

    if (!jobId) {
        setStatus(rankingStatus, "Please select a job first.", "error");
        return;
    }

    setStatus(
        rankingStatus,
        "AI is analyzing all resumes. This may take some time...",
        "info"
    );

    rankingResults.innerHTML = "";
    setButtonLoading(rankBtn, true, "Find Best Candidates");

    try {
        const response = await fetch(`${API_BASE}/api/match/rank?jobId=${encodeURIComponent(jobId)}`, {
            method: "POST"
        });

        if (!response.ok) {
            throw new Error(await readError(response));
        }

        const candidates = await response.json();

        setStatus(
            rankingStatus,
            `${candidates.length} candidate${candidates.length === 1 ? "" : "s"} analyzed.`,
            "success"
        );

        displayRankingResults(candidates);
        setStatus(globalStatus, "Candidate ranking completed.", "success");

    } catch (error) {
        console.error("Ranking error:", error);
        setStatus(rankingStatus, `Ranking failed: ${error.message}`, "error");
        setStatus(globalStatus, "Ranking failed. Check your Groq configuration.", "error");
    } finally {
        setButtonLoading(rankBtn, false, "Find Best Candidates");
    }
});

// ============================================================
// 13. DISPLAY RANKING RESULTS
// ============================================================

function displayRankingResults(candidates) {
    const container = document.getElementById("rankingResults");

    container.innerHTML = "";

    if (!candidates || candidates.length === 0) {
        container.innerHTML = '<p class="muted">No candidates could be ranked. Check that resumes exist and contain readable text.</p>';
        return;
    }

    candidates.forEach((candidate, index) => {
        const score = Number(candidate.score) || 0;
        const scoreClass = score >= 75 ? "high" : score >= 50 ? "medium" : "low";

        const matchedSkills = Array.isArray(candidate.matchedSkills)
            ? candidate.matchedSkills
            : [];

        const missingSkills = Array.isArray(candidate.missingSkills)
            ? candidate.missingSkills
            : [];

        const card = document.createElement("div");
        card.className = "candidate-card";

        card.innerHTML = `
            <div class="candidate-top">
                <div>
                    <div class="rank-number">Rank #${index + 1}</div>
                    <h3>${escapeHtml(candidate.candidateName || "Unknown candidate")}</h3>
                    <div class="item-meta">${escapeHtml(candidate.fileName || "Resume")}</div>
                </div>
                <div class="score ${scoreClass}">${score}%</div>
            </div>

            <div class="recommendation">
                ${escapeHtml(candidate.recommendation || "REVIEW")}
            </div>

            <div class="analysis-section">
                <strong>Matched skills</strong>
                <div class="skills">
                    ${renderSkills(matchedSkills, "matched", "None identified")}
                </div>
            </div>

            <div class="analysis-section">
                <strong>Missing skills</strong>
                <div class="skills">
                    ${renderSkills(missingSkills, "missing", "None identified")}
                </div>
            </div>

            <div class="analysis-section">
                <strong>AI explanation</strong>
                <div class="item-meta">
                    ${escapeHtml(candidate.explanation || "No explanation returned by AI.")}
                </div>
            </div>
        `;

        container.appendChild(card);
    });
}

function renderSkills(skills, className, emptyText) {
    if (!skills.length) {
        return `<span class="muted">${escapeHtml(emptyText)}</span>`;
    }

    return skills
        .map(skill => `<span class="skill ${className}">${escapeHtml(skill)}</span>`)
        .join("");
}
```
