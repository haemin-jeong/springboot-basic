package org.prgrms.dev.command;

import org.prgrms.dev.io.Input;
import org.prgrms.dev.io.Output;
import org.prgrms.dev.voucher.domain.VoucherDto;
import org.prgrms.dev.voucher.exception.InvalidArgumentException;
import org.prgrms.dev.voucher.service.VoucherService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class CreateCommand implements Command {
    private static final Logger logger = LoggerFactory.getLogger(CreateCommand.class);
    private static final String CURSOR = "> ";

    @Override
    public boolean execute(Input input, Output output, VoucherService voucherService) {
        try {
            output.selectVoucherType();
            String voucherType = input.input(CURSOR);
            long discount = Long.parseLong((input.input("타입에 맞는 할인 정보를 입력하세요. " + CURSOR)));
            VoucherDto voucherDto = new VoucherDto(UUID.randomUUID(), voucherType, discount);
            voucherService.createVoucher(voucherDto);
        } catch (NumberFormatException | InvalidArgumentException e) {
            logger.error(e.getMessage());
            output.printInvalidNumber();
        } catch (IllegalArgumentException e) {
            logger.error(e.getMessage());
            output.printInvalidVoucherType();
        }
        return true;
    }
}
