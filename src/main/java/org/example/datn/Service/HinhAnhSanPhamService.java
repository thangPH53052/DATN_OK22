package org.example.datn.Service;

import org.example.datn.Entity.HinhAnhSanPham;
import org.example.datn.Repository.HinhAnhSanPhamRepository;
import org.example.datn.dto.HinhAnhSanPhamDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class HinhAnhSanPhamService {

    private final HinhAnhSanPhamRepository hinhAnhSanPhamRepository;

    @Autowired
    public HinhAnhSanPhamService(HinhAnhSanPhamRepository hinhAnhSanPhamRepository) {
        this.hinhAnhSanPhamRepository = hinhAnhSanPhamRepository;
    }

    // Lấy toàn bộ hình ảnh
    public List<HinhAnhSanPhamDTO> getAll() {
        List<HinhAnhSanPham> entities = hinhAnhSanPhamRepository.findAll();
        return entities.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    // Lấy hình ảnh theo id sản phẩm
    public List<HinhAnhSanPhamDTO> getBySanPhamId(Integer sanPhamId) {
        List<HinhAnhSanPham> entities = hinhAnhSanPhamRepository.findBySanPham_Id(sanPhamId);
        return entities.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    // Lấy hình ảnh theo ID
    public HinhAnhSanPhamDTO getById(Integer id) {
        Optional<HinhAnhSanPham> optional = hinhAnhSanPhamRepository.findById(id);
        return optional.map(this::convertToDTO)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hình ảnh với id: " + id));
    }

    // Thêm hình ảnh (nếu sau này bạn muốn CRUD riêng)
    public HinhAnhSanPhamDTO save(HinhAnhSanPham entity) {
        HinhAnhSanPham saved = hinhAnhSanPhamRepository.save(entity);
        return convertToDTO(saved);
    }

    // Xóa hình ảnh
    public void delete(Integer id) {
        hinhAnhSanPhamRepository.deleteById(id);
    }

    // Convert Entity sang DTO
    private HinhAnhSanPhamDTO convertToDTO(HinhAnhSanPham entity) {
        HinhAnhSanPhamDTO dto = new HinhAnhSanPhamDTO();
        dto.setId(entity.getId());
        dto.setUrl(entity.getUrl());
        dto.setIdSanPham(entity.getSanPham() != null ? entity.getSanPham().getId() : null);
        return dto;
    }
}
