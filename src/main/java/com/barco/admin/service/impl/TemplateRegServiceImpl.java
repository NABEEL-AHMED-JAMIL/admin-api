package com.barco.admin.service.impl;

import com.barco.admin.service.LookupDataCacheService;
import com.barco.admin.service.TemplateRegService;
import com.barco.common.utility.BarcoUtil;
import com.barco.model.dto.request.TemplateRegRequest;
import com.barco.model.dto.response.AppResponse;
import com.barco.model.dto.response.TemplateRegResponse;
import com.barco.model.pojo.AppUser;
import com.barco.model.pojo.TemplateReg;
import com.barco.model.repository.AppUserRepository;
import com.barco.model.repository.TemplateRegRepository;
import com.barco.model.util.MessageUtil;
import com.barco.model.util.lookup.APPLICATION_STATUS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Nabeel Ahmed
 * TemplateReg can be email and etc
 */
@Service
public class TemplateRegServiceImpl implements TemplateRegService {

    private Logger logger = LoggerFactory.getLogger(TemplateRegServiceImpl.class);

    @Autowired
    private AppUserRepository appUserRepository;
    @Autowired
    private TemplateRegRepository templateRegRepository;
    @Autowired
    private LookupDataCacheService lookupDataCacheService;

    public TemplateRegServiceImpl() {}

    /**
     * Method use to add the email template in db
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse addTemplateReg(TemplateRegRequest payload) throws Exception {
        logger.info("Request addTemplateReg :- " + payload);
        if (BarcoUtil.isNull(payload.getSessionUser().getUsername())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.USERNAME_MISSING);
        }
        Optional<AppUser> appUser = this.appUserRepository.findByUsernameAndStatus(
            payload.getSessionUser().getUsername(), APPLICATION_STATUS.ACTIVE);
        if (!appUser.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.APPUSER_NOT_FOUND);
        } else if (BarcoUtil.isNull(payload.getTemplateName())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.TEMPLATE_NAME_MISSING);
        } else if (BarcoUtil.isNull(payload.getTemplateContent())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.TEMPLATE_CONTENT_MISSING);
        } else if (this.templateRegRepository.findFirstByTemplateNameAndStatusNot(
            payload.getTemplateName(), APPLICATION_STATUS.DELETE).isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.TEMPLATE_REG_ALREADY_EXIST);
        }
        TemplateReg templateReg = new TemplateReg();
        templateReg.setTemplateName(payload.getTemplateName());
        templateReg.setTemplateContent(payload.getTemplateContent());
        templateReg.setCreatedBy(appUser.get());
        templateReg.setUpdatedBy(appUser.get());
        templateReg.setStatus(APPLICATION_STATUS.ACTIVE);
        this.templateRegRepository.save(templateReg);
        return new AppResponse(BarcoUtil.SUCCESS, String.format(MessageUtil.DATA_SAVED, templateReg.getId()), payload);
    }

    /**
     * Method use to edit the email template in db
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse editTemplateReg(TemplateRegRequest payload) throws Exception {
        logger.info("Request editTemplateReg :- " + payload);
        if (BarcoUtil.isNull(payload.getSessionUser().getUsername())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.USERNAME_MISSING);
        }
        Optional<AppUser> appUser = this.appUserRepository.findByUsernameAndStatus(
            payload.getSessionUser().getUsername(), APPLICATION_STATUS.ACTIVE);
        if (!appUser.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.APPUSER_NOT_FOUND);
        } else if (BarcoUtil.isNull(payload.getId())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.ID_MISSING);
        } else if (BarcoUtil.isNull(payload.getTemplateName())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.TEMPLATE_NAME_MISSING);
        } else if (BarcoUtil.isNull(payload.getTemplateContent())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.TEMPLATE_CONTENT_MISSING);
        }
        Optional<TemplateReg> templateReg = this.templateRegRepository.findByIdAndUsername(
            payload.getId(), appUser.get().getUsername());
        if (!templateReg.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.TEMPLATE_REG_NOT_FOUND);
        }
        if (!BarcoUtil.isNull(payload.getTemplateName())) {
            templateReg.get().setTemplateName(payload.getTemplateName());
        }
        if (!BarcoUtil.isNull(payload.getTemplateContent())) {
            templateReg.get().setTemplateContent(payload.getTemplateContent());
        }
        if (!BarcoUtil.isNull(payload.getStatus())) {
            templateReg.get().setStatus(APPLICATION_STATUS.getByLookupCode(payload.getStatus()));
        }
        templateReg.get().setUpdatedBy(appUser.get());
        this.templateRegRepository.save(templateReg.get());
        return new AppResponse(BarcoUtil.SUCCESS, String.format(MessageUtil.DATA_SAVED, payload.getId()), payload);
    }

    /**
     * Method use to find the email template from db by template id
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse findTemplateRegByTemplateId(TemplateRegRequest payload) throws Exception {
        logger.info("Request findTemplateRegByTemplateId :- " + payload);
        if (BarcoUtil.isNull(payload.getSessionUser().getUsername())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.USERNAME_MISSING);
        }
        Optional<AppUser> appUser = this.appUserRepository.findByUsernameAndStatus(
            payload.getSessionUser().getUsername(), APPLICATION_STATUS.ACTIVE);
        if (!appUser.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.APPUSER_NOT_FOUND);
        } else if (BarcoUtil.isNull(payload.getId())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.ID_MISSING);
        }
        Optional<TemplateReg> templateReg = this.templateRegRepository.findByIdAndUsername(
            payload.getId(), appUser.get().getUsername());
        if (!templateReg.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.TEMPLATE_REG_NOT_FOUND);
        }
        return new AppResponse(BarcoUtil.SUCCESS, MessageUtil.DATA_FETCH_SUCCESSFULLY,
            getTemplateRegResponse(templateReg.get()));
    }

    /**
     * Method use to find all email template from db by username
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse fetchTemplateReg(TemplateRegRequest payload) throws Exception {
        logger.info("Request fetchTemplateReg :- " + payload);
        if (BarcoUtil.isNull(payload.getSessionUser().getUsername())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.USERNAME_MISSING);
        }
        Optional<AppUser> appUser = this.appUserRepository.findByUsernameAndStatus(
            payload.getSessionUser().getUsername(), APPLICATION_STATUS.ACTIVE);
        if (!appUser.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.APPUSER_NOT_FOUND);
        }
        return new AppResponse(BarcoUtil.SUCCESS, MessageUtil.DATA_FETCH_SUCCESSFULLY,
            this.templateRegRepository.findAllByUsername(appUser.get().getUsername())
                .stream().map(templateReg -> getTemplateRegResponse(templateReg))
                .collect(Collectors.toList()));
    }

    /**
     * Method use to delete template by template id and username
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse deleteTemplateReg(TemplateRegRequest payload) throws Exception {
        logger.info("Request deleteTemplateReg :- " + payload);
        if (BarcoUtil.isNull(payload.getSessionUser().getUsername())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.USERNAME_MISSING);
        }
        Optional<AppUser> appUser = this.appUserRepository.findByUsernameAndStatus(
            payload.getSessionUser().getUsername(), APPLICATION_STATUS.ACTIVE);
        if (!appUser.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.APPUSER_NOT_FOUND);
        } else if (BarcoUtil.isNull(payload.getId())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.ID_MISSING);
        }
        Optional<TemplateReg> templateReg = this.templateRegRepository.findByIdAndUsername(
            payload.getId(), appUser.get().getUsername());
        if (!templateReg.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.TEMPLATE_REG_NOT_FOUND);
        }
        this.templateRegRepository.delete(templateReg.get());
        return new AppResponse(BarcoUtil.SUCCESS, String.format(MessageUtil.DATA_DELETED, payload.getId()), payload);
    }


    /**
     * Method use to get the templateReg response
     * @param templateReg
     * @return TemplateRegResponse
     * */
    private TemplateRegResponse getTemplateRegResponse(TemplateReg templateReg) {
        TemplateRegResponse templateRegResponse = new TemplateRegResponse();
        templateRegResponse.setId(templateReg.getId());
        templateRegResponse.setTemplateName(templateReg.getTemplateName());
        templateRegResponse.setTemplateContent(templateReg.getTemplateContent());
        templateRegResponse.setStatus(APPLICATION_STATUS.getStatusByLookupType(templateReg.getStatus().getLookupType()));
        templateRegResponse.setCreatedBy(getActionUser(templateReg.getCreatedBy()));
        templateRegResponse.setUpdatedBy(getActionUser(templateReg.getUpdatedBy()));
        templateRegResponse.setDateUpdated(templateReg.getDateUpdated());
        templateRegResponse.setDateCreated(templateReg.getDateCreated());
        return templateRegResponse;
    }
}
