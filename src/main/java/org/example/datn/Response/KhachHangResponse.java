package org.example.datn.Response;

import org.example.datn.Entity.KhachHang;

public class KhachHangResponse {

    private boolean thanhCong;
    private String thongBao;
    private KhachHang duLieu;

    public static KhachHangResponse thanhCong(KhachHang data, String message) {
        return new KhachHangResponse(true, message, data);
    }

    public static KhachHangResponse thatBai(String message) {
        return new KhachHangResponse(false, message, null);
    }

    public KhachHangResponse() {}

    public KhachHangResponse(boolean thanhCong, String thongBao, KhachHang duLieu) {
        this.thanhCong = thanhCong;
        this.thongBao = thongBao;
        this.duLieu = duLieu;
    }

    public boolean isThanhCong() {
        return thanhCong;
    }

    public String getThongBao() {
        return thongBao;
    }

    public KhachHang getDuLieu() {
        return duLieu;
    }
}
