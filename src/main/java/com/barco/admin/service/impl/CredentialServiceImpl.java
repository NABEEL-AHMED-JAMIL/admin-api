package com.barco.admin.service.impl;

import com.barco.admin.service.CredentialService;
import com.barco.admin.service.LookupDataCacheService;
import com.barco.common.utility.BarcoUtil;
import com.barco.model.dto.request.CredentialRequest;
import com.barco.model.dto.response.AppResponse;
import com.barco.model.dto.response.CredentialResponse;
import com.barco.model.pojo.AppUser;
import com.barco.model.pojo.Credential;
import com.barco.model.repository.AppUserRepository;
import com.barco.model.repository.CredentialRepository;
import com.barco.model.util.ProcessUtil;
import com.barco.model.util.lookuputil.APPLICATION_STATUS;
import com.barco.model.util.lookuputil.CREDENTIAL_TYPE;
import com.barco.model.util.lookuputil.GLookup;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Nabeel Ahmed
 */
@Service
@Transactional
public class CredentialServiceImpl implements CredentialService {

    private Logger logger = LoggerFactory.getLogger(CredentialServiceImpl.class);

    @Autowired
    private CredentialRepository credentialRepository;
    @Autowired
    private AppUserRepository appUserRepository;
    @Autowired
    private LookupDataCacheService lookupDataCacheService;

    @Override
    public AppResponse addCredential(CredentialRequest credentialRequest) throws Exception {
        logger.info("Request addCredential :- " + credentialRequest);
        if (BarcoUtil.isNull(credentialRequest.getAccessUserDetail().getUsername())) {
            return new AppResponse(ProcessUtil.ERROR, "Username missing.");
        }
        Optional<AppUser> adminUser = this.appUserRepository.findByUsernameAndStatus(
            credentialRequest.getAccessUserDetail().getUsername(), APPLICATION_STATUS.ACTIVE.getLookupCode());
        if (!adminUser.isPresent()) {
            return new AppResponse(ProcessUtil.ERROR, "AppUser not found");
        } else if (BarcoUtil.isNull(credentialRequest.getCredentialName())) {
            return new AppResponse(ProcessUtil.ERROR, "CredentialName missing.");
        } else if (BarcoUtil.isNull(credentialRequest.getCredentialType())) {
            return new AppResponse(ProcessUtil.ERROR, "CredentialType missing.");
        } else if (BarcoUtil.isNull(credentialRequest.getCredentialContent())) {
            return new AppResponse(ProcessUtil.ERROR, "CredentialContent missing.");
        }
        Credential credential = new Credential();
        credential.setAppUser(adminUser.get());
        credential.setCredentialName(credentialRequest.getCredentialName());
        credential.setCredentialType(credentialRequest.getCredentialType());
        credential.setStatus(APPLICATION_STATUS.ACTIVE.getLookupCode());
        credential.setCredentialContent(new Gson().toJson(credentialRequest.getCredentialContent()));
        this.credentialRepository.save(credential);
        credentialRequest.setCredentialId(credential.getCredentialId());
        return new AppResponse(ProcessUtil.SUCCESS, String.format(
            "Credential save with %d.", credentialRequest.getCredentialId()));
    }

    @Override
    public AppResponse updateCredential(CredentialRequest credentialRequest) throws Exception {
        logger.info("Request updateCredential :- " + credentialRequest);
        if (BarcoUtil.isNull(credentialRequest.getAccessUserDetail().getUsername())) {
            return new AppResponse(ProcessUtil.ERROR, "Username missing.");
        }
        Optional<AppUser> adminUser = this.appUserRepository.findByUsernameAndStatus(
            credentialRequest.getAccessUserDetail().getUsername(), APPLICATION_STATUS.ACTIVE.getLookupCode());
        if (!adminUser.isPresent()) {
            return new AppResponse(ProcessUtil.ERROR, "AppUser not found");
        } else if (BarcoUtil.isNull(credentialRequest.getCredentialId())) {
            return new AppResponse(ProcessUtil.ERROR, "CredentialId missing.");
        } else if (BarcoUtil.isNull(credentialRequest.getCredentialName())) {
            return new AppResponse(ProcessUtil.ERROR, "CredentialName missing.");
        } else if (BarcoUtil.isNull(credentialRequest.getCredentialType())) {
            return new AppResponse(ProcessUtil.ERROR, "CredentialType missing.");
        } else if (BarcoUtil.isNull(credentialRequest.getCredentialContent())) {
            return new AppResponse(ProcessUtil.ERROR, "CredentialContent missing.");
        } else if (BarcoUtil.isNull(credentialRequest.getStatus())) {
            return new AppResponse(ProcessUtil.ERROR, "Status missing.");
        }
        Optional<Credential> credentialOptional = this.credentialRepository.findByCredentialIdAndUsernameAndStatus(
            credentialRequest.getCredentialId(), adminUser.get().getUsername(), APPLICATION_STATUS.ACTIVE.getLookupCode());
        if (!credentialOptional.isPresent()) {
            return new AppResponse(ProcessUtil.ERROR, "Credential not found");
        }
        credentialOptional.get().setCredentialName(credentialRequest.getCredentialName());
        credentialOptional.get().setCredentialType(credentialRequest.getCredentialType());
        credentialOptional.get().setStatus(credentialRequest.getStatus());
        credentialOptional.get().setCredentialContent(new Gson().toJson(credentialRequest.getCredentialContent()));
        this.credentialRepository.save(credentialOptional.get());
        return new AppResponse(ProcessUtil.SUCCESS, String.format(
            "Credential save with %d.", credentialRequest.getCredentialId()));
    }

    @Override
    public AppResponse fetchAllCredential(CredentialRequest credentialRequest) throws Exception {
        logger.info("Request deleteCredential :- " + credentialRequest);
        if (BarcoUtil.isNull(credentialRequest.getAccessUserDetail().getUsername())) {
            return new AppResponse(ProcessUtil.ERROR, "Username missing.");
        }
        Optional<AppUser> adminUser = this.appUserRepository.findByUsernameAndStatus(
            credentialRequest.getAccessUserDetail().getUsername(), APPLICATION_STATUS.ACTIVE.getLookupCode());
        if (!adminUser.isPresent()) {
            return new AppResponse(ProcessUtil.ERROR, "AppUser not found");
        }
        List<CredentialResponse> credentialResponseList = this.credentialRepository.findAllByUsernameAndStatusNotIn(
            adminUser.get().getUsername(), APPLICATION_STATUS.DELETE.getLookupCode())
            .stream().map(credential -> {
                CredentialResponse credentialResponse = new CredentialResponse();
                credentialResponse.setCredentialId(credential.getCredentialId());
                credentialResponse.setCredentialName(credential.getCredentialName());
                credentialResponse.setCredentialType(GLookup.getGLookup(this.lookupDataCacheService.getChildLookupById(
                    CREDENTIAL_TYPE.getName(), credential.getCredentialType())));
                credentialResponse.setStatus(GLookup.getGLookup(this.lookupDataCacheService.getChildLookupById(
                    APPLICATION_STATUS.getName(), credential.getStatus())));
                credentialResponse.setDateCreated(credential.getDateCreated());
                return credentialResponse;
            }).collect(Collectors.toList());
        return new AppResponse(ProcessUtil.SUCCESS, "Data fetch successfully.", credentialResponseList);
    }

    @Override
    public AppResponse fetchCredentialByCredentialId(CredentialRequest credentialRequest) throws Exception {
        logger.info("Request fetchCredentialByCredentialId :- " + credentialRequest);
        if (BarcoUtil.isNull(credentialRequest.getAccessUserDetail().getUsername())) {
            return new AppResponse(ProcessUtil.ERROR, "Username missing.");
        } else if (BarcoUtil.isNull(credentialRequest.getCredentialId())) {
            return new AppResponse(ProcessUtil.ERROR, "CredentialId missing.");
        }
        Optional<AppUser> adminUser = this.appUserRepository.findByUsernameAndStatus(
            credentialRequest.getAccessUserDetail().getUsername(), APPLICATION_STATUS.ACTIVE.getLookupCode());
        if (!adminUser.isPresent()) {
            return new AppResponse(ProcessUtil.ERROR, "AppUser not found");
        }
        Optional<Credential> credentialOptional = this.credentialRepository.findByCredentialIdAndUsernameAndStatusNotIn(
            credentialRequest.getCredentialId(), adminUser.get().getUsername(), APPLICATION_STATUS.DELETE.getLookupCode());
        if (!credentialOptional.isPresent()) {
            return new AppResponse(ProcessUtil.ERROR, "Credential not found");
        }
        CredentialResponse credentialResponse = new CredentialResponse();
        credentialResponse.setCredentialId(credentialOptional.get().getCredentialId());
        credentialResponse.setCredentialName(credentialOptional.get().getCredentialName());
        credentialResponse.setCredentialType(GLookup.getGLookup(this.lookupDataCacheService.getChildLookupById(
            CREDENTIAL_TYPE.getName(), credentialOptional.get().getCredentialType())));
        credentialResponse.setStatus(GLookup.getGLookup(this.lookupDataCacheService.getChildLookupById(
            APPLICATION_STATUS.getName(), credentialOptional.get().getStatus())));
        credentialResponse.setDateCreated(credentialOptional.get().getDateCreated());
        credentialResponse.setCredentialContent(new Gson().fromJson(
            credentialOptional.get().getCredentialContent(), Object.class));
        return new AppResponse(ProcessUtil.SUCCESS, "Data fetch successfully.", credentialResponse);
    }

    @Override
    public AppResponse deleteCredential(CredentialRequest credentialRequest) throws Exception {
        logger.info("Request deleteCredential :- " + credentialRequest);
        if (BarcoUtil.isNull(credentialRequest.getAccessUserDetail().getUsername())) {
            return new AppResponse(ProcessUtil.ERROR, "Username missing.");
        } else if (BarcoUtil.isNull(credentialRequest.getCredentialId())) {
            return new AppResponse(ProcessUtil.ERROR, "CredentialId missing.");
        }
        Optional<AppUser> adminUser = this.appUserRepository.findByUsernameAndStatus(
            credentialRequest.getAccessUserDetail().getUsername(), APPLICATION_STATUS.ACTIVE.getLookupCode());
        if (!adminUser.isPresent()) {
            return new AppResponse(ProcessUtil.ERROR, "AppUser not found");
        }
        Optional<Credential> credentialOptional = this.credentialRepository.findByCredentialIdAndUsernameAndStatusNotIn(
            credentialRequest.getCredentialId(), adminUser.get().getUsername(), APPLICATION_STATUS.DELETE.getLookupCode());
        if (!credentialOptional.isPresent()) {
            return new AppResponse(ProcessUtil.ERROR, "Credential not found");
        }
        credentialOptional.get().setStatus(APPLICATION_STATUS.DELETE.getLookupCode());
        this.credentialRepository.save(credentialOptional.get());
        return new AppResponse(ProcessUtil.SUCCESS, "Data delete successfully.");
    }

}
