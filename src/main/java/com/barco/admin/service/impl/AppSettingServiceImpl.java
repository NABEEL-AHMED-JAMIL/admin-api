package com.barco.admin.service.impl;

import com.barco.admin.service.IAppSettingService;
import com.barco.common.utility.ApplicationConstants;
import com.barco.common.utility.BarcoUtil;
import com.barco.model.dto.AppSettingDto;
import com.barco.model.dto.ResponseDTO;
import com.barco.model.dto.SearchTextDto;
import com.barco.model.enums.ApiCode;
import com.barco.model.enums.Status;
import com.barco.model.pojo.AppSetting;
import com.barco.model.repository.AppSettingRepository;
import com.barco.model.service.QueryResultMapper;
import com.barco.model.service.QueryServices;
import com.barco.model.util.PagingUtil;
import com.barco.model.util.QueryUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author Nabeel Ahmed
 */
@Service
@Transactional
public class AppSettingServiceImpl implements IAppSettingService {

    public Logger logger = LogManager.getLogger(AppSettingServiceImpl.class);

    @Autowired
    private AppSettingRepository appSettingRepository;

    @Autowired
    private QueryUtil queryUtil;

    @Autowired
    private QueryServices queryServices;

    @Autowired
    private QueryResultMapper queryResultMapper;


    @Override
    public ResponseDTO createAppSetting(AppSettingDto appSettingDto) {
        if (BarcoUtil.isNull(appSettingDto.getCreatedBy())) {
            return new ResponseDTO(ApiCode.INVALID_REQUEST, ApplicationConstants.APP_SETTING_CREATED_BY_MISSING);
        } else if (BarcoUtil.isNull(appSettingDto.getStatus())) {
            return new ResponseDTO(ApiCode.INVALID_REQUEST, ApplicationConstants.APP_SETTING_STATUS_MISSING);
        } else if (BarcoUtil.isNull(appSettingDto.getSettingKey())) {
            return new ResponseDTO(ApiCode.INVALID_REQUEST, ApplicationConstants.APP_SETTING_KEY_MISSING);
        } else if (BarcoUtil.isNull(appSettingDto.getSettingValue())) {
            return new ResponseDTO(ApiCode.INVALID_REQUEST, ApplicationConstants.APP_SETTING_VALUE_MISSING);
        } else if (BarcoUtil.isNull(appSettingDto.getDescription())) {
            return new ResponseDTO(ApiCode.INVALID_REQUEST, ApplicationConstants.APP_SETTING_DESCRIPTION_MISSING);
        } else if (this.appSettingRepository.findBySettingKey(appSettingDto.getSettingKey()).isPresent()) {
            return new ResponseDTO(ApiCode.INVALID_REQUEST, ApplicationConstants.APP_SETTING_KEY_ALREADY_EXIST);
        }
        AppSetting appSetting = new AppSetting(appSettingDto.getCreatedBy(), appSettingDto.getStatus(),
            appSettingDto.getSettingKey(), appSettingDto.getSettingValue(), appSettingDto.getDescription());
        this.appSettingRepository.saveAndFlush(appSetting);
        appSettingDto.setAppSettingId(appSetting.getAppSettingId());
        return new ResponseDTO(ApiCode.SUCCESS, ApplicationConstants.SUCCESS_MSG, appSettingDto);
    }

    @Override
    public ResponseDTO statusChange(Long appSettingId, Long appUserId, Status status) {
        Optional<AppSetting> appSetting = this.appSettingRepository.findById(appSettingId);
        if (appSetting.isPresent()) {
            appSetting.get().setStatus(status);
            appSetting.get().setModifiedBy(appUserId);
            this.appSettingRepository.saveAndFlush(appSetting.get());
            return new ResponseDTO(ApiCode.SUCCESS, ApplicationConstants.SUCCESS_MSG);
        }
        return new ResponseDTO(ApiCode.INVALID_REQUEST, ApplicationConstants.HTTP_404_MSG);
    }

    @Override
    public ResponseDTO findAllAppSettingByAppUserIdInPagination(Long appUserId, String startDate, String endDate,
        Pageable paging, SearchTextDto searchTextDto) {
        Object countQueryResult = this.queryServices.executeQueryForSingleResult(this.queryUtil.findAllAppSettingByAppUserIdInPagination(
            true, appUserId, startDate, endDate, searchTextDto));
        if (!BarcoUtil.isNull(countQueryResult)) {
            /* fetch Record According to Pagination*/
            List<Object[]> result = this.queryServices.executeQuery(
                this.queryUtil.findAllAppSettingByAppUserIdInPagination(false, appUserId, startDate, endDate, searchTextDto), paging);
            if (!BarcoUtil.isNull(result) && result.size() > 0) {
                return new ResponseDTO(ApiCode.SUCCESS, ApplicationConstants.SUCCESS_MSG,
                this.queryResultMapper.findAllAppSettingByAppUserIdInPagination(result)        ,
                PagingUtil.convertEntityToPagingDTO(Long.valueOf(countQueryResult.toString()), paging));
            }
        }
        return new ResponseDTO(ApiCode.INVALID_REQUEST, ApplicationConstants.HTTP_404_MSG, new ArrayList<>(),
                PagingUtil.convertEntityToPagingDTO(Long.valueOf(countQueryResult.toString()), paging));
    }
}
