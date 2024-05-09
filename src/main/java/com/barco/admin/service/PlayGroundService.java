package com.barco.admin.service;

import com.barco.model.dto.request.PlayGroundRequest;
import com.barco.model.dto.response.AppResponse;

/**
 * @author Nabeel Ahmed
 */
public interface PlayGroundService extends RootService {

    public AppResponse fetchPlayGroundData() throws Exception;
    
    public AppResponse fetchAllFormForPlayGround(PlayGroundRequest request) throws Exception;

    public AppResponse fetchFormForPlayGroundByFormId(PlayGroundRequest request) throws Exception;

}
