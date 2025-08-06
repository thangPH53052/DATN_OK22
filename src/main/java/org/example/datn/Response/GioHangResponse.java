package org.example.datn.Response;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GioHangResponse {
    private Integer id; // Giữ lại: ID của chi tiết giỏ hàng
    private Integer idSanPhamChiTiet; // ✅ Thêm dòng này

    private String tenSanPham;
    private String hinhAnh;
    private String kichThuoc;
    private String mauSac;
    private Integer soLuong;
    private Double giaGoc;
    private Double giaSauKhuyenMai;
    private Double tongTien;
}
