package org.example.datn.Service;

import org.example.datn.Entity.*;
import org.example.datn.Repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.example.datn.Repository.NhanVienRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class BanHangService {

    @Autowired
    private HoaDonRepository hoaDonRepo;
    @Autowired
    private KhachHangRepository khachHangRepo;
    @Autowired
    private SanPhamChiTietRepository spctRepo;
    @Autowired
    private HoaDonChiTietRepository hdctRepo;
    @Autowired
    private VoucherRepository voucherRepo;
    @Autowired
    private NhanVienRepository nhanVienRepo;
    @Autowired
    private DanhMucRepository danhMucRepo;
    @Autowired
    private ThuongHieuRepository thuongHieuRepo;
    @Autowired
    private ChatLieuRepository chatLieuRepo;
    @Autowired
    private KieuDayRepository kieuDayRepo;
    @Autowired
    private LoaiKhoaRepository loaiKhoaRepo;
    @Autowired
    private MauSacRepository mauSacRepo;
    @Autowired
    private KichThuocRepository kichThuocRepo;

    public List<HoaDon> getHoaDonTam() {
        return hoaDonRepo.findTop5ByTrangThai(0);
    }

    public void taoHoaDonTam(String tenKhachHang) {
        HoaDon hoaDon = new HoaDon();
        hoaDon.setNgayTao(new Date());
        hoaDon.setTrangThai(0);
        if (tenKhachHang != null && !tenKhachHang.isBlank()) {
            Optional<KhachHang> khOptional = khachHangRepo.findByTen(tenKhachHang);
            khOptional.ifPresent(hoaDon::setKhachHang);
        }
        hoaDonRepo.save(hoaDon);
    }

    public HoaDon getChiTietHoaDon(Integer id) {
        return hoaDonRepo.findById(id).orElse(null);
    }

    public List<SanPhamChiTiet> getAllSanPham() {
        return spctRepo.findAll();
    }

    public void themSanPhamVaoHoaDon(Integer hoaDonId, Integer spctId, Integer soLuong) {
        Optional<HoaDon> hdOpt = hoaDonRepo.findById(hoaDonId);
        Optional<SanPhamChiTiet> spctOpt = spctRepo.findById(spctId);

        if (hdOpt.isPresent() && spctOpt.isPresent()) {
            HoaDon hoaDon = hdOpt.get();
            SanPhamChiTiet spct = spctOpt.get();

            if (soLuong > spct.getSoLuong()) {
                throw new IllegalArgumentException("Số lượng yêu cầu vượt quá số lượng kho.");
            }

            HoaDonChiTiet chiTiet = new HoaDonChiTiet();
            chiTiet.setHoaDon(hoaDon);
            chiTiet.setSanPhamChiTiet(spct);
            chiTiet.setSoLuong(soLuong);
            chiTiet.setDonGia(spct.getGiaSauKhuyenMai());

            hdctRepo.save(chiTiet);

            spct.setSoLuong(spct.getSoLuong() - soLuong);
            spctRepo.save(spct);

            capNhatTongTienHoaDon(hoaDonId);
        }
    }

    public void apDungVoucher(Integer hoaDonId, Integer voucherId) {
        Optional<HoaDon> hdOpt = hoaDonRepo.findById(hoaDonId);
        Optional<Voucher> voucherOpt = voucherRepo.findById(voucherId);

        if (hdOpt.isPresent() && voucherOpt.isPresent()) {
            HoaDon hoaDon = hdOpt.get();
            hoaDon.setVoucher(voucherOpt.get());
            hoaDonRepo.save(hoaDon);
        }
    }

    @Transactional
    public void xoaHoaDon(Integer hoaDonId) {
        Optional<HoaDon> optional = hoaDonRepo.findById(hoaDonId);
        if (optional.isPresent()) {
            List<HoaDonChiTiet> chiTietList = hdctRepo.findByHoaDonId(hoaDonId);

            for (HoaDonChiTiet ct : chiTietList) {
                SanPhamChiTiet spct = ct.getSanPhamChiTiet();
                spct.setSoLuong(spct.getSoLuong() + ct.getSoLuong());
                spctRepo.save(spct);
            }

            hdctRepo.deleteAll(chiTietList);
            hoaDonRepo.deleteById(hoaDonId);
        }
    }

    public void taoHoaDonTamTheoKhach(Integer khachHangId) {
        HoaDon hoaDon = new HoaDon();
        hoaDon.setNgayTao(new Date());
        hoaDon.setTrangThai(0);
        hoaDon.setTongTien(0.0);
        
        // Lấy nhân viên hiện tại (giả sử là nhân viên ID=1 cho ví dụ này)
        NhanVien nhanVien = nhanVienRepo.findById(1)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhân viên với ID=1"));
        hoaDon.setNhanVien(nhanVien);
        
        if (khachHangId != null) {
            khachHangRepo.findById(khachHangId).ifPresent(hoaDon::setKhachHang);
        }
        hoaDonRepo.save(hoaDon);
    }

    public List<Voucher> getVoucherDangHoatDong() {
        Date now = new Date();
        return voucherRepo.findAll().stream()
                .filter(v -> Boolean.TRUE.equals(v.getTrangThai())
                        && v.getNgayBatDau() != null && v.getNgayKetThuc() != null
                        && !v.getNgayBatDau().after(now)
                        && !v.getNgayKetThuc().before(now))
                .toList();
    }

    public List<SanPhamChiTiet> getSanPhamDangBan(String tenSanPham, String mauSac, String kichThuoc, 
                                       Integer danhMucId, Integer thuongHieuId, Integer chatLieuId, 
                                       Integer kieuDayId, Integer loaiKhoaId, Double giaMin, Double giaMax) {
        return spctRepo.findAll().stream()
                .filter(spct -> Boolean.TRUE.equals(spct.getTrangThai()) && spct.getSoLuong() > 0)
                .filter(spct -> tenSanPham == null || tenSanPham.isBlank() ||
                        (spct.getSanPham() != null && spct.getSanPham().getTen() != null &&
                                spct.getSanPham().getTen().toLowerCase().contains(tenSanPham.toLowerCase())))
                .filter(spct -> mauSac == null || mauSac.isBlank() ||
                        (spct.getMauSac() != null && spct.getMauSac().getTen() != null &&
                                spct.getMauSac().getTen().toLowerCase().contains(mauSac.toLowerCase())))
                .filter(spct -> kichThuoc == null || kichThuoc.isBlank() ||
                        (spct.getKichThuoc() != null && spct.getKichThuoc().getTen() != null &&
                                spct.getKichThuoc().getTen().toLowerCase().contains(kichThuoc.toLowerCase())))
                .filter(spct -> danhMucId == null || 
                        (spct.getSanPham() != null && spct.getSanPham().getDanhMuc() != null && 
                        spct.getSanPham().getDanhMuc().getId().equals(danhMucId)))
                .filter(spct -> thuongHieuId == null || 
                        (spct.getSanPham() != null && spct.getSanPham().getThuongHieu() != null && 
                        spct.getSanPham().getThuongHieu().getId().equals(thuongHieuId)))
                .filter(spct -> chatLieuId == null || 
                        (spct.getSanPham() != null && spct.getSanPham().getChatLieu() != null && 
                        spct.getSanPham().getChatLieu().getId().equals(chatLieuId)))
                .filter(spct -> kieuDayId == null || 
                        (spct.getSanPham() != null && spct.getSanPham().getKieuDay() != null && 
                        spct.getSanPham().getKieuDay().getId().equals(kieuDayId)))
                .filter(spct -> loaiKhoaId == null || 
                        (spct.getSanPham() != null && spct.getSanPham().getLoaiKhoa() != null && 
                        spct.getSanPham().getLoaiKhoa().getId().equals(loaiKhoaId)))
                .filter(spct -> giaMin == null || spct.getGiaSauKhuyenMai() >= giaMin)
                .filter(spct -> giaMax == null || spct.getGiaSauKhuyenMai() <= giaMax)
                .toList();
    }

    public void capNhatTongTienHoaDon(Integer hoaDonId) {
        Optional<HoaDon> hdOpt = hoaDonRepo.findById(hoaDonId);
        if (hdOpt.isPresent()) {
            HoaDon hoaDon = hdOpt.get();
            List<HoaDonChiTiet> chiTietList = hdctRepo.findByHoaDonId(hoaDonId);

            double tong = 0.0;
            for (HoaDonChiTiet ct : chiTietList) {
                tong += ct.getDonGia() * ct.getSoLuong();
            }

            hoaDon.setTongTien(tong);
            hoaDonRepo.save(hoaDon);
        }
    }

    public void luuHoaDon(HoaDon hoaDon) {
        hoaDonRepo.save(hoaDon);
    }

    public void boVoucher(Integer hoaDonId) {
        Optional<HoaDon> hoaDonOpt = hoaDonRepo.findById(hoaDonId);
        hoaDonOpt.ifPresent(hoaDon -> {
            hoaDon.setVoucher(null);
            hoaDonRepo.save(hoaDon);
        });
    }
    
    // Các phương thức lấy dữ liệu cho bộ lọc
    public List<DanhMuc> getAllDanhMuc() {
        return danhMucRepo.findAll();
    }
    
    public List<ThuongHieu> getAllThuongHieu() {
        return thuongHieuRepo.findAll();
    }
    
    public List<ChatLieu> getAllChatLieu() {
        return chatLieuRepo.findAll();
    }
    
    public List<KieuDay> getAllKieuDay() {
        return kieuDayRepo.findAll();
    }
    
    public List<LoaiKhoa> getAllLoaiKhoa() {
        return loaiKhoaRepo.findAll();
    }
    
    public List<MauSac> getAllMauSac() {
        return mauSacRepo.findAll();
    }
    
    public List<KichThuoc> getAllKichThuoc() {
        return kichThuocRepo.findAll();
    }
    
    // Phương thức để lấy giá min và max của sản phẩm
    public double[] getMinMaxPrice() {
        List<SanPhamChiTiet> products = spctRepo.findAll();
        if (products.isEmpty()) {
            return new double[]{0, 10000000}; // Default range
        }
        
        double min = Double.MAX_VALUE;
        double max = Double.MIN_VALUE;
        
        for (SanPhamChiTiet spct : products) {
            Double price = spct.getGiaSauKhuyenMai();
            if (price < min) min = price;
            if (price > max) max = price;
        }
        
        return new double[]{min, max};
    }
}
