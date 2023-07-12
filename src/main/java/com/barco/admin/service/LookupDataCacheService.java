package com.barco.admin.service;

import com.barco.model.dto.request.FileUploadRequest;
import com.barco.model.dto.request.LookupDataRequest;
import com.barco.model.dto.response.AppResponse;
import com.barco.model.dto.response.LookupDataResponse;
import com.barco.model.util.lookuputil.GLookup;
import java.io.ByteArrayOutputStream;

/**
 * @author Nabeel Ahmed
 */
public interface LookupDataCacheService {

    public AppResponse fetchCacheData() throws Exception;

    public AppResponse addLookupData(LookupDataRequest requestPayload) throws Exception;

    public AppResponse updateLookupData(LookupDataRequest requestPayload) throws Exception;

    public AppResponse fetchSubLookupByParentId(LookupDataRequest requestPayload) throws Exception;

    public AppResponse fetchLookupByLookupType(LookupDataRequest requestPayload) throws Exception;

    public AppResponse fetchAllLookup(LookupDataRequest requestPayload) throws Exception;

    public AppResponse deleteLookupData(LookupDataRequest requestPayload) throws Exception;

    public ByteArrayOutputStream downloadLookupTemplateFile() throws Exception;

    public ByteArrayOutputStream downloadLookup(LookupDataRequest requestPayload) throws Exception;

    public AppResponse uploadLookup(FileUploadRequest requestPayload) throws Exception;

    public LookupDataResponse getParentLookupById(String lookupType);

    public LookupDataResponse getChildLookupById(String parentLookupType, Long childLookupCode);

    public GLookup<Long, String> getGLookup(String parentLookup, Long childLookupCode);

}
