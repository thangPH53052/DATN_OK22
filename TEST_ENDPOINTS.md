# Hướng dẫn Test các Endpoint mới

## 🎯 Các URL cần test:

### 1. Dashboard Tổng quan
```
GET http://localhost:8082/hoa-don/dashboard
```
- Hiển thị thống kê tổng quan
- Biểu đồ doanh thu
- Hóa đơn gần đây

### 2. Hóa đơn tại quầy
```
GET http://localhost:8082/hoa-don-tai-quay/view
```
- Danh sách hóa đơn tại quầy
- Thống kê theo trạng thái

```
GET http://localhost:8082/hoa-don-tai-quay/chi-tiet/{id}
```
- Chi tiết hóa đơn tại quầy
- Cập nhật trạng thái

### 3. Hóa đơn Online
```
GET http://localhost:8082/hoa-don-online/view
```
- Danh sách hóa đơn online
- Thống kê theo trạng thái

```
GET http://localhost:8082/hoa-don-online/chi-tiet/{id}
```
- Chi tiết hóa đơn online
- Cập nhật trạng thái

## 🔧 Các Controller đã tạo:

1. **HoaDonDashboardController** - Dashboard tổng quan
2. **HoaDonTaiQuayController** - Quản lý hóa đơn tại quầy
3. **HoaDonOnlineController** - Quản lý hóa đơn online (đã cập nhật)
4. **HoaDonTaiQuayService** - Service cho hóa đơn tại quầy

## 📁 Các file HTML đã tạo:

1. `templates/HoaDon/dashboard.html` - Dashboard tổng quan
2. `templates/HoaDonTaiQuay/index.html` - Danh sách hóa đơn tại quầy
3. `templates/HoaDonTaiQuay/chi-tiet.html` - Chi tiết hóa đơn tại quầy
4. `templates/HoaDonOnline/index.html` - Danh sách hóa đơn online
5. `templates/HoaDonOnline/chi-tiet.html` - Chi tiết hóa đơn online

## 🚀 Cách test:

1. **Khởi động ứng dụng:**
   ```bash
   mvn spring-boot:run
   ```

2. **Truy cập Dashboard:**
   ```
   http://localhost:8082/hoa-don/dashboard
   ```

3. **Test từng trang:**
   - Dashboard: `http://localhost:8082/hoa-don/dashboard`
   - Hóa đơn tại quầy: `http://localhost:8082/hoa-don-tai-quay/view`
   - Hóa đơn online: `http://localhost:8082/hoa-don-online/view`

4. **Test chức năng cập nhật trạng thái:**
   - Vào chi tiết hóa đơn
   - Thay đổi trạng thái
   - Nhấn "Xác nhận thay đổi"

## ⚠️ Lưu ý:

1. **Dữ liệu test:** Cần có dữ liệu trong database để hiển thị
2. **Trạng thái enum:** 
   - HoaDonTaiQuay: TAM_THOI, DA_THANH_TOAN, DA_HUY
   - HoaDonOnline: CHO_XU_LY, DA_THANH_TOAN, DANG_GIAO_HANG
3. **Navigation:** Các nút chuyển đổi giữa các trang đã được thiết lập

## 🔍 Debug nếu có lỗi:

1. **Kiểm tra Console:** Xem log lỗi
2. **Kiểm tra Database:** Đảm bảo có dữ liệu
3. **Kiểm tra URL:** Đảm bảo đúng endpoint
4. **Kiểm tra Template:** Đảm bảo file HTML tồn tại

## 📞 Hỗ trợ:

Nếu gặp vấn đề, kiểm tra:
- Log trong console
- Database connection
- Template path
- Controller mapping 