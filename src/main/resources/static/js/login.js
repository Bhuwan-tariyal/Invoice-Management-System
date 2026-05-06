document.getElementById("loginForm").addEventListener("submit", function(e) {
    e.preventDefault();

    const result = document.getElementById("result");
    result.innerText = "Logging in...";
    result.style.color = "black";

    fetch("/api/auth/login", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({
            email: document.getElementById("email").value,
            password: document.getElementById("password").value
        })
    })
    .then(res => {
        if (!res.ok) {
            return res.text().then(err => { throw new Error(err); });
        }
        return res.json();
    })
    .then(data => {
        result.style.color = "green";
        result.innerText = data.message || "Login successful 🎉";

        // 🔥 store login state
        localStorage.setItem("token", data.token);

        // 🔥 redirect
        setTimeout(() => {
            window.location.href = "/dashboard";
        }, 1000);
    })
    .catch(err => {
        result.style.color = "red";
        result.innerText = err.message;
    });
});