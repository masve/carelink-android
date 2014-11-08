package com.dtu.mark.carelink_android.Decoding.Stick;

/**
 * Created by mark on 08/11/14.
 */
public class StatusModel {
    String ok;
    String ack;
    String success;
    String status;
    String iface;
    String debug_iface;
    String size;
    String unknown_b;
    String unknown_c;
    String unknown_d;
    String unknown_e;
    String unknown_f;
    String unknown_g;
    String unknown_h;

    public StatusModel(String ok, String ack, String success, String status, String iface, String debug_iface, String size, String unknown_b, String unknown_c, String unknown_d, String unknown_e, String unknown_f, String unknown_g, String unknown_h) {
        this.ok = ok;
        this.ack = ack;
        this.success = success;
        this.status = status;
        this.iface = iface;
        this.debug_iface = debug_iface;
        this.size = size;
        this.unknown_b = unknown_b;
        this.unknown_c = unknown_c;
        this.unknown_d = unknown_d;
        this.unknown_e = unknown_e;
        this.unknown_f = unknown_f;
        this.unknown_g = unknown_g;
        this.unknown_h = unknown_h;
    }

    public String getOk() {
        return ok;
    }

    public String getAck() {
        return ack;
    }

    public String getSuccess() {
        return success;
    }

    public String getStatus() {
        return status;
    }

    public String getIface() {
        return iface;
    }

    public String getDebug_iface() {
        return debug_iface;
    }

    public String getSize() {
        return size;
    }

    public String getUnknown_b() {
        return unknown_b;
    }

    public String getUnknown_c() {
        return unknown_c;
    }

    public String getUnknown_d() {
        return unknown_d;
    }

    public String getUnknown_e() {
        return unknown_e;
    }

    public String getUnknown_f() {
        return unknown_f;
    }

    public String getUnknown_g() {
        return unknown_g;
    }

    public String getUnknown_h() {
        return unknown_h;
    }

    @Override
    public String toString() {
        return "StatusModel{" +
                "ok='" + ok + '\'' +
                ", ack='" + ack + '\'' +
                ", success='" + success + '\'' +
                ", status='" + status + '\'' +
                ", iface='" + iface + '\'' +
                ", debug_iface='" + debug_iface + '\'' +
                ", size='" + size + '\'' +
                ", unknown_b='" + unknown_b + '\'' +
                ", unknown_c='" + unknown_c + '\'' +
                ", unknown_d='" + unknown_d + '\'' +
                ", unknown_e='" + unknown_e + '\'' +
                ", unknown_f='" + unknown_f + '\'' +
                ", unknown_g='" + unknown_g + '\'' +
                ", unknown_h='" + unknown_h + '\'' +
                '}';
    }
}
