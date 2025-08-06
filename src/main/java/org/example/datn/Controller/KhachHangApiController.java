package org.example.datn.Controller;

import jakarta.servlet.http.HttpSession;

import java.util.Map;

import org.example.datn.Entity.KhachHang;
import org.example.datn.Response.AuthRequest;
import org.example.datn.Service.KhachHangService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/khach-hang")
public class KhachHangApiController {

    @Autowired
    private KhachHangService service;

    @PostMapping("/dang-ky")
    public ResponseEntity<?> dangKy(@RequestBody KhachHang kh) {
        return service.dangKy(kh);
    }

    @PostMapping("/dang-nhap")
    public ResponseEntity<?> dangNhap(@RequestBody AuthRequest request) {
        return service.dangNhap(request);
    }

    @PostMapping("/dang-xuat")
    public ResponseEntity<?> dangXuat(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok("Đăng xuất thành công");
    }

    // ✅ API: Lấy thông tin khách hàng qua email (dùng khi load thanh toán)
    @GetMapping("/email/{email}")
    public ResponseEntity<?> getByEmail(@PathVariable String email) {
        return service.getByEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    // ✅ API: Cập nhật thông tin khách hàng
    @PutMapping("/cap-nhat")
    public ResponseEntity<?> capNhatThongTin(@RequestBody KhachHang khachHang) {
        return service.capNhatThongTin(khachHang);
    }
    
    // ✅ API: Đổi mật khẩu
    @PutMapping("/doi-mat-khau")
    public ResponseEntity<?> doiMatKhau(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String matKhauCu = request.get("matKhauCu");
        String matKhauMoi = request.get("matKhauMoi");
        
        return service.doiMatKhau(email, matKhauCu, matKhauMoi);
    }
}
