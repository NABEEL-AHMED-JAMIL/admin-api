package com.barco.admin.service;

import com.barco.model.dto.StorageDetailDto;
import com.barco.model.dto.ResponseDTO;
import com.barco.model.enums.Status;
import com.barco.model.pojo.pagination.PaginationDetail;

public interface IStorageDetailService {

    public ResponseDTO createKey(StorageDetailDto storageDetailDto) throws Exception;

    // get key by id
    public ResponseDTO getKey(Long keyId, Long appUserId) throws Exception;

    // change status task by id
    // Inactive(0), Active(1), Delete(3),
    public ResponseDTO statusChange(Long keyId, Status taskStatus) throws Exception;

    public ResponseDTO findAllKeyByAppUserIdInPagination(Long appUserId, PaginationDetail paginationDetail) throws Exception;

}
