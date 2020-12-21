package com.barco.admin.repository;

import com.barco.model.enums.Status;
import com.barco.model.pojo.AccessService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
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

    @Query(value = "select access_service.* from access_service\n" +
            "inner join user_access_service on access_service.id = user_access_service.service_id\n" +
            "inner join app_user on app_user.id = user_access_service.user_id\n" +
            "where user_access_service.user_id = 1000 and user_access_service.service_id = 1000", nativeQuery = true)
    Optional<AccessService> findByIdAndUserAccess(Long id, Long accessId);
}
