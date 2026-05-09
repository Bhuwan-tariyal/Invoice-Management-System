// 🔐 Protect page
if (!localStorage.getItem("token")) {

    window.location.href = "/login";
}

function showSection(section) {
    document.getElementById("home").classList.add("hidden");
    document.getElementById("about").classList.add("hidden");

    document.getElementById(section).classList.remove("hidden");
}

function logout() {
    localStorage.removeItem("token");
    window.location.href = "/login";
}

function loadProfile() {
    const loader = document.getElementById('loader-wrapper');
    loader.style.display = 'flex';
    const token = localStorage.getItem("token");

    fetch("/api/profile", {
        method: "GET",
        headers: {
            "Authorization": "Bearer " + token
        }
    })
    .then(res => {
       if (res.status === 403) {
          logout();
       } else if (!res.ok) throw new Error("Failed");
        return res.json();
    })
    .then(data => {
           document.getElementById("profileName").innerText = data.customerName;
           document.getElementById("profileEmail").innerText = data.customerEmail;
           document.getElementById("profilePhone").innerText = data.customerPhone;
           document.getElementById("profileAddress").innerText = data.customerAddress;


        document.getElementById("profileDrawer").classList.add("open");
        document.getElementById("drawerOverlay").classList.add("show");
    })
    .catch(err => {
        alert("Failed to load profile");
    }).finally(() => {
          // 3. Hide the loader (This runs regardless of success or error)
          loader.style.display = 'none';
      });
}

function openStudentModal() {
    document.getElementById("studentModal").style.display = "flex";
}

function closeStudentModal() {
    document.getElementById("studentModal").style.display = "none";
    clearErrors();
}


function saveStudent() {
    if(!validateStudentDetails()) return ;
    const loader = document.getElementById('loader-wrapper');
    loader.style.display = 'flex';
    const token = localStorage.getItem("token");

    const student = {
        firstName: document.getElementById("firstName").value,
        lastName: document.getElementById("lastName").value,
        email: document.getElementById("email").value,
        phoneNumber: document.getElementById("phoneNumber").value,
        parentName : document.getElementById("parentName").value
    };

    fetch("/api/students/add", {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            "Authorization": "Bearer " + token
        },
        body: JSON.stringify(student)
    })
    .then(res => {
        if (res.status === 403) {
           logout();
        } else if (!res.ok) throw new Error("Failed");
        return res.json();
    })
    .then(data => {
        alert("Student added successfully 🎉");

        closeStudentModal();

        // clear fields
        document.getElementById("firstName").value = "";
        document.getElementById("lastName").value = "";
        document.getElementById("email").value = "";
        document.getElementById("phoneNumber").value = "";
    })
    .catch(err => {
        alert("Error saving student");
    }).finally(() => {
          // 3. Hide the loader (This runs regardless of success or error)
          loader.style.display = 'none';
      });
}

function loadStudents(dropdownId) {
        const loader = document.getElementById('loader-wrapper');
        loader.style.display = 'flex';
        fetch("/api/students/list", {
            headers: {
                "Authorization": "Bearer " + localStorage.getItem("token")
            }
        })
        .then(res => res.json())
        .then(data => {

            let dropdown = document.getElementById(dropdownId);
            dropdown.innerHTML = "";
            let option = document.createElement("option");
            option.text = "Select Student";
            option.value = "";
            dropdown.appendChild(option);
            data.forEach(s => {
                let option = document.createElement("option");
                option.value = s.id;
                option.text = s.fullName;
                dropdown.appendChild(option);
            });
    }).finally(() => {
          // 3. Hide the loader (This runs regardless of success or error)
          loader.style.display = 'none';
      });
    }

    function openInvoiceModal() {
        loadStudents("studentIdInvoice"); // load dropdown

        // Auto-fill today's date
        const today = new Date();
        const dd = String(today.getDate()).padStart(2, '0');
        const mm = String(today.getMonth() + 1).padStart(2, '0');
        const yyyy = today.getFullYear();
        document.getElementById('date').value = `${dd}/${mm}/${yyyy}`;
        document.getElementById("invoiceModal").style.display = "flex";
    }

    function closeInvoiceModal() {
        document.getElementById("invoiceModal").style.display = "none";
        clearErrors();
    }

    function saveInvoice() {
        if(!validateInvoiceDetails()) return ;
        const loader = document.getElementById('loader-wrapper');
        loader.style.display = 'flex';
        const token = localStorage.getItem("token");

        const invoice = {
            amount: document.getElementById("amount").value,
            hours: document.getElementById("hours").value,
            description: document.getElementById("description").value,
            studentId:  document.getElementById("studentIdInvoice").value,
            date : document.getElementById("date").value
        };

        fetch("/api/invoices/add", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                "Authorization": "Bearer " + token
            },
            body: JSON.stringify(invoice)
        })
        .then(res => {
            if (res.status === 403) {
               logout();
            } else if (!res.ok) throw new Error("Failed");
            return res.text();
        })
        .then(data => {
            alert("Invoice created successfully 🎉");
            closeInvoiceModal();
        })
        .catch(err => {
            alert("Error creating invoice");
        }).finally(() => {
              // 3. Hide the loader (This runs regardless of success or error)
              loader.style.display = 'none';
          });
    }


function openDownloadInvoiceModal() {
    loadStudents("studentIdInvoiceDownload");
    const now = new Date();
    const mm = String(now.getMonth() + 1).padStart(2, '0');
    const yyyy = now.getFullYear();
    const dd = String(now.getDate()).padStart(2, '0');

    document.getElementById('fromDate').value = `01/${mm}/${yyyy}`;
    document.getElementById('toDate').value = `${dd}/${mm}/${yyyy}`;
    document.getElementById("invoiceDownloadModal").style.display = "flex";
}

function closeDownloadInvoiceModal() {
    document.getElementById("invoiceDownloadModal").style.display = "none";
    clearErrors();
}


function downloadInvoice() {
    if(!validateDownloadInvoiceDetails()) return ;
    const loader = document.getElementById('loader-wrapper');
    loader.style.display = 'flex';
    const request = {
    studentId  : document.getElementById("studentIdInvoiceDownload").value,
    fromDate : document.getElementById("fromDate").value,
    toDate : document.getElementById("toDate").value
    };

    fetch("api/invoices/download", {
        method: 'POST',
        headers: {
            "Content-Type": "application/json",
            'Authorization': 'Bearer ' + localStorage.getItem('token')
        },
         body: JSON.stringify(request)
    })
    .then(response => response.blob())
    .then(blob => {
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement("a");
        a.href = url;
        a.download = "invoice.pdf";
        document.body.appendChild(a);
        a.click();
        a.remove();
        closeModal();
    })
    .catch(err => console.error(err))
    .finally(() => {
              // 3. Hide the loader (This runs regardless of success or error)
              loader.style.display = 'none';
          });
}

function closeProfile() {
    document.getElementById("profileDrawer").classList.remove("open");
    document.getElementById("drawerOverlay").classList.remove("show");
}



function loadDashboardStats() {
    const loader = document.getElementById('loader-wrapper');
    loader.style.display = 'flex';
    const token = localStorage.getItem("token");
    debugger;
    fetch("/api/dashboard/stats", {
        headers: {
            "Authorization": "Bearer " + token
        }
    })
    .then(res => {
        if (res.status === 403) {
           logout();
        } else if (!res.ok) throw new Error("Failed");
        return res.json();
    })
    .then(data => {

        document.getElementById("totalStudents").innerText = data.totalStudents;
        document.getElementById("totalInvoices").innerText = data.totalInvoices;
        document.getElementById("totalRevenue").innerText = "$" + data.totalRevenue;
        document.getElementById("userName").innerText = data.customerName;
    })
    .catch(err => console.error(err))
    .finally(() => {
                  // 3. Hide the loader (This runs regardless of success or error)
                  loader.style.display = 'none';
              });
}

function loadInvoices() {

    const token = localStorage.getItem("token");

//    fetch("/api/invoices", {
//        headers: {
//            "Authorization": "Bearer " + token
//        }
//    })
//    .then(res => res.json())
//    .then(data => {
//
//        let rows = "";
//
//        data.forEach(inv => {
//            rows += `
//                <tr>
//                    <td>${inv.id}</td>
//                    <td>${inv.studentName}</td>
//                    <td>₹${inv.amount}</td>
//                    <td>${inv.date}</td>
//                    <td>${inv.hours}</td>
//                    <td>
//                        <button class="table-btn" onclick="downloadSingle(${inv.id})">
//                            <i class="fa-solid fa-download"></i>
//                        </button>
//                    </td>
//                </tr>
//            `;
//        });
//
//        document.getElementById("invoiceTableBody").innerHTML = rows;
//    })
//    .catch(err => console.error(err));
}

document.addEventListener("DOMContentLoaded", function () {
    loadDashboardStats();
    loadInvoices();
});


 function validateStudentDetails() {
    let isValid = true;

    const fName = document.getElementById('firstName');
    const lName = document.getElementById('lastName');
    const pName = document.getElementById('parentName');
    const email = document.getElementById('email');
    const phone = document.getElementById('phoneNumber');

    const toggleError = (elementId, condition) => {
        const group = document.getElementById(elementId);
        if (condition) {
            group.classList.add('error');
            isValid = false;
        } else {
            group.classList.remove('error');
        }
    };

    // Validation Rules
    toggleError('fg-firstName', fName.value.trim().length < 2);
    toggleError('fg-lastName', lName.value.trim().length < 2);
    toggleError('fg-parentName', pName.value.trim().length < 3);

    // Email Regex
    const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    toggleError('fg-email', !emailPattern.test(email.value.trim()));

    // International Phone Regex: Allows +, numbers, spaces, dashes. Minimum 7 characters.
    const phonePattern = /^[\+]?[(]?[0-9]{3}[)]?[-\s\.]?[0-9]{3}[-\s\.]?[0-9]{4,6}$/im;
    // Alternative simpler check if you want it very loose:
    const isPhoneEmpty = phone.value.trim().length < 7;
    toggleError('fg-phoneNumber', isPhoneEmpty);

   return isValid;
}

 function validateInvoiceDetails() {
    const fields = ['studentIdInvoice', 'date', 'amount', 'hours', 'description'];
    let isValid = true;

    fields.forEach(f => {
      const input = document.getElementById(f);
      if (input.value.trim() === "" || input.value === "0") {
          document.getElementById('err-' + f).classList.add('error');
          isValid = false;
      } else {
          document.getElementById('err-' + f).classList.remove('error');
      }
    });
    return isValid;
 }


function validateDownloadInvoiceDetails() {
    const fields = ['studentIdInvoiceDownload', 'fromDate', 'toDate'];
    let isValid = true;

    fields.forEach(f => {
        const input = document.getElementById(f);
        if (input.value.trim() === "") {
            document.getElementById('fg-' + f).classList.add('error');
            isValid = false;
        } else {
            document.getElementById('fg-' + f).classList.remove('error');
        }
    });
    return isValid;
}
 function clearErrors() {
     document.querySelectorAll('.form-group').forEach(el => el.classList.remove('error'));
 }

 function toggleExtra() {
     const extra = document.getElementById('extraInfo');
     const btn = document.getElementById('toggleBtn');

     if (extra.classList.contains('hidden')) {
         extra.classList.remove('hidden');
         btn.textContent = 'Hide Roadmap';
     } else {
         extra.classList.add('hidden');
         btn.textContent = 'Show System Roadmap';
     }
 }