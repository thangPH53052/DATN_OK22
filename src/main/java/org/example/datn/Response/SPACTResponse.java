package org.example.datn.Response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SPACTResponse {
    private Integer id;
    private String tenSanPham;
    private Double giaBan;
    private Double giaSauKhuyenMai;
    private String moTa;
    private String kichThuoc;
    private Float canNang;
    private Float dungTich;
    private String thuongHieu;
    private String chatLieu;
    private String loaiKhoa;
    private String kieuDay;
    private String danhMuc;
    private String mauSac;
    private String kichThuocPhanLoai;
    private Integer soLuong;
    private List<String> hinhAnhUrls;

    // ➕ THÊM:
    private List<MauSacOption> dsMauSac;
    private List<KichThuocOption> dsKichThuoc;

    @Data
    @AllArgsConstructor
    public static class MauSacOption {
        private String ten;
        private String maMau; 
        private boolean hetHang;
    }

    @Data
    @AllArgsConstructor
    public static class KichThuocOption {
        private String ten;
        private boolean hetHang;
    }
}
