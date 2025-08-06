document.addEventListener("DOMContentLoaded", () => {
  const loginForm = document.getElementById("loginForm");
  const registerForm = document.getElementById("registerForm");
  const messageBox = document.getElementById("message");

  // ==== XỬ LÝ ĐĂNG NHẬP ====
  if (loginForm) {
    loginForm.addEventListener("submit", function (e) {
      e.preventDefault();

      const email = document.getElementById("loginEmail").value.trim();
      const password = document.getElementById("loginPassword").value.trim();

      if (!email || !password) {
        messageBox.textContent = "Vui lòng nhập đầy đủ email và mật khẩu.";
        return;
      }

      fetch("http://localhost:8082/api/khach-hang/dang-nhap", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        credentials: "include", // Cho phép cookie nếu server hỗ trợ
        body: JSON.stringify({
          email: email,
          matKhau: password,
        }),
      })
        .then(async (res) => {
          if (!res.ok) {
            const errorData = await res.text();
            throw new Error(errorData || "Tài khoản hoặc mật khẩu không đúng!");
          }
          return res.json();
        })
        .then((data) => {
          localStorage.setItem("user", JSON.stringify(data));
          alert("Đăng nhập thành công!");
          window.location.href = "/index.html"; // Trang chủ
        })
        .catch((err) => {
          messageBox.textContent = err.message;
        });
    });
  }

  // ==== XỬ LÝ ĐĂNG KÝ ====
  if (registerForm) {
    registerForm.addEventListener("submit", function (e) {
      e.preventDefault();

      const ten = document.getElementById("registerTen").value.trim();
      const email = document.getElementById("registerEmail").value.trim();
      const password = document.getElementById("registerPassword").value.trim();

      if (!ten || !email || !password) {
        messageBox.textContent = "Vui lòng nhập đầy đủ thông tin đăng ký.";
        return;
      }

      fetch("http://localhost:8082/api/khach-hang/dang-ky", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          ten: ten,
          email: email,
          matKhau: password,
        }),
      })
        .then(async (res) => {
          if (!res.ok) {
            const errorData = await res.text();
            throw new Error(errorData || "Email đã tồn tại hoặc thông tin không hợp lệ!");
          }
          return res.json();
        })
        .then(() => {
          alert("Đăng ký thành công! Vui lòng đăng nhập.");
          registerForm.reset(); // Xóa form
        })
        .catch((err) => {
          messageBox.textContent = err.message;
        });
    });
  }
});
