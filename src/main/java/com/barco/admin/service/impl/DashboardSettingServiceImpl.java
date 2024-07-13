package com.barco.admin.service.impl;

import com.barco.admin.service.DashboardSettingService;
import com.barco.admin.service.LookupDataCacheService;
import com.barco.common.utility.BarcoUtil;
import com.barco.model.dto.request.DashboardSettingRequest;
import com.barco.model.dto.response.AppResponse;
import com.barco.model.dto.response.DashboardSettingResponse;
import com.barco.model.pojo.AppUser;
import com.barco.model.pojo.DashboardSetting;
import com.barco.model.pojo.LookupData;
import com.barco.model.repository.AppUserRepository;
import com.barco.model.repository.DashboardSettingRepository;
import com.barco.model.repository.LookupDataRepository;
import com.barco.model.util.MessageUtil;
import com.barco.model.util.lookup.APPLICATION_STATUS;
import com.barco.model.util.lookup.DASHBOARD_TYPE;
import com.barco.model.util.lookup.GLookup;
import com.barco.model.util.lookup.UI_LOOKUP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Nabeel Ahmed
 */
@Service
public class DashboardSettingServiceImpl implements DashboardSettingService {

    private Logger logger = LoggerFactory.getLogger(DashboardSettingServiceImpl.class);

    @Autowired
    private AppUserRepository appUserRepository;
    @Autowired
    private DashboardSettingRepository dashboardSettingRepository;
    @Autowired
    private LookupDataRepository lookupDataRepository;
    @Autowired
    private LookupDataCacheService lookupDataCacheService;

    /**
     * Method use to add dashboard setting
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse addDashboardSetting(DashboardSettingRequest payload) throws Exception {
        logger.info("Request addDashboardSetting :- " + payload);
        if (BarcoUtil.isNull(payload.getSessionUser().getUsername())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.USERNAME_MISSING);
        }
        Optional<AppUser> adminUser = this.appUserRepository.findByUsernameAndStatus(
            payload.getSessionUser().getUsername(), APPLICATION_STATUS.ACTIVE);
        if (!adminUser.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.APPUSER_NOT_FOUND);
        } else if (BarcoUtil.isNull(payload.getName())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.DASHBOARD_NAME_MISSING);
        } else if (BarcoUtil.isNull(payload.getGroupType())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.DASHBOARD_GROUP_TYPE_MISSING);
        } else if (BarcoUtil.isNull(payload.getDescription())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.DASHBOARD_DESCRIPTION_MISSING);
        } else if (BarcoUtil.isNull(payload.getBoardType())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.DASHBOARD_BOARD_TYPE_MISSING);
        } else if (BarcoUtil.isNull(payload.getDashboardUrl())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.DASHBOARD_DASHBOARD_URL_MISSING);
        } else if (BarcoUtil.isNull(payload.getIframe())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.DASHBOARD_IFRAME_MISSING);
        }
        DashboardSetting dashboardSetting = getDashboardSetting(payload, adminUser.get());
        dashboardSetting = this.dashboardSettingRepository.save(dashboardSetting);
        Optional<LookupData> groupType = this.lookupDataRepository.findByLookupType(payload.getGroupType());
        if (groupType.isPresent()) {
            dashboardSetting.setGroupType(groupType.get());
        }
        return new AppResponse(BarcoUtil.SUCCESS, String.format(MessageUtil.DATA_SAVED, dashboardSetting.getId().toString()));
    }

    /**
     * Method use to update the dashboard setting
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse updateDashboardSetting(DashboardSettingRequest payload) throws Exception {
        logger.info("Request updateDashboardSetting :- " + payload);
        if (BarcoUtil.isNull(payload.getSessionUser().getUsername())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.USERNAME_MISSING);
        }
        Optional<AppUser> adminUser = this.appUserRepository.findByUsernameAndStatus(
            payload.getSessionUser().getUsername(), APPLICATION_STATUS.ACTIVE);
        if (!adminUser.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.APPUSER_NOT_FOUND);
        } else if (BarcoUtil.isNull(payload.getId())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.DASHBOARD_ID_MISSING);
        }  else if (BarcoUtil.isNull(payload.getName())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.DASHBOARD_NAME_MISSING);
        } else if (BarcoUtil.isNull(payload.getGroupType())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.DASHBOARD_GROUP_TYPE_MISSING);
        } else if (BarcoUtil.isNull(payload.getDescription())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.DASHBOARD_DESCRIPTION_MISSING);
        } else if (BarcoUtil.isNull(payload.getBoardType())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.DASHBOARD_BOARD_TYPE_MISSING);
        } else if (BarcoUtil.isNull(payload.getDashboardUrl())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.DASHBOARD_DASHBOARD_URL_MISSING);
        } else if (BarcoUtil.isNull(payload.getIframe())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.DASHBOARD_IFRAME_MISSING);
        }
        Optional<DashboardSetting> dashboardSetting = this.dashboardSettingRepository.findByIdAndUsernameAndStatusNot(
            payload.getId(), payload.getSessionUser().getUsername(), APPLICATION_STATUS.DELETE);
        if (!dashboardSetting.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.DASHBOARD_NOT_FOUND);
        }
        dashboardSetting.get().setName(payload.getName());
        Optional<LookupData> groupType = this.lookupDataRepository.findByLookupType(payload.getGroupType());
        if (groupType.isPresent()) {
            dashboardSetting.get().setGroupType(groupType.get());
        }
        dashboardSetting.get().setDescription(payload.getDescription());
        dashboardSetting.get().setBoardType(DASHBOARD_TYPE.getByLookupCode(payload.getBoardType()));
        dashboardSetting.get().setDashboardUrl(payload.getDashboardUrl());
        dashboardSetting.get().setIframe(UI_LOOKUP.getByLookupCode(payload.getIframe()));
        dashboardSetting.get().setUpdatedBy(adminUser.get());
        if (!BarcoUtil.isNull(payload.getStatus())) {
            dashboardSetting.get().setStatus(APPLICATION_STATUS.getByLookupCode(payload.getStatus()));
        }
        this.dashboardSettingRepository.save(dashboardSetting.get());
        return new AppResponse(BarcoUtil.SUCCESS, String.format(MessageUtil.DATA_UPDATE, payload.getId().toString()));
    }

    /**
     * Method use to fetch all dashboard setting
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse fetchAllDashboardSetting(DashboardSettingRequest payload) throws Exception {
        logger.info("Request fetchAllDashboardSetting :- " + payload);
        if (BarcoUtil.isNull(payload.getSessionUser().getUsername())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.USERNAME_MISSING);
        }
        Optional<AppUser> adminUser = this.appUserRepository.findByUsernameAndStatus(
            payload.getSessionUser().getUsername(), APPLICATION_STATUS.ACTIVE);
        if (!adminUser.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.APPUSER_NOT_FOUND);
        }
        List<DashboardSetting> dashboardSettings;
        if (!BarcoUtil.isNull(payload.getStartDate()) && !BarcoUtil.isNull(payload.getEndDate())) {
            Timestamp startDate = Timestamp.valueOf(payload.getStartDate() + BarcoUtil.START_DATE);
            Timestamp endDate = Timestamp.valueOf(payload.getEndDate() + BarcoUtil.END_DATE);
            dashboardSettings = this.dashboardSettingRepository.findAllByDateCreatedBetweenAndUsernameAndStatusNot(
                startDate, endDate, payload.getSessionUser().getUsername(), APPLICATION_STATUS.DELETE);
        } else {
            dashboardSettings = this.dashboardSettingRepository.findAllByCreatedByAndStatusNot(adminUser.get(), APPLICATION_STATUS.DELETE);
        }
        List<DashboardSettingResponse> dashboardSettingResponses = dashboardSettings.stream()
            .map(dashboardSetting -> {
                DashboardSettingResponse dashboardSettingResponse = this.getDashboardSettingResponse(dashboardSetting);
                dashboardSettingResponse.setBoardType(GLookup.getGLookup(this.lookupDataCacheService
                    .getChildLookupDataByParentLookupTypeAndChildLookupCode(DASHBOARD_TYPE.getName(),
                        Long.valueOf(dashboardSetting.getBoardType().getLookupCode()))));
                if (!BarcoUtil.isNull(dashboardSetting.getGroupType())) {
                    LookupData lookupData = dashboardSetting.getGroupType();
                    dashboardSettingResponse.setGroupType(new GLookup(lookupData.getLookupType(),
                        lookupData.getLookupCode().toString(), lookupData.getLookupValue()));
                }
                return dashboardSettingResponse;
            }).collect(Collectors.toList());
        return new AppResponse(BarcoUtil.SUCCESS, MessageUtil.DATA_FETCH_SUCCESSFULLY, dashboardSettingResponses);
    }

    /**
     * Method use to fetch dashboard by dashboard id
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse fetchDashboardSettingById(DashboardSettingRequest payload) throws Exception {
        logger.info("Request fetchDashboardSettingById :- " + payload);
        if (BarcoUtil.isNull(payload.getSessionUser().getUsername())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.USERNAME_MISSING);
        }
        Optional<AppUser> adminUser = this.appUserRepository.findByUsernameAndStatus(
            payload.getSessionUser().getUsername(), APPLICATION_STATUS.ACTIVE);
        if (!adminUser.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.APPUSER_NOT_FOUND);
        } else if (BarcoUtil.isNull(payload.getId())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.DASHBOARD_ID_MISSING);
        }
        Optional<DashboardSetting> dashboardSetting = this.dashboardSettingRepository.findByIdAndUsernameAndStatusNot(
            payload.getId(), payload.getSessionUser().getUsername(), APPLICATION_STATUS.DELETE);
        if (!dashboardSetting.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.DASHBOARD_NOT_FOUND);
        }
        DashboardSettingResponse dashboardSettingResponse = getDashboardSettingResponse(dashboardSetting.get());
        dashboardSettingResponse.setBoardType(GLookup.getGLookup(this.lookupDataCacheService.getChildLookupDataByParentLookupTypeAndChildLookupCode(
            DASHBOARD_TYPE.getName(), Long.valueOf(dashboardSetting.get().getBoardType().getLookupCode()))));
        if (!BarcoUtil.isNull(dashboardSetting.get().getGroupType())) {
            LookupData lookupData = dashboardSetting.get().getGroupType();
            dashboardSettingResponse.setGroupType(new GLookup(lookupData.getLookupType(),
                lookupData.getLookupCode().toString(), lookupData.getLookupValue()));
        }
        return new AppResponse(BarcoUtil.SUCCESS, MessageUtil.DATA_FETCH_SUCCESSFULLY, dashboardSettingResponse);
    }

    /**
     * Method use to fetch all dashboard by group
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse fetchAllDashboardSettingByGroup(DashboardSettingRequest payload) throws Exception {
        logger.info("Request fetchAllDashboardSettingByGroup :- " + payload);
        if (BarcoUtil.isNull(payload.getSessionUser().getUsername())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.USERNAME_MISSING);
        }
        Optional<AppUser> adminUser = this.appUserRepository.findByUsernameAndStatus(
            payload.getSessionUser().getUsername(), APPLICATION_STATUS.ACTIVE);
        if (!adminUser.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.APPUSER_NOT_FOUND);
        }
        List<DashboardSetting> dashboardSettings;
        if (!BarcoUtil.isNull(payload.getStartDate()) && !BarcoUtil.isNull(payload.getEndDate())) {
            Timestamp startDate = Timestamp.valueOf(payload.getStartDate() + BarcoUtil.START_DATE);
            Timestamp endDate = Timestamp.valueOf(payload.getEndDate() + BarcoUtil.END_DATE);
            dashboardSettings = this.dashboardSettingRepository.findAllByDateCreatedBetweenAndUsernameAndStatusNot(
                startDate, endDate, payload.getSessionUser().getUsername(), APPLICATION_STATUS.DELETE);
        } else {
            dashboardSettings = this.dashboardSettingRepository.findAllByCreatedByAndStatusNot(adminUser.get(), APPLICATION_STATUS.DELETE);
        }
        return new AppResponse(BarcoUtil.SUCCESS, MessageUtil.DATA_FETCH_SUCCESSFULLY, dashboardSettings.stream()
            .filter(dashboardSetting -> !BarcoUtil.isNull(dashboardSetting.getGroupType())
                && dashboardSetting.getStatus().equals(APPLICATION_STATUS.ACTIVE))
            .map(dashboardSetting -> {
                DashboardSettingResponse dashboardSettingResponse = getDashboardSettingResponse(dashboardSetting);
                dashboardSettingResponse.setBoardType(GLookup.getGLookup(this.lookupDataCacheService
                    .getChildLookupDataByParentLookupTypeAndChildLookupCode(DASHBOARD_TYPE.getName(),
                        Long.valueOf(dashboardSetting.getBoardType().getLookupCode()))));
                LookupData lookupData = dashboardSetting.getGroupType();
                dashboardSettingResponse.setGroupType(new GLookup(lookupData.getLookupType(),
                        lookupData.getLookupCode().toString(), lookupData.getLookupValue()));
                return dashboardSettingResponse;
            }).collect(Collectors.groupingBy(dashboardSetting -> (String) dashboardSetting.getGroupType().getLookupValue(), Collectors.toList())));
    }

    /**
     * Method use to delete the dashboard by id
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse deleteDashboardSettingById(DashboardSettingRequest payload) throws Exception {
        logger.info("Request deleteDashboardSettingById :- " + payload);
        if (BarcoUtil.isNull(payload.getSessionUser().getUsername())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.USERNAME_MISSING);
        }
        Optional<AppUser> adminUser = this.appUserRepository.findByUsernameAndStatus(
            payload.getSessionUser().getUsername(), APPLICATION_STATUS.ACTIVE);
        if (!adminUser.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.APPUSER_NOT_FOUND);
        } else if (BarcoUtil.isNull(payload.getId())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.DASHBOARD_ID_MISSING);
        }
        Optional<DashboardSetting> dashboardSetting = this.dashboardSettingRepository.findByIdAndUsernameAndStatusNot(
            payload.getId(), payload.getSessionUser().getUsername(), APPLICATION_STATUS.DELETE);
        if (!dashboardSetting.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.DASHBOARD_NOT_FOUND);
        }
        dashboardSetting.get().setStatus(APPLICATION_STATUS.DELETE);
        this.dashboardSettingRepository.save(dashboardSetting.get());
        return new AppResponse(BarcoUtil.SUCCESS, String.format(MessageUtil.DATA_DELETED, payload.getId().toString()));
    }

    /**
     * Method use to delete all dashboard
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse deleteAllDashboardSetting(DashboardSettingRequest payload) throws Exception {
        logger.info("Request deleteAllDashboardSetting :- " + payload);
        if (BarcoUtil.isNull(payload.getSessionUser().getUsername())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.USERNAME_MISSING);
        }
        Optional<AppUser> appUser = this.appUserRepository.findByUsernameAndStatus(
            payload.getSessionUser().getUsername(), APPLICATION_STATUS.ACTIVE);
        if (!appUser.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.APPUSER_NOT_FOUND);
        } else if (BarcoUtil.isNull(payload.getIds())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.IDS_MISSING);
        }
        List<DashboardSetting> dashboardSettings = this.dashboardSettingRepository.findAllByIdIn(payload.getIds());
        dashboardSettings.forEach(dashboardSetting -> dashboardSetting.setStatus(APPLICATION_STATUS.DELETE));
        this.dashboardSettingRepository.saveAll(dashboardSettings);
        return new AppResponse(BarcoUtil.SUCCESS, String.format(MessageUtil.DATA_DELETED, ""), payload);
    }
}
