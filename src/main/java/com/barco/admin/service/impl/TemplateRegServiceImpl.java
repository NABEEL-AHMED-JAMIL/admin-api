package com.barco.admin.service.impl;

import com.barco.admin.service.LookupDataCacheService;
import com.barco.admin.service.TemplateRegService;
import com.barco.common.utility.BarcoUtil;
import com.barco.model.dto.request.TemplateRegRequest;
import com.barco.model.dto.response.AppResponse;
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
import javax.transaction.Transactional;
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

    @Override
    public AppResponse addTemplateReg(TemplateRegRequest payload) throws Exception {
        logger.info("Request addTemplateReg :- " + payload);
        AppResponse validationResponse = this.validateAddOrUpdatePayload(payload);
        if (!BarcoUtil.isNull(validationResponse)) {
            return validationResponse;
        }
        if (this.templateRegRepository.findFirstByTemplateNameAndStatusNot(
            payload.getTemplateName(), APPLICATION_STATUS.DELETE).isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.TEMPLATE_REG_ALREADY_EXIST);
        }
        TemplateReg templateReg = this.createTemplateReg(payload);
        this.templateRegRepository.save(templateReg);
        return new AppResponse(BarcoUtil.SUCCESS, String.format(MessageUtil.DATA_SAVED, templateReg.getId()), payload);
    }

    @Override
    public AppResponse editTemplateReg(TemplateRegRequest payload) throws Exception {
        logger.info("Request editTemplateReg :- " + payload);
        AppResponse validationResponse = this.validateAddOrUpdatePayload(payload);
        if (!BarcoUtil.isNull(validationResponse)) {
            return validationResponse;
        }
        Optional<TemplateReg> templateRegOpt = this.templateRegRepository.findByIdAndUsername(
            payload.getId(), payload.getSessionUser().getUsername());
        if (!templateRegOpt.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.TEMPLATE_REG_NOT_FOUND);
        }
        TemplateReg templateReg = this.updateTemplateRegFromPayload(templateRegOpt.get(), payload);
        this.templateRegRepository.save(templateReg);
        return new AppResponse(BarcoUtil.SUCCESS, String.format(MessageUtil.DATA_SAVED, payload.getId()), payload);
    }

    @Override
    public AppResponse findTemplateRegByTemplateId(TemplateRegRequest payload) throws Exception {
        logger.info("Request findTemplateRegByTemplateId :- " + payload);
        AppResponse validationResponse = this.validateUsernameAndId(payload);
        if (!BarcoUtil.isNull(validationResponse)) {
            return validationResponse;
        }
        Optional<TemplateReg> templateRegOpt = this.templateRegRepository.findByIdAndUsername(
            payload.getId(), payload.getSessionUser().getUsername());
        if (!templateRegOpt.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.TEMPLATE_REG_NOT_FOUND);
        }
        return new AppResponse(BarcoUtil.SUCCESS, MessageUtil.DATA_FETCH_SUCCESSFULLY, this.getTemplateRegResponse(templateRegOpt.get()));
    }

    @Override
    public AppResponse fetchTemplateReg(TemplateRegRequest payload) throws Exception {
        logger.info("Request fetchTemplateReg :- " + payload);
        AppResponse validationResponse = this.validateUsername(payload);
        if (!BarcoUtil.isNull(validationResponse)) {
            return validationResponse;
        }
        return new AppResponse(BarcoUtil.SUCCESS, MessageUtil.DATA_FETCH_SUCCESSFULLY,
            this.templateRegRepository.findAllByUsernameOrderByDateCreatedDesc(payload.getSessionUser().getUsername()).stream()
                .map(this::getTemplateRegResponse).collect(Collectors.toList()));
    }

    @Override
    public AppResponse deleteTemplateReg(TemplateRegRequest payload) throws Exception {
        logger.info("Request deleteTemplateReg :- " + payload);
        AppResponse validationResponse = this.validateUsernameAndId(payload);
        if (!BarcoUtil.isNull(validationResponse)) {
            return validationResponse;
        }
        Optional<TemplateReg> templateRegOpt = this.templateRegRepository.findByIdAndUsername(
            payload.getId(), payload.getSessionUser().getUsername());
        if (!templateRegOpt.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.TEMPLATE_REG_NOT_FOUND);
        }
        this.templateRegRepository.delete(templateRegOpt.get());
        return new AppResponse(BarcoUtil.SUCCESS, String.format(MessageUtil.DATA_DELETED, payload.getId()), payload);
    }

    @Override
    @Transactional
    public AppResponse deleteAllTemplateReg(TemplateRegRequest payload) throws Exception {
        logger.info("Request deleteAllTemplateReg :- " + payload);
        AppResponse validationResponse = this.validateUsername(payload);
        if (!BarcoUtil.isNull(validationResponse)) {
            return validationResponse;
        } else if (BarcoUtil.isNull(payload.getIds())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.IDS_MISSING);
        }
        this.templateRegRepository.deleteAll(this.templateRegRepository.findAllByIdIn(payload.getIds()));
        return new AppResponse(BarcoUtil.SUCCESS, MessageUtil.DATA_DELETED_ALL, payload);
    }

    private AppResponse validateAddOrUpdatePayload(TemplateRegRequest payload) {
        if (BarcoUtil.isNull(payload.getSessionUser().getUsername())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.USERNAME_MISSING);
        }
        Optional<AppUser> appUserOpt = appUserRepository.findByUsernameAndStatus(
            payload.getSessionUser().getUsername(), APPLICATION_STATUS.ACTIVE);
        if (!appUserOpt.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.APPUSER_NOT_FOUND);
        } else if (BarcoUtil.isNull(payload.getTemplateName())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.TEMPLATE_NAME_MISSING);
        } else if (BarcoUtil.isNull(payload.getDescription())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.TEMPLATE_DESCRIPTION_MISSING);
        } else if (BarcoUtil.isNull(payload.getTemplateContent())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.TEMPLATE_CONTENT_MISSING);
        }
        return (AppResponse) BarcoUtil.NULL;
    }

    private AppResponse validateUsername(TemplateRegRequest payload) {
        if (BarcoUtil.isNull(payload.getSessionUser().getUsername())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.USERNAME_MISSING);
        } else if (!this.appUserRepository.findByUsernameAndStatus(
            payload.getSessionUser().getUsername(), APPLICATION_STATUS.ACTIVE).isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.APPUSER_NOT_FOUND);
        }
        return (AppResponse) BarcoUtil.NULL;
    }

    private AppResponse validateUsernameAndId(TemplateRegRequest payload) {
        AppResponse validationResponse = this.validateUsername(payload);
        if (!BarcoUtil.isNull(validationResponse)) {
            return validationResponse;
        } else if (BarcoUtil.isNull(payload.getId())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.ID_MISSING);
        }
        return (AppResponse) BarcoUtil.NULL;
    }

    private TemplateReg createTemplateReg(TemplateRegRequest payload) throws Exception {
        TemplateReg templateReg = new TemplateReg();
        Optional<AppUser> appUserOpt = this.appUserRepository.findByUsernameAndStatus(
            payload.getSessionUser().getUsername(), APPLICATION_STATUS.ACTIVE);
        if (appUserOpt.isPresent()) {
            AppUser appUser = appUserOpt.get();
            templateReg.setTemplateName(payload.getTemplateName());
            templateReg.setDescription(payload.getDescription());
            templateReg.setTemplateContent(payload.getTemplateContent());
            templateReg.setCreatedBy(appUser);
            templateReg.setUpdatedBy(appUser);
            templateReg.setStatus(APPLICATION_STATUS.ACTIVE);
        }
        return templateReg;
    }

    private TemplateReg updateTemplateRegFromPayload(TemplateReg templateReg, TemplateRegRequest payload) {
        if (!BarcoUtil.isNull(payload.getTemplateName())) {
            templateReg.setTemplateName(payload.getTemplateName());
        }
        if (!BarcoUtil.isNull(payload.getDescription())) {
            templateReg.setDescription(payload.getDescription());
        }
        if (!BarcoUtil.isNull(payload.getTemplateContent())) {
            templateReg.setTemplateContent(payload.getTemplateContent());
        }
        if (!BarcoUtil.isNull(payload.getStatus())) {
            templateReg.setStatus(APPLICATION_STATUS.getByLookupCode(payload.getStatus()));
        }
        Optional<AppUser> appUserOpt = this.appUserRepository.findByUsernameAndStatus(
            payload.getSessionUser().getUsername(), APPLICATION_STATUS.ACTIVE);
        if (appUserOpt.isPresent()) {
            templateReg.setUpdatedBy(appUserOpt.get());
        }
        return templateReg;
    }

}
