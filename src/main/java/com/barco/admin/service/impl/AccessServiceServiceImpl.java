package com.barco.admin.service.impl;

import com.barco.admin.repository.AccessServiceRepository;
import com.barco.admin.service.IAccessServiceService;
import com.barco.common.utility.ApplicationConstants;
import com.barco.model.dto.AccessServiceDto;
import com.barco.model.dto.ResponseDTO;
import com.barco.model.enums.ApiCode;
import com.barco.model.enums.Status;
import com.barco.model.pojo.AccessService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Nabeel Ahmed
 */
@Service
@Transactional
@Scope("prototype")
public class AccessServiceServiceImpl implements IAccessServiceService {

    public Logger logger = LogManager.getLogger(AccessServiceServiceImpl.class);

    @Autowired
    private AccessServiceRepository accessServiceRepository;

    @Override
    public ResponseDTO createAccessService(AccessServiceDto accessServiceDto) throws Exception {
        AccessService accessService = new AccessService();
        accessService.setServiceName(accessServiceDto.getServiceName());
        accessService.setInternalServiceName(accessServiceDto.getInternalServiceName());
        accessService.setStatus(Status.Active);
        accessService.setCreatedBy(accessServiceDto.getCreatedBy());
        this.accessServiceRepository.saveAndFlush(accessService);
        accessServiceDto.setId(accessService.getId());
        return new ResponseDTO(ApiCode.SUCCESS, ApplicationConstants.SUCCESS_MSG, accessServiceDto);
    }

    @Override
    public ResponseDTO statusChange(Long accessServiceId, Long appUserId, Status accessServiceStatus) throws Exception {
        // get the storage attache with task
        Optional<AccessService> accessService = this.accessServiceRepository.findByIdAndCreatedByAndStatusNot(accessServiceId, appUserId, Status.Delete);
        if (accessService.isPresent() && accessServiceStatus.equals(Status.Active)) {
            // active storage if storage disable
            accessService.get().setStatus(accessServiceStatus);
            accessService.get().setModifiedBy(appUserId);
            return new ResponseDTO(ApiCode.SUCCESS, ApplicationConstants.SUCCESS_MSG);
        } else if (accessService.isPresent() && (accessServiceStatus.equals(Status.Delete) || accessServiceStatus.equals(Status.Inactive))) {
            Long userAttacheCount = this.accessServiceRepository.countByAccessServiced(accessServiceId);
            if (userAttacheCount > 0) {
                return new ResponseDTO(ApiCode.INVALID_REQUEST, ApplicationConstants.ACCESS_SERVICE_ATTACHE_WITH_USERS);
            } else {
                // active storage if storage disable
                accessService.get().setStatus(accessServiceStatus);
                accessService.get().setModifiedBy(appUserId);
                return new ResponseDTO(ApiCode.SUCCESS, ApplicationConstants.SUCCESS_MSG);
            }
        }
        return new ResponseDTO(ApiCode.INVALID_REQUEST, ApplicationConstants.HTTP_404_MSG);
    }

    @Override
    public ResponseDTO getAllAccessService() throws Exception {
        List<AccessServiceDto> accessServiceList = this.accessServiceRepository.findAllByStatus(Status.Active)
            .stream().map(accessService -> {
                AccessServiceDto accessServiceDto = new AccessServiceDto();
                accessServiceDto.setId(accessService.getId());
                accessServiceDto.setServiceName(accessService.getServiceName());
                accessServiceDto.setInternalServiceName(accessService.getInternalServiceName());
                accessServiceDto.setModifiedAt(accessService.getModifiedAt());
                accessServiceDto.setModifiedBy(accessService.getModifiedBy());
                accessServiceDto.setCreatedAt(accessService.getCreatedAt());
                accessServiceDto.setCreatedBy(accessService.getCreatedBy());
                accessServiceDto.setStatus(accessService.getStatus());
                return accessServiceDto;
        }).collect(Collectors.toList());
        return new ResponseDTO(ApiCode.SUCCESS, ApplicationConstants.SUCCESS_MSG, accessServiceList);
    }

}
