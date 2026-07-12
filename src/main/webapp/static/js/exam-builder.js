const tabs = document.querySelectorAll(".type-tab");
const groups = document.querySelectorAll(".picker-group");

tabs.forEach((tab) => {
    tab.addEventListener("click", () => {
        const type = tab.dataset.type;
        tabs.forEach((item) => item.classList.remove("active"));
        tab.classList.add("active");

        groups.forEach((group) => {
            group.hidden = type !== "ALL" && group.dataset.type !== type;
        });
    });
});
