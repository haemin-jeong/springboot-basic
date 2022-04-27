package com.prgrms.vouchermanagement.voucher.controller;

import com.prgrms.vouchermanagement.voucher.Voucher;
import com.prgrms.vouchermanagement.voucher.VoucherType;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class VoucherDto {
    private UUID voucherId;
    private long amount;
    private VoucherType voucherType;
    private LocalDateTime createdAt;

    private VoucherDto() {

    }

    public VoucherDto(UUID voucherId, long amount, VoucherType voucherType, LocalDateTime createdAt) {
        this.voucherId = voucherId;
        this.amount = amount;
        this.voucherType = voucherType;
        this.createdAt = createdAt;
    }

    public static VoucherDto from(Voucher voucher) {
        return new VoucherDto(voucher.getVoucherId(), voucher.getAmount(), VoucherType.getVoucherType(voucher), voucher.getCreatedAt());
    }

    public static List<VoucherDto> convertList(List<Voucher> vouchers) {
        List<VoucherDto> voucherDtos = new ArrayList<>();
        vouchers.forEach(voucher -> voucherDtos.add(VoucherDto.from(voucher)));
        return voucherDtos;
    }

    public UUID getVoucherId() {
        return voucherId;
    }

    public long getAmount() {
        return amount;
    }

    public VoucherType getVoucherType() {
        return voucherType;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public static VoucherDto getEmptyVoucherDto() {
        return new VoucherDto();
    }
}