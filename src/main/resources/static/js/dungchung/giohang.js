document.addEventListener("DOMContentLoaded", function () {
  const user = JSON.parse(localStorage.getItem("user"));
  if (!user) {
    alert("⚠ Bạn cần đăng nhập để xem giỏ hàng!");
    window.location.href = "/dangnhap.html";
    return;
  }

  loadGioHang(user.email);
  // Đã bỏ dòng loadLichSuMuaHang(user.email)
});

// Hàm tải giỏ hàng
function loadGioHang(email) {
  fetch(`/api/gio-hang/${email}`, {
    headers: {
      Authorization: `Bearer ${
        JSON.parse(localStorage.getItem("user"))?.token
      }`,
    },
  })
    .then((res) => {
      if (!res.ok) throw new Error("Lỗi tải giỏ hàng");
      return res.json();
    })
    .then((data) => {
      // Chuyển đổi cấu trúc dữ liệu từ backend
      const cart = {
        items: data.map((item) => ({
          id: item.id, // ID của item trong giỏ hàng
          idSanPhamChiTiet: item.idSanPhamChiTiet, // ID sản phẩm chi tiết
          tenSanPham: item.tenSanPham,
          hinhAnh: item.hinhAnh,
          mauSac: item.mauSac,
          kichThuoc: item.kichThuoc,
          soLuong: item.soLuong,
          giaBan: item.giaGoc,
          giaSauKhuyenMai: item.giaSauKhuyenMai,
        })),
      };

      localStorage.setItem("gioHangDat", JSON.stringify(cart.items));
      renderGioHang(cart);
    })
    .catch((err) => {
      console.error("Lỗi khi tải giỏ hàng:", err);
      renderGioHang({ items: [] });
    });
}
// Hàm hiển thị giỏ hàng
function renderGioHang(cart) {
  const cartBody = document.getElementById("cartBody");
  cartBody.innerHTML = "";

  if (!cart?.items || cart.items.length === 0) {
    cartBody.innerHTML = `
      <tr>
        <td colspan="8" class="text-center">🛒 Giỏ hàng trống</td>
      </tr>
    `;
    return;
  }

  let tongTien = 0;

  cart.items.forEach((item) => {
    // Xử lý đường dẫn ảnh mặc định nếu không có
    const hinhAnh = item.hinhAnh
      ? item.hinhAnh.startsWith("http")
        ? item.hinhAnh
        : `/images/${item.hinhAnh}`
      : "/images/no-image.jpg";

    const thanhTien = item.soLuong * (item.giaSauKhuyenMai || item.giaBan || 0);
    tongTien += thanhTien;

    cartBody.innerHTML += `
      <tr data-id="${item.id}">
        <td><img src="${hinhAnh}" width="60" height="60" style="object-fit: cover" alt="${
      item.tenSanPham
    }"></td>
        <td>${item.tenSanPham}</td>
        <td>${item.mauSac}</td>
        <td>${item.kichThuoc}</td>
        <td>
          <button onclick="capNhatSoLuong(${item.id}, ${item.soLuong - 1})" ${
      item.soLuong <= 1 ? "disabled" : ""
    }>-</button>
          <span style="margin: 0 8px">${item.soLuong}</span>
          <button onclick="capNhatSoLuong(${item.id}, ${
      item.soLuong + 1
    })">+</button>
        </td>
        <td>${(item.giaSauKhuyenMai || item.giaBan || 0).toLocaleString(
          "vi-VN"
        )}₫</td>
        <td>${thanhTien.toLocaleString("vi-VN")}₫</td>
        <td><button onclick="xoaSanPham(${
          item.id
        })" class="btn-delete">🗑</button></td>
      </tr>
    `;
  });

  // Thêm tổng cộng và nút thanh toán
  cartBody.innerHTML += `
    <tr>
      <td colspan="6" class="text-end"><strong>Tổng cộng:</strong></td>
      <td colspan="2"><strong>${tongTien.toLocaleString("vi-VN")}₫</strong></td>
    </tr>
    <tr>
      <td colspan="8" class="text-end">
        <button onclick="chuyenTrangThanhToan()" class="btn btn-primary">
          ✅ Tiến hành thanh toán
        </button>
      </td>
    </tr>
  `;
}
// Cập nhật số lượng
function capNhatSoLuong(idGioHang, soLuongMoi) {
  const user = JSON.parse(localStorage.getItem("user"));
  if (!user || soLuongMoi < 1) return;

  fetch("/api/gio-hang/cap-nhat", {
    method: "PUT",
    headers: {
      "Content-Type": "application/json",
      Authorization: `Bearer ${user.token}`,
    },
    body: JSON.stringify({
      id: idGioHang, // Sử dụng ID giỏ hàng thay vì ID sản phẩm
      soLuong: soLuongMoi,
    }),
  })
    .then((res) => {
      if (!res.ok) throw new Error("Cập nhật thất bại");
      loadGioHang(user.email);
    })
    .catch((err) => console.error("Lỗi cập nhật:", err));
}

// Xóa sản phẩm
function xoaSanPham(idGioHang) {
  if (!confirm("Bạn có chắc muốn xóa sản phẩm này?")) return;

  const user = JSON.parse(localStorage.getItem("user"));
  if (!user) return;

  fetch(`/api/gio-hang/${idGioHang}`, {
    method: "DELETE",
    headers: {
      Authorization: `Bearer ${user.token}`,
    },
  })
    .then((res) => {
      if (!res.ok) throw new Error("Xóa thất bại");
      loadGioHang(user.email);
    })
    .catch((err) => console.error("Lỗi xóa sản phẩm:", err));
}

// Hàm xóa sản phẩm
function xoaSanPham(idSanPham) {
  if (!confirm("Bạn có chắc muốn xóa sản phẩm này?")) return;

  const user = JSON.parse(localStorage.getItem("user"));
  if (!user) return;

  fetch(`/api/gio-hang/xoa/${idSanPham}`, {
    method: "DELETE",
    headers: {
      Authorization: `Bearer ${user.token}`,
    },
  })
    .then((res) => {
      if (!res.ok) throw new Error("Xóa thất bại");
      return res.json();
    })
    .then(() => loadGioHang(user.email))
    .catch((err) => console.error("Lỗi xóa sản phẩm:", err));
}

// Hàm đặt hàng
async function datHang() {
  const user = JSON.parse(localStorage.getItem("user"));
  if (!user) return;

  const btn = document.querySelector(".btn-primary");
  const originalText = btn.textContent;
  btn.disabled = true;
  btn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Đang xử lý...';

  try {
    const cartItems = JSON.parse(localStorage.getItem("gioHangDat")) || [];
    if (cartItems.length === 0) throw new Error("Giỏ hàng trống");

    const res = await fetch("/hoa-don/api/tao", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${user.token}`,
      },
      body: JSON.stringify({
        email: user.email,
        items: cartItems,
      }),
    });

    if (!res.ok) {
      const error = await res.json().catch(() => ({}));
      throw new Error(error.message || "Đặt hàng thất bại");
    }

    const data = await res.json();

    // Clear giỏ hàng
    await clearGioHang(user.email);

    alert(`🎉 Đặt hàng thành công! Mã đơn: ${data.maHoaDon}`);
    window.location.href = `/don-hang.html?id=${data.id}`;
  } catch (err) {
    console.error("Lỗi đặt hàng:", err);
    alert(`❌ Lỗi: ${err.message || "Có lỗi xảy ra khi đặt hàng"}`);
  } finally {
    btn.disabled = false;
    btn.innerHTML = originalText;
  }
}

// Hàm clear giỏ hàng
async function clearGioHang(email) {
  try {
    localStorage.removeItem("gioHangDat");
    await fetch(`/api/gio-hang/clear/${email}`, {
      method: "PUT",
      headers: {
        Authorization: `Bearer ${
          JSON.parse(localStorage.getItem("user")).token
        }`,
      },
    });
  } catch (err) {
    console.error("Lỗi khi clear giỏ hàng:", err);
  }
}
function chuyenTrangThanhToan() {
  const user = JSON.parse(localStorage.getItem("user"));
  if (!user) {
    alert("Vui lòng đăng nhập để thanh toán");
    window.location.href = "/dangnhap.html";
    return;
  }

  const gioHang = JSON.parse(localStorage.getItem("gioHangDat")) || [];
  if (gioHang.length === 0) {
    alert("Giỏ hàng trống, không thể thanh toán");
    return;
  }

  // Lưu thông tin giỏ hàng vào localStorage để trang thanh toán sử dụng
  localStorage.setItem("gioHangThanhToan", JSON.stringify(gioHang));

  // Chuyển hướng sang trang thanh toán
  window.location.href = "/thanhtoan.html";
}
