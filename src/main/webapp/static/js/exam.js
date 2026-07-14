const sidebarTimer = document.getElementById("sidebarTimer");
const form = document.getElementById("examForm");

if (sidebarTimer && form) {
    let remaining = Number(sidebarTimer.dataset.minutes || "0") * 60;
    let submitted = false;
    let switchCount = 0;
    const switchLimit = Math.max(1, Number(sidebarTimer.dataset.switchLimit || "2"));
    const timerCard = sidebarTimer.closest(".timer-card");

    const submitExam = () => {
        if (!submitted) {
            submitted = true;
            form.submit();
        }
    };

    const setState = () => {
        if (!timerCard) return;
        timerCard.classList.remove("warning", "danger");
        if (remaining <= 60) timerCard.classList.add("danger");
        else if (remaining <= 300) timerCard.classList.add("warning");
    };

    const render = () => {
        const minutes = Math.floor(Math.max(remaining, 0) / 60);
        const seconds = Math.max(remaining, 0) % 60;
        sidebarTimer.textContent = `${String(minutes).padStart(2, "0")}:${String(seconds).padStart(2, "0")}`;
        setState();
        if (remaining <= 0) {
            submitExam();
            return;
        }
        remaining -= 1;
        window.setTimeout(render, 1000);
    };

    const handleSwitch = () => {
        if (submitted) return;
        switchCount += 1;
        if (switchCount < switchLimit) {
            alert(`系统检测到你离开考试页面。这是第 ${switchCount} 次警告，第 ${switchLimit} 次切屏将强制提交试卷。`);
        } else {
            alert(`系统检测到第 ${switchCount} 次切屏，已达到限制，试卷将被强制提交。`);
            submitExam();
        }
    };

    document.addEventListener("visibilitychange", () => {
        if (document.hidden) handleSwitch();
    });

    form.addEventListener("submit", () => {
        submitted = true;
    });

    render();
}
