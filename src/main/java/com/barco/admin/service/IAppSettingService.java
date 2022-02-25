package com.barco.admin.service;

import com.barco.model.dto.AppSettingDto;
import com.barco.model.dto.ResponseDTO;
import com.barco.model.dto.SearchTextDto;
import com.barco.model.enums.Status;
import org.springframework.data.domain.Pageable;

/**
 * @author Nabeel Ahmed
 */
public interface IAppSettingService {

    public ResponseDTO createAppSetting(AppSettingDto appSettingDto);

    public ResponseDTO statusChange(Long appSettingId, Long appUserId, Status status);

    public ResponseDTO findAllAppSettingByAppUserIdInPagination(Long appUserId, String startDate, String endDate,
           Pageable paging, SearchTextDto searchTextDto);

}