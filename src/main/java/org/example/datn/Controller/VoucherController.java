package org.example.datn.Controller;

import org.example.datn.Entity.Voucher;
import org.example.datn.Service.VoucherService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/voucher")
public class VoucherController {

    private final VoucherService voucherService;

    public VoucherController(VoucherService voucherService) {
        this.voucherService = voucherService;
    }

    // ===========================
    // ✅ KHU VỰC ADMIN PAGE
    // ===========================

    // Trang chính hiển thị form + danh sách voucher
    @GetMapping("/view")
    public String listVoucher(Model model) {
        model.addAttribute("listVoucher", voucherService.getAllVoucher());
        return "voucher/list"; // Trả về view Thymeleaf
    }

    // Trả về dữ liệu JSON cho 1 voucher (dùng trong fetch sửa)
    @GetMapping("/view/edit/{id}")
    @ResponseBody
    public Voucher getVoucher(@PathVariable("id") Integer id) {
        return voucherService.getVoucherById(id);
    }

    // Lưu voucher từ form (dùng cho Ajax gửi JSON)
    @PostMapping("/view/save")
    @ResponseBody
    public String saveVoucher(@RequestBody Voucher voucher) {
        if (voucher.getMa() == null || voucher.getMa().trim().isEmpty()) {
            return "❌ Mã voucher không được để trống!";
        }
        voucherService.saveVoucher(voucher);
        return "✅ Lưu voucher thành công!";
    }

    // Bật/tắt trạng thái voucher (tạm dừng hoặc kích hoạt)
    @GetMapping("/view/toggle-status/{id}")
    public String toggleStatus(@PathVariable("id") Integer id) {
        voucherService.toggleStatus(id);
        return "redirect:/voucher/view";
    }

    // ===========================
    // ✅ KHU VỰC API DÙNG CHO FRONTEND THANH TOÁN
    // ===========================

    // ✅ Lấy danh sách các voucher đang hoạt động (dùng ở frontend thanh toán)
    @GetMapping("/api/dang-hoat-dong")
    @ResponseBody
    public ResponseEntity<List<Voucher>> getVoucherActive() {
        List<Voucher> active = voucherService.getVoucherDangHoatDong();
        return ResponseEntity.ok(active);
    }

    // ✅ Lấy thông tin voucher theo mã (nếu người dùng nhập mã voucher thủ công)
    @GetMapping("/api/theo-ma/{ma}")
    @ResponseBody
    public ResponseEntity<?> getVoucherByMa(@PathVariable("ma") String ma) {
        Voucher v = voucherService.getVoucherByMa(ma);
        if (v != null && Boolean.TRUE.equals(v.getTrangThai())) {
            return ResponseEntity.ok(v);
        }
        return ResponseEntity.badRequest().body("❌ Mã voucher không hợp lệ hoặc đã hết hiệu lực.");
    }
    // Thêm vào VoucherController.java
@GetMapping("/api/kiem-tra/{id}")
@ResponseBody
public ResponseEntity<Map<String, Object>> kiemTraVoucher(
    @PathVariable Integer id) {
    
    Voucher voucher = voucherService.getVoucherById(id);
    
    // Chỉ kiểm tra 2 điều kiện cơ bản
    if (voucher == null || !Boolean.TRUE.equals(voucher.getTrangThai())) {
        return ResponseEntity.ok(Map.of(
            "valid", false,
            "message", "Voucher không hợp lệ"
        ));
    }
    
    return ResponseEntity.ok(Map.of(
        "valid", true,
        "phanTramGiam", voucher.getPhanTramGiam()
        
    ));
}
}
