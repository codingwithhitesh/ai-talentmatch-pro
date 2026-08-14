const resumeForm = document.getElementById("resumeForm");
const jobForm = document.getElementById("jobForm");
const result = document.getElementById("result");

resumeForm.addEventListener("submit", async (e) => {
    e.preventDefault();

    const form = new FormData();
    form.append("file", document.getElementById("resumeFile").files[0]);
    form.append("candidateName", document.getElementById("candidateName").value);

    try {
        const response = await fetch("/api/resumes/upload", {
            method: "POST",
            body: form
        });

        if (!response.ok) throw new Error(await response.text());

        const resume = await response.json();

        document.getElementById("resumeId").value = resume.resumeId;
        document.getElementById("resumeStatus").textContent =
            "Uploaded successfully. Resume ID: " + resume.resumeId;
    } catch (err) {
        document.getElementById("resumeStatus").textContent =
            "Upload failed: " + err.message;
    }
});

jobForm.addEventListener("submit", async (e) => {
    e.preventDefault();

    const job = {
        title: document.getElementById("title").value,
        companyName: document.getElementById("companyName").value,
        description: document.getElementById("description").value,
        industry: document.getElementById("industry").value,
        role: document.getElementById("role").value,
        experience: Number(document.getElementById("experience").value)
    };

    try {
        const response = await fetch("/api/jobs", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(job)
        });

        if (!response.ok) throw new Error(await response.text());

        const savedJob = await response.json();

        document.getElementById("jobId").value = savedJob.jobId;
        document.getElementById("jobStatus").textContent =
            "Job created successfully. Job ID: " + savedJob.jobId;
    } catch (err) {
        document.getElementById("jobStatus").textContent =
            "Job creation failed: " + err.message;
    }
});

document.getElementById("matchBtn").addEventListener("click", async () => {
    const jobId = document.getElementById("jobId").value;
    const resumeId = document.getElementById("resumeId").value;

    if (!jobId || !resumeId) {
        result.textContent = "Enter/select a Resume ID and Job ID first.";
        return;
    }

    result.textContent = "AI is analyzing...";

    try {
        const response = await fetch(
            `/api/match?jobId=${jobId}&resumeId=${resumeId}`,
            { method: "POST" }
        );

        if (!response.ok) throw new Error(await response.text());

        result.textContent = await response.text();
    } catch (err) {
        result.textContent = "Matching failed: " + err.message;
    }
});
