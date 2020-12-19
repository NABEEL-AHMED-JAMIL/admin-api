package com.barco.admin.repository;

import com.barco.model.enums.Status;
import com.barco.model.pojo.AccessService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * @author Nabeel Ahmed
 */
@Repository
public interface AccessServiceRepository extends JpaRepository<AccessService, Long> {


    Optional<AccessService> findByIdAndStatus(Long id, Status status);
}
