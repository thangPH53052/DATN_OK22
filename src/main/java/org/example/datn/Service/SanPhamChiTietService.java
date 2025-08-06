package org.example.datn.Service;

import org.example.datn.Entity.*;
import org.example.datn.Repository.*;
import org.example.datn.Response.SPACTResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SanPhamChiTietService {

    @Autowired
    private SanPhamChiTietRepository repository;

    @Autowired
    private MauSacRepository mauSacRepository;

    @Autowired
    private KichThuocRepository kichThuocRepository;

    @Autowired
    private KhuyenMaiRepository khuyenMaiRepository;
    @Autowired
    private SanPhamRepository sanPhamRepository;

    public List<SanPhamChiTiet> getAll() {
        return repository.findAll();
    }

    public List<SanPhamChiTiet> getFullInfo() {
        return repository.getSanPhamDangBanFull();
    }

    public SanPhamChiTiet getById(Integer id) {
        return repository.findById(id).orElse(null);
    }

    public void add(Integer idSanPham, Integer idMauSac, Integer idKichThuoc,
            Integer idKhuyenMai, Double giaBan, Double giaNhap,
            Integer soLuong, Boolean trangThai) {

        SanPhamChiTiet ct = new SanPhamChiTiet();
        ct.setSanPham(new SanPham(idSanPham));
        ct.setMauSac(new MauSac(idMauSac));
        ct.setKichThuoc(new KichThuoc(idKichThuoc));
        ct.setKhuyenMai(idKhuyenMai != null ? new KhuyenMai(idKhuyenMai) : null);
        ct.setGiaBan(giaBan);
        ct.setGiaNhap(giaNhap);
        ct.setSoLuong(soLuong);
        ct.setTrangThai(trangThai != null ? trangThai : true);

        repository.save(ct);
    }

    public void update(Integer id, Integer idMauSac, Integer idKichThuoc,
            Integer idKhuyenMai, Double giaBan, Double giaNhap,
            Integer soLuong, Boolean trangThai) {

        SanPhamChiTiet ct = repository.findById(id).orElse(null);
        if (ct == null)
            return;

        ct.setMauSac(mauSacRepository.findById(idMauSac).orElse(null));
        ct.setKichThuoc(kichThuocRepository.findById(idKichThuoc).orElse(null));
        ct.setKhuyenMai(idKhuyenMai != null ? khuyenMaiRepository.findById(idKhuyenMai).orElse(null) : null);
        ct.setGiaBan(giaBan);
        ct.setGiaNhap(giaNhap);
        ct.setSoLuong(soLuong);
        ct.setTrangThai(trangThai != null ? trangThai : true);

        repository.save(ct);
    }

    public void delete(Integer id) {
        repository.deleteById(id);
    }

    public SanPhamChiTiet findById(Integer id) {
        return repository.findById(id).orElse(null);
    }

    public void update(Integer id, Integer idSanPham, Integer idMauSac, Integer idKichThuoc,
            Integer idKhuyenMai, Double giaBan, Double giaNhap, Integer soLuong, Boolean trangThai) {

        SanPhamChiTiet ct = repository.findById(id).orElse(null);
        if (ct != null) {
            ct.setSanPham(sanPhamRepository.findById(idSanPham).orElse(null));
            ct.setMauSac(mauSacRepository.findById(idMauSac).orElse(null));
            ct.setKichThuoc(kichThuocRepository.findById(idKichThuoc).orElse(null));
            ct.setKhuyenMai(idKhuyenMai != null ? khuyenMaiRepository.findById(idKhuyenMai).orElse(null) : null);
            ct.setGiaBan(giaBan);
            ct.setGiaNhap(giaNhap);
            ct.setSoLuong(soLuong);
            ct.setTrangThai(trangThai);
            repository.save(ct);
        }
    }

    public void chuyenTrangThai(Integer id) {
        SanPhamChiTiet ct = repository.findById(id).orElse(null);
        if (ct != null) {
            ct.setTrangThai(!ct.getTrangThai()); // Đảo trạng thái
            repository.save(ct);
        }
    }

    public boolean isExist(Integer idSanPham, Integer idMauSac, Integer idKichThuoc) {
        return repository.existsBySanPham_IdAndMauSac_IdAndKichThuoc_Id(idSanPham, idMauSac, idKichThuoc);
    }

    public SPACTResponse layChiTietSanPham(Integer id) {
        SanPhamChiTiet spct = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm chi tiết với id: " + id));

        SanPham sp = spct.getSanPham();

        List<SanPhamChiTiet> bienTheList = repository.findBySanPham(sp);
        List<SPACTResponse.MauSacOption> dsMauSac = bienTheList.stream()
                .collect(Collectors.collectingAndThen(
                        Collectors.toMap(
                                item -> item.getMauSac().getTen(), // key theo tên
                                item -> new SPACTResponse.MauSacOption(
                                        item.getMauSac().getTen(),
                                        item.getMauSac().getMaMau(),
                                        item.getSoLuong() <= 0),
                                (first, second) -> {
                                    // nếu có trùng màu, mà một trong hai còn hàng => chưa hết hàng
                                    first.setHetHang(first.isHetHang() && second.isHetHang());
                                    return first;
                                }),
                        map -> new ArrayList<>(map.values())));
        List<SPACTResponse.KichThuocOption> dsKichThuoc = bienTheList.stream()
                .collect(Collectors.collectingAndThen(
                        Collectors.toMap(
                                item -> item.getKichThuoc().getTen(),
                                item -> new SPACTResponse.KichThuocOption(
                                        item.getKichThuoc().getTen(),
                                        item.getSoLuong() <= 0),
                                (first, second) -> {
                                    first.setHetHang(first.isHetHang() && second.isHetHang());
                                    return first;
                                }),
                        map -> new ArrayList<>(map.values())));

        return new SPACTResponse(
                spct.getId(),
                sp.getTen(),
                spct.getGiaBan(),
                spct.getGiaSauKhuyenMai(),
                sp.getMoTa(),
                sp.getKichThuoc(),
                sp.getCanNang(),
                sp.getDungTich(),
                sp.getThuongHieu().getTen(),
                sp.getChatLieu().getTen(),
                sp.getLoaiKhoa().getTen(),
                sp.getKieuDay().getTen(),
                sp.getDanhMuc().getTen(),
                spct.getMauSac().getTen(),
                spct.getKichThuoc().getTen(),
                spct.getSoLuong(),
                sp.getHinhAnhUrls().stream().map(url -> "/images/" + url).toList(),
                dsMauSac,
                dsKichThuoc);
    }

}
