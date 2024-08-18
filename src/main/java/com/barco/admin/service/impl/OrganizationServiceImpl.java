package com.barco.admin.service.impl;

import com.barco.admin.service.LookupDataCacheService;
import com.barco.admin.service.OrganizationService;
import com.barco.common.utility.BarcoUtil;
import com.barco.model.dto.request.OrganizationRequest;
import com.barco.model.dto.response.AppResponse;
import com.barco.model.pojo.AppUser;
import com.barco.model.pojo.ETLCountry;
import com.barco.model.pojo.Organization;
import com.barco.model.repository.AppUserRepository;
import com.barco.model.repository.ETLCountryRepository;
import com.barco.model.repository.OrganizationRepository;
import com.barco.model.util.MessageUtil;
import com.barco.model.util.lookup.APPLICATION_STATUS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

/**
 * @author Nabeel Ahmed
 */
@Service
public class OrganizationServiceImpl implements OrganizationService {

    private Logger logger = LoggerFactory.getLogger(OrganizationServiceImpl.class);

    @Autowired
    private AppUserRepository appUserRepository;
    @Autowired
    private OrganizationRepository organizationRepository;
    @Autowired
    private ETLCountryRepository etlCountryRepository;
    @Autowired
    private LookupDataCacheService lookupDataCacheService;

    /**
     * Method use to create the org
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse addOrg(OrganizationRequest payload) throws Exception {
        logger.info("Request addOrg :- " + payload);
        AppResponse validationResponse = this.validateAddOrUpdatePayload(payload);
        if (!BarcoUtil.isNull(validationResponse)) {
            return validationResponse;
        }
        Optional<ETLCountry> etlCountry = this.etlCountryRepository.findByCountryCode(payload.getCountryCode());
        if (!etlCountry.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.ORG_COUNTRY_CODE_NOT_FOUND);
        }
        Organization organization = this.createOrganization(payload);
        this.organizationRepository.save(organization);
        return new AppResponse(BarcoUtil.SUCCESS, String.format(MessageUtil.DATA_SAVED, organization.getId()), payload);
    }

    /**
     * Method use to update the org
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse updateOrg(OrganizationRequest payload) throws Exception {
        logger.info("Request updateOrg :- " + payload);
        AppResponse validationResponse = this.validateAddOrUpdatePayload(payload);
        if (!BarcoUtil.isNull(validationResponse)) {
            return validationResponse;
        } else if (!BarcoUtil.isNull(payload.getId())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.ID_MISSING);
        }
        Optional<Organization> organizationOpt = this.organizationRepository.findByIdAndUsernameAndStatusNot(
            payload.getId(), payload.getSessionUser().getUsername(), APPLICATION_STATUS.DELETE);
        if (!organizationOpt.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.ORG_NOT_FOUND);
        }
        this.organizationRepository.save(this.updateTemplateRegFromPayload(organizationOpt.get(), payload));
        return new AppResponse(BarcoUtil.SUCCESS, String.format(MessageUtil.DATA_SAVED, payload.getId()), payload);
    }

    /**
     * Method use to fetch the org by id
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse fetchOrgById(OrganizationRequest payload) throws Exception {
        logger.info("Request fetchOrgById :- " + payload);
        AppResponse usernameExist = this.isUsernameExist(payload);
        if (!BarcoUtil.isNull(usernameExist)) {
            return usernameExist;
        }
        Optional<Organization> organizationOpt = this.organizationRepository.findByIdAndUsernameAndStatusNot(
            payload.getId(), payload.getSessionUser().getUsername(), APPLICATION_STATUS.DELETE);
        if (!organizationOpt.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.TEMPLATE_REG_NOT_FOUND);
        }
        return new AppResponse(BarcoUtil.SUCCESS, MessageUtil.DATA_FETCH_SUCCESSFULLY, this.getOrganizationResponse(organizationOpt.get()));
    }

    /**
     * Method use to fetch the all org
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse fetchAllOrg(OrganizationRequest payload) throws Exception {
        logger.info("Request fetchAllOrg :- " + payload);
        AppResponse validationResponse = this.validateUsername(payload);
        if (!BarcoUtil.isNull(validationResponse)) {
            return validationResponse;
        }
        return new AppResponse(BarcoUtil.SUCCESS, MessageUtil.DATA_FETCH_SUCCESSFULLY);
    }

    /**
     * Method use to delete the org by id
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse deleteOrgById(OrganizationRequest payload) throws Exception {
        logger.info("Request deleteOrgById :- " + payload);
        AppResponse usernameExist = this.isUsernameExist(payload);
        if (!BarcoUtil.isNull(usernameExist)) {
            return usernameExist;
        }
        Optional<Organization> organization = this.organizationRepository.findByIdAndUsername(
            payload.getId(), payload.getSessionUser().getUsername());
        if (!organization.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.ORG_NOT_FOUND);
        }
        // delete the org and delete the all user and delete the all setting and resource as well
        return new AppResponse(BarcoUtil.SUCCESS, String.format(MessageUtil.DATA_DELETED, payload.getId()), payload);
    }

    /**
     * Method use to delete all org
     * @param payload
     * @return AppResponse
     * */
    @Override
    public AppResponse deleteAllOrg(OrganizationRequest payload) throws Exception {
        logger.info("Request deleteAllOrg :- " + payload);
        AppResponse validationResponse = this.validateUsername(payload);
        if (!BarcoUtil.isNull(validationResponse)) {
            return validationResponse;
        } else if (BarcoUtil.isNull(payload.getIds())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.IDS_MISSING);
        }
        // delete the org and delete the all user and delete the all setting and resource as well
        return new AppResponse(BarcoUtil.SUCCESS, MessageUtil.DATA_DELETED_ALL, payload);
    }

    /**
     * Method use to validate the payload
     * @param payload
     * @return AppResponse
     * */
    private AppResponse validateAddOrUpdatePayload(OrganizationRequest payload) throws Exception {
        if (BarcoUtil.isNull(payload.getSessionUser().getUsername())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.USERNAME_MISSING);
        }
        Optional<AppUser> adminUser = this.appUserRepository.findByUsernameAndStatus(
            payload.getSessionUser().getUsername(), APPLICATION_STATUS.ACTIVE);
        if (!adminUser.isPresent()) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.APPUSER_NOT_FOUND);
        }
        if (BarcoUtil.isNull(payload.getName())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.ORG_NAME_MISSING);
        } else if (BarcoUtil.isNull(payload.getEmail())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.ORG_EMAIL_MISSING);
        } else if (BarcoUtil.isNull(payload.getPhone())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.ORG_PHONE_MISSING);
        } else if (BarcoUtil.isNull(payload.getCountryCode())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.ORG_COUNTRY_CODE_MISSING);
        } else if (BarcoUtil.isNull(payload.getAddress())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.ORG_ADDRESS_MISSING);
        }
        return (AppResponse) BarcoUtil.NULL;
    }

    /**
     * Method use to validate the username
     * @param payload
     * @return AppResponse
     * */
    private AppResponse validateUsername(OrganizationRequest payload) throws Exception {
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
    private AppResponse isUsernameExist(OrganizationRequest payload) throws Exception {
        AppResponse validationResponse = this.validateUsername(payload);
        if (!BarcoUtil.isNull(validationResponse)) {
            return validationResponse;
        } else if (BarcoUtil.isNull(payload.getId())) {
            return new AppResponse(BarcoUtil.ERROR, MessageUtil.ID_MISSING);
        }
        return (AppResponse) BarcoUtil.NULL;
    }

    /**
     * Method use to create the organization reg
     * @param payload
     * @return Organization
     * */
    private Organization createOrganization(OrganizationRequest payload) throws Exception {
        Optional<AppUser> appUserOpt = this.appUserRepository.findByUsernameAndStatus(
            payload.getSessionUser().getUsername(), APPLICATION_STATUS.ACTIVE);
        Organization organization = new Organization();
        organization.setName(payload.getName());
        organization.setPhone(payload.getPhone());
        organization.setAddress(payload.getAddress());
        organization.setCountry(this.etlCountryRepository.findByCountryCode(payload.getCountryCode()).get());
        organization.setCreatedBy(appUserOpt.get());
        organization.setUpdatedBy(appUserOpt.get());
        organization.setStatus(APPLICATION_STATUS.ACTIVE);
        return organization;
    }

    /**
     * Method use to update the org
     * @param organization
     * @param payload
     * @return Organization
     * */
    private Organization updateTemplateRegFromPayload(Organization organization,
        OrganizationRequest payload) throws Exception {
        if (!BarcoUtil.isNull(payload.getName())) {
            organization.setName(payload.getName());
        }
        if (!BarcoUtil.isNull(payload.getPhone())) {
            organization.setPhone(payload.getPhone());
        }
        if (!BarcoUtil.isNull(payload.getAddress())) {
            organization.setAddress(payload.getAddress());
        }
        if (!BarcoUtil.isNull(payload.getCountryCode())) {
            organization.setCountry(this.etlCountryRepository.findByCountryCode(payload.getCountryCode()).get());
        }
        if (!BarcoUtil.isNull(payload.getStatus())) {
            organization.setStatus(APPLICATION_STATUS.getByLookupCode(payload.getStatus()));
        }
        Optional<AppUser> appUserOpt = this.appUserRepository.findByUsernameAndStatus(
            payload.getSessionUser().getUsername(), APPLICATION_STATUS.ACTIVE);
        if (appUserOpt.isPresent()) {
            organization.setUpdatedBy(appUserOpt.get());
        }
        return organization;
    }

}
