package org.example.datn.Service;

import lombok.RequiredArgsConstructor;
import org.example.datn.Entity.*;
import org.example.datn.Repository.*;
import org.example.datn.Response.DonHangRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class HoaDonService {

    private final HoaDonRepository hoaDonRepository;
    private final HoaDonChiTietRepository hoaDonChiTietRepository;
    private final KhachHangRepository khachHangRepository;
    private final SanPhamChiTietRepository spctRepository;
    private final VoucherRepository voucherRepository;

    public List<HoaDon> getAll() {
        List<HoaDon> list = hoaDonRepository.findAllWithJoins();

        list.sort((hd1, hd2) -> {
            // Xử lý null cho trạng thái
            int t1 = hd1.getTrangThai() != null ? hd1.getTrangThai() : 0;
            int t2 = hd2.getTrangThai() != null ? hd2.getTrangThai() : 0;

            // Ưu tiên đẩy hóa đơn đã thanh toán (trạng thái 1) xuống cuối
            if (t1 == 1 && t2 != 1)
                return 1;
            if (t1 != 1 && t2 == 1)
                return -1;

            // Xử lý null cho updatedAt - ưu tiên ngayTao nếu updatedAt null
            Date date1 = hd1.getUpdatedAt() != null ? hd1.getUpdatedAt() : hd1.getNgayTao();
            Date date2 = hd2.getUpdatedAt() != null ? hd2.getUpdatedAt() : hd2.getNgayTao();

            // Đảm bảo không bao giờ null
            date1 = date1 != null ? date1 : new Date(0);
            date2 = date2 != null ? date2 : new Date(0);

            return date2.compareTo(date1); // Mới nhất xuống dưới
        });

        return list;
    }

    public HoaDon getById(Integer id) {
        return hoaDonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn với ID: " + id));
    }

    public HoaDon save(HoaDon hoaDon) {
        if (hoaDon.getNgayTao() == null) {
            hoaDon.setNgayTao(new Date());
        }
        hoaDon.setUpdatedAt(new Date());
        return hoaDonRepository.save(hoaDon);
    }

    public void delete(Integer id) {
        HoaDon hoaDon = getById(id);
        if (!hoaDon.getChiTietList().isEmpty()) {
            hoaDonChiTietRepository.deleteAll(hoaDon.getChiTietList());
        }
        hoaDonRepository.delete(hoaDon);
    }

    @Transactional
    public HoaDon taoHoaDon(DonHangRequest req) {
        // Validate request
        if (req == null || req.getItems() == null || req.getItems().isEmpty()) {
            throw new IllegalArgumentException("Thông tin đơn hàng không hợp lệ");
        }

        // Xử lý khách hàng
        KhachHang kh = khachHangRepository.findByEmail(req.getEmail())
                .orElseGet(() -> {
                    KhachHang newKh = new KhachHang();
                    newKh.setEmail(req.getEmail());
                    newKh.setTen(req.getHoTen());
                    newKh.setDiaChi(req.getDiaChi());
                    newKh.setSdt(req.getSdt());
                    return khachHangRepository.save(newKh);
                });

        // Tạo hóa đơn
        HoaDon hoaDon = new HoaDon();
        hoaDon.setNgayTao(new Date());
        hoaDon.setUpdatedAt(new Date());
        hoaDon.setTrangThai(0); // Chờ xử lý
        hoaDon.setKhachHang(kh);

        // Xử lý từng sản phẩm
        List<HoaDonChiTiet> chiTietList = new ArrayList<>();
        double tongTien = 0;

        for (DonHangRequest.ItemRequest item : req.getItems()) {
            SanPhamChiTiet spct = spctRepository.findById(item.getIdSanPhamChiTiet())
                    .orElseThrow(() -> new RuntimeException(
                            "Không tìm thấy sản phẩm với ID: " + item.getIdSanPhamChiTiet()));

            // Validate số lượng
            if (item.getSoLuong() <= 0) {
                throw new IllegalArgumentException("Số lượng sản phẩm phải lớn hơn 0");
            }

            // Tính đơn giá
            double donGia = Optional.ofNullable(item.getGiaSauKhuyenMai())
                    .filter(g -> g > 0)
                    .orElse(spct.getGiaBan());

            // Tạo chi tiết hóa đơn
            HoaDonChiTiet ct = new HoaDonChiTiet();
            ct.setHoaDon(hoaDon);
            ct.setSanPhamChiTiet(spct);
            ct.setSoLuong(item.getSoLuong());
            ct.setDonGia(donGia);
            chiTietList.add(ct);

            tongTien += donGia * item.getSoLuong();
        }

        // Xử lý voucher
        if (req.getVoucherId() != null) {
            Voucher voucher = voucherRepository.findById(req.getVoucherId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy voucher với ID: " + req.getVoucherId()));

            // Validate voucher
            if (voucher.getPhanTramGiam() <= 0 || voucher.getPhanTramGiam() > 100) {
                throw new IllegalArgumentException("Phần trăm giảm giá voucher không hợp lệ");
            }

            hoaDon.setVoucher(voucher);
            double giamGia = tongTien * (voucher.getPhanTramGiam() / 100.0);
            tongTien -= giamGia;
        }

        hoaDon.setTongTien(tongTien);
        hoaDon.setChiTietList(chiTietList);

        // Lưu vào database
        HoaDon savedHoaDon = hoaDonRepository.save(hoaDon);
        hoaDonChiTietRepository.saveAll(chiTietList);

        return savedHoaDon;
    }

    public HoaDon updateTrangThai(Integer id, Integer trangThai) {
        // Validate trạng thái
        if (trangThai == null || trangThai < 0 || trangThai > 2) {
            throw new IllegalArgumentException("Trạng thái không hợp lệ");
        }

        HoaDon hoaDon = getById(id);
        hoaDon.setTrangThai(trangThai);
        hoaDon.setUpdatedAt(new Date());
        return hoaDonRepository.save(hoaDon);
    }

    public List<HoaDon> getHoaDonTaiQuay() {
        return getAll().stream()
                .filter(hd -> hd.getNhanVien() != null)
                .collect(Collectors.toList());
    }

    public List<HoaDon> getHoaDonOnline() {
        return getAll().stream()
                .filter(hd -> hd.getNhanVien() == null)
                .collect(Collectors.toList());
    }
}