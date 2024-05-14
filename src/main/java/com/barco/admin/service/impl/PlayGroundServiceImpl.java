package com.barco.admin.service.impl;

import com.barco.admin.service.PlayGroundService;
import com.barco.common.utility.BarcoUtil;
import com.barco.model.dto.request.PlayGroundRequest;
import com.barco.model.dto.response.AppResponse;
import com.barco.model.dto.dform.IDynamicForm;
import com.barco.model.pojo.AppUser;
import com.barco.model.pojo.GenForm;
import com.barco.model.repository.AppUserRepository;
import com.barco.model.repository.GenFormRepository;
import com.barco.model.util.MessageUtil;
import com.barco.model.util.lookup.APPLICATION_STATUS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Nabeel Ahmed
 * play grond service
 */
@Service
public class PlayGroundServiceImpl extends DynamicFormService implements PlayGroundService {

    private Logger logger = LoggerFactory.getLogger(PlayGroundServiceImpl.class);

    @Autowired
    private AppUserRepository appUserRepository;
    @Autowired
    private GenFormRepository genFormRepository;

    /**
     * Method use to fetch all form for test
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse fetchAllFormForPlayGround(PlayGroundRequest payload) throws Exception {
        logger.info("Request fetchAllFormForPlayGround :- " + payload);
        if (BarcoUtil.isNull(payload.getSessionUser().getUsername())) {
            throw new Exception(MessageUtil.USERNAME_MISSING);
        }
        Optional<AppUser> adminUser = this.appUserRepository.findByUsernameAndStatus(
            payload.getSessionUser().getUsername(), APPLICATION_STATUS.ACTIVE);
        if (!adminUser.isPresent()) {
            throw new Exception(MessageUtil.APPUSER_NOT_FOUND);
        }
        Timestamp startDate = Timestamp.valueOf(payload.getStartDate() + BarcoUtil.START_DATE);
        Timestamp endDate = Timestamp.valueOf(payload.getEndDate() + BarcoUtil.END_DATE);
        return new AppResponse(BarcoUtil.SUCCESS, MessageUtil.DATA_FETCH_SUCCESSFULLY,
            this.genFormRepository.findAllByDateCreatedBetweenAndCreatedByAndStatusNotOrderByDateCreatedDesc(
               startDate, endDate, adminUser.get(), APPLICATION_STATUS.DELETE).stream()
               .map(genForm -> {
                    IDynamicForm dynamicForm = new IDynamicForm();
                    dynamicForm.setId(genForm.getId());
                    dynamicForm.setName(genForm.getFormName());
                    return dynamicForm;
            }).collect(Collectors.toList()));
    }

    /**
     * Method use to fetch the form by form id for test
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse fetchFormForPlayGroundByFormId(PlayGroundRequest payload) throws Exception {
        logger.info("Request fetchFormForPlayGroundByFormId :- " + payload);
        if (BarcoUtil.isNull(payload.getSessionUser().getUsername())) {
            throw new Exception(MessageUtil.USERNAME_MISSING);
        }
        Optional<AppUser> adminUser = this.appUserRepository.findByUsernameAndStatus(
            payload.getSessionUser().getUsername(), APPLICATION_STATUS.ACTIVE);
        if (!adminUser.isPresent()) {
            throw new Exception(MessageUtil.APPUSER_NOT_FOUND);
        }
        Optional<GenForm> genForm = this.genFormRepository.findByIdAndCreatedByAndStatus(
            payload.getId(), adminUser.get(), APPLICATION_STATUS.ACTIVE);
        if (!genForm.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.FORM_NOT_FOUND);
        }
        return new AppResponse(BarcoUtil.SUCCESS, MessageUtil.DATA_FETCH_SUCCESSFULLY, getDynamicForm(genForm.get()));
    }

}
