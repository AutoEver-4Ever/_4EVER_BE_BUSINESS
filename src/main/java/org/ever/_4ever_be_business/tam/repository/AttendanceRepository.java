package org.ever._4ever_be_business.tam.repository;

import org.ever._4ever_be_business.tam.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, String>, AttendanceRepositoryCustom {
    /**
     * 직원 ID로 모든 출퇴근 기록 조회 (근무일 내림차순)
     *
     * @param employeeId 직원 ID
     * @return 출퇴근 기록 목록
     */
    List<Attendance> findAllByEmployeeIdOrderByWorkDateDesc(String employeeId);
}
