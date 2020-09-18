package com.barco.admin.service.impl;

import com.barco.admin.service.IStorageDetailService;
import com.barco.model.dto.StorageDetailDto;
import com.barco.model.dto.ResponseDTO;
import com.barco.model.enums.Status;
import com.barco.model.pojo.pagination.PaginationDetail;
import com.barco.model.repository.StorageDetailRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
@Scope("prototype")
public class StorageDetailServiceImpl implements IStorageDetailService {

    private Logger logger = LoggerFactory.getLogger(StorageDetailServiceImpl.class);

    @Autowired
    private StorageDetailRepository storageDetailRepository;

    @Override
    public ResponseDTO createKey(StorageDetailDto storageDetailDto) throws Exception {
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

}
