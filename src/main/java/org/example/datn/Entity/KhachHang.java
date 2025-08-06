package org.example.datn.Entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "KhachHang")
public class KhachHang {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String ma;

    @Column(name = "HoTen")
    private String ten;

    private String sdt;
    private String email;
    private String diaChi;
    private Boolean gioiTinh;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Temporal(TemporalType.DATE)
    @Column(name = "NgaySinh")
    private Date ngaySinh;

    private String matKhau;
    private Boolean trangThai;

    @OneToMany(mappedBy = "khachHang")
    @JsonIgnore
    private List<HoaDon> hoaDons;

    @OneToMany(mappedBy = "khachHang")
    @JsonIgnore
    private List<GioHangChiTiet> gioHangs;

    @OneToMany(mappedBy = "khachHang")
    @JsonIgnore
    private List<DanhGia> danhGias;
}
