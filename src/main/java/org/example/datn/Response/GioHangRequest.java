package org.example.datn.Response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GioHangRequest {
    private String email;
    private Integer idSanPhamChiTiet;
    private Integer soLuong;
}



