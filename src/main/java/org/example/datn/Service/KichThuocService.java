package org.example.datn.Service;

import org.example.datn.Entity.KichThuoc;
import org.example.datn.Repository.KichThuocRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class KichThuocService {

    @Autowired
    private KichThuocRepository kichThuocRepository;

    public List<KichThuoc> getAllKichThuoc() {
        return kichThuocRepository.findAll();
    }

    public KichThuoc getById(Integer id) {
        return kichThuocRepository.findById(id).orElse(null);
    }

    public KichThuoc getByMa(String ma) {
        return kichThuocRepository.findByMa(ma);
    }

    public KichThuoc saveOrUpdate(KichThuoc kichThuoc) {
        return kichThuocRepository.save(kichThuoc);
    }

    public void deleteById(Integer id) {
        kichThuocRepository.deleteById(id);
    }
}
