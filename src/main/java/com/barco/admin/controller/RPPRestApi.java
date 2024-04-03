package com.barco.admin.controller;

import com.barco.admin.service.RPPService;
import com.barco.common.utility.BarcoUtil;
import com.barco.common.utility.ExceptionUtil;
import com.barco.common.utility.excel.ExcelUtil;
import com.barco.model.dto.request.*;
import com.barco.model.dto.response.AppResponse;
import com.barco.model.security.UserSessionDetail;
import com.barco.model.util.MessageUtil;
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
 * Api use to perform the rpp
 * @author Nabeel Ahmed
 */
@RestController
@CrossOrigin(origins = "*")
@RequestMapping(value = "/rpp.json")
public class RPPRestApi {

    private Logger logger = LoggerFactory.getLogger(RPPRestApi.class);

    @Autowired
    private RPPService rppService;

    /**
     * Api use to create the role
     * @param payload
     * @return ResponseEntity<?>
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('DB') or hasRole('DEV')")
    @RequestMapping(path="/addRole", method=RequestMethod.POST)
    public ResponseEntity<?> addRole(@RequestBody RoleRequest payload) {
        try {
            return new ResponseEntity<>(this.rppService.addRole(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while addRole ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ExceptionUtil.getRootCauseMessage(ex)), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Api use to update the role
     * @param payload
     * @return ResponseEntity<?>
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('DB') or hasRole('DEV')")
    @RequestMapping(path="/updateRole", method=RequestMethod.POST)
    public ResponseEntity<?> updateRole(@RequestBody RoleRequest payload) {
        try {
            return new ResponseEntity<>(this.rppService.updateRole(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while updateRole ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ExceptionUtil.getRootCauseMessage(ex)), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Api use to find all role
     * @param payload
     * @return ResponseEntity<?>
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('DB') or hasRole('DEV')")
    @RequestMapping(path="/fetchAllRole", method=RequestMethod.POST)
    public ResponseEntity<?> fetchAllRole(@RequestBody RoleRequest payload) {
        try {
            return new ResponseEntity<>(this.rppService.fetchAllRole(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while fetchAllRole ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ExceptionUtil.getRootCauseMessage(ex)), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Api use to find the role py id
     * @param payload
     * @return ResponseEntity<?>
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('DB') or hasRole('DEV')")
    @RequestMapping(path="/findRoleById", method=RequestMethod.POST)
    public ResponseEntity<?> findRoleById(@RequestBody RoleRequest payload) {
        try {
            return new ResponseEntity<>(this.rppService.findRoleById(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while findRoleById ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ExceptionUtil.getRootCauseMessage(ex)), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Api use to find the role py id
     * @param payload
     * @return ResponseEntity<?>
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('DB') or hasRole('DEV')")
    @RequestMapping(path="/deleteRoleById", method=RequestMethod.POST)
    public ResponseEntity<?> deleteRoleById(@RequestBody RoleRequest payload) {
        try {
            return new ResponseEntity<>(this.rppService.deleteRoleById(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while deleteRoleById ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ExceptionUtil.getRootCauseMessage(ex)), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- downloadRoleTemplateFile
     * Api use to download role template
     * @return ResponseEntity<?> downloadRoleTemplateFile
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('DB') or hasRole('DEV')")
    @RequestMapping(value = "/downloadRoleTemplateFile", method = RequestMethod.GET)
    public ResponseEntity<?> downloadRoleTemplateFile() {
        try {
            HttpHeaders headers = new HttpHeaders();
            DateFormat dateFormat = new SimpleDateFormat(BarcoUtil.SIMPLE_DATE_PATTERN);
            String fileName = "BatchRoleDownload-"+dateFormat.format(new Date())+"-"+ UUID.randomUUID() + ExcelUtil.XLSX_EXTENSION;
            headers.add(BarcoUtil.CONTENT_DISPOSITION,BarcoUtil.FILE_NAME_HEADER + fileName);
            return ResponseEntity.ok().headers(headers).body(this.rppService.downloadRoleTemplateFile().toByteArray());
        } catch (Exception ex) {
            logger.error("An error occurred while downloadRoleTemplateFile xlsx file", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ExceptionUtil.getRootCauseMessage(ex)), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- downloadRole
     * Api use to download the role data
     * @return ResponseEntity<?> downloadRole
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('DB') or hasRole('DEV')")
    @RequestMapping(value = "/downloadRole", method = RequestMethod.POST)
    public ResponseEntity<?> downloadRole(@RequestBody RoleRequest payload) {
        try {
            HttpHeaders headers = new HttpHeaders();
            DateFormat dateFormat = new SimpleDateFormat(BarcoUtil.SIMPLE_DATE_PATTERN);
            String fileName = "BatchRoleDownload-"+dateFormat.format(new Date())+"-"+ UUID.randomUUID() + ExcelUtil.XLSX_EXTENSION;
            headers.add(BarcoUtil.CONTENT_DISPOSITION,BarcoUtil.FILE_NAME_HEADER + fileName);
            return ResponseEntity.ok().headers(headers).body(this.rppService.downloadRole(payload).toByteArray());
        } catch (Exception ex) {
            logger.error("An error occurred while downloadRole ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ExceptionUtil.getRootCauseMessage(ex)), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- uploadRole
     * Api use to upload the role
     * @return ResponseEntity<?> uploadRole
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('DB') or hasRole('DEV')")
    @RequestMapping(value = "/uploadRole", method = RequestMethod.POST)
    public ResponseEntity<?> uploadRole(FileUploadRequest payload) {
        try {
            if (!BarcoUtil.isNull(payload.getFile())) {
                return new ResponseEntity<>(this.rppService.uploadRole(payload), HttpStatus.OK);
            }
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, MessageUtil.DATA_NOT_FOUND), HttpStatus.BAD_REQUEST);
        } catch (Exception ex) {
            logger.error("An error occurred while uploadRole ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ExceptionUtil.getRootCauseMessage(ex)), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Api use to add the profile
     * @param payload
     * @return ResponseEntity<?>
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('DB') or hasRole('DEV')")
    @RequestMapping(path="/addProfile", method=RequestMethod.POST)
    public ResponseEntity<?> addProfile(@RequestBody ProfileRequest payload) {
        try {
            return new ResponseEntity<>(this.rppService.addProfile(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while addProfile ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ExceptionUtil.getRootCauseMessage(ex)), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Api use to update the profile
     * @param payload
     * @return ResponseEntity<?>
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('DB') or hasRole('DEV')")
    @RequestMapping(path="/updateProfile", method=RequestMethod.POST)
    public ResponseEntity<?> updateProfile(@RequestBody ProfileRequest payload) {
        try {
            return new ResponseEntity<>(this.rppService.updateProfile(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while updateProfile ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ExceptionUtil.getRootCauseMessage(ex)), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Api use to fetch all profile
     * @param payload
     * @return ResponseEntity<?>
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('DB') or hasRole('DEV')")
    @RequestMapping(path="/fetchAllProfile", method=RequestMethod.POST)
    public ResponseEntity<?> fetchAllProfile(@RequestBody ProfileRequest payload) {
        try {
            return new ResponseEntity<>(this.rppService.fetchAllProfile(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while fetchAllProfile ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ExceptionUtil.getRootCauseMessage(ex)), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Api use to fetch profile by id
     * @param payload
     * @return ResponseEntity<?>
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('DB') or hasRole('DEV')")
    @RequestMapping(path="/fetchProfileById",  method=RequestMethod.POST)
    public ResponseEntity<?> fetchProfileById(@RequestBody ProfileRequest payload) {
        try {
            return new ResponseEntity<>(this.rppService.fetchProfileById(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while fetchProfileById ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ExceptionUtil.getRootCauseMessage(ex)), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Api use to delete profile by id
     * @param payload
     * @return ResponseEntity<?>
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('DB') or hasRole('DEV')")
    @RequestMapping(path="/deleteProfileById",  method=RequestMethod.POST)
    public ResponseEntity<?> deleteProfileById(@RequestBody ProfileRequest payload) {
        try {
            return new ResponseEntity<>(this.rppService.deleteProfileById(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while deleteProfileById ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ExceptionUtil.getRootCauseMessage(ex)), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- downloadProfileTemplateFile
     * Api use to download profile template
     * @return ResponseEntity<?> downloadProfileTemplateFile
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('DB') or hasRole('DEV')")
    @RequestMapping(value = "/downloadProfileTemplateFile", method = RequestMethod.GET)
    public ResponseEntity<?> downloadProfileTemplateFile() {
        try {
            HttpHeaders headers = new HttpHeaders();
            DateFormat dateFormat = new SimpleDateFormat(BarcoUtil.SIMPLE_DATE_PATTERN);
            String fileName = "BatchProfileDownload-"+dateFormat.format(new Date())+"-"+ UUID.randomUUID() + ExcelUtil.XLSX_EXTENSION;
            headers.add(BarcoUtil.CONTENT_DISPOSITION,BarcoUtil.FILE_NAME_HEADER + fileName);
            return ResponseEntity.ok().headers(headers).body(this.rppService.downloadProfileTemplateFile().toByteArray());
        } catch (Exception ex) {
            logger.error("An error occurred while downloadProfileTemplateFile xlsx file", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ExceptionUtil.getRootCauseMessage(ex)), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- downloadProfile
     * Api use to download the profile
     * @return ResponseEntity<?> downloadProfile
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('DB') or hasRole('DEV')")
    @RequestMapping(value = "/downloadProfile", method = RequestMethod.POST)
    public ResponseEntity<?> downloadProfile(@RequestBody ProfileRequest payload) {
        try {
            HttpHeaders headers = new HttpHeaders();
            DateFormat dateFormat = new SimpleDateFormat(BarcoUtil.SIMPLE_DATE_PATTERN);
            String fileName = "BatchProfileDownload-"+dateFormat.format(new Date())+"-"+ UUID.randomUUID() + ExcelUtil.XLSX_EXTENSION;
            headers.add(BarcoUtil.CONTENT_DISPOSITION,BarcoUtil.FILE_NAME_HEADER + fileName);
            return ResponseEntity.ok().headers(headers).body(this.rppService.downloadProfile(payload).toByteArray());
        } catch (Exception ex) {
            logger.error("An error occurred while downloadProfile ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ExceptionUtil.getRootCauseMessage(ex)), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- uploadProfile
     * Api use to upload the profile
     * @return ResponseEntity<?> uploadProfile
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('DB') or hasRole('DEV')")
    @RequestMapping(value = "/uploadProfile", method = RequestMethod.POST)
    public ResponseEntity<?> uploadProfile(FileUploadRequest payload) {
        try {
            if (!BarcoUtil.isNull(payload.getFile())) {
                return new ResponseEntity<>(this.rppService.uploadProfile(payload), HttpStatus.OK);
            }
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, MessageUtil.DATA_NOT_FOUND), HttpStatus.BAD_REQUEST);
        } catch (Exception ex) {
            logger.error("An error occurred while uploadProfile ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ExceptionUtil.getRootCauseMessage(ex)), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Api use to fetch add the permission
     * @param payload
     * @return ResponseEntity<?>
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('DB') or hasRole('DEV')")
    @RequestMapping(path="/addPermission", method=RequestMethod.POST)
    public ResponseEntity<?> addPermission(@RequestBody PermissionRequest payload) {
        try {
            return new ResponseEntity<>(this.rppService.addPermission(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while addPermission ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ExceptionUtil.getRootCauseMessage(ex)), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Api use to fetch update the permission
     * @param payload
     * @return ResponseEntity<?>
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('DB') or hasRole('DEV')")
    @RequestMapping(path="/updatePermission", method=RequestMethod.POST)
    public ResponseEntity<?> updatePermission(@RequestBody PermissionRequest payload) {
        try {
            return new ResponseEntity<>(this.rppService.updatePermission(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while updatePermission ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ExceptionUtil.getRootCauseMessage(ex)), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Api use to fetch all the permission
     * @param payload
     * @return ResponseEntity<?>
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('DB') or hasRole('DEV')")
    @RequestMapping(path="/fetchAllPermission", method=RequestMethod.POST)
    public ResponseEntity<?> fetchAllPermission(@RequestBody PermissionRequest payload) {
        try {
            return new ResponseEntity<>(this.rppService.fetchAllPermission(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while fetchAllPermission ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ExceptionUtil.getRootCauseMessage(ex)), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Api use to fetch permission by id
     * @param payload
     * @return ResponseEntity<?>
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('DB') or hasRole('DEV')")
    @RequestMapping(path="/fetchPermissionById", method=RequestMethod.POST)
    public ResponseEntity<?> fetchPermissionById(@RequestBody PermissionRequest payload) {
        try {
            return new ResponseEntity<>(this.rppService.fetchPermissionById(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while fetchPermissionById ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ExceptionUtil.getRootCauseMessage(ex)), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Api use to delete permission by id
     * @param payload
     * @return ResponseEntity<?>
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('DB') or hasRole('DEV')")
    @RequestMapping(path="/deletePermissionById", method=RequestMethod.POST)
    public ResponseEntity<?> deletePermissionById(@RequestBody PermissionRequest payload) {
        try {
            return new ResponseEntity<>(this.rppService.deletePermissionById(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while deletePermissionById ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ExceptionUtil.getRootCauseMessage(ex)), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- downloadPermissionTemplateFile
     * Api use to download permission template
     * @return ResponseEntity<?> downloadPermissionTemplateFile
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('DB') or hasRole('DEV')")
    @RequestMapping(value = "/downloadPermissionTemplateFile", method = RequestMethod.GET)
    public ResponseEntity<?> downloadPermissionTemplateFile() {
        try {
            HttpHeaders headers = new HttpHeaders();
            DateFormat dateFormat = new SimpleDateFormat(BarcoUtil.SIMPLE_DATE_PATTERN);
            String fileName = "BatchPermissionDownload-"+dateFormat.format(new Date())+"-"+ UUID.randomUUID() + ExcelUtil.XLSX_EXTENSION;
            headers.add(BarcoUtil.CONTENT_DISPOSITION,BarcoUtil.FILE_NAME_HEADER + fileName);
            return ResponseEntity.ok().headers(headers).body(this.rppService.downloadPermissionTemplateFile().toByteArray());
        } catch (Exception ex) {
            logger.error("An error occurred while downloadPermissionTemplateFile xlsx file", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ExceptionUtil.getRootCauseMessage(ex)), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- downloadPermission
     * Api use to download the permission
     * @return ResponseEntity<?> downloadPermission
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('DB') or hasRole('DEV')")
    @RequestMapping(value = "/downloadPermission", method = RequestMethod.POST)
    public ResponseEntity<?> downloadPermission(@RequestBody PermissionRequest payload) {
        try {
            HttpHeaders headers = new HttpHeaders();
            DateFormat dateFormat = new SimpleDateFormat(BarcoUtil.SIMPLE_DATE_PATTERN);
            String fileName = "BatchPermissionDownload-"+dateFormat.format(new Date())+"-"+ UUID.randomUUID() + ExcelUtil.XLSX_EXTENSION;
            headers.add(BarcoUtil.CONTENT_DISPOSITION,BarcoUtil.FILE_NAME_HEADER + fileName);
            return ResponseEntity.ok().headers(headers).body(this.rppService.downloadPermission(payload).toByteArray());
        } catch (Exception ex) {
            logger.error("An error occurred while downloadPermission ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ExceptionUtil.getRootCauseMessage(ex)), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- uploadPermission
     * Api use to upload the permission
     * @return ResponseEntity<?> uploadPermission
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('DB') or hasRole('DEV')")
    @RequestMapping(value = "/uploadPermission", method = RequestMethod.POST)
    public ResponseEntity<?> uploadPermission(FileUploadRequest payload) {
        try {
            if (!BarcoUtil.isNull(payload.getFile())) {
                return new ResponseEntity<>(this.rppService.uploadPermission(payload), HttpStatus.OK);
            }
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, MessageUtil.DATA_NOT_FOUND), HttpStatus.BAD_REQUEST);
        } catch (Exception ex) {
            logger.error("An error occurred while uploadPermission ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ExceptionUtil.getRootCauseMessage(ex)), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- fetchLinkProfilePermission
     * Api use to fetch link-> profile & permission
     * @return ResponseEntity<?> fetchLinkProfilePermission
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('DB') or hasRole('DEV')")
    @RequestMapping(value = "/fetchLinkProfilePermission", method = RequestMethod.POST)
    public ResponseEntity<?> fetchLinkProfilePermission(@RequestBody LinkPPRequest payload) {
        try {
            return new ResponseEntity<>(this.rppService.fetchLinkProfilePermission(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while fetchLinkProfilePermission ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ExceptionUtil.getRootCauseMessage(ex)), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- updateLinkProfilePermission
     * Api use to add/edit link-> profile & permission
     * @return ResponseEntity<?> updateLinkProfilePermission
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('DB') or hasRole('DEV')")
    @RequestMapping(value = "/updateLinkProfilePermission", method = RequestMethod.POST)
    public ResponseEntity<?> updateLinkProfilePermission(@RequestBody LinkPPRequest payload) {
        try {
            return new ResponseEntity<>(this.rppService.updateLinkProfilePermission(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while updateLinkProfilePermission ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ExceptionUtil.getRootCauseMessage(ex)), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Api use to fetch the role with root user
     * @param payload
     * @return ResponseEntity<?>
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('DB') or hasRole('DEV')")
    @RequestMapping(path="/fetchLinkRoleWithRootUser", method=RequestMethod.POST)
    public ResponseEntity<?> fetchLinkRoleWithRootUser(@RequestBody LinkRURequest payload) {
        try {
            UserSessionDetail userSessionDetail = (UserSessionDetail) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            payload.setSessionUser(new SessionUser(userSessionDetail.getId(), userSessionDetail.getEmail(), userSessionDetail.getUsername()));
            return new ResponseEntity<>(this.rppService.fetchLinkRoleWithRootUser(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while fetchLinkRoleWithRootUser ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ExceptionUtil.getRootCauseMessage(ex)), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Api use to link the role with root user
     * @param payload
     * @return ResponseEntity<?>
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('DB') or hasRole('DEV')")
    @RequestMapping(path="/linkRoleWithRootUser", method=RequestMethod.POST)
    public ResponseEntity<?> linkRoleWithRootUser(@RequestBody LinkRURequest payload) {
        try {
            UserSessionDetail userSessionDetail = (UserSessionDetail) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            payload.setSessionUser(new SessionUser(userSessionDetail.getId(), userSessionDetail.getEmail(), userSessionDetail.getUsername()));
            return new ResponseEntity<>(this.rppService.linkRoleWithRootUser(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while linkRoleWithRootUser ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ExceptionUtil.getRootCauseMessage(ex)), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Api use to fetch the profile with root user
     * @param payload
     * @return ResponseEntity<?>
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('DB') or hasRole('DEV')")
    @RequestMapping(path="/fetchLinkProfileWithRootUser", method=RequestMethod.POST)
    public ResponseEntity<?> fetchLinkProfileWithRootUser(@RequestBody LinkPURequest payload) {
        try {
            UserSessionDetail userSessionDetail = (UserSessionDetail) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            payload.setSessionUser(new SessionUser(userSessionDetail.getId(), userSessionDetail.getEmail(), userSessionDetail.getUsername()));
            return new ResponseEntity<>(this.rppService.fetchLinkProfileWithRootUser(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while fetchLinkProfileWithRootUser ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ExceptionUtil.getRootCauseMessage(ex)), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Api use to link the profile with root user
     * @param payload
     * @return ResponseEntity<?>
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('DB') or hasRole('DEV')")
    @RequestMapping(path="/linkProfileWithRootUser", method=RequestMethod.POST)
    public ResponseEntity<?> linkProfileWithRootUser(@RequestBody LinkPURequest payload) {
        try {
            UserSessionDetail userSessionDetail = (UserSessionDetail) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            payload.setSessionUser(new SessionUser(userSessionDetail.getId(), userSessionDetail.getEmail(), userSessionDetail.getUsername()));
            return new ResponseEntity<>(this.rppService.linkProfileWithRootUser(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while linkProfileWithRootUser ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ExceptionUtil.getRootCauseMessage(ex)), HttpStatus.BAD_REQUEST);
        }
    }

}
