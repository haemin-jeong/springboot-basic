package org.prgrms.kdt.service;

import lombok.AllArgsConstructor;
import lombok.Builder;
import org.prgrms.kdt.domain.Voucher;
import org.prgrms.kdt.domain.VoucherEntity;
import org.prgrms.kdt.enumType.VoucherStatus;
import org.prgrms.kdt.factory.VoucherFactory;
import org.prgrms.kdt.jdbcRepository.VoucherJdbcRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@AllArgsConstructor
public class VoucherService {

//    @Autowired
//    private final MemoryVoucherRepository memoryVoucherRepository;

    @Autowired
    private final VoucherJdbcRepository voucherJdbcRepository;

    @Autowired
    private VoucherFactory voucherFactory;

    public VoucherEntity createVoucher(int voucherStatus){
        Voucher v = voucherFactory.getDiscounterVoucher(voucherStatus);

        var voucherEntity = VoucherEntity.builder()
                .voucherId(v.getVoucherId())
                .voucherType(Optional.of(v.getType()))
                .discount(Optional.of(v.discountCoupon()))
                .createdAt(LocalDateTime.now())
                .build();
        voucherJdbcRepository.insert(voucherEntity);
        return voucherEntity;
    }




    //
//    public Voucher getVoucher(UUID voucherId) {
//        return memoryVoucherRepository.findById(voucherId)
//                .orElseThrow(() -> new RuntimeException(String.format("Can not find a voucher %s", voucherId)));
//    }
//
//    public List<Voucher> findAll() {
//        return memoryVoucherRepository.findAll();
//    }


}