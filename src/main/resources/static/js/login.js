const sections = ['loginSection', 'forgotSection', 'resetSection','otpSection'];
const authCard = document.getElementById('authCard');
const statusMsg = document.getElementById('statusMsg');
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
        if(data.success) {
            result.innerText = data.message || "Login successful 🎉";
            // store login state
            localStorage.setItem("token", data.token);
            // redirect
            setTimeout(() => {
                window.location.href = "/dashboard";
            }, 1000);
        } else {
            result.style.color = "red";
            result.innerText = "Please enter correct credentials";
        }

    })
    .catch(err => {
        result.style.color = "red";
        result.innerText = "Please enter correct credentials";
    });
});

// STEP 2: Send otp on Email (Transitions to Step 3)
document.getElementById('sendOTPForm').addEventListener('submit', (e) => {
    e.preventDefault();
    const btn = e.target.querySelector('button');
    const emailValue = document.getElementById('recoveryEmail').value;
    btn.innerHTML = '<i class="fa-solid fa-spinner fa-spin"></i> Sending...';

    fetch("/api/auth/forgot-password", {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify({ email: emailValue })
    })
    .then(res => {
       if (!res.ok) throw new Error("Failed");
       return res.json();
    })
    .then(data => {
        // Success: Move to OTP entry screen
        if(data.success) {
            showSection('otpSection');
        } else {
            triggerError("Invalid Email Address");
            btn.innerHTML = 'Send Code';
            btn.disabled = false;
        }
    })
    .catch(err => {
        triggerError("Invalid Email Address");
        btn.innerHTML = 'Send Code';
        btn.disabled = false;
    });
});


//STEP 3: varify Otp
document.getElementById('varifyOtpForm').addEventListener('submit', (e) => {
    e.preventDefault();
    const btn = e.target.querySelector('button');
    const otpValue = Array.from(otpInputs).map(i => i.value).join('');
    const emailValue = document.getElementById('recoveryEmail').value;

    btn.innerHTML = '<i class="fa-solid fa-spinner fa-spin"></i> Sending...';

    fetch("/api/auth/verify-otp", {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify({ email: emailValue ,otp :otpValue})
    })
    .then(res => {
       if (!res.ok) throw new Error("Failed");
       return res.json();
    })
    .then(data => {
        if(data.success) {
            // Success: Move to OTP entry screen
            localStorage.setItem("ResetToken", data.token);
            showSection('resetSection');
        } else {
            triggerError("Invalid Otp. Please try again.");
            btn.innerHTML = 'Varify Otp';
            btn.disabled = false;
        }
    })
    .catch(err => {
        triggerError("Invalid Otp. Please try again.");
        btn.innerHTML = 'Varify Otp';
        btn.disabled = false;
    });
});


// STEP 3: Set New Password
document.getElementById('setNewPasswordForm').addEventListener('submit', (e) => {
    e.preventDefault();
    const p1 = document.getElementById('newPass');
    const p2 = document.getElementById('confirmPass');
    const btn = e.target.querySelector('button');

    if(p1.value !== p2.value) {
        triggerError("Passwords do not match!");
        return;
    }

    btn.innerHTML = '<i class="fa-solid fa-spinner fa-spin"></i> Updating...';
    const token = localStorage.getItem("ResetToken");

    fetch("/api/customers/updatePassword", {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
             "Authorization": "Bearer " + token
        },
        body: JSON.stringify({ password: p1.value })
    })
    .then(res => {
        if(res.status === 401){
            localStorage.removeItem("token");
            window.location.href = "/login";
        }else if (!res.ok) throw new Error("Failed");
       return res.json();
    })
    .then(data => {
        btn.style.display = 'none';
        statusMsg.className = 'msg-box success-msg';
        statusMsg.innerHTML = `<i class="fa-solid fa-check-double"></i> Password updated! Returning to login...`;
        setTimeout(() => {
            showSection('loginSection');
            btn.style.display = 'block';
            btn.innerHTML = 'Update Password';
            localStorage.removeItem("ResetToken");
        }, 2500);
    })
    .catch(err => {
        triggerError("Unauthorized Access");
        btn.innerHTML = 'Update Password';
        btn.disabled = false;
    });
});


function showSection(targetId) {
    sections.forEach(id => document.getElementById(id).classList.add('hidden'));
    document.getElementById(targetId).classList.remove('hidden');
    clearStatus();
}

// OTP Auto-focus logic
const otpInputs = document.querySelectorAll('.otp-input');
otpInputs.forEach((input, index) => { input.addEventListener('input', (e) => { if (e.target.value.length > 0 && index < otpInputs.length - 1) { otpInputs[index + 1].focus();
        }
    });
    input.addEventListener('keydown', (e) => {
        if (e.key === 'Backspace' && !e.target.value && index > 0) {
            otpInputs[index - 1].focus();
        }
    });
});


function triggerError(message) {
    authCard.classList.add('shake');
    setTimeout(() => authCard.classList.remove('shake'), 400);
    statusMsg.className = 'msg-box error-msg';
    statusMsg.innerHTML = `<i class="fa-solid fa-circle-exclamation"></i> ${message}`;
}

 function clearStatus() {
    statusMsg.className = 'msg-box';
    statusMsg.innerHTML = '';
    document.querySelectorAll('input').forEach(i => i.classList.remove('input-error'));
}


function togglePass(id, icon) {
    const input = document.getElementById(id);
    input.type = input.type === 'password' ? 'text' : 'password';
    icon.classList.toggle('fa-eye');
    icon.classList.toggle('fa-eye-slash');
}