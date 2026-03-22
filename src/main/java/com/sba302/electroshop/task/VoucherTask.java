package com.sba302.electroshop.task;

import com.sba302.electroshop.repository.VoucherRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class VoucherTask {

    private final VoucherRepository voucherRepository;

    /**
     * Đồng bộ trạng thái Voucher mỗi phút.
     * Deactive voucher hết hạn hoặc hết lượt dùng.
     * Active voucher đến hạn dùng.
     */
    @Scheduled(cron = "0 * * * * *")
    @Transactional
    public void syncVoucherStatus() {
        log.debug("Starting voucher status synchronization task...");
        LocalDateTime now = LocalDateTime.now();

        int expired = voucherRepository.deactivateExpiredVouchers(now);
        if (expired > 0) {
            log.info("Deactivated {} expired vouchers", expired);
        }

        int reachedLimit = voucherRepository.deactivateVouchersReachedLimit();
        if (reachedLimit > 0) {
            log.info("Deactivated {} vouchers that reached usage limit", reachedLimit);
        }

        int notStarted = voucherRepository.deactivateNotStartedVouchers(now);
        if (notStarted > 0) {
            log.info("Deactivated {} vouchers that haven't started yet", notStarted);
        }

        int activated = voucherRepository.activateValidVouchers(now);
        if (activated > 0) {
            log.info("Activated {} valid vouchers", activated);
        }
    }
}
