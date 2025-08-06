package org.example.datn.dto;

import lombok.*;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HoaDonDTO {
    private Integer id;
    private Date ngayTao;
    private Integer trangThai;
    private Double tongTien;
    private Integer idKhachHang;
    private Integer idNhanVien;
    private Integer idVoucher;
}
