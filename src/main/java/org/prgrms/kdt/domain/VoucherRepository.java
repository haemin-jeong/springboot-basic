package org.prgrms.kdt.domain;

import org.prgrms.kdt.strategy.Voucher;

import java.util.Optional;
import java.util.UUID;

public interface VoucherRepository {
    Optional<Voucher> findById(UUID voucherId);
}