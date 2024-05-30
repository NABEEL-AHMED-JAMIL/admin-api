package com.barco.admin.service;

import com.barco.common.utility.BarcoUtil;
import com.barco.common.utility.ExceptionUtil;
import com.barco.common.utility.excel.ExcelUtil;
import com.barco.model.dto.request.FileUploadRequest;
import com.barco.model.dto.request.LinkEBURequest;
import com.barco.model.dto.request.EventBridgeRequest;
import com.barco.model.dto.request.LookupDataRequest;
import com.barco.model.dto.response.AppResponse;
import com.barco.model.util.MessageUtil;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * @author Nabeel Ahmed
 */
public interface EventBridgeService extends RootService {

    public AppResponse addEventBridge(EventBridgeRequest payload) throws Exception;

    public AppResponse updateEventBridge(EventBridgeRequest payload) throws Exception;

    public AppResponse fetchAllEventBridge(EventBridgeRequest payload) throws Exception;

    public AppResponse fetchEventBridgeById(EventBridgeRequest payload) throws Exception;

    public AppResponse fetchEventBridgeByBridgeType(EventBridgeRequest payload) throws Exception;

    public AppResponse deleteEventBridgeById(EventBridgeRequest payload) throws Exception;

    public AppResponse deleteAllEventBridge(EventBridgeRequest payload) throws Exception;

    public AppResponse fetchLinkEventBridgeWitUser(EventBridgeRequest payload) throws Exception;

    public AppResponse linkEventBridgeWithUser(LinkEBURequest payload) throws Exception;

    public AppResponse genEventBridgeToken(LinkEBURequest payload) throws Exception;

    public ByteArrayOutputStream downloadEventBridgeTemplateFile() throws Exception;

    public ByteArrayOutputStream downloadEventBridge(EventBridgeRequest payload) throws Exception;

    public AppResponse uploadEventBridge(FileUploadRequest payload) throws Exception;

}
