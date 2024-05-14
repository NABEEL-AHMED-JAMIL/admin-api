package com.barco.admin.controller;

import com.barco.admin.service.SourceTaskService;
import com.barco.common.utility.BarcoUtil;
import com.barco.common.utility.ExceptionUtil;
import com.barco.model.dto.request.STTRequest;
import com.barco.model.dto.request.SourceTaskRequest;
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
@CrossOrigin(origins="*")
@RequestMapping(value="/source_task.json")
public class SourceTaskRestApi {

    private Logger logger = LoggerFactory.getLogger(SourceTaskRestApi.class);

    @Autowired
    private SourceTaskService sourceTaskService;

    /**
     * @apiName :- addSourceTask
     * @apiNote :- Api use to add the source task
     * @param payload
     * @return ResponseEntity<?> addSourceTask
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('ADMIN') or hasRole('USER')")
    @RequestMapping(value = "/addSourceTask", method = RequestMethod.POST)
    public ResponseEntity<?> addSourceTask(@RequestBody SourceTaskRequest payload) {
        try {
            return new ResponseEntity<>(this.sourceTaskService.addSourceTask(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while addSourceTask ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ExceptionUtil.getRootCauseMessage(ex)), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- editSourceTask
     * @apiNote :- Api use to edit the source task
     * @param payload
     * @return ResponseEntity<?> editSourceTask
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('ADMIN') or hasRole('USER')")
    @RequestMapping(value = "/editSourceTask", method = RequestMethod.POST)
    public ResponseEntity<?> editSourceTask(@RequestBody SourceTaskRequest payload) {
        try {
            return new ResponseEntity<>(this.sourceTaskService.editSourceTask(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while editSourceTask ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ExceptionUtil.getRootCauseMessage(ex)), HttpStatus.BAD_REQUEST);
        }
    }

    // delete source task
    /**
     * @apiName :- deleteSourceTask
     * @apiNote :- Api use to delete the source task
     * @param payload
     * @return ResponseEntity<?> deleteSourceTask
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('ADMIN') or hasRole('USER')")
    @RequestMapping(value = "/deleteSourceTask", method = RequestMethod.POST)
    public ResponseEntity<?> deleteSourceTask(@RequestBody SourceTaskRequest payload) {
        try {
            return new ResponseEntity<>(this.sourceTaskService.deleteSourceTask(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while deleteSourceTask ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ExceptionUtil.getRootCauseMessage(ex)), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- deleteAllSourceTask
     * @apiNote :- Api use to add the credential data
     * @param payload
     * @return ResponseEntity<?> deleteAllSourceTask
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('ADMIN') or hasRole('USER')")
    @RequestMapping(value = "/deleteAllSourceTask", method = RequestMethod.POST)
    public ResponseEntity<?> deleteAllSourceTask(@RequestBody SourceTaskRequest payload) {
        try {
            return new ResponseEntity<>(this.sourceTaskService.deleteAllSourceTask(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while deleteAllSourceTask ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ExceptionUtil.getRootCauseMessage(ex)), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- fetchAllSourceTask
     * @apiNote :- Api use to add the credential data
     * @param payload
     * @return ResponseEntity<?> fetchAllSourceTask
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('ADMIN') or hasRole('USER')")
    @RequestMapping(value = "/fetchAllSourceTask", method = RequestMethod.POST)
    public ResponseEntity<?> fetchAllSourceTask(@RequestBody SourceTaskRequest payload) {
        try {
            return new ResponseEntity<>(this.sourceTaskService.fetchAllSourceTask(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while fetchAllSourceTask ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ExceptionUtil.getRootCauseMessage(ex)), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- fetchSourceTaskById
     * @apiNote :- Api use to fetch the source task by id
     * @param payload
     * @return ResponseEntity<?> fetchSourceTaskById
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('ADMIN') or hasRole('USER')")
    @RequestMapping(value = "/fetchSourceTaskById", method = RequestMethod.POST)
    public ResponseEntity<?> fetchSourceTaskById(@RequestBody SourceTaskRequest payload) {
        try {
            return new ResponseEntity<>(this.sourceTaskService.fetchSourceTaskById(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while fetchSourceTaskById ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ExceptionUtil.getRootCauseMessage(ex)), HttpStatus.BAD_REQUEST);
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
            return new ResponseEntity<>(this.sourceTaskService.fetchAllSTT(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while fetchAllSTT ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ExceptionUtil.getRootCauseMessage(ex)), HttpStatus.BAD_REQUEST);
        }
    }

}
