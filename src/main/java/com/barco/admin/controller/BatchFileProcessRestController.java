package com.barco.admin.controller;

import com.barco.common.utility.ApplicationConstants;
import com.barco.common.utility.ExceptionUtil;
import com.barco.model.dto.BatchFileDto;
import com.barco.model.dto.ResponseDTO;
import com.barco.model.enums.ApiCode;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * @author Nabeel Ahmed
 */
@RestController
@CrossOrigin(origins = "*")
@RequestMapping(value = "/batch.json", produces = MediaType.APPLICATION_JSON_VALUE)
@Api(tags = { "Batch-File-Process := Batch File Process Rest-EndPoint" })
public class BatchFileProcessRestController {

    private Logger logger = LoggerFactory.getLogger(BatchFileProcessRestController.class);

//    // create batch
//    @ResponseStatus(HttpStatus.OK)
//    @RequestMapping(value = "/create-batch", method = RequestMethod.POST)
//    @ApiOperation(value = "Download BatchFile Template", notes = "Endpoint help download batchFile template.")
//    public ResponseEntity<?> createBatch(@RequestBody BatchFileDto batchFileDto) {
//        ResponseEntity response = null;
//        try {
//            logger.info("Request for downloadBatchFile " + batchFileDto);
//        } catch (Exception ex) {
//            logger.info("Error during downloadBatchFile " + ExceptionUtil.getRootCause(ex));
//            response = new ResponseEntity<>(
//                    new ResponseDTO (ApiCode.HTTP_500, ApplicationConstants.UNEXPECTED_ERROR), HttpStatus.OK);
//        }
//        return response;
//    }
//
//    // delete batch
//    @ResponseStatus(HttpStatus.OK)
//    @RequestMapping(value = "/downloadBatchFile", method = RequestMethod.POST)
//    @ApiOperation(value = "Download BatchFile Template", notes = "Endpoint help download batchFile template.")
//    public ResponseEntity<?> downloadBatchFile(@RequestBody BatchFileDto batchFileDto) {
//        ResponseEntity response = null;
//        try {
//            logger.info("Request for downloadBatchFile " + batchFileDto);
//            HttpHeaders headers = new HttpHeaders();
//            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
//            String fileName = "BatchUpload-"+dateFormat.format(new Date())+"-"+ UUID.randomUUID() + ".xls";
//            headers.add("Content-Disposition", "attachment; filename=" + fileName);
//
//        } catch (Exception ex) {
//            logger.info("Error during downloadBatchFile " + ExceptionUtil.getRootCause(ex));
//            response = new ResponseEntity<>(
//                    new ResponseDTO (ApiCode.HTTP_500, ApplicationConstants.UNEXPECTED_ERROR), HttpStatus.OK);
//        }
//        return response;
//    }
//
//    // update batch
//    @ResponseStatus(HttpStatus.OK)
//    @RequestMapping(value = "/downloadBatchFile", method = RequestMethod.POST)
//    @ApiOperation(value = "Download BatchFile Template", notes = "Endpoint help download batchFile template.")
//    public ResponseEntity<?> downloadBatchFile(@RequestBody BatchFileDto batchFileDto) {
//        ResponseEntity response = null;
//        try {
//            logger.info("Request for downloadBatchFile " + batchFileDto);
//            HttpHeaders headers = new HttpHeaders();
//            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
//            String fileName = "BatchUpload-"+dateFormat.format(new Date())+"-"+ UUID.randomUUID() + ".xls";
//            headers.add("Content-Disposition", "attachment; filename=" + fileName);
//
//        } catch (Exception ex) {
//            logger.info("Error during downloadBatchFile " + ExceptionUtil.getRootCause(ex));
//            response = new ResponseEntity<>(
//                    new ResponseDTO (ApiCode.HTTP_500, ApplicationConstants.UNEXPECTED_ERROR), HttpStatus.OK);
//        }
//        return response;
//    }
//
//    // batch list
//    @ResponseStatus(HttpStatus.OK)
//    @RequestMapping(value = "/downloadBatchFile", method = RequestMethod.POST)
//    @ApiOperation(value = "Download BatchFile Template", notes = "Endpoint help download batchFile template.")
//    public ResponseEntity<?> downloadBatchFile(@RequestBody BatchFileDto batchFileDto) {
//        ResponseEntity response = null;
//        try {
//        } catch (Exception ex) {
//            logger.info("Error during downloadBatchFile " + ExceptionUtil.getRootCause(ex));
//            response = new ResponseEntity<>(
//                    new ResponseDTO (ApiCode.HTTP_500, ApplicationConstants.UNEXPECTED_ERROR), HttpStatus.OK);
//        }
//        return response;
//    }
//
//
//    // download batch file
//    @ResponseStatus(HttpStatus.OK)
//    @RequestMapping(value = "/downloadBatchFile", method = RequestMethod.POST)
//    @ApiOperation(value = "Download BatchFile Template", notes = "Endpoint help download batchFile template.")
//    public ResponseEntity<?> downloadBatchFile(@RequestBody BatchFileDto batchFileDto) {
//        ResponseEntity response = null;
//        try {
//        } catch (Exception ex) {
//            logger.info("Error during downloadBatchFile " + ExceptionUtil.getRootCause(ex));
//            response = new ResponseEntity<>(
//                    new ResponseDTO (ApiCode.HTTP_500, ApplicationConstants.UNEXPECTED_ERROR), HttpStatus.OK);
//        }
//        return response;
//    }
//
//    // batch file template
//    @ResponseStatus(HttpStatus.OK)
//    @RequestMapping(value = "/downloadBatchFile", method = RequestMethod.POST)
//    @ApiOperation(value = "Download BatchFile Template", notes = "Endpoint help download batchFile template.")
//    public ResponseEntity<?> downloadBatchFile(@RequestBody BatchFileDto batchFileDto) {
//        ResponseEntity response = null;
//        try {
//            logger.info("Request for downloadBatchFile " + batchFileDto);
//            HttpHeaders headers = new HttpHeaders();
//            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
//            String fileName = "BatchUpload-"+dateFormat.format(new Date())+"-"+ UUID.randomUUID() + ".xls";
//            headers.add("Content-Disposition", "attachment; filename=" + fileName);
//
//        } catch (Exception ex) {
//            logger.info("Error during downloadBatchFile " + ExceptionUtil.getRootCause(ex));
//            response = new ResponseEntity<>(
//                new ResponseDTO (ApiCode.HTTP_500, ApplicationConstants.UNEXPECTED_ERROR), HttpStatus.OK);
//        }
//        return response;
//    }

}
