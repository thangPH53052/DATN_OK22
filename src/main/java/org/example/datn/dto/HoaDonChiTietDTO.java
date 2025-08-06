package org.example.datn.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HoaDonChiTietDTO {
    private Integer id;
    private Integer soLuong;
    private Double donGia;
    private Integer idHoaDon;
    private Integer idSanPhamChiTiet;
}
