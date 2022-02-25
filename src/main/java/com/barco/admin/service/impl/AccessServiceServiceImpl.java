package com.barco.admin.service.impl;

import com.barco.admin.service.IAccessServiceService;
import com.barco.common.utility.ApplicationConstants;
import com.barco.common.utility.BarcoUtil;
import com.barco.model.dto.AccessServiceDto;
import com.barco.model.dto.ResponseDTO;
import com.barco.model.enums.ApiCode;
import com.barco.model.enums.Status;
import com.barco.model.pojo.AccessService;
import com.barco.model.repository.AccessServiceRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Nabeel Ahmed
 */
@Service
@Transactional
public class AccessServiceServiceImpl implements IAccessServiceService {

    public Logger logger = LogManager.getLogger(AccessServiceServiceImpl.class);

    @Autowired
    private AccessServiceRepository accessServiceRepository;

    @Override
    public ResponseDTO createAccessService(AccessServiceDto accessServiceDto) {
        if (BarcoUtil.isNull(accessServiceDto.getCreatedBy())) {
            return new ResponseDTO(ApiCode.INVALID_REQUEST, ApplicationConstants.ACCESS_SERVICE_CREATED_BY_MISSING);
        } else if (BarcoUtil.isNull(accessServiceDto.getStatus())) {
            return new ResponseDTO(ApiCode.INVALID_REQUEST, ApplicationConstants.ACCESS_SERVICE_STATUS_MISSING);
        } else if (BarcoUtil.isNull(accessServiceDto.getServiceName())) {
            return new ResponseDTO(ApiCode.INVALID_REQUEST, ApplicationConstants.ACCESS_SERVICE_SERVICE_NAME_MISSING);
        } else if (BarcoUtil.isNull(accessServiceDto.getDescription())) {
            return new ResponseDTO(ApiCode.INVALID_REQUEST, ApplicationConstants.ACCESS_SERVICE_DESCRIPTION_MISSING);
        } else if (this.accessServiceRepository.findByServiceName(accessServiceDto.getServiceName()).isPresent()) {
            return new ResponseDTO(ApiCode.INVALID_REQUEST, ApplicationConstants.ACCESS_SERVICE_SERVICE_NAME_ALREADY_EXIST);
        }
        AccessService accessService = new AccessService(accessServiceDto.getCreatedBy(), accessServiceDto.getStatus(),
            accessServiceDto.getServiceName(), accessServiceDto.getDescription());
        this.accessServiceRepository.saveAndFlush(accessService);
        accessServiceDto.setServiceId(accessService.getServiceId());
        return new ResponseDTO(ApiCode.SUCCESS, ApplicationConstants.SUCCESS_MSG, accessServiceDto);
    }

    @Override
    public ResponseDTO statusChange(Long accessServiceId, Long appUserId, Status status) {
        // get the storage attache with task
        Optional<AccessService> accessService = this.accessServiceRepository.findById(accessServiceId);
        if (accessService.isPresent()) {
            // active storage if storage disable
            accessService.get().setStatus(status);
            accessService.get().setModifiedBy(appUserId);
            return new ResponseDTO(ApiCode.SUCCESS, ApplicationConstants.SUCCESS_MSG);
        }
        return new ResponseDTO(ApiCode.INVALID_REQUEST, ApplicationConstants.HTTP_404_MSG);
    }

    @Override
    public ResponseDTO getAllAccessService() {
        return new ResponseDTO(ApiCode.SUCCESS, ApplicationConstants.SUCCESS_MSG,
            this.accessServiceRepository.findAllByStatus(Status.Active)
                .stream().map(accessService -> {
                    AccessServiceDto accessServiceDto = new AccessServiceDto();
                    accessServiceDto.setServiceId(accessService.getServiceId());
                    accessServiceDto.setServiceName(accessService.getServiceName());
                    accessServiceDto.setDescription(accessService.getDescription());
                    accessServiceDto.setModifiedAt(accessService.getModifiedAt());
                    accessServiceDto.setModifiedBy(accessService.getModifiedBy());
                    accessServiceDto.setCreatedAt(accessService.getCreatedAt());
                    accessServiceDto.setCreatedBy(accessService.getCreatedBy());
                    accessServiceDto.setStatus(accessService.getStatus());
                    return accessServiceDto;
            }).collect(Collectors.toList()));
    }
}