package org.example.datn.Repository;

import java.util.Optional;

import org.example.datn.Entity.KhachHang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KhachHangRepository extends JpaRepository<KhachHang, Integer> {

    // ✅ Tìm theo tên
    Optional<KhachHang> findByTen(String ten);

    // ✅ Tìm theo email (cho đăng nhập)
    Optional<KhachHang> findByEmail(String email);

    // ✅ Kiểm tra email đã tồn tại chưa (cho đăng ký)
    boolean existsByEmail(String email);
}
