package com.barco.admin.service;

import com.barco.common.utility.excel.SheetFiled;
import com.barco.model.dto.request.FileUploadRequest;
import com.barco.model.dto.request.LookupDataRequest;
import com.barco.model.dto.response.AppResponse;
import com.barco.model.dto.response.LookupDataResponse;
import java.io.ByteArrayOutputStream;
import java.util.Map;

/**
 * @author Nabeel Ahmed
 */
public interface LookupDataCacheService extends RootService {

    public AppResponse fetchCacheData() throws Exception;

    public AppResponse addLookupData(LookupDataRequest payload) throws Exception;

    public AppResponse updateLookupData(LookupDataRequest payload) throws Exception;

    public AppResponse findAllParentLookupByUsername(LookupDataRequest payload) throws Exception;

    public AppResponse fetchSubLookupDataByParentLookupDataId(LookupDataRequest payload) throws Exception;

    public AppResponse fetchLookupDataByLookupType(LookupDataRequest payload) throws Exception;

    public AppResponse deleteLookupData(LookupDataRequest payload) throws Exception;

    public ByteArrayOutputStream downloadLookupDataTemplateFile() throws Exception;

    public ByteArrayOutputStream downloadLookupData(LookupDataRequest payload) throws Exception;

    public AppResponse uploadLookupData(FileUploadRequest payload) throws Exception;

    public LookupDataResponse getParentLookupDataByParentLookupType(String parentLookupType);

    public LookupDataResponse getChildLookupDataByParentLookupTypeAndChildLookupCode(String parentLookupType, Long lookupCode);

    public Map<String, SheetFiled> getSheetFiledMap();
}
