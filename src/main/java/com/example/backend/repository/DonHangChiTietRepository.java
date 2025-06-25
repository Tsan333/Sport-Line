package com.example.backend.repository;

import com.example.backend.entity.DonHangChiTiet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DonHangChiTietRepository extends JpaRepository<DonHangChiTiet,Integer> {

    @Modifying
    @Query("DELETE FROM DonHangChiTiet d WHERE d.donHang.id = :idDonHang")
    void deleteByDonHangId(@Param("idDonHang") Integer idDonHang);
}
