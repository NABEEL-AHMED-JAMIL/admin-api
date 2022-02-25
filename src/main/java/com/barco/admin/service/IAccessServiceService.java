package com.barco.admin.service;

import com.barco.model.dto.AccessServiceDto;
import com.barco.model.dto.ResponseDTO;
import com.barco.model.enums.Status;

/**
 * @author Nabeel Ahmed
 */
public interface IAccessServiceService {

    public ResponseDTO createAccessService(AccessServiceDto accessService);

    public ResponseDTO statusChange(Long accessServiceId, Long appUserId, Status status);

    public ResponseDTO getAllAccessService();

}