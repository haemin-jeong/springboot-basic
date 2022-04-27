package com.prgrms.vouchermanagement.wallet;

import com.prgrms.vouchermanagement.customer.Customer;
import com.prgrms.vouchermanagement.customer.CustomerService;
import com.prgrms.vouchermanagement.voucher.Voucher;
import com.prgrms.vouchermanagement.voucher.service.VoucherService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.prgrms.vouchermanagement.voucher.VoucherType.FIXED_DISCOUNT;
import static com.prgrms.vouchermanagement.voucher.VoucherType.PERCENT_DISCOUNT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VoucherWalletServiceTest {

    @Mock
    VoucherWalletRepository voucherWalletRepository;

    @Mock
    VoucherService voucherService;

    @Mock
    CustomerService customerService;

    @Test
    @DisplayName("Customer 지갑에 Voucher를 추가한다.")
    void addVoucherToWalletTest() {
        // given
        VoucherWalletService voucherWalletService = new VoucherWalletService(voucherWalletRepository, voucherService, customerService);
        UUID voucherId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        when(customerService.isRegisteredCustomer(customerId)).thenReturn(true);
        when(voucherService.isRegisteredVoucher(voucherId)).thenReturn(true);

        // when
        voucherWalletService.addVoucherToWallet(customerId, voucherId);

        // then
        verify(voucherWalletRepository).save(any());
    }

    @Test
    @DisplayName("지갑에 Voucher를 추가하는데 존재하지 않는 customerId를 전달하면 예외가 발생한다.")
    void addVoucherWalletNotExistsCustomerTest() {
        // given
        VoucherWalletService voucherWalletService = new VoucherWalletService(voucherWalletRepository, voucherService, customerService);
        UUID voucherId = UUID.randomUUID();
        UUID wrongCustomerId = UUID.randomUUID();
        when(customerService.isRegisteredCustomer(wrongCustomerId)).thenReturn(false);
        when(voucherService.isRegisteredVoucher(voucherId)).thenReturn(true);

        // then
        assertThatThrownBy(() -> {
            // when
            voucherWalletService.addVoucherToWallet(wrongCustomerId, voucherId);
        })
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("등록되지 않은 Customer입니다.");

        verify(voucherWalletRepository, times(0)).save(any());
    }

    @Test
    @DisplayName("지갑에 Voucher를 추가하는데 존재하지 않는 voucherId를 전달하면 예외가 발생한다.")
    void addVoucherWalletNotExistsVoucherTest() {
        // given
        VoucherWalletService voucherWalletService = new VoucherWalletService(voucherWalletRepository, voucherService, customerService);
        UUID wrongVoucherId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        when(voucherService.isRegisteredVoucher(wrongVoucherId)).thenReturn(false);

        // then
        assertThatThrownBy(() -> {
            // when
            voucherWalletService.addVoucherToWallet(customerId, wrongVoucherId);
        })
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("등록되지 않은 Voucher입니다.");

        verify(voucherWalletRepository, times(0)).save(any());
    }

    @Test
    @DisplayName("wallet에 있는 voucher를 삭제한다.")
    void removeVoucherInWalletTest() {
        // given
        VoucherWalletService voucherWalletService = new VoucherWalletService(voucherWalletRepository, voucherService, customerService);
        UUID walletId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        UUID voucherId = UUID.randomUUID();
        Wallet wallet = Wallet.of(walletId, customerId, voucherId);
        when(voucherWalletRepository.findWallet(walletId)).thenReturn(Optional.of(wallet));

        // when
        voucherWalletService.removeVoucherInWallet(walletId);

        // then
        verify(voucherWalletRepository).removeWallet(walletId);
    }

    @Test
    @DisplayName("존재하지 않는 walletId로 wallet에 있는 voucher를 삭제하려하면 예외가 발생한다.")
    void removeVoucherInWalletWrongWalletIdTest() {
        // given
        VoucherWalletService voucherWalletService = new VoucherWalletService(voucherWalletRepository, voucherService, customerService);
        UUID wrongWalletId = UUID.randomUUID();
        when(voucherWalletRepository.findWallet(wrongWalletId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> {
            voucherWalletService.removeVoucherInWallet(wrongWalletId);
        }).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("등록되지 않은 Wallet입니다.");

        verify(voucherWalletRepository, times(0)).removeWallet(wrongWalletId);
    }

    @Test
    @DisplayName("특정 Customer가 가지고 있는 Voucher를 조회한다.")
    void findVoucherByCustomerTest() {
        // given
        VoucherWalletService voucherWalletService = new VoucherWalletService(voucherWalletRepository, voucherService, customerService);
        UUID customerId = UUID.randomUUID();
        Voucher voucher1 = FIXED_DISCOUNT.constructor(UUID.randomUUID(), 5000, LocalDateTime.now());
        Voucher voucher2 = PERCENT_DISCOUNT.constructor(UUID.randomUUID(), 50, LocalDateTime.now());
        Voucher voucher3 = FIXED_DISCOUNT.constructor(UUID.randomUUID(), 55000, LocalDateTime.now());
        when(customerService.isRegisteredCustomer(customerId)).thenReturn(true);
        when(voucherWalletRepository.findVoucherByCustomer(customerId)).thenReturn(List.of(voucher1, voucher2, voucher3));

        // when
        List<Voucher> vouchers = voucherWalletService.findVoucherByCustomer(customerId);

        // then
        assertThat(vouchers.size()).isEqualTo(3);
        assertThat(vouchers).contains(voucher1, voucher2, voucher3);
    }

    @Test
    @DisplayName("Customer가 가지고 있는 Voucher를 조회하는데 존재하지 않는 customerId로 조회하면 예외가 발생한다. ")
    void findVoucherByCustomerWrongCustomerIdTest() {
        // given
        VoucherWalletService voucherWalletService = new VoucherWalletService(voucherWalletRepository, voucherService, customerService);
        UUID wrongCustomerId = UUID.randomUUID();
        when(customerService.isRegisteredCustomer(wrongCustomerId)).thenReturn(false);

        // then
        assertThatThrownBy(() -> {
            // when
            List<Voucher> vouchers = voucherWalletService.findVoucherByCustomer(wrongCustomerId);
        })
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("등록되지 않은 Customer입니다.");

        verify(voucherWalletRepository, times(0)).findVoucherByCustomer(wrongCustomerId);
    }

    @Test
    @DisplayName("특정 Voucher를 가지고 있는 Customer를 조회한다.")
    void findCustomerByVoucherTest() {
        // given
        VoucherWalletService voucherWalletService = new VoucherWalletService(voucherWalletRepository, voucherService, customerService);
        Customer customer1 = Customer.of(UUID.randomUUID(), "aaa", "aaa@gmail.com", LocalDateTime.now());
        Customer customer2 = Customer.of(UUID.randomUUID(), "bbb", "bbb@gmail.com", LocalDateTime.now());
        Customer customer3 = Customer.of(UUID.randomUUID(), "ccc", "ccc@gmail.com", LocalDateTime.now());
        UUID voucherId = UUID.randomUUID();
        when(voucherService.isRegisteredVoucher(voucherId)).thenReturn(true);
        when(voucherWalletRepository.findCustomerByVoucher(voucherId)).thenReturn(List.of(customer1, customer2, customer3));

        // when
        List<Customer> customers = voucherWalletService.findCustomerByVoucher(voucherId);

        // then
        assertThat(customers.size()).isEqualTo(3);
        assertThat(customers).contains(customer1, customer2, customer3);
    }

    @Test
    @DisplayName("특정 Voucher를 가지고 있는 Customer를 조회하는데 존재하지 않는 voucherId로 조회하면 예외가 발생한다. ")
    void findCustomerByVoucherWrongVoucherIdTest() {
        // given
        VoucherWalletService voucherWalletService = new VoucherWalletService(voucherWalletRepository, voucherService, customerService);
        UUID wrongVoucherId = UUID.randomUUID();
        when(voucherService.isRegisteredVoucher(wrongVoucherId)).thenReturn(false);

        // then
        assertThatThrownBy(() -> {
            // when
            List<Customer> customers = voucherWalletService.findCustomerByVoucher(wrongVoucherId);
        })
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("등록되지 않은 Voucher입니다.");

        verify(voucherWalletRepository, times(0)).findCustomerByVoucher(wrongVoucherId);
    }
}