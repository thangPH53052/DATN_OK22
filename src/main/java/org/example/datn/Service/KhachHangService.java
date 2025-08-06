// src/main/java/org/example/datn/Service/KhachHangService.java
package org.example.datn.Service;

import org.example.datn.Entity.KhachHang;
import org.example.datn.Repository.KhachHangRepository;
import org.example.datn.Response.AuthRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class KhachHangService {

    @Autowired
    private KhachHangRepository khachHangRepository;

    public List<KhachHang> getAllKhachHang() {
        return khachHangRepository.findAll();
    }

    public Optional<KhachHang> getById(Integer id) {
        return khachHangRepository.findById(id);
    }

    public void save(KhachHang khachHang) {
        khachHangRepository.save(khachHang);
    }

    public void doiTrangThai(Integer id) {
        khachHangRepository.findById(id).ifPresent(kh -> {
            kh.setTrangThai(!kh.getTrangThai());
            khachHangRepository.save(kh);
        });
    }

    public ResponseEntity<?> dangKy(KhachHang kh) {
        if (khachHangRepository.existsByEmail(kh.getEmail())) {
            return ResponseEntity.badRequest().body("Email đã tồn tại.");
        }

        kh.setTrangThai(true);
        kh.setMa("KH" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());

        KhachHang saved = khachHangRepository.save(kh);
        return ResponseEntity.ok(saved);
    }

    public ResponseEntity<?> dangNhap(AuthRequest request) {
        Optional<KhachHang> opt = khachHangRepository.findByEmail(request.getEmail());

        if (opt.isEmpty()) {
            return ResponseEntity.status(401).body("Tài khoản không tồn tại.");
        }

        KhachHang kh = opt.get();
        if (!request.getMatKhau().equals(kh.getMatKhau())) {
            return ResponseEntity.status(401).body("Mật khẩu không đúng.");
        }

        return ResponseEntity.ok(Map.of(
            "ten", kh.getTen(),
            "email", kh.getEmail()
        ));
    }
    public Optional<KhachHang> getByEmail(String email) {
        return khachHangRepository.findByEmail(email);
    }
    
    public ResponseEntity<?> capNhatThongTin(KhachHang khachHangMoi) {
        return khachHangRepository.findByEmail(khachHangMoi.getEmail())
                .map(khachHang -> {
                    // Chỉ cập nhật các thông tin cần thiết, không cập nhật mật khẩu
                    khachHang.setTen(khachHangMoi.getTen());
                    khachHang.setSdt(khachHangMoi.getSdt());
                    khachHang.setDiaChi(khachHangMoi.getDiaChi());
                    khachHang.setGioiTinh(khachHangMoi.getGioiTinh());
                    khachHang.setNgaySinh(khachHangMoi.getNgaySinh());
                    
                    KhachHang updated = khachHangRepository.save(khachHang);
                    return ResponseEntity.ok(updated);
                })
                .orElse(ResponseEntity.notFound().build());
    }
    
    public ResponseEntity<?> doiMatKhau(String email, String matKhauCu, String matKhauMoi) {
        return khachHangRepository.findByEmail(email)
                .map(khachHang -> {
                    // Kiểm tra mật khẩu cũ
                    if (!matKhauCu.equals(khachHang.getMatKhau())) {
                        return ResponseEntity.badRequest().body("Mật khẩu cũ không đúng");
                    }
                    
                    // Cập nhật mật khẩu mới
                    khachHang.setMatKhau(matKhauMoi);
                    khachHangRepository.save(khachHang);
                    
                    return ResponseEntity.ok(Map.of("message", "Đổi mật khẩu thành công"));
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
