package org.example.datn.Controller;

import lombok.RequiredArgsConstructor;
import org.example.datn.Entity.HoaDon;
import org.example.datn.Service.HoaDonService;
import org.example.datn.Response.DonHangRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;

import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/hoa-don")
public class HoaDonController {

    private final HoaDonService hoaDonService;

    // ✅ Hiển thị tất cả hóa đơn
    @GetMapping("/view")
    public String index(Model model) {
        List<HoaDon> hoaDons = hoaDonService.getAll();

        model.addAttribute("hoaDons", hoaDons);
        return "HoaDon/index";
    }

    // ✅ Xem chi tiết hóa đơn theo id
    @GetMapping("/chi-tiet/{id}")
    public String getChiTietHoaDon(@PathVariable("id") Integer id, Model model) {
        HoaDon hoaDon = hoaDonService.getById(id);
        model.addAttribute("chiTiets", hoaDon.getChiTietList());
        return "HoaDon/fragmentChiTiet :: chiTietTable";
    }

    // ✅ Giao diện form chi tiết hóa đơn
    @GetMapping("/chi-tiet-form/{id}")
    public String xemChiTietForm(@PathVariable("id") Integer id, Model model) {
        HoaDon hoaDon = hoaDonService.getById(id);

        double tongTien = hoaDon.getChiTietList()
                .stream()
                .mapToDouble(ct -> ct.getDonGia() * ct.getSoLuong())
                .sum();

        model.addAttribute("hoaDon", hoaDon);
        model.addAttribute("tongTien", tongTien);

        return "HoaDon/fragmentChiTiet";
    }

    // ✅ Cập nhật trạng thái đơn hàng
    @PostMapping("/cap-nhat-trang-thai")
    @ResponseBody
    public String capNhatTrangThai(@RequestParam Integer hoaDonId, @RequestParam Integer trangThai) {
        HoaDon hoaDon = hoaDonService.getById(hoaDonId);
        if (hoaDon != null) {
            hoaDon.setTrangThai(trangThai);
            hoaDonService.save(hoaDon);
            return "Đã cập nhật trạng thái.";
        }
        return "Không tìm thấy hóa đơn.";
    }

    @PostMapping("/api/tao")
    @ResponseBody
    public ResponseEntity<?> datHang(@RequestBody DonHangRequest req) {
        try {
            HoaDon hoaDon = hoaDonService.taoHoaDon(req);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "hoaDonId", hoaDon.getId()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Thêm vào HoaDonController.java
    @GetMapping("/thanh-toan/thanh-cong/{id}")
    public String thanhToanThanhCong(@PathVariable Integer id, Model model, HttpSession session) {
        // Xóa session giỏ hàng nếu tồn tại
        session.removeAttribute("gioHang");

        // Lấy thông tin hóa đơn
        HoaDon hoaDon = hoaDonService.getById(id);
        model.addAttribute("hoaDon", hoaDon);

        return "HoaDon/thanhCong";
    }

    @GetMapping("/tai-quay")
    public String hoaDonTaiQuay(Model model) {
        List<HoaDon> hoaDons = hoaDonService.getHoaDonTaiQuay();
        model.addAttribute("hoaDons", hoaDons);
        return "HoaDon/index";
    }

    @GetMapping("/online")
    public String hoaDonOnline(Model model) {
        List<HoaDon> hoaDons = hoaDonService.getHoaDonOnline();
        model.addAttribute("hoaDons", hoaDons);
        return "HoaDon/index";
    }
}
