package com.barco.admin.service;

import com.barco.model.dto.request.*;
import com.barco.model.dto.response.AppResponse;
import java.io.ByteArrayOutputStream;

/**
 * @author Nabeel Ahmed
 */
public interface RPPService extends RootService {

    // role
    public AppResponse addRole(RoleRequest payload) throws Exception;

    public AppResponse updateRole(RoleRequest payload) throws Exception;

    public AppResponse fetchAllRole(RoleRequest payload) throws Exception;

    public AppResponse findRoleById(RoleRequest payload) throws Exception;

    public AppResponse deleteRoleById(RoleRequest payload) throws Exception;

    public AppResponse deleteAllRole(RoleRequest payload) throws Exception;

    public ByteArrayOutputStream downloadRoleTemplateFile() throws Exception;

    public ByteArrayOutputStream downloadRole(RoleRequest payload) throws Exception;

    public AppResponse uploadRole(FileUploadRequest payload) throws Exception;

    public AppResponse addProfile(ProfileRequest payload) throws Exception;

    public AppResponse updateProfile(ProfileRequest payload) throws Exception;

    public AppResponse fetchAllProfile(ProfileRequest payload) throws Exception;

    public AppResponse fetchProfileById(ProfileRequest payload) throws Exception;

    public AppResponse deleteProfileById(ProfileRequest payload) throws Exception;

    public AppResponse deleteAllProfile(ProfileRequest payload) throws Exception;

    public ByteArrayOutputStream downloadProfileTemplateFile() throws Exception;

    public ByteArrayOutputStream downloadProfile(ProfileRequest payload) throws Exception;

    public AppResponse uploadProfile(FileUploadRequest payload) throws Exception;

    // permission
    public AppResponse addPermission(PermissionRequest payload) throws Exception;

    public AppResponse updatePermission(PermissionRequest payload) throws Exception;

    public AppResponse fetchAllPermission(PermissionRequest payload) throws Exception;

    public AppResponse fetchPermissionById(PermissionRequest payload) throws Exception;

    public AppResponse deletePermissionById(PermissionRequest payload) throws Exception;

    public AppResponse deleteAllPermission(PermissionRequest payload) throws Exception;

    public ByteArrayOutputStream downloadPermissionTemplateFile() throws Exception;

    public ByteArrayOutputStream downloadPermission(PermissionRequest payload) throws Exception;

    public AppResponse uploadPermission(FileUploadRequest payload) throws Exception;

    public AppResponse fetchLinkProfilePermission(LinkPPRequest payload) throws Exception;

    public AppResponse updateLinkProfilePermission(LinkPPRequest payload) throws Exception;

    public AppResponse fetchLinkRoleWithRootUser(LinkRURequest payload) throws Exception;

    public AppResponse linkRoleWithRootUser(LinkRURequest payload) throws Exception;

    public AppResponse fetchLinkProfileWithRootUser(LinkPURequest payload) throws Exception;

    public AppResponse linkProfileWithRootUser(LinkPURequest payload) throws Exception;

    public AppResponse fetchProfileWithUser(LinkPURequest payload) throws Exception;

    public AppResponse fetchRoleWithUser(LinkRURequest payload) throws Exception;

}
