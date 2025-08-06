package org.example.datn.Service;

import lombok.RequiredArgsConstructor;
import org.example.datn.Entity.*;
import org.example.datn.Repository.GioHangChiTietRepository;
import org.example.datn.Repository.HoaDonChiTietRepository;
import org.example.datn.Repository.HoaDonRepository;
import org.example.datn.Repository.KhachHangRepository;
import org.example.datn.Repository.SanPhamChiTietRepository;
import org.example.datn.Response.GioHangRequest;
import org.example.datn.Response.GioHangResponse;
import org.example.datn.Response.LichSuMuaHangResponse;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GioHangService {

    private final GioHangChiTietRepository gioHangRepo;
    private final KhachHangRepository khachHangRepo;
    private final SanPhamChiTietRepository sanPhamChiTietRepo;
    private final HoaDonRepository hoaDonRepository;
    private final HoaDonChiTietRepository hoaDonChiTietRepository;

    /**
     * Thêm sản phẩm vào giỏ hàng
     */
    public void themVaoGioHang(GioHangRequest req) {
        KhachHang khachHang = khachHangRepo.findByEmail(req.getEmail())
                .orElseThrow(() -> new RuntimeException("❌ Khách hàng không tồn tại!"));

        SanPhamChiTiet spct = sanPhamChiTietRepo.findById(req.getIdSanPhamChiTiet())
                .orElseThrow(() -> new RuntimeException("❌ Sản phẩm chi tiết không tồn tại!"));

        Optional<GioHangChiTiet> existing = gioHangRepo
                .findByKhachHangEmailAndSanPhamChiTietId(req.getEmail(), req.getIdSanPhamChiTiet());

        if (existing.isPresent()) {
            GioHangChiTiet ghct = existing.get();
            ghct.setSoLuong(ghct.getSoLuong() + req.getSoLuong());
            gioHangRepo.save(ghct);
        } else {
            GioHangChiTiet ghct = new GioHangChiTiet();
            ghct.setKhachHang(khachHang);
            ghct.setSanPhamChiTiet(spct);
            ghct.setSoLuong(req.getSoLuong());
            ghct.setNgayThem(LocalDateTime.now());
            ghct.setTrangThai(true);

            gioHangRepo.save(ghct);
        }
    }

    /**
     * Lấy danh sách giỏ hàng của 1 khách hàng
     */
    public List<GioHangResponse> getByEmail(String email) {
        List<GioHangChiTiet> list = gioHangRepo.findByKhachHangEmail(email);

        return list.stream().map(gh -> {
            SanPhamChiTiet spct = gh.getSanPhamChiTiet();
            SanPham sp = spct.getSanPham();

            String tenSanPham = sp != null ? sp.getTen() : "Không rõ";
            String hinhAnh = "/images/default.jpg";

            try {
                hinhAnh = Optional.ofNullable(sp)
                        .map(SanPham::getHinhAnhUrls)
                        .flatMap(urls -> urls.stream().findFirst())
                        .orElse("/images/default.jpg");
            } catch (Exception e) {
                System.out.println("⚠ Không thể lấy ảnh sản phẩm: " + e.getMessage());
            }

            String kichThuoc = Optional.ofNullable(spct.getKichThuoc())
                    .map(KichThuoc::getTen)
                    .orElse("Không có");

            String mauSac = Optional.ofNullable(spct.getMauSac())
                    .map(MauSac::getTen)
                    .orElse("Không có");

            double giaGoc = Optional.ofNullable(spct.getGiaBan()).orElse(0.0);
            double giaSauKM = Optional.ofNullable(spct.getGiaSauKhuyenMai()).orElse(giaGoc);

            return GioHangResponse.builder()
                    .id(gh.getId()) // ID giỏ hàng
                    .idSanPhamChiTiet(spct.getId()) // ✅ THÊM DÒNG NÀY
                    .tenSanPham(tenSanPham)
                    .hinhAnh(hinhAnh)
                    .soLuong(gh.getSoLuong())
                    .giaGoc(giaGoc)
                    .giaSauKhuyenMai(giaSauKM)
                    .tongTien(giaSauKM * gh.getSoLuong())
                    .kichThuoc(kichThuoc)
                    .mauSac(mauSac)
                    .build();
        }).collect(Collectors.toList());
    }

    /**
     * ✅ Cập nhật số lượng sản phẩm trong giỏ
     */
    public void capNhatSoLuong(int id, int soLuongMoi) {
        GioHangChiTiet ghct = gioHangRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm trong giỏ hàng"));

        if (soLuongMoi <= 0) {
            gioHangRepo.deleteById(id);
        } else {
            ghct.setSoLuong(soLuongMoi);
            gioHangRepo.save(ghct);
        }
    }

    /**
     * ✅ Xoá sản phẩm khỏi giỏ
     */
    public void xoaSanPhamTrongGio(int id) {
        if (gioHangRepo.existsById(id)) {
            gioHangRepo.deleteById(id);
        } else {
            throw new RuntimeException("❌ Không tìm thấy sản phẩm cần xoá!");
        }
    }

    /**
     * ✅ Xóa toàn bộ giỏ hàng của khách hàng theo email
     */
    public void clearGioHang(String email) {
        List<GioHangChiTiet> items = gioHangRepo.findByKhachHangEmail(email);
        gioHangRepo.deleteAll(items);
    }
    
    /**
     * ✅ Lấy lịch sử mua hàng của khách hàng theo email
     */
    public List<LichSuMuaHangResponse> getLichSuMuaHang(String email) {
        // Lấy khách hàng từ email
        KhachHang khachHang = khachHangRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("❌ Khách hàng không tồn tại!"));

        // Lấy danh sách hóa đơn của khách hàng (chỉ lấy đơn hàng online - không có nhân viên)
        List<HoaDon> hoaDons = hoaDonRepository.findByKhachHangIdAndNhanVienIsNull(khachHang.getId());

        List<LichSuMuaHangResponse> result = new ArrayList<>();

        // Chuyển đổi danh sách hóa đơn thành response
        for (HoaDon hoaDon : hoaDons) {
            LichSuMuaHangResponse donHang = new LichSuMuaHangResponse();
            donHang.setId(hoaDon.getId());
            donHang.setNgayTao(hoaDon.getNgayTao());
            donHang.setTrangThai(hoaDon.getTrangThai());
            donHang.setTongTien(hoaDon.getTongTien());
            
            // Tính tổng tiền trước giảm (tổng tiền của tất cả sản phẩm)
            Double tongTienTruocGiam = 0.0;
            for (HoaDonChiTiet chiTiet : hoaDon.getChiTietList()) {
                tongTienTruocGiam += chiTiet.getDonGia() * chiTiet.getSoLuong();
            }
            donHang.setTongTienTruocGiam(tongTienTruocGiam);
            
            // Thông tin voucher nếu có
            if (hoaDon.getVoucher() != null) {
                donHang.setMaVoucher(hoaDon.getVoucher().getMa());
                donHang.setPhanTramGiam(hoaDon.getVoucher().getPhanTramGiam());
                donHang.setTienGiam(tongTienTruocGiam - hoaDon.getTongTien());
            } else {
                donHang.setTienGiam(0.0);
                donHang.setPhanTramGiam(0);
            }

            // Lấy chi tiết sản phẩm trong hóa đơn
            List<LichSuMuaHangResponse.SanPhamResponse> sanPhams = new ArrayList<>();
            for (HoaDonChiTiet chiTiet : hoaDon.getChiTietList()) {
                LichSuMuaHangResponse.SanPhamResponse sp = new LichSuMuaHangResponse.SanPhamResponse();
                sp.setTenSanPham(chiTiet.getSanPhamChiTiet().getSanPham().getTen());
                sp.setSoLuong(chiTiet.getSoLuong());
                sp.setDonGia(chiTiet.getDonGia());
                sp.setThanhTien(chiTiet.getDonGia() * chiTiet.getSoLuong());
                // Không lấy getCreatedAt() vì không có, để null hoặc có thể lấy ngày tạo hóa đơn
                sp.setNgayThem(hoaDon.getNgayTao());

                // Lấy thông tin màu sắc và kích thước nếu có
                if (chiTiet.getSanPhamChiTiet().getMauSac() != null) {
                    sp.setMauSac(chiTiet.getSanPhamChiTiet().getMauSac().getTen());
                }
                if (chiTiet.getSanPhamChiTiet().getKichThuoc() != null) {
                    sp.setKichThuoc(chiTiet.getSanPhamChiTiet().getKichThuoc().getTen());
                }

                sanPhams.add(sp);
            }

            donHang.setSanPhams(sanPhams);
            result.add(donHang);
        }

        return result;
    }
}
