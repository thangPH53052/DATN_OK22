package org.example.datn.Response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LichSuMuaHangResponse {
    private Integer id;
    private Date ngayTao;
    private Integer trangThai;
    private Double tongTien;
    private String maVoucher;
    private Integer phanTramGiam;
    private Double tienGiam;
    private Double tongTienTruocGiam;
    private List<SanPhamResponse> sanPhams;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SanPhamResponse {
        private String tenSanPham;
        private String mauSac;
        private String kichThuoc;
        private Integer soLuong;
        private Double donGia;
        private Double thanhTien;
        private Date ngayThem;
    }
}