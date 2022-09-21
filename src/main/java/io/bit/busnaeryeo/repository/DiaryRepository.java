package io.bit.busnaeryeo.repository;

import io.bit.busnaeryeo.domain.entity.Diary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DiaryRepository extends JpaRepository<Diary, Long> {

    @Query(value = "SELECT * FROM diary " +
            "WHERE user_id = :id " +
            "ORDER BY created_date DESC " +
            "LIMIT 0,7;"
            ,nativeQuery = true)
    List<Diary> findCurrentTenDiariesByUserId(@Param("id") Long id);

    @Query(value = "SELECT DISTINCT(AVG(sentimental) OVER(PARTITION BY user_id)) as avg_sentimental FROM diary " +
            "WHERE user_id = :id"
            ,nativeQuery = true)
    List<Double> caculateAverageTotalSentimental(@Param("id") Long id);

    @Query(value = "SELECT DISTINCT(AVG(sentimental) OVER(PARTITION BY user_id)) as avg_sentimental FROM diary " +
            "WHERE user_id = :id " +
            "ORDER BY created_date DESC " +
            "LIMIT 0,7;"
            ,nativeQuery = true)
    List<Double> caculateAverageCurrentTenDiariesSentimental(@Param("id") Long id);

    List<Diary> findAllByUserId(Long id);
    Diary findByUserIdAndCreatedDate(Long userId, String date);
//    Diary findByIdAndCreatedDate(Long id, String date);
}
