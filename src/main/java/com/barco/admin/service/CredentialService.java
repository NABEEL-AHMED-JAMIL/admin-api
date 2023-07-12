package com.barco.admin.service;

import com.barco.model.dto.request.CredentialRequest;
import com.barco.model.dto.response.AppResponse;

/**
 * @author Nabeel Ahmed
 */
public interface CredentialService {

    public AppResponse addCredential(CredentialRequest requestPayload) throws Exception ;

    public AppResponse updateCredential(CredentialRequest requestPayload) throws Exception ;

    public AppResponse fetchAllCredential(CredentialRequest requestPayload) throws Exception ;

    public AppResponse fetchCredentialByCredentialId(CredentialRequest requestPayload) throws Exception ;

    public AppResponse deleteCredential(CredentialRequest requestPayload) throws Exception ;

}
