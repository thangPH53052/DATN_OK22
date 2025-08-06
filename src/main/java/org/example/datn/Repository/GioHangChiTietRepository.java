package org.example.datn.Repository;

import java.util.List;
import java.util.Optional;

import org.example.datn.Entity.GioHangChiTiet;
import org.example.datn.Entity.KhachHang;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GioHangChiTietRepository extends JpaRepository<GioHangChiTiet, Integer> {
    List<GioHangChiTiet> findByKhachHangIdAndTrangThaiTrue(Integer khachHangId);

    List<GioHangChiTiet> findAllByKhachHang(KhachHang khachHang);

    List<GioHangChiTiet> findByKhachHangEmail(String email);

    Optional<GioHangChiTiet> findByKhachHangEmailAndSanPhamChiTietId(String email, Integer sanPhamChiTietId);
}
