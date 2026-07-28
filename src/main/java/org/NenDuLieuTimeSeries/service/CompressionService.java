package org.NenDuLieuTimeSeries.service;

public class CompressionService {

    // Giá trị cuối cùng đã ghi xuống log
    private volatile Integer lastSavedUser = null;

    // Thời điểm ghi log gần nhất
    private volatile long lastSavedTime = 0;

    /**
     * Trả về:
     * INFO  : biến động > 2% so với giá trị lần cuối được ghi
     * DEBUG : đủ 12 giây chưa ghi (kể từ lần ghi cuối)
     * NONE  : không ghi
     *
     * Lưu ý: so sánh với dữ liệu CUỐI CÙNG được ghi (lastSavedUser),
     * không phải dữ liệu query lần trước.
     */
    public synchronized SaveType check(int currentUser) {

        long now = System.currentTimeMillis();

        // Lần đầu tiên: ghi ngay với INFO
        if (lastSavedUser == null) {

            lastSavedUser = currentUser;
            lastSavedTime = now;

            return SaveType.INFO;
        }

        //-----------------------
        // Kiểm tra biến động > 2%
        //-----------------------

        double percent =
                Math.abs(currentUser - lastSavedUser)
                        * 100.0
                        / lastSavedUser;

        if (percent > 2) {

            lastSavedUser = currentUser;
            lastSavedTime = now;

            return SaveType.INFO;
        }

        //-----------------------
        // Kiểm tra đủ 12 giây
        //-----------------------

        if (now - lastSavedTime >= 12_000) {

            lastSavedUser = currentUser;
            lastSavedTime = now;

            return SaveType.DEBUG;
        }

        return SaveType.NONE;
    }
}
