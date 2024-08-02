package com.barco.admin.controller;

import com.barco.admin.service.SourceTaskTypeService;
import com.barco.common.utility.BarcoUtil;
import com.barco.common.utility.ExceptionUtil;
import com.barco.model.dto.request.STTRequest;
import com.barco.model.dto.response.AppResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * @author Nabeel Ahmed
 */
@RestController
@CrossOrigin(origins="*")
@RequestMapping(value="/stt.json")
public class SourceTaskTypeRestApi {

    private Logger logger = LoggerFactory.getLogger(SourceTaskTypeRestApi.class);

    @Autowired
    private SourceTaskTypeService sourceTaskTypeService;

    /**
     * @apiName :- addSTT
     * @apiNote :- Api use to create stt (source task type)
     * @param payload
     * @return ResponseEntity<?>
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('ADMIN') or hasRole('USER')")
    @RequestMapping(value = "/addSTT", method = RequestMethod.POST)
    public ResponseEntity<?> addSTT(@RequestBody STTRequest payload) {
        try {
            return new ResponseEntity<>(this.sourceTaskTypeService.addSTT(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while addSTT ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ex.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- editSTT
     * @apiNote :- Api use to update stt (source task type)
     * @param payload
     * @return ResponseEntity<?>
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('ADMIN') or hasRole('USER')")
    @RequestMapping(value = "/editSTT", method = RequestMethod.POST)
    public ResponseEntity<?> editSTT(@RequestBody STTRequest payload) {
        try {
            return new ResponseEntity<>(this.sourceTaskTypeService.editSTT(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while editSTT ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ex.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- deleteSTT
     * @apiNote :- Api use to delete stt (source task type)
     * @param payload
     * @return ResponseEntity<?>
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('ADMIN') or hasRole('USER')")
    @RequestMapping(value = "/deleteSTT", method = RequestMethod.POST)
    public ResponseEntity<?> deleteSTT(@RequestBody STTRequest payload) {
        try {
            return new ResponseEntity<>(this.sourceTaskTypeService.deleteSTT(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while deleteSTT ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ex.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- fetchSTTBySttId
     * @apiNote :- Api use to fetch stt by stt id(source task type)
     * @param payload
     * @return ResponseEntity<?>
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('ADMIN') or hasRole('USER')")
    @RequestMapping(value = "/fetchSTTBySttId", method = RequestMethod.POST)
    public ResponseEntity<?> fetchSTTBySttId(@RequestBody STTRequest payload) {
        try {
            return new ResponseEntity<>(this.sourceTaskTypeService.fetchSTTBySttId(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while fetchSTTBySttId ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ex.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- fetchAllSTT
     * @apiNote :- Api use to fetch stt(source task type)
     * @param payload
     * @return ResponseEntity<?>
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('ADMIN') or hasRole('USER')")
    @RequestMapping(value = "/fetchAllSTT", method = RequestMethod.POST)
    public ResponseEntity<?> fetchAllSTT(@RequestBody STTRequest payload) {
        try {
            return new ResponseEntity<>(this.sourceTaskTypeService.fetchAllSTT(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while fetchAllSTT ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ex.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- deleteAllSTT
     * @apiNote :- Api use to delete all stt
     * @param payload
     * @return ResponseEntity<?>
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('ADMIN') or hasRole('USER')")
    @RequestMapping(value = "/deleteAllSTT", method = RequestMethod.POST)
    public ResponseEntity<?> deleteAllSTT(@RequestBody STTRequest payload) {
        try {
            return new ResponseEntity<>(this.sourceTaskTypeService.deleteAllSTT(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while deleteAllSTT ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ex.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- fetchAllSTTLinkForm
     * @apiNote :- Api use to fetch link stt with form
     * @param payload
     * @return ResponseEntity<?>
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('ADMIN') or hasRole('USER')")
    @RequestMapping(value = "/fetchAllSTTLinkForm", method = RequestMethod.POST)
    public ResponseEntity<?> fetchAllSTTLinkForm(@RequestBody STTRequest payload) {
        try {
            return new ResponseEntity<>(this.sourceTaskTypeService.fetchAllSTTLinkForm(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while fetchAllSTTLinkForm ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ex.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- linkSTTForm
     * @apiNote :- Api use to link stt with form
     * @param payload
     * @return ResponseEntity<?>
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('ADMIN') or hasRole('USER')")
    @RequestMapping(value = "/linkSTTForm", method = RequestMethod.POST)
    public ResponseEntity<?> linkSTTForm(@RequestBody STTRequest payload) {
        try {
            return new ResponseEntity<>(this.sourceTaskTypeService.linkSTTForm(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while linkSTTForm ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ex.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

}
