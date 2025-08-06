package org.example.datn.Response;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class DonHangRequest {
    private String email;
    private String hoTen;
    private String sdt;
    private String diaChi;
    private String ghiChu;
    private Integer voucherId;

    private List<ItemRequest> items;

    @Getter
    @Setter
    public static class ItemRequest {
        private Integer idSanPhamChiTiet;
        private Integer soLuong;
        private Double giaSauKhuyenMai; // ✅ FE gửi giá (dùng cho log kiểm tra)
    }
}
