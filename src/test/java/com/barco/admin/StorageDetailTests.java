package com.barco.admin;

import com.barco.model.dto.ResponseDTO;
import com.barco.model.dto.StorageDetailDto;
import com.barco.model.enums.Status;
import com.barco.model.searchspec.PaginationDetail;
import org.junit.jupiter.api.Test;

public class StorageDetailTests {

    @Test
    void createStorage() {
    }

    @Test
    void getStorageById() {
    }

    @Test
    void statusChange() {
    }

    @Test
    void findAllStorageByAppUserIdInPagination() {
    }

    @Test
    void pingStorage() {
    }

    public ResponseDTO createStorage(StorageDetailDto storageDetailDto) throws Exception {
        return null;
    }

    // get key by id
    public ResponseDTO getStorageById(Long storageId, Long appUserId) throws Exception {
        return null;
    }

    // change status task by id
    // Inactive(0), Active(1), Delete(3),
    public ResponseDTO statusChange(Long storageId, Long appUserId, Status storageStatus) throws Exception {
        return null;
    }

    public ResponseDTO findAllStorageByAppUserIdInPagination(Long appUserId, PaginationDetail paginationDetail) throws Exception {
        return null;
    }

    public ResponseDTO pingStorage(StorageDetailDto storageDetailDto) throws Exception {
        return null;
    }
}
