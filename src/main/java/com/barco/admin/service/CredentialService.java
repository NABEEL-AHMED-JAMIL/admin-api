package com.barco.admin.service;

import com.barco.model.dto.request.CredentialRequest;
import com.barco.model.dto.response.AppResponse;

/**
 * @author Nabeel Ahmed
 */
public interface CredentialService extends RootService {

    public AppResponse addCredential(CredentialRequest payload) throws Exception;

    public AppResponse updateCredential(CredentialRequest payload) throws Exception;

    public AppResponse fetchAllCredential(CredentialRequest payload) throws Exception;

    public AppResponse fetchAllCredentialByType(CredentialRequest payload) throws Exception;

    public AppResponse fetchCredentialById(CredentialRequest payload) throws Exception;

    public AppResponse deleteCredential(CredentialRequest payload) throws Exception;

    public AppResponse deleteAllCredential(CredentialRequest payload) throws Exception;

}
