package org.example.datn.dto;

import lombok.*;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DanhGiaDTO {
    private Integer id;
    private Integer soSao;
    private String binhLuan;
    private Date ngayDanhGia;
    private Integer idKhachHang;
    private Integer idSanPham;
}
