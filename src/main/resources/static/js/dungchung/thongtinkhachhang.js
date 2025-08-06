document.addEventListener("DOMContentLoaded", function () {
  const user = JSON.parse(localStorage.getItem("user"));
  if (!user) {
    window.location.href = "/dangnhap.html";
    return;
  }
  
  // Hiển thị thông tin khách hàng
  renderCustomerInfo(user);
  
  // Hiển thị lịch sử đơn hàng (từ API)
  fetchOrderHistory(user);
  
  // Hiển thị sản phẩm trong giỏ hàng
  renderCartProducts(user);
});

// Hiển thị thông tin khách hàng
function renderCustomerInfo(user) {
  document.getElementById("customerInfo").innerHTML = `
    <table class="customer-info-table">
      <tr>
        <td>Tài khoản:</td>
        <td>${user.email || ""}</td>
        <td></td>
      </tr>
      <tr>
        <td>Mật khẩu:</td>
        <td>••••••••</td>
        <td><button class="edit-button" onclick="showChangePasswordModal()"><i class="fa fa-pencil"></i></button></td>
      </tr>
      <tr>
        <td>Họ:</td>
        <td>${user.ho || ""}</td>
        <td></td>
      </tr>
      <tr>
        <td>Tên:</td>
        <td id="tenKhachHang">${user.ten || ""}</td>
        <td><button class="edit-button" onclick="showEditNameModal()"><i class="fa fa-pencil"></i></button></td>
      </tr>
      <tr>
        <td>Email:</td>
        <td>${user.email || ""}</td>
        <td></td>
      </tr>
      <tr id="tongTienDaMua">
        <td>Tổng tiền đã mua:</td>
        <td>Đang tải...</td>
        <td></td>
      </tr>
      <tr id="soLuongSanPhamDaMua">
        <td>Số lượng sản phẩm đã mua:</td>
        <td>Đang tải...</td>
        <td></td>
      </tr>
    </table>
    
    <!-- Modal đổi tên -->
    <div id="editNameModal" class="modal">
      <div class="modal-content">
        <span class="close" onclick="closeEditNameModal()">&times;</span>
        <h2>Đổi tên</h2>
        <div class="form-group">
          <label for="newName">Tên mới:</label>
          <input type="text" id="newName" value="${user.ten || ''}" class="form-control">
        </div>
        <div class="form-actions">
          <button onclick="updateName()" class="btn-primary">Lưu</button>
          <button onclick="closeEditNameModal()" class="btn-secondary">Hủy</button>
        </div>
        <div id="nameUpdateMessage" class="message"></div>
      </div>
    </div>
    
    <!-- Modal đổi mật khẩu -->
    <div id="changePasswordModal" class="modal">
      <div class="modal-content">
        <span class="close" onclick="closeChangePasswordModal()">&times;</span>
        <h2>Đổi mật khẩu</h2>
        <div class="form-group">
          <label for="currentPassword">Mật khẩu hiện tại:</label>
          <input type="password" id="currentPassword" class="form-control">
        </div>
        <div class="form-group">
          <label for="newPassword">Mật khẩu mới:</label>
          <input type="password" id="newPassword" class="form-control">
        </div>
        <div class="form-group">
          <label for="confirmPassword">Xác nhận mật khẩu mới:</label>
          <input type="password" id="confirmPassword" class="form-control">
        </div>
        <div class="form-actions">
          <button onclick="updatePassword()" class="btn-primary">Lưu</button>
          <button onclick="closeChangePasswordModal()" class="btn-secondary">Hủy</button>
        </div>
        <div id="passwordUpdateMessage" class="message"></div>
      </div>
    </div>
  `;
}

// Lấy và hiển thị lịch sử đơn hàng từ API
async function fetchOrderHistory(user) {
  try {
    const response = await fetch(`/api/gio-hang/lich-su-mua-hang/${user.email}`);
    if (!response.ok) {
      throw new Error("Không thể lấy lịch sử mua hàng");
    }
    
    const data = await response.json();
    renderOrderHistory(data);
    
    // Cập nhật tổng tiền và số lượng sản phẩm đã mua
    let tongTien = 0;
    let soLuongSanPham = 0;
    
    data.forEach(order => {
      tongTien += order.tongTien || 0;
      order.sanPhams.forEach(sp => {
        soLuongSanPham += sp.soLuong || 0;
      });
    });
    
    document.querySelector("#tongTienDaMua td:nth-child(2)").textContent = tongTien.toLocaleString() + "₫";
    document.querySelector("#soLuongSanPhamDaMua td:nth-child(2)").textContent = soLuongSanPham;
    
  } catch (error) {
    console.error("Lỗi khi lấy lịch sử mua hàng:", error);
    document.getElementById("orderHistory").innerHTML = 
      `<div class="error-message">Không thể tải lịch sử mua hàng.<br>${error.message}</div>`;
  }
}

// Hiển thị lịch sử đơn hàng từ dữ liệu API
function renderOrderHistory(orderHistory) {
  if (!orderHistory || orderHistory.length === 0) {
    document.getElementById("orderHistory").innerHTML = '<div class="empty-message">Chưa có đơn hàng nào.</div>';
    return;
  }

  let html = "";
  orderHistory.forEach(order => {
    // Xác định trạng thái đơn hàng
    let statusText = "Đang chờ xử lý";
    let statusClass = "pending";
    
    if (order.trangThai === 1) {
      statusText = "Đã hoàn thành";
      statusClass = "completed";
    } else if (order.trangThai === 2) {
      statusText = "Đã hủy";
      statusClass = "cancelled";
    }
    
    // Format ngày tháng
    const orderDate = new Date(order.ngayTao);
    const formattedDate = `${orderDate.getDate()}/${orderDate.getMonth() + 1}/${orderDate.getFullYear()}`;
    const formattedTime = `${orderDate.getHours()}:${orderDate.getMinutes()}:${orderDate.getSeconds()}`;
    
    html += `
      <div class="order-container">
        <div class="order-header">
          <span class="order-date">Đơn hàng ngày: ${formattedTime} ${formattedDate}</span>
          <span class="order-status ${statusClass}">${statusText}</span>
        </div>
        <table class="product-table">
          <thead>
            <tr>
              <th>STT</th>
              <th>Sản phẩm</th>
              <th class="text-center">Kích thước/Màu sắc</th>
              <th class="text-right">Giá</th>
              <th class="text-center">Số lượng</th>
              <th class="text-right">Thành tiền</th>
            </tr>
          </thead>
          <tbody>
    `;

    order.sanPhams.forEach((product, index) => {
      // Format ngày thêm vào giỏ nếu có
      let addedDateText = "";
      if (product.ngayThem) {
        const addedDate = new Date(product.ngayThem);
        addedDateText = `${addedDate.getDate()}/${addedDate.getMonth() + 1}/${addedDate.getFullYear()}`;
      }
      
      html += `
        <tr>
          <td>${index + 1}</td>
          <td class="product-name">${product.tenSanPham || ""}</td>
          <td class="text-center">${product.kichThuoc || "N/A"} / ${product.mauSac || "N/A"}</td>
          <td class="text-right price">${product.donGia ? product.donGia.toLocaleString() : 0} ₫</td>
          <td class="text-center">${product.soLuong || 1}</td>
          <td class="text-right total">${product.thanhTien ? product.thanhTien.toLocaleString() : 0} ₫</td>
        </tr>
      `;
    });

    html += `
          </tbody>
          <tfoot>`;
          
    // Hiển thị tổng tiền trước giảm nếu có voucher
    if (order.maVoucher) {
      html += `
            <tr class="summary-row">
              <td colspan="5" class="text-right"><strong>Tổng tiền gốc:</strong></td>
              <td class="text-right total">${order.tongTienTruocGiam ? order.tongTienTruocGiam.toLocaleString() : 0} ₫</td>
            </tr>
            <tr class="summary-row discount-row">
              <td colspan="5" class="text-right"><strong>Giảm giá (${order.phanTramGiam}%) - Voucher: ${order.maVoucher}</strong></td>
              <td class="text-right discount">-${order.tienGiam ? order.tienGiam.toLocaleString() : 0} ₫</td>
            </tr>`;
    }
    
    html += `
            <tr class="summary-row grand-total-row">
              <td colspan="5" class="text-right"><strong>TỔNG TIỀN:</strong></td>
              <td class="text-right grand-total">${order.tongTien ? order.tongTien.toLocaleString() : 0} ₫</td>
            </tr>
          </tfoot>
        </table>
      </div>
    `;
  });

  document.getElementById("orderHistory").innerHTML = html;
}

// Hiển thị sản phẩm trong giỏ hàng
function renderCartProducts(user) {
  // Khởi tạo biến để tính tổng tiền
  let tongTienGoc = 0;
  let tongTienSauGiam = 0;
  let giamGia = 0;
  
  // Lấy voucher đã lưu (nếu có)
  const voucher = JSON.parse(localStorage.getItem("selectedVoucher")) || null;
  
  // Lấy sản phẩm trong giỏ hàng
  fetch(`/api/gio-hang/${user.email}`)
    .then(res => {
      if (!res.ok) {
        throw new Error("Lỗi API: " + res.status + " " + res.statusText);
      }
      return res.json();
    })
    .then(data => {
      let html = "";
      if (data && data.length > 0) {
        html += `<table class="product-table">
          <thead>
            <tr>
              <th>Tên sản phẩm</th>
              <th class="text-center">Kích thước</th>
              <th class="text-center">Màu sắc</th>
              <th class="text-center">Số lượng</th>
              <th class="text-right">Giá bán</th>
              <th class="text-right">Tổng tiền</th>
            </tr>
          </thead>
          <tbody>`;
        data.forEach(item => {
          const giaBan = item.giaSauKhuyenMai ? item.giaSauKhuyenMai : (item.giaGoc || 0);
          const soLuong = item.soLuong || 1;
          const tongTien = giaBan * soLuong;
          
          // Cộng dồn vào tổng tiền gốc
          tongTienGoc += tongTien;
          
          html += `<tr>
            <td class="product-name">${item.tenSanPham || ""}</td>
            <td class="text-center">${item.kichThuoc || "N/A"}</td>
            <td class="text-center">${item.mauSac || "N/A"}</td>
            <td class="text-center">${soLuong}</td>
            <td class="text-right price">${giaBan.toLocaleString()} đ</td>
            <td class="text-right total">${tongTien.toLocaleString()} đ</td>
          </tr>`;
        });
        
        // Tính giảm giá nếu có voucher
        if (voucher) {
          giamGia = Math.round(tongTienGoc * (voucher.phanTramGiam / 100));
          if (voucher.giaTriGiamToiDa && giamGia > voucher.giaTriGiamToiDa) {
            giamGia = voucher.giaTriGiamToiDa;
          }
        }
        
        // Tính tổng tiền sau giảm
        tongTienSauGiam = tongTienGoc - giamGia;
        
        // Thêm dòng tổng tiền vào bảng
        html += `</tbody>
          <tfoot>
            <tr class="summary-row">
              <td colspan="4" class="text-right"><strong>Tổng tiền:</strong></td>
              <td colspan="2" class="text-right total">${tongTienGoc.toLocaleString()} đ</td>
            </tr>`;
            
        if (voucher) {
          html += `
            <tr class="summary-row discount-row">
              <td colspan="4" class="text-right"><strong>Giảm giá (${voucher.phanTramGiam}%):</strong></td>
              <td colspan="2" class="text-right discount">-${giamGia.toLocaleString()} đ</td>
            </tr>
            <tr class="summary-row grand-total-row">
              <td colspan="4" class="text-right"><strong>Thành tiền:</strong></td>
              <td colspan="2" class="text-right grand-total">${tongTienSauGiam.toLocaleString()} đ</td>
            </tr>`;
        }
        
        html += `</tfoot></table>`;
      } else {
        html = '<div class="empty-message">Chưa có sản phẩm nào trong giỏ hàng</div>';
      }
      document.getElementById("cartProducts").innerHTML = html;
    })
    .catch(err => {
      document.getElementById("cartProducts").innerHTML = 
        `<div class="error-message">Không thể tải dữ liệu sản phẩm.<br>${err.message}</div>`;
    });
}

// Hiển thị modal đổi tên
function showEditNameModal() {
  document.getElementById("editNameModal").style.display = "block";
}

// Đóng modal đổi tên
function closeEditNameModal() {
  document.getElementById("editNameModal").style.display = "none";
}

// Hiển thị modal đổi mật khẩu
function showChangePasswordModal() {
  document.getElementById("changePasswordModal").style.display = "block";
}

// Đóng modal đổi mật khẩu
function closeChangePasswordModal() {
  document.getElementById("changePasswordModal").style.display = "none";
}

// Cập nhật tên
async function updateName() {
  const user = JSON.parse(localStorage.getItem("user"));
  const newName = document.getElementById("newName").value;
  
  if (!newName || newName.trim() === "") {
    document.getElementById("nameUpdateMessage").textContent = "Tên không được để trống";
    document.getElementById("nameUpdateMessage").className = "message error";
    return;
  }
  
  try {
    // Lấy thông tin khách hàng hiện tại
    const response = await fetch(`/api/khach-hang/email/${user.email}`);
    if (!response.ok) {
      throw new Error("Không thể lấy thông tin khách hàng");
    }
    
    const khachHang = await response.json();
    
    // Cập nhật tên
    khachHang.ten = newName;
    
    // Gọi API cập nhật thông tin
    const updateResponse = await fetch("/api/khach-hang/cap-nhat", {
      method: "PUT",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(khachHang),
    });
    
    if (!updateResponse.ok) {
      throw new Error("Không thể cập nhật thông tin");
    }
    
    const updatedKhachHang = await updateResponse.json();
    
    // Cập nhật thông tin trong localStorage
    user.ten = updatedKhachHang.ten;
    localStorage.setItem("user", JSON.stringify(user));
    
    // Cập nhật hiển thị
    document.getElementById("tenKhachHang").textContent = updatedKhachHang.ten;
    
    // Hiển thị thông báo thành công
    document.getElementById("nameUpdateMessage").textContent = "Cập nhật tên thành công";
    document.getElementById("nameUpdateMessage").className = "message success";
    
    // Đóng modal sau 1.5 giây
    setTimeout(() => {
      closeEditNameModal();
    }, 1500);
    
  } catch (error) {
    console.error("Lỗi khi cập nhật tên:", error);
    document.getElementById("nameUpdateMessage").textContent = error.message;
    document.getElementById("nameUpdateMessage").className = "message error";
  }
}

// Cập nhật mật khẩu
async function updatePassword() {
  const user = JSON.parse(localStorage.getItem("user"));
  const currentPassword = document.getElementById("currentPassword").value;
  const newPassword = document.getElementById("newPassword").value;
  const confirmPassword = document.getElementById("confirmPassword").value;
  
  // Kiểm tra dữ liệu
  if (!currentPassword || !newPassword || !confirmPassword) {
    document.getElementById("passwordUpdateMessage").textContent = "Vui lòng điền đầy đủ thông tin";
    document.getElementById("passwordUpdateMessage").className = "message error";
    return;
  }
  
  if (newPassword !== confirmPassword) {
    document.getElementById("passwordUpdateMessage").textContent = "Mật khẩu mới không khớp";
    document.getElementById("passwordUpdateMessage").className = "message error";
    return;
  }
  
  try {
    // Gọi API đổi mật khẩu
    const response = await fetch("/api/khach-hang/doi-mat-khau", {
      method: "PUT",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        email: user.email,
        matKhauCu: currentPassword,
        matKhauMoi: newPassword
      }),
    });
    
    const result = await response.json();
    
    if (!response.ok) {
      throw new Error(result.message || "Không thể đổi mật khẩu");
    }
    
    // Hiển thị thông báo thành công
    document.getElementById("passwordUpdateMessage").textContent = "Đổi mật khẩu thành công";
    document.getElementById("passwordUpdateMessage").className = "message success";
    
    // Xóa dữ liệu đã nhập
    document.getElementById("currentPassword").value = "";
    document.getElementById("newPassword").value = "";
    document.getElementById("confirmPassword").value = "";
    
    // Đóng modal sau 1.5 giây
    setTimeout(() => {
      closeChangePasswordModal();
    }, 1500);
    
  } catch (error) {
    console.error("Lỗi khi đổi mật khẩu:", error);
    document.getElementById("passwordUpdateMessage").textContent = error.message;
    document.getElementById("passwordUpdateMessage").className = "message error";
  }
}