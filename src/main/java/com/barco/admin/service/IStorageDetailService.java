package com.barco.admin.service;

import com.barco.model.dto.PaggingDto;
import com.barco.model.dto.SearchTextDto;
import com.barco.model.dto.StorageDetailDto;
import com.barco.model.dto.ResponseDTO;
import com.barco.model.enums.Status;
import com.barco.model.searchspec.PaginationDetail;

/**
 * @author Nabeel Ahmed
 */
public interface IStorageDetailService {

    public ResponseDTO createStorage(StorageDetailDto storageDetailDto) throws Exception;

    // get key by id
    public ResponseDTO getStorageById(Long storageId, Long appUserId) throws Exception;

    // change status task by id
    // Inactive(0), Active(1), Delete(3),
    public ResponseDTO statusChange(Long storageId, Long appUserId, Status storageStatus) throws Exception;

    public ResponseDTO findAllStorageByAppUserIdInPagination(PaggingDto pagging, Long adminId, SearchTextDto searchTextDto,
         String startDate, String endDate) throws Exception;

    public ResponseDTO pingStorage(StorageDetailDto storageDetailDto) throws Exception;

}
