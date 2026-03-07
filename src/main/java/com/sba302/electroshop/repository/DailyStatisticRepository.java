package com.sba302.electroshop.repository;

import com.sba302.electroshop.entity.DailyStatistic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DailyStatisticRepository extends JpaRepository<DailyStatistic, Long> {
    Optional<DailyStatistic> findByStatisticDate(LocalDate date);

    List<DailyStatistic> findByStatisticDateBetweenOrderByStatisticDateAsc(LocalDate startDate, LocalDate endDate);
}
