package com.barco.admin.service;

import com.barco.model.dto.AccessServiceDto;
import com.barco.model.dto.ResponseDTO;
import com.barco.model.enums.Status;

/**
 * @author Nabeel Ahmed
 */
public interface IAccessServiceService {

    ResponseDTO createAccessService(AccessServiceDto accessService) throws Exception;

    // change status task by id (Inactive(0), Active(1), Delete(3))
    ResponseDTO statusChange(Long accessServiceId, Long appUserId, Status accessServiceStatus) throws Exception;

    ResponseDTO getAllAccessService() throws Exception;
}
