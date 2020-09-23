package com.barco.admin.service.impl;

import com.barco.admin.service.IStorageDetailService;
import com.barco.common.utility.ApplicationConstants;
import com.barco.model.dto.StorageDetailDto;
import com.barco.model.dto.ResponseDTO;
import com.barco.model.enums.ApiCode;
import com.barco.model.enums.Status;
import com.barco.model.pojo.AppUser;
import com.barco.model.pojo.StorageDetail;
import com.barco.model.pojo.pagination.PaginationDetail;
import com.barco.model.repository.AppUserRepository;
import com.barco.model.repository.StorageDetailRepository;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.enums.EnumUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
@Transactional
@Scope("prototype")
public class StorageDetailServiceImpl implements IStorageDetailService {

    private Logger logger = LoggerFactory.getLogger(StorageDetailServiceImpl.class);

    @Autowired
    private AppUserRepository appUserRepository;
    @Autowired
    private StorageDetailRepository storageDetailRepository;

    @Override // method use for create and update both
    public ResponseDTO createKey(StorageDetailDto storageDetailDto) throws Exception {
        if(StringUtils.isEmpty(storageDetailDto.getStorageKeyName())) {
            return new ResponseDTO(ApiCode.INVALID_REQUEST, ApplicationConstants.STORAGE_KEY_NAME_MISSING);
        } else if (this.storageDetailRepository.findByStorageKeyNameAndStatus(storageDetailDto.getStorageKeyName(), Status.Active).isPresent()) {
            return new ResponseDTO(ApiCode.INVALID_REQUEST, ApplicationConstants.STORAGE_KEY_ALREADY_EXIST);
        } else if (storageDetailDto.getStorageDetailJson() == null) {
            return new ResponseDTO(ApiCode.INVALID_REQUEST, ApplicationConstants.STORAGE_KEY_JSON_MISSING);
        } else if (storageDetailDto.getKeyType() == null) {
            return new ResponseDTO(ApiCode.INVALID_REQUEST, ApplicationConstants.STORAGE_KEY_TYPE_MISSING);
        }
        Optional<AppUser> appUser = this.appUserRepository.findByIdAndStatus(storageDetailDto.getCreatedBy(), Status.Active);
        if (!appUser.isPresent()) {
            return new ResponseDTO(ApiCode.INVALID_REQUEST, ApplicationConstants.USER_NOT_FOUND);
        }
        StorageDetail storageDetail = null;
        if (storageDetailDto.getId() != null) {
            storageDetail = this.storageDetailRepository.findById(storageDetailDto.getId()).get();
            storageDetail.setModifiedBy(appUser.get().getId());
            if (storageDetailDto.getStatus() != null) {
                storageDetail.setStatus(storageDetailDto.getStatus());
            }
        } else {
            storageDetail = new StorageDetail();
            storageDetail.setCreatedBy(appUser.get().getId());
            storageDetail.setStatus(Status.Active);
        }
        storageDetail.setStorageKeyName(storageDetailDto.getStorageKeyName());
        storageDetail.setStorageDetailJson(storageDetailDto.getStorageDetailJson());
        storageDetail.setKeyType(storageDetailDto.getKeyType());

        this.storageDetailRepository.save(storageDetail);

        return null;
    }

    @Override
    public ResponseDTO getKey(Long keyId, Long appUserId) throws Exception {
        return null;
    }

    @Override
    public ResponseDTO statusChange(Long keyId, Status taskStatus) throws Exception {
        return null;
    }

    @Override
    public ResponseDTO findAllKeyByAppUserIdInPagination(Long appUserId, PaginationDetail paginationDetail) throws Exception {
        return null;
    }

    @Override
    public ResponseDTO pingStorage(StorageDetailDto storageDetailDto) throws Exception {
        return null;
    }

}
