package com.barco.admin.service;

import com.barco.model.dto.request.EnVariablesRequest;
import com.barco.model.dto.request.FileUploadRequest;
import com.barco.model.dto.request.GroupRequest;
import com.barco.model.dto.response.AppResponse;
import java.io.ByteArrayOutputStream;

/**
 * @author Nabeel Ahmed
 */
public interface MgGroupService extends RootService  {

    public AppResponse addGroup(GroupRequest payload) throws Exception;

    public AppResponse updateGroup(GroupRequest payload) throws Exception;

    public AppResponse fetchAllGroup(GroupRequest payload) throws Exception;

    public AppResponse fetchGroupById(GroupRequest payload) throws Exception;

    public AppResponse deleteGroupById(GroupRequest payload) throws Exception;

    public AppResponse addGroupTeamLead(GroupRequest payload) throws Exception;

    public AppResponse fetchLinkGroupWithUser(GroupRequest payload) throws Exception;

    public AppResponse linkGroupWithUser(GroupRequest payload) throws Exception;

    public ByteArrayOutputStream downloadGroupTemplateFile() throws Exception;

    public ByteArrayOutputStream downloadGroup(EnVariablesRequest payload) throws Exception;

    public AppResponse uploadGroup(FileUploadRequest payload) throws Exception;

}
