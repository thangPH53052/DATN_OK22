package org.example.datn.Repository;

import org.example.datn.Entity.KichThuoc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KichThuocRepository extends JpaRepository<KichThuoc, Integer> {
    KichThuoc findByMa(String ma);
}
