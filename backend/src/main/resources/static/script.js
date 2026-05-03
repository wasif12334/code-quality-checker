async function analyzeCode() {
    const codeInput = document.getElementById("code");
    const resultDiv = document.getElementById("result");
    const loadingDiv = document.getElementById("loading");
    const analyzeBtn = document.getElementById("analyzeBtn");
    
    const code = codeInput.value.trim();

    if (!code) {
        // Show temporary error if empty
        resultDiv.classList.remove("empty-state");
        resultDiv.className = "error";
        resultDiv.innerHTML = "⚠️ Please paste some Java code to analyze.";
        return;
    }

    // UI State: Loading
    resultDiv.classList.add("hidden");
    loadingDiv.classList.remove("hidden");
    analyzeBtn.disabled = true;
    analyzeBtn.innerHTML = `
        <svg class="spinner" width="16" height="16" viewBox="0 0 24 24" fill="none" style="animation: spin 1s linear infinite; border: none;">
            <circle cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4" stroke-linecap="round" stroke-dasharray="31.4 31.4" opacity="0.3"></circle>
            <path d="M12 2C6.477 2 2 6.477 2 12" stroke="currentColor" stroke-width="4" stroke-linecap="round"></path>
        </svg>
        <span>Analyzing...</span>
    `;

    try {
        // We use relative path because the frontend and backend are now served together
        let res = await fetch("/api/analyze", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            // Wrap in the CodeRequest JSON object structure expected by Spring Boot
            body: JSON.stringify({ code: code })
        });

        if (!res.ok) {
            throw new Error(`Server responded with status: ${res.status}`);
        }

        let text = await res.text();
        
        try {
            const jsonResponse = JSON.parse(text);
            
            // Extract the markdown text from the Gemini JSON response
            if (jsonResponse.candidates && jsonResponse.candidates.length > 0) {
                const markdownText = jsonResponse.candidates[0].content.parts[0].text;
                
                // UI State: Success
                resultDiv.className = "markdown-content"; 
                resultDiv.innerHTML = marked.parse(markdownText);
            } else {
                resultDiv.className = "";
                resultDiv.innerText = "No analysis result found in response.";
            }
        } catch (e) {
            // Fallback if response is plain text (like an error message)
            resultDiv.className = "";
            resultDiv.innerText = text;
        }
        
    } catch (error) {
        // UI State: Error
        resultDiv.className = "error";
        resultDiv.innerText = "❌ Analysis failed. " + error.message;
    } finally {
        // Restore button state
        loadingDiv.classList.add("hidden");
        resultDiv.classList.remove("hidden");
        analyzeBtn.disabled = false;
        analyzeBtn.innerHTML = `
            <span>Analyze Code</span>
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M5 12H19M19 12L12 5M19 12L12 19" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
        `;
    }
}
