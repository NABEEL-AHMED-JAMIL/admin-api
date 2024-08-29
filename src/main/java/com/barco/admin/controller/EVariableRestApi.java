package com.barco.admin.controller;

import com.barco.admin.service.EVariableService;
import com.barco.common.utility.BarcoUtil;
import com.barco.common.utility.ExceptionUtil;
import com.barco.common.utility.excel.ExcelUtil;
import com.barco.model.dto.request.*;
import com.barco.model.dto.response.AppResponse;
import com.barco.model.security.UserSessionDetail;
import com.barco.model.util.MessageUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * Api use to perform crud operation
 * @author Nabeel Ahmed
 */
@RestController
@CrossOrigin(origins="*")
@RequestMapping(value="/eVariable.json")
@Api(value = "E-Variable Rest Api",
    description = "E-Variable Service : Service related to the [Environment Variable] for user.")
public class EVariableRestApi {

    private Logger logger = LoggerFactory.getLogger(EVariableRestApi.class);

    @Autowired
    private EVariableService eVariableService;

    /**
     * @apiName :- addEnVariable
     * @apiNote :- Method use to add en variable
     * @param payload
     * @return ResponseEntity
     * */
    @ApiOperation(value = "Api use to add new environment variable.", response = ResponseEntity.class)
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('DB') or hasRole('DEV')")
    @RequestMapping(value="/addEnVariable", method= RequestMethod.POST)
    public ResponseEntity<?> addEnVariable(@RequestBody EnVariablesRequest payload) {
        try {
            UserSessionDetail userSessionDetail = (UserSessionDetail) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            payload.setSessionUser(new SessionUser(userSessionDetail.getId(), userSessionDetail.getEmail(), userSessionDetail.getUsername()));
            return new ResponseEntity<>(this.eVariableService.addEnVariable(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while addEnVariable ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ex.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- updateEnVariable
     * @apiNote :- Method use to edit the en variable
     * @param payload
     * @return ResponseEntity
     * */
    @ApiOperation(value = "Api use to update environment variable.", response = ResponseEntity.class)
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('DB') or hasRole('DEV')")
    @RequestMapping(value="/updateEnVariable", method= RequestMethod.POST)
    public ResponseEntity<?> updateEnVariable(@RequestBody EnVariablesRequest payload) {
        try {
            UserSessionDetail userSessionDetail = (UserSessionDetail) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            payload.setSessionUser(new SessionUser(userSessionDetail.getId(), userSessionDetail.getEmail(), userSessionDetail.getUsername()));
            return new ResponseEntity<>(this.eVariableService.updateEnVariable(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while updateEnVariable ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ex.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- fetchAllEnVariable
     * @apiNote :- Method use to fetch the data
     * @param payload
     * @return ResponseEntity
     * */
    @ApiOperation(value = "Api use to fetch all environment variable.", response = ResponseEntity.class)
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('DB') or hasRole('DEV')")
    @RequestMapping(value="/fetchAllEnVariable", method= RequestMethod.POST)
    public ResponseEntity<?> fetchAllEnVariable(@RequestBody EnVariablesRequest payload) {
        try {
            return new ResponseEntity<>(this.eVariableService.fetchAllEnVariable(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while fetchAllEnVariable ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ex.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- fetchEnVariableById
     * @apiNote :- Method use to fetch the data
     * @param payload
     * @return ResponseEntity
     * */
    @ApiOperation(value = "Api use to fetch environment variable by id.", response = ResponseEntity.class)
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('DB') or hasRole('DEV')")
    @RequestMapping(value="/fetchEnVariableById", method= RequestMethod.POST)
    public ResponseEntity<?> fetchEnVariableById(@RequestBody EnVariablesRequest payload) {
        try {
            return new ResponseEntity<>(this.eVariableService.fetchEnVariableById(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while fetchEnVariableById ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ex.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- fetchUserEnvByEnvKey
     * @apiNote :- Method use to fetch the data
     * @param payload
     * @return ResponseEntity
     * */
    @ApiOperation(value = "Api use to fetch environment variable link with user.", response = ResponseEntity.class)
    @RequestMapping(value="/fetchUserEnvByEnvKey", method= RequestMethod.POST)
    public ResponseEntity<?> fetchUserEnvByEnvKey(@RequestBody EnVariablesRequest payload) {
        try {
            UserSessionDetail userSessionDetail = (UserSessionDetail) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            payload.setSessionUser(new SessionUser(userSessionDetail.getId(), userSessionDetail.getEmail(), userSessionDetail.getUsername()));
            return new ResponseEntity<>(this.eVariableService.fetchUserEnvByEnvKey(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while fetchUserEnvByEnvKey ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ex.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- deleteEnVariableById
     * @apiNote :- Method use to fetch the data
     * @param payload
     * @return ResponseEntity
     * */
    @ApiOperation(value = "Api use to delete environment variable by id.", response = ResponseEntity.class)
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('DB') or hasRole('DEV')")
    @RequestMapping(value="/deleteEnVariableById", method= RequestMethod.POST)
    public ResponseEntity<?> deleteEnVariableById(@RequestBody EnVariablesRequest payload) {
        try {
            return new ResponseEntity<>(this.eVariableService.deleteEnVariableById(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while deleteEnVariableById ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ex.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- deleteAllEnVariable
     * @apiNote :- Api use to delete en-variable by ids
     * @param payload
     * @return ResponseEntity<?>
     * */
    @ApiOperation(value = "Api use to delete all environment variables.", response = ResponseEntity.class)
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('DB') or hasRole('DEV')")
    @RequestMapping(path="/deleteAllEnVariable", method=RequestMethod.POST)
    public ResponseEntity<?> deleteAllEnVariable(@RequestBody EnVariablesRequest payload) {
        try {
            return new ResponseEntity<>(this.eVariableService.deleteAllEnVariable(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while deleteAllEnVariable ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ex.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- downloadEnVariableTemplateFile
     * @apiNote :- Api use to download en-variable template
     * @return ResponseEntity<?> downloadEnVariableTemplateFile
     * */
    @ApiOperation(value = "Api use to download template for environment variables.", response = ResponseEntity.class)
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('DB') or hasRole('DEV')")
    @RequestMapping(value = "/downloadEnVariableTemplateFile", method = RequestMethod.GET)
    public ResponseEntity<?> downloadEnVariableTemplateFile() {
        try {
            HttpHeaders headers = new HttpHeaders();
            DateFormat dateFormat = new SimpleDateFormat(BarcoUtil.SIMPLE_DATE_PATTERN);
            String fileName = "BatchEnVariableDownload-"+dateFormat.format(new Date())+"-"+ UUID.randomUUID() + ExcelUtil.XLSX_EXTENSION;
            headers.add(BarcoUtil.CONTENT_DISPOSITION,BarcoUtil.FILE_NAME_HEADER + fileName);
                return ResponseEntity.ok().headers(headers).body(this.eVariableService.downloadEnVariableTemplateFile().toByteArray());
        } catch (Exception ex) {
            logger.error("An error occurred while downloadEnVariableTemplateFile xlsx file", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ex.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- downloadEnVariable
     * @apiNote :- Api use to download the en-variable
     * @return ResponseEntity<?> downloadEnVariable
     * */
    @ApiOperation(value = "Api use to download environment variables.", response = ResponseEntity.class)
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('DB') or hasRole('DEV')")
    @RequestMapping(value = "/downloadEnVariable", method = RequestMethod.POST)
    public ResponseEntity<?> downloadEnVariable(@RequestBody EnVariablesRequest payload) {
        try {
            HttpHeaders headers = new HttpHeaders();
            DateFormat dateFormat = new SimpleDateFormat(BarcoUtil.SIMPLE_DATE_PATTERN);
            String fileName = "BatchEnVariableDownload-"+dateFormat.format(new Date())+"-"+ UUID.randomUUID() + ExcelUtil.XLSX_EXTENSION;
            headers.add(BarcoUtil.CONTENT_DISPOSITION,BarcoUtil.FILE_NAME_HEADER + fileName);
            return ResponseEntity.ok().headers(headers).body(this.eVariableService.downloadEnVariable(payload).toByteArray());
        } catch (Exception ex) {
            logger.error("An error occurred while downloadEnVariable ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ex.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- uploadEnVariable
     * @apiNote :- Api use to upload the en-variable
     * @return ResponseEntity<?> uploadEnVariable
     * */
    @ApiOperation(value = "Api use to upload environment variables.", response = ResponseEntity.class)
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('DB') or hasRole('DEV')")
    @RequestMapping(value = "/uploadEnVariable", method = RequestMethod.POST)
    public ResponseEntity<?> uploadEnVariable(FileUploadRequest payload) {
        try {
            if (!BarcoUtil.isNull(payload.getFile())) {
                return new ResponseEntity<>(this.eVariableService.uploadEnVariable(payload), HttpStatus.OK);
            }
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, MessageUtil.DATA_NOT_FOUND), HttpStatus.BAD_REQUEST);
        } catch (Exception ex) {
            logger.error("An error occurred while uploadEnVariable ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ex.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- fetchLinkEVariableWitUser
     * @apiNote :- Api use to fetch e-variable with root user
     * @return ResponseEntity<?> fetchLinkEVariableWitUser
     * */
    @ApiOperation(value = "Api use to fetch link environment variables with user.", response = ResponseEntity.class)
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('DB') or hasRole('DEV')")
    @RequestMapping(value = "/fetchLinkEVariableWitUser", method = RequestMethod.POST)
    public ResponseEntity<?> fetchLinkEVariableWitUser(@RequestBody LinkEURequest payload) {
        try {
            return new ResponseEntity<>(this.eVariableService.fetchLinkEVariableWitUser(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while fetchLinkEVariableWitUser ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ex.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- linkEVariableWithUser
     * @apiNote :- Api use to link e-variable with root user
     * @return ResponseEntity<?> linkEVariableWithUser
     * */
    @ApiOperation(value = "Api use to link environment variables with user.", response = ResponseEntity.class)
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('DB') or hasRole('DEV')")
    @RequestMapping(value = "/linkEVariableWithUser", method = RequestMethod.POST)
    public ResponseEntity<?> linkEVariableWithUser(@RequestBody LinkEURequest payload) {
        try {
            UserSessionDetail userSessionDetail = (UserSessionDetail) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            payload.setSessionUser(new SessionUser(userSessionDetail.getId(), userSessionDetail.getEmail(), userSessionDetail.getUsername()));
            return new ResponseEntity<>(this.eVariableService.linkEVariableWithUser(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while linkEVariableWithUser ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ex.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

}
