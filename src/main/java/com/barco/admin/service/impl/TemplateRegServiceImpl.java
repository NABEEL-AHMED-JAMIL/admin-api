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

    /**
     * Method use to add the template
     * @param payload
     * @return AppResponse
     * */
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

    /**
     * Method use to update the template
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse updateTemplateReg(TemplateRegRequest payload) throws Exception {
        logger.info("Request updateTemplateReg :- " + payload);
        AppResponse validationResponse = this.validateAddOrUpdatePayload(payload);
        if (!BarcoUtil.isNull(validationResponse)) {
            return validationResponse;
        } else if (BarcoUtil.isNull(payload.getId())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.ID_MISSING);
        }
        Optional<TemplateReg> templateRegOpt = this.templateRegRepository.findByIdAndUsername(
            payload.getId(), payload.getSessionUser().getUsername());
        if (!templateRegOpt.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.TEMPLATE_REG_NOT_FOUND);
        }
        this.templateRegRepository.save(this.updateTemplateRegPayload(templateRegOpt.get(), payload));
        return new AppResponse(BarcoUtil.SUCCESS, String.format(MessageUtil.DATA_UPDATE, payload.getId()), payload);
    }

    /**
     * Method use to fetch the template by id
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse findTemplateRegByTemplateId(TemplateRegRequest payload) throws Exception {
        logger.info("Request findTemplateRegByTemplateId :- " + payload);
        AppResponse usernameExist = this.isUsernameExist(payload);
        if (!BarcoUtil.isNull(usernameExist)) {
            return usernameExist;
        }
        Optional<TemplateReg> templateReg = this.templateRegRepository.findByIdAndUsername(
            payload.getId(), payload.getSessionUser().getUsername());
        if (!templateReg.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.TEMPLATE_REG_NOT_FOUND);
        }
        return new AppResponse(BarcoUtil.SUCCESS, MessageUtil.DATA_FETCH_SUCCESSFULLY, this.getTemplateRegResponse(templateReg.get()));
    }

    /**
     * Method use to fetch the template
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse fetchTemplateReg(TemplateRegRequest payload) throws Exception {
        logger.info("Request fetchTemplateReg :- " + payload);
        AppResponse validationResponse = this.validateUsername(payload);
        if (!BarcoUtil.isNull(validationResponse)) {
            return validationResponse;
        }
        return new AppResponse(BarcoUtil.SUCCESS, MessageUtil.DATA_FETCH_SUCCESSFULLY,
            this.templateRegRepository.findAllByUsernameOrderByDateCreatedDesc(payload.getSessionUser().getUsername())
                .stream().map(this::getTemplateRegResponse).collect(Collectors.toList()));
    }

    /**
     * Method use to delete the template
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse deleteTemplateReg(TemplateRegRequest payload) throws Exception {
        logger.info("Request deleteTemplateReg :- " + payload);
        AppResponse usernameExist = this.isUsernameExist(payload);
        if (!BarcoUtil.isNull(usernameExist)) {
            return usernameExist;
        }
        Optional<TemplateReg> templateReg = this.templateRegRepository.findByIdAndUsername(
            payload.getId(), payload.getSessionUser().getUsername());
        if (!templateReg.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.TEMPLATE_REG_NOT_FOUND);
        }
        this.templateRegRepository.delete(templateReg.get());
        return new AppResponse(BarcoUtil.SUCCESS, String.format(MessageUtil.DATA_DELETED, payload.getId()), payload);
    }

    /**
     * Method use to delete the template
     * @param payload
     * @return AppResponse
     * */
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

    /**
     * Method use to validate the payload
     * @param payload
     * @return AppResponse
     * */
    private AppResponse validateAddOrUpdatePayload(TemplateRegRequest payload) throws Exception {
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

    /**
     * Method use to validate the username
     * @param payload
     * @return AppResponse
     * */
    private AppResponse validateUsername(TemplateRegRequest payload) throws Exception {
        if (BarcoUtil.isNull(payload.getSessionUser().getUsername())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.USERNAME_MISSING);
        } else if (!this.appUserRepository.findByUsernameAndStatus(
            payload.getSessionUser().getUsername(), APPLICATION_STATUS.ACTIVE).isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.APPUSER_NOT_FOUND);
        }
        return (AppResponse) BarcoUtil.NULL;
    }

    /**
     * Method use to get the valid username
     * check user exist or not
     * @param payload
     * @return AppResponse
     * */
    private AppResponse isUsernameExist(TemplateRegRequest payload) throws Exception {
        AppResponse validationResponse = this.validateUsername(payload);
        if (!BarcoUtil.isNull(validationResponse)) {
            return validationResponse;
        } else if (BarcoUtil.isNull(payload.getId())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.ID_MISSING);
        }
        return (AppResponse) BarcoUtil.NULL;
    }

    /**
     * Method use to create the template reg
     * @param payload
     * @return TemplateReg
     * */
    private TemplateReg createTemplateReg(TemplateRegRequest payload) throws Exception {
        Optional<AppUser> appUserOpt = this.appUserRepository.findByUsernameAndStatus(
            payload.getSessionUser().getUsername(), APPLICATION_STATUS.ACTIVE);
        TemplateReg templateReg = new TemplateReg();
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

    /**
     * Method use to update the template reg
     * @param templateReg
     * @param payload
     * @return TemplateReg
     * */
    private TemplateReg updateTemplateRegPayload(TemplateReg templateReg,
        TemplateRegRequest payload) throws Exception {
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
