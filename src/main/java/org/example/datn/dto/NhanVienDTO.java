package org.example.datn.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NhanVienDTO {
    private Integer id;
    private String ma;
    private String ten;
    private String sdt;
    private String email;
    private Boolean gioiTinh;
}
