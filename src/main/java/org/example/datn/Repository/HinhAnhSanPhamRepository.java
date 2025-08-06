package org.example.datn.Repository;

import org.example.datn.Entity.HinhAnhSanPham;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HinhAnhSanPhamRepository extends JpaRepository<HinhAnhSanPham, Integer> {

    // Lấy danh sách hình ảnh theo ID sản phẩm
    List<HinhAnhSanPham> findBySanPham_Id(Integer idSanPham);
}
