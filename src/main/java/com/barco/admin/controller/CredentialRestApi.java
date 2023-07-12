package com.barco.admin.controller;

import com.barco.common.utility.ExceptionUtil;
import com.barco.model.dto.request.CredentialRequest;
import com.barco.model.dto.response.AppResponse;
import com.barco.model.service.UserDetailsImpl;
import com.barco.model.util.ProcessUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import com.barco.admin.service.CredentialService;

/**
 * Api use to perform crud operation
 * @author Nabeel Ahmed
 */
@RestController
@CrossOrigin(origins = "*")
@RequestMapping(value = "/credential.json")
public class CredentialRestApi {

    private Logger logger = LoggerFactory.getLogger(CredentialRestApi.class);

    @Autowired
    private CredentialService credentialService;

    /**
     * api-status :- done
     * @apiName :- addCredential
     * Api use to add the credential data
     * @param requestPayload
     * @return ResponseEntity<?> addCredential
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('ADMIN')")
    @RequestMapping(value = "/addCredential", method = RequestMethod.POST)
    public ResponseEntity<?> addCredential(@RequestBody CredentialRequest requestPayload) {
        try {
            UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            requestPayload.addAccessUserDetail(userDetails.getUsername());
            return new ResponseEntity<>(this.credentialService.addCredential(requestPayload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while addCredential ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(ProcessUtil.ERROR,
                "Some internal error occurred contact with support."), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * api-status :- done
     * @apiName :- updateCredential
     * Api use to update the credential data
     * @param requestPayload
     * @return ResponseEntity<?> updateCredential
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('ADMIN')")
    @RequestMapping(value = "/updateCredential", method = RequestMethod.PUT)
    public ResponseEntity<?> updateCredential(@RequestBody CredentialRequest requestPayload) {
        try {
            UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            requestPayload.addAccessUserDetail(userDetails.getUsername());
            return new ResponseEntity<>(this.credentialService.updateCredential(requestPayload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while updateCredential ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(ProcessUtil.ERROR,
                "Some internal error occurred contact with support."), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * api-status :- done
     * @apiName :- fetchAllCredential
     * Api use to fetch the lookup detail
     * @param requestPayload
     * @return ResponseEntity<?> fetchAllCredential
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('ADMIN')")
    @RequestMapping(value = "/fetchAllCredential", method = RequestMethod.POST)
    public ResponseEntity<?> fetchAllCredential(@RequestBody CredentialRequest requestPayload) {
        try {
            UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            requestPayload.addAccessUserDetail(userDetails.getUsername());
            return new ResponseEntity<>(this.credentialService.fetchAllCredential(requestPayload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while fetchAllCredential ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(ProcessUtil.ERROR,
                "Some internal error occurred contact with support."), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * api-status :- done
     * @apiName :- fetchCredentialByCredentialId
     * Api use to fetch the Credential by Credential id
     * @param requestPayload
     * @return ResponseEntity<?> fetchCredentialByCredentialId
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('ADMIN')")
    @RequestMapping(value = "/fetchCredentialByCredentialId", method = RequestMethod.POST)
    public ResponseEntity<?> fetchCredentialByCredentialId(@RequestBody CredentialRequest requestPayload) {
        try {
            UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            requestPayload.addAccessUserDetail(userDetails.getUsername());
            return new ResponseEntity<>(this.credentialService.fetchCredentialByCredentialId(requestPayload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while fetchCredentialByCredentialId ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(ProcessUtil.ERROR,
                "Some internal error occurred contact with support."), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * api-status :- done
     * @apiName :- deleteCredential
     * Api use to delete the lookup data
     * @param requestPayload
     * @return ResponseEntity<?> deleteCredential
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('ADMIN')")
    @RequestMapping(value = "/deleteCredential", method = RequestMethod.PUT)
    public ResponseEntity<?> deleteCredential(@RequestBody CredentialRequest requestPayload) {
        try {
            UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            requestPayload.addAccessUserDetail(userDetails.getUsername());
            return new ResponseEntity<>(this.credentialService.deleteCredential(requestPayload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while deleteCredential ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(ProcessUtil.ERROR,
                "Some internal error occurred contact with support."), HttpStatus.BAD_REQUEST);
        }
    }
}
