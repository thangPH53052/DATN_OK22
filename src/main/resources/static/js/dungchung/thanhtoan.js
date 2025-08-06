let giamPhanTram = 0;
let selectedVoucherId = null;
let tongTienGoc = 0;
let isProcessingOrder = false;
document.addEventListener("DOMContentLoaded", function () {
  // Kiểm tra đăng nhập
  const user = JSON.parse(localStorage.getItem("user"));
  if (!user) {
    alert("⚠ Bạn cần đăng nhập!");
    window.location.href = "/dangnhap.html";
    return;
  }

  // Lấy và validate giỏ hàng
  const gioHang = validateGioHang();
  if (!gioHang) return;

  // Load thông tin
  loadCustomerInfo(user);
  loadVouchers();
  renderSanPham(gioHang);

  // Thêm sự kiện cho nút đặt hàng (đảm bảo chỉ đăng ký 1 lần)
  const checkoutForm = document.getElementById("checkoutForm");
  checkoutForm.removeEventListener("submit", datHang); // Xóa trước nếu tồn tại
  checkoutForm.addEventListener("submit", datHang);
});

// Hàm validate giỏ hàng
function validateGioHang() {
  const gioHang = JSON.parse(localStorage.getItem("gioHangDat")) || [];

  if (!Array.isArray(gioHang)) {
    alert("❌ Dữ liệu giỏ hàng không hợp lệ!");
    localStorage.removeItem("gioHangDat");
    window.location.href = "/giohang.html";
    return null;
  }

  if (gioHang.length === 0) {
    alert("❗ Giỏ hàng trống!");
    window.location.href = "/giohang.html";
    return null;
  }

  const hasInvalid = gioHang.some(
    (sp) =>
      !sp.idSanPhamChiTiet ||
      !sp.tenSanPham ||
      !sp.mauSac ||
      !sp.kichThuoc ||
      !sp.soLuong ||
      !sp.giaSauKhuyenMai
  );

  if (hasInvalid) {
    alert("❌ Giỏ hàng có sản phẩm thiếu thông tin!");
    console.error("Giỏ hàng lỗi:", gioHang);
    localStorage.removeItem("gioHangDat");
    window.location.href = "/giohang.html";
    return null;
  }

  return gioHang;
}

// Hàm load thông tin khách hàng
function loadCustomerInfo(user) {
  fetch(`/api/khach-hang/email/${user.email}`)
    .then((res) => (res.ok ? res.json() : Promise.reject()))
    .then((kh) => {
      document.getElementById("hoTen").value = kh.ten || "";
      document.getElementById("sdt").value = kh.sdt || "";
      document.getElementById("diaChi").value = kh.diaChi || "";
      localStorage.setItem("user", JSON.stringify({ ...user, ...kh }));
    })
    .catch(() => {
      document.getElementById("hoTen").value = user.ten || "";
      document.getElementById("sdt").value = user.sdt || "";
      document.getElementById("diaChi").value = user.diaChi || "";
    });
}

// Hàm load voucher
function loadVouchers() {
  fetch("/voucher/api/dang-hoat-dong")
    .then((res) => (res.ok ? res.json() : Promise.reject()))
    .then((vouchers) => {
      const select = document.getElementById("voucherSelect");
      select.innerHTML = '<option value="">-- Không sử dụng --</option>';

      vouchers.forEach((v) => {
        const option = new Option(
          `${v.ma} - Giảm ${v.phanTramGiam}% (Tối đa ${
            v.giaTriGiamToiDa?.toLocaleString("vi-VN") || "không giới hạn"
          }₫)`,
          v.id
        );
        option.dataset.phanTramGiam = v.phanTramGiam;
        option.dataset.giaTriGiamToiDa = v.giaTriGiamToiDa || 0;
        select.add(option);
      });
    })
    .catch((err) => console.error("Lỗi tải voucher:", err));
}

// Hàm hiển thị sản phẩm (chỉ hiển thị, không cho chỉnh sửa)
function renderSanPham(cart) {
  const list = document.getElementById("productList");
  list.innerHTML = "";
  tongTienGoc = 0;

  cart.forEach((item) => {
    const gia = parseFloat(item.giaSauKhuyenMai) || 0;
    const soLuong = parseInt(item.soLuong) || 1;
    const thanhTien = gia * soLuong;
    tongTienGoc += thanhTien;

    list.innerHTML += `
      <div class="product-item d-flex justify-content-between align-items-center border-bottom py-2">
        <div class="product-info">
          <div><strong>${item.tenSanPham}</strong></div>
          <div class="text-muted">${item.mauSac} / ${item.kichThuoc}</div>
        </div>
        <div class="product-quantity text-end">
          <div>x${soLuong}</div>
        </div>
        <div class="product-price text-end">
          <div>${thanhTien.toLocaleString("vi-VN")}₫</div>
        </div>
      </div>
    `;
  });

  updatePriceSummary();
}

// Hàm cập nhật tổng tiền
function updatePriceSummary() {
  const giamTien = tinhTienGiam(tongTienGoc, giamPhanTram);
  const tongSauGiam = tongTienGoc - giamTien;

  document.getElementById("subtotal").textContent =
    tongTienGoc.toLocaleString("vi-VN") + "₫";
  document.getElementById("discount").textContent =
    "-" + giamTien.toLocaleString("vi-VN") + "₫";
  document.getElementById("total").textContent =
    tongSauGiam.toLocaleString("vi-VN") + "₫";
}

// Hàm tính tiền giảm giá
function tinhTienGiam(tongTien, phanTramGiam) {
  if (!phanTramGiam) return 0;

  const select = document.getElementById("voucherSelect");
  const selectedOption = select.options[select.selectedIndex];
  if (!selectedOption) return 0;

  const giamToiDa = parseFloat(selectedOption.dataset.giaTriGiamToiDa) || 0;
  const giamTien = tongTien * (phanTramGiam / 100);

  return giamToiDa > 0 ? Math.min(giamTien, giamToiDa) : giamTien;
}

// Hàm chọn voucher
// Hàm chọn voucher
function chonVoucher() {
  const select = document.getElementById("voucherSelect");
  const voucherId = select.value;
  
  if (!voucherId) {
    giamPhanTram = 0;
    selectedVoucherId = null;
    document.getElementById("voucherMessage").textContent = "";
    updatePriceSummary();
    return;
  }

  // Gọi API kiểm tra (không cần email)
  fetch(`/voucher/api/kiem-tra/${voucherId}`)
    .then(res => res.json())
    .then(data => {
      if (data.valid) {
        giamPhanTram = data.phanTramGiam;
        selectedVoucherId = voucherId;
        document.getElementById("voucherMessage").textContent = 
          `✔ Giảm ${data.phanTramGiam}%`;
        updatePriceSummary();
      } else {
        select.value = "";
        alert(data.message);
      }
    })
    .catch(err => {
      console.error("Lỗi kiểm tra voucher:", err);
      select.value = "";
    });
}

// Hàm đặt hàng
// Hàm đặt hàng (phiên bản cải tiến)
async function datHang(event) {
  event.preventDefault();

  // Kiểm tra nếu đang xử lý thì bỏ qua
  if (isProcessingOrder) return;
  isProcessingOrder = true;

  const btnSubmit = event.target.querySelector('button[type="submit"]');
  const originalText = btnSubmit.textContent;
  btnSubmit.disabled = true;
  btnSubmit.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Đang xử lý...';

  try {
    const user = JSON.parse(localStorage.getItem("user"));
    const cart = JSON.parse(localStorage.getItem("gioHangDat")) || [];

    // Validate thông tin
    const hoTen = document.getElementById("hoTen").value.trim();
    const diaChi = document.getElementById("diaChi").value.trim();
    const sdt = document.getElementById("sdt").value.trim();

    if (!hoTen || !diaChi || !sdt) {
      throw new Error("Vui lòng điền đầy đủ thông tin khách hàng");
    }

    // Kiểm tra voucher nếu có
    if (selectedVoucherId) {
      const voucherValid = await checkVoucherValid(selectedVoucherId, user.email);
      if (!voucherValid) {
        throw new Error("Voucher không hợp lệ hoặc đã sử dụng");
      }
    }

    // Tạo đơn hàng
    const hoaDonData = {
      email: user.email,
      hoTen,
      diaChi,
      sdt,
      ghiChu: document.getElementById("ghiChu").value.trim(),
      paymentMethod: document.querySelector('input[name="paymentMethod"]:checked').value,
      voucherId: selectedVoucherId,
      items: cart.map((item) => ({
        idSanPhamChiTiet: item.idSanPhamChiTiet,
        tenSanPham: item.tenSanPham,
        mauSac: item.mauSac,
        kichThuoc: item.kichThuoc,
        soLuong: parseInt(item.soLuong) || 1,
        giaSauKhuyenMai: parseFloat(item.giaSauKhuyenMai) || 0,
        hinhAnh: item.hinhAnh,
      })),
      tongTien: tongTienGoc - tinhTienGiam(tongTienGoc, giamPhanTram),
      tongTienGoc,
      giamGia: tinhTienGiam(tongTienGoc, giamPhanTram),
    };

    // Gửi đơn hàng online
    const res = await fetch("/hoa-don/api/tao", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${user.token}`,
      },
      body: JSON.stringify(hoaDonData),
    });

    if (!res.ok) {
      const error = await res.json();
      throw new Error(error.message || "Đặt hàng thất bại");
    }

    const data = await res.json();

    // Clear giỏ hàng (cả localStorage và DB)
    await clearGioHang(user.email);

    // Sửa phần hiển thị thông báo - chỉ hiển thị 1 lần
    if (!window.orderSuccessShown) {
      window.orderSuccessShown = true;
      alert(`🎉 Đặt hàng thành công! Mã đơn: ${data.maHoaDon}`);
      window.location.href = "/index.html";
    }

  } catch (err) {
    console.error("Lỗi đặt hàng:", err);
    alert(`❌ Lỗi: ${err.message}`);
  } finally {
    isProcessingOrder = false;
    btnSubmit.disabled = false;
    btnSubmit.textContent = originalText;
  }
}

// Hàm kiểm tra voucher
async function checkVoucherValid(voucherId, email) {
  try {
    const res = await fetch(`/voucher/api/kiem-tra/${voucherId}?email=${email}`);
    if (!res.ok) {
      const error = await res.json().catch(() => ({}));
      console.error("Chi tiết lỗi voucher:", error);
      throw new Error(error.message || "Voucher không hợp lệ");
    }
    const data = await res.json();
    return data.valid;
  } catch (err) {
    console.error("Lỗi kiểm tra voucher:", err);
    throw err; // Re-throw để hiển thị thông báo chính xác
  }
}

// Hàm clear giỏ hàng (không xóa DB)
async function clearGioHang(email) {
  try {
    // Xóa localStorage
    localStorage.removeItem("gioHangDat");

    await fetch(`/api/gio-hang/clear/${email}`, {
      method: "PUT",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${
          JSON.parse(localStorage.getItem("user")).token
        }`,
      },
    });
  } catch (err) {
    console.error("Lỗi khi clear giỏ hàng:", err);
  }
}
 