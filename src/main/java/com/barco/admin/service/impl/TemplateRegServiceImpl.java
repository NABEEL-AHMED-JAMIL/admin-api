package com.barco.admin.service.impl;

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

    public TemplateRegServiceImpl() {}

    /**
     * Method use to add the template
     * @param payload
     * @return AppResponse
     * @throws Exception
     * */
    @Override
    public AppResponse addTemplateReg(TemplateRegRequest payload) throws Exception {
        logger.info("Request addTemplateReg :- {}.", payload);
        AppResponse validationResponse = this.validateAddOrUpdatePayload(payload);
        if (!BarcoUtil.isNull(validationResponse)) {
            return validationResponse;
        }
        if (this.templateRegRepository.findFirstByTemplateNameAndStatusNot(payload.getTemplateName(), APPLICATION_STATUS.DELETE).isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.TEMPLATE_REG_ALREADY_EXIST);
        }
        TemplateReg templateReg = this.createTemplateReg(payload);
        this.templateRegRepository.save(templateReg);
        return new AppResponse(BarcoUtil.SUCCESS, String.format(MessageUtil.DATA_SAVED, templateReg.getUuid()), payload);
    }

    /**
     * Method use to update the template
     * @param payload
     * @return AppResponse
     * @throws Exception
     * */
    @Override
    public AppResponse updateTemplateReg(TemplateRegRequest payload) throws Exception {
        logger.info("Request updateTemplateReg :- {}.", payload);
        AppResponse validationResponse = this.validateAddOrUpdatePayload(payload);
        if (!BarcoUtil.isNull(validationResponse)) {
            return validationResponse;
        } else if (BarcoUtil.isNull(payload.getUuid())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.ID_MISSING);
        }
        Optional<TemplateReg> templateRegOpt = this.templateRegRepository.findByUuidAndStatusNot(payload.getUuid(), APPLICATION_STATUS.DELETE);
        if (templateRegOpt.isEmpty()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.TEMPLATE_REG_NOT_FOUND);
        }
        this.templateRegRepository.save(this.updateTemplateRegPayload(templateRegOpt.get(), payload));
        return new AppResponse(BarcoUtil.SUCCESS, String.format(MessageUtil.DATA_UPDATE, payload.getUuid()), payload);
    }

    /**
     * Method use to fetch the template by id
     * @param payload
     * @return AppResponse
     * @throws Exception
     * */
    @Override
    public AppResponse findTemplateRegByTemplateId(TemplateRegRequest payload) throws Exception {
        logger.info("Request findTemplateRegByTemplateId :- {}.", payload);
        if (BarcoUtil.isNull(payload.getUuid())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.ID_MISSING);
        }
        return this.templateRegRepository.findByUuidAndStatusNot(payload.getUuid(), APPLICATION_STATUS.DELETE)
            .map(reg -> new AppResponse(BarcoUtil.SUCCESS, MessageUtil.DATA_FETCH_SUCCESSFULLY, this.getTemplateRegResponse(reg)))
            .orElseGet(() -> new AppResponse(BarcoUtil.ERROR, MessageUtil.TEMPLATE_REG_NOT_FOUND));
    }

    /**
     * Method use to fetch the template
     * @param payload
     * @return AppResponse
     * @throws Exception
     * */
    @Override
    public AppResponse fetchTemplateReg(TemplateRegRequest payload) throws Exception {
        logger.info("Request fetchTemplateReg :- {}.", payload);
        return new AppResponse(BarcoUtil.SUCCESS, MessageUtil.DATA_FETCH_SUCCESSFULLY,
            this.templateRegRepository.findAllByStatusNotOrderByDateCreatedDesc(APPLICATION_STATUS.DELETE)
                .stream().map(this::getTemplateRegResponse).collect(Collectors.toList()));
    }

    /**
     * Method use to delete the template
     * @param payload
     * @return AppResponse
     * @throws Exception
     * */
    @Override
    public AppResponse deleteTemplateReg(TemplateRegRequest payload) throws Exception {
        logger.info("Request deleteTemplateReg :- {}.", payload);
        if (BarcoUtil.isNull(payload.getUuid())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.ID_MISSING);
        }
        Optional<TemplateReg> templateReg = this.templateRegRepository.findByUuidAndStatusNot(payload.getUuid(), APPLICATION_STATUS.DELETE);
        if (templateReg.isEmpty()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.TEMPLATE_REG_NOT_FOUND);
        }
        this.templateRegRepository.delete(templateReg.get());
        return new AppResponse(BarcoUtil.SUCCESS, String.format(MessageUtil.DATA_DELETED, payload.getUuid()), payload);
    }

    /**
     * Method use to delete the template
     * @param payload
     * @return AppResponse
     * @throws Exception
     * */
    @Override
    @Transactional
    public AppResponse deleteAllTemplateReg(TemplateRegRequest payload) throws Exception {
        logger.info("Request deleteAllTemplateReg :- {}.", payload);
        if (BarcoUtil.isNull(payload.getUuids())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.IDS_MISSING);
        }
        this.templateRegRepository.deleteAll(this.templateRegRepository.findAllByUuidInAndStatusNot(payload.getUuids(), APPLICATION_STATUS.DELETE));
        return new AppResponse(BarcoUtil.SUCCESS, MessageUtil.DATA_DELETED_ALL, payload);
    }

    /**
     * Method use to validate the payload
     * @param payload
     * @return AppResponse
     * @throws Exception
     * */
    private AppResponse validateAddOrUpdatePayload(TemplateRegRequest payload) throws Exception {
        if (BarcoUtil.isNull(payload.getTemplateName())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.TEMPLATE_NAME_MISSING);
        } else if (BarcoUtil.isNull(payload.getDescription())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.TEMPLATE_DESCRIPTION_MISSING);
        } else if (BarcoUtil.isNull(payload.getTemplateContent())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.TEMPLATE_CONTENT_MISSING);
        }
        return (AppResponse) BarcoUtil.NULL;
    }

    /**
     * Method use to create the template reg
     * @param payload
     * @return TemplateReg
     * @throws Exception
     * */
    private TemplateReg createTemplateReg(TemplateRegRequest payload) throws Exception {
        Optional<AppUser> appUserOpt = this.appUserRepository.findByUsernameAndStatus(payload.getSessionUser().getUsername(), APPLICATION_STATUS.ACTIVE);
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
     * @throws Exception
     * */
    private TemplateReg updateTemplateRegPayload(TemplateReg templateReg, TemplateRegRequest payload) throws Exception {
        templateReg.setTemplateName(payload.getTemplateName());
        templateReg.setDescription(payload.getDescription());
        templateReg.setTemplateContent(payload.getTemplateContent());
        if (!BarcoUtil.isNull(payload.getStatus())) {
            templateReg.setStatus(APPLICATION_STATUS.getByLookupCode(payload.getStatus()));
        }
        Optional<AppUser> appUserOpt = this.appUserRepository.findByUsernameAndStatus(payload.getSessionUser().getUsername(), APPLICATION_STATUS.ACTIVE);
        appUserOpt.ifPresent(templateReg::setUpdatedBy);
        return templateReg;
    }

}
