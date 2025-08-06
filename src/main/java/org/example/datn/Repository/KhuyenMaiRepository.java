package org.example.datn.Repository;

import org.example.datn.Entity.KhuyenMai;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KhuyenMaiRepository extends JpaRepository<KhuyenMai, Integer> {
    KhuyenMai findByMa(String ma);
}
