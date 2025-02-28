package com.prgrms.vouchermanagement.voucher;

import java.time.LocalDateTime;
import java.util.Arrays;

public enum VoucherType {
    FIXED_DISCOUNT(1, "fixed"), PERCENT_DISCOUNT(2, "percent");

    private final int order;
    private final String description;

    VoucherType(int order, String description) {
        this.order = order;
        this.description = description;
    }

    /**
     * 메뉴에서 입력받은 voucher 번호와 일치하는 VoucherType 을 반환한다.
     */
    public static VoucherType getVoucherType(int order) throws IllegalArgumentException {
        return Arrays.stream(VoucherType.values())
                .filter(v -> v.order == order)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("order와 매칭되는 VoucherType이 없습니다."));
    }

    public static VoucherType getVoucherType(Voucher voucher)  throws IllegalArgumentException {
        if (FixedAmountVoucher.class.equals(voucher.getClass())) {
            return FIXED_DISCOUNT;
        } else if (PercentDiscountVoucher.class.equals(voucher.getClass())) {
            return PERCENT_DISCOUNT;
        } else {
            throw new IllegalArgumentException("voucher 매칭되는 VoucherType이 없습니다.");
        }
    }

    /**
     * @throws IllegalArgumentException : amount 값이 유효한 범위의 값이 아닌 경우 of 메서드에서 던져진다.
     */
    public Voucher constructor(Long voucherId, long amount, LocalDateTime createdAt) {
        switch (this) {
            case FIXED_DISCOUNT:
                return FixedAmountVoucher.of(voucherId, amount, createdAt);
            case PERCENT_DISCOUNT:
                return PercentDiscountVoucher.of(voucherId, amount, createdAt);
            default:
                throw new IllegalArgumentException("일치하는 VoucherType이 없습니다");
        }
    }

    public Voucher constructor(long amount, LocalDateTime createdAt) {
        return constructor(null, amount, createdAt);
    }

    public String getDescription() {
        return description;
    }
}
