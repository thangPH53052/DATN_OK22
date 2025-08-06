// dưa phần vừa làm vào đây cho tôi

function addHeader() {
  const user = JSON.parse(localStorage.getItem("user"));
  const authHtml = user
    ? `<span id="userInfoBtn" style="color:white; cursor:pointer;">👤 ${user.ten}</span>
     <a href="#" onclick="dangXuat()" style="margin-left: 10px;">Đăng xuất</a>`
    : `<a href="/dangnhap.html">Đăng nhập</a>`;

  const headerHTML = `
    <header class="main-header">
      <div class="logo">
        <a href="/"><img src="img/logo.jpg" alt="Logo" height="50"></a>
      </div>
      <nav class="main-nav">
        <ul>
          <li><a href="/">Trang chủ</a></li>
          <li><a href="/giohang.html">Giỏ hàng</a></li>
          <li id="authArea">${authHtml}</li>
        </ul>
      </nav>
    </header>
  `;

  document.write(headerHTML);

  // Bổ sung sự kiện click để chuyển trang thông tin khách hàng
  setTimeout(() => {
    const btn = document.getElementById("userInfoBtn");
    if (btn) {
      btn.onclick = function() {
        window.location.href = "/thongtinkhachhang.html";
      };
    }
  }, 0);
}

function dangXuat() {
  fetch("http://localhost:8082/api/khach-hang/dang-xuat", {
    method: "POST",
    credentials: "include"
  })
  .then(response => {
    if (response.ok) {
      localStorage.removeItem("user");
      window.location.href = "/index.html";
    } else {
      throw new Error("Không thể đăng xuất");
    }
  })
  .catch(error => {
    console.error("Lỗi đăng xuất:", error);
  });
}
