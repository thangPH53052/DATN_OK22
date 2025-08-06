package org.example.datn.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class KhachHangDTO {
    private Integer id;
    private String ma;
    private String ten;
    private String sdt;
    private String email;
    private String diaChi;
    private Boolean gioiTinh;
}
