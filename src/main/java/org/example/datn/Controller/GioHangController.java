package org.example.datn.Controller;

import lombok.RequiredArgsConstructor;
import org.example.datn.Response.GioHangRequest;
import org.example.datn.Service.GioHangService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/gio-hang")
@RequiredArgsConstructor
public class GioHangController {

    private final GioHangService gioHangService;

    // ✅ Thêm sản phẩm vào giỏ hàng
    @PostMapping
    public ResponseEntity<?> them(@RequestBody GioHangRequest req) {
        gioHangService.themVaoGioHang(req);
        return ResponseEntity.ok(Map.of("thanhCong", true));
    }

    // ✅ Lấy giỏ hàng theo email
    @GetMapping("/{email}")
    public ResponseEntity<?> getAll(@PathVariable String email) {
        return ResponseEntity.ok(gioHangService.getByEmail(email));
    }

    // ✅ Xoá sản phẩm khỏi giỏ hàng
    @DeleteMapping("/{id}")
    public ResponseEntity<?> xoaSanPham(@PathVariable int id) {
        gioHangService.xoaSanPhamTrongGio(id);
        return ResponseEntity.ok(Map.of("success", true));
    }

    // ✅ Cập nhật số lượng sản phẩm trong giỏ hàng
    @PutMapping("/cap-nhat")
    public ResponseEntity<?> capNhatSoLuong(@RequestBody Map<String, Object> body) {
        int id = Integer.parseInt(body.get("id").toString());
        int soLuong = Integer.parseInt(body.get("soLuong").toString());
        gioHangService.capNhatSoLuong(id, soLuong);
        return ResponseEntity.ok(Map.of("success", true));
    }
    
    // ✅ Xóa toàn bộ giỏ hàng theo email (sau khi thanh toán)
    @PutMapping("/clear/{email}")
    public ResponseEntity<?> clearGioHang(@PathVariable String email) {
        gioHangService.clearGioHang(email);
        return ResponseEntity.ok(Map.of("success", true));
    }
    
    // ✅ Lấy lịch sử mua hàng của khách hàng theo email
    @GetMapping("/lich-su-mua-hang/{email}")
    public ResponseEntity<?> getLichSuMuaHang(@PathVariable String email) {
        return ResponseEntity.ok(gioHangService.getLichSuMuaHang(email));
    }
}