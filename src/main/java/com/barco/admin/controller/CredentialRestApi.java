package com.barco.admin.controller;

import com.barco.admin.service.CredentialService;
import com.barco.common.utility.BarcoUtil;
import com.barco.common.utility.ExceptionUtil;
import com.barco.model.dto.request.CredentialRequest;
import com.barco.model.dto.response.AppResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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
     * @apiName :- addCredential
     * @apiNote :- Api use to add the credential data
     * @param payload
     * @return ResponseEntity<?> addCredential
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('ADMIN') or hasRole('USER')")
    @RequestMapping(value = "/addCredential", method = RequestMethod.POST)
    public ResponseEntity<?> addCredential(@RequestBody CredentialRequest payload) {
        try {
            return new ResponseEntity<>(this.credentialService.addCredential(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while addCredential ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ex.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- updateCredential
     * @apiNote :- Api use to update the credential data
     * @param payload
     * @return ResponseEntity<?> updateCredential
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('ADMIN') or hasRole('USER')")
    @RequestMapping(value = "/updateCredential", method = RequestMethod.PUT)
    public ResponseEntity<?> updateCredential(@RequestBody CredentialRequest payload) {
        try {
            return new ResponseEntity<>(this.credentialService.updateCredential(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while updateCredential ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ex.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- fetchAllCredential
     * @apiNote :- Api use to fetch the lookup detail
     * @return ResponseEntity<?> fetchAllCredential
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('ADMIN') or hasRole('USER')")
    @RequestMapping(value = "/fetchAllCredential", method = RequestMethod.POST)
    public ResponseEntity<?> fetchAllCredential(@RequestBody CredentialRequest payload) {
        try {
            return new ResponseEntity<>(this.credentialService.fetchAllCredential(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while fetchAllCredential ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ex.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- fetchAllCredentialByType
     * @apiNote :- Api use to fetch the Credential by Credential Type
     * @param payload
     * @return ResponseEntity<?> fetchAllCredentialByType
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('ADMIN') or hasRole('USER')")
    @RequestMapping(value = "/fetchAllCredentialByType", method = RequestMethod.POST)
    public ResponseEntity<?> fetchAllCredentialByType(@RequestBody CredentialRequest payload) {
        try {
            return new ResponseEntity<>(this.credentialService.fetchAllCredentialByType(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while fetchAllCredentialByType ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ex.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- fetchCredentialByCredentialId
     * @apiNote :- Api use to fetch the Credential by Credential id
     * @param payload
     * @return ResponseEntity<?> fetchCredentialByCredentialId
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('ADMIN') or hasRole('USER')")
    @RequestMapping(value = "/fetchCredentialById", method = RequestMethod.POST)
    public ResponseEntity<?> fetchCredentialById(@RequestBody CredentialRequest payload) {
        try {
            return new ResponseEntity<>(this.credentialService.fetchCredentialById(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while fetchCredentialById ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ex.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- deleteCredential
     * @apiNote :- Api use to delete the lookup data
     * @param payload
     * @return ResponseEntity<?> deleteCredential
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('ADMIN') or hasRole('USER')")
    @RequestMapping(value = "/deleteCredential", method = RequestMethod.PUT)
    public ResponseEntity<?> deleteCredential(@RequestBody CredentialRequest payload) {
        try {
            return new ResponseEntity<>(this.credentialService.deleteCredential(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while deleteCredential ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ex.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- deleteAllCredential
     * @apiNote :- Api use to delete templateReg
     * @param payload
     * @return ResponseEntity<?>
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('ADMIN') or hasRole('USER')")
    @RequestMapping(path="/deleteAllCredential", method=RequestMethod.POST)
    public ResponseEntity<?> deleteAllCredential(@RequestBody CredentialRequest payload) {
        try {
            return new ResponseEntity<>(this.credentialService.deleteAllCredential(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while deleteAllCredential ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ex.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }
}
