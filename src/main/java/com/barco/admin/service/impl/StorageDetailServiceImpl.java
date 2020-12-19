package com.barco.admin.service.impl;

import com.barco.admin.service.IStorageDetailService;
import com.barco.common.manager.aws.impl.AwsBucketManagerImpl;
import com.barco.common.manager.aws.properties.AwsProperties;
import com.barco.common.manager.ftp.FtpFileExchange;
import com.barco.common.utility.ApplicationConstants;
import com.barco.model.dto.*;
import com.barco.model.enums.ApiCode;
import com.barco.model.enums.KeyType;
import com.barco.model.enums.Status;
import com.barco.model.pojo.AppUser;
import com.barco.model.pojo.StorageDetail;
import com.barco.model.pojo.ext.AWS;
import com.barco.model.pojo.ext.FTP;
import com.barco.model.repository.AppUserRepository;
import com.barco.model.repository.StorageDetailRepository;
import com.barco.model.repository.TaskRepository;
import com.barco.model.service.QueryServices;
import com.barco.model.util.PagingUtil;
import com.barco.model.util.QueryUtil;
import com.google.gson.Gson;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author Nabeel Ahmed
 */
@Service
@Transactional
@Scope("prototype")
public class StorageDetailServiceImpl implements IStorageDetailService {

    private Logger logger = LoggerFactory.getLogger(StorageDetailServiceImpl.class);

    @Autowired
    private AppUserRepository appUserRepository;
    @Autowired
    private StorageDetailRepository storageDetailRepository;
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private QueryServices queryServices;
    @Autowired
    private QueryUtil queryUtil;


    @Override // all json detail should be encrypted store
    public ResponseDTO createStorage(StorageDetailDto storageDetailDto) throws Exception {
        if (StringUtils.isEmpty(storageDetailDto.getStorageKeyName())) {
            return new ResponseDTO(ApiCode.INVALID_REQUEST, ApplicationConstants.STORAGE_KEY_NAME_MISSING);
        } else if (this.storageDetailRepository.findByStorageKeyNameAndCreatedByAndStatus(storageDetailDto.getStorageKeyName(),
                storageDetailDto.getCreatedBy(), Status.Active).isPresent() && storageDetailDto.getId() == null) {
            return new ResponseDTO(ApiCode.INVALID_REQUEST, ApplicationConstants.STORAGE_KEY_ALREADY_EXIST);
        } else if (storageDetailDto.getKeyType() == null) {
            return new ResponseDTO(ApiCode.INVALID_REQUEST, ApplicationConstants.STORAGE_KEY_TYPE_MISSING);
        } else if (storageDetailDto.getStorageDetailJson() == null) {
            return new ResponseDTO(ApiCode.INVALID_REQUEST, ApplicationConstants.STORAGE_KEY_JSON_MISSING);
        }
        Optional<AppUser> appUser = this.appUserRepository.findByIdAndStatus(storageDetailDto.getCreatedBy(), Status.Active);
        if (!appUser.isPresent()) {
            return new ResponseDTO(ApiCode.INVALID_REQUEST, ApplicationConstants.USER_NOT_FOUND);
        }
        StorageDetail storageDetail = null;
        if (storageDetailDto.getId() != null) {
            storageDetail = this.storageDetailRepository.findByIdAndStatus(storageDetailDto.getId(), Status.Active);
            if (storageDetail != null) {
                storageDetail.setModifiedBy(appUser.get().getId());
            } else {
                return new ResponseDTO(ApiCode.INVALID_REQUEST, ApplicationConstants.STORAGE_KEY_NOT_FOUND);
            }
        } else {
            storageDetail = new StorageDetail();
            storageDetail.setCreatedBy(appUser.get().getId());
            storageDetail.setStatus(Status.Active);
        }
        storageDetail.setStorageKeyName(storageDetailDto.getStorageKeyName());
        storageDetail.setStorageDetailJson(storageDetailDto.getStorageDetailJson());
        storageDetail.setKeyType(storageDetailDto.getKeyType());
        this.storageDetailRepository.saveAndFlush(storageDetail);
        storageDetailDto.setId(storageDetail.getId());
        return new ResponseDTO(ApiCode.SUCCESS, ApplicationConstants.SUCCESS_MSG, storageDetailDto);
    }

    @Override
    public ResponseDTO getStorageById(Long storageId, Long appUserId) throws Exception {
        Optional<StorageDetail> storage = this.storageDetailRepository.findByIdAndCreatedByAndStatus(storageId, appUserId, Status.Active);
        if (storage.isPresent()) {
            return new ResponseDTO(ApiCode.SUCCESS, ApplicationConstants.SUCCESS_MSG, storage.get());
        }
        return new ResponseDTO(ApiCode.INVALID_REQUEST, ApplicationConstants.HTTP_404_MSG);
    }

    @Override
    public ResponseDTO statusChange(Long storageId, Long appUserId, Status storageStatus) throws Exception {
        // get the storage attache with task
        Optional<StorageDetail> storageDetail = this.storageDetailRepository.findByIdAndCreatedByAndStatusNot(storageId, appUserId, Status.Delete);
        if (storageDetail.isPresent() && storageStatus.equals(Status.Active)) {
            // active storage if storage disable
            storageDetail.get().setStatus(storageStatus);
            storageDetail.get().setModifiedBy(appUserId);
            this.storageDetailRepository.saveAndFlush(storageDetail.get());
            return new ResponseDTO(ApiCode.SUCCESS, ApplicationConstants.SUCCESS_MSG);
        } else if (storageDetail.isPresent() && (storageStatus.equals(Status.Delete) || storageStatus.equals(Status.Inactive))) {
            Long storageAttacheCount = this.taskRepository.countByStorageId(storageId);
            if (storageAttacheCount > 0) {
                return new ResponseDTO(ApiCode.INVALID_REQUEST, ApplicationConstants.STORAGE_ATTACHE_WITH_TASK);
            } else {
                storageDetail.get().setStatus(storageStatus);
                storageDetail.get().setModifiedBy(appUserId);
                this.storageDetailRepository.saveAndFlush(storageDetail.get());
                return new ResponseDTO(ApiCode.SUCCESS, ApplicationConstants.SUCCESS_MSG);
            }
        }
        return new ResponseDTO(ApiCode.INVALID_REQUEST, ApplicationConstants.HTTP_404_MSG);
    }

    @Override
    public ResponseDTO findAllStorageByAppUserIdInPagination(Pageable paging, Long adminId, SearchTextDto searchTextDto,
         String startDate, String endDate, String order, String columnName) throws Exception {
        ResponseDTO responseDTO = null;
        Object countQueryResult = this.queryServices.executeQueryForSingleResult(
            this.queryUtil.adminStoreList(true, adminId, startDate, endDate, searchTextDto));
        if (countQueryResult != null) {
            /* fetch Record According to Pagination*/
            List<Object[]> result = this.queryServices.executeQuery(
                this.queryUtil.adminStoreList(false, adminId, startDate, endDate, searchTextDto), paging);
            if (result != null && result.size() > 0) {
                List<StorageDetailDto> storageDetailDtos = new ArrayList<>();
                for(Object[] obj : result) {
                    StorageDetailDto storageDetailDto = new StorageDetailDto();
                    if (obj[0] != null) {
                        storageDetailDto.setId(new Long(obj[0].toString()));
                    }
                    if (obj[1] != null) {
                        storageDetailDto.setCreatedAt(Timestamp.valueOf(obj[1].toString()));
                    }
                    if (obj[2] != null) {
                        storageDetailDto.setStorageKeyName(obj[2].toString());;
                    }
                    if (obj[3] != null) {
                        storageDetailDto.setKeyType(KeyType.getKeyType(new Long(obj[3].toString())));
                    }
                    storageDetailDtos.add(storageDetailDto);
                }
                responseDTO = new ResponseDTO(ApiCode.SUCCESS, ApplicationConstants.SUCCESS_MSG, storageDetailDtos,
                        PagingUtil.convertEntityToPagingDTO(Long.valueOf(countQueryResult.toString()),paging));
            }
        } else {
            responseDTO = new ResponseDTO(ApiCode.SUCCESS, ApplicationConstants.SUCCESS_MSG, new ArrayList<>());
        }
        return responseDTO;
    }

    @Override
    public ResponseDTO pingStorage(StorageDetailDto storageDetailDto) throws Exception {
        ResponseDTO responseDTO = null;
        Gson gson = new Gson();
        if (storageDetailDto.getKeyType().equals(KeyType.AWS)) {
            AWS aws = gson.fromJson(storageDetailDto.getStorageDetailJson().toString(), AWS.class);
            AwsBucketManagerImpl awsBucketManager = new AwsBucketManagerImpl();
            if (aws != null) {
                if (StringUtils.isEmpty(aws.getAccessKey()) || StringUtils.isEmpty(aws.getSecretKey()) || StringUtils.isEmpty(aws.getRegion())) {
                    responseDTO = new ResponseDTO(ApiCode.INVALID_REQUEST, ApplicationConstants.AWS_DETAIL_MISSING);
                } else {
                    awsBucketManager.amazonS3(new AwsProperties(aws.getRegion(), aws.getAccessKey(), aws.getSecretKey()));
                    for (Map.Entry<String, String> bucket:aws.getBucketName().entrySet()) {
                        if (!awsBucketManager.isBucketExist(bucket.getKey())) {
                            responseDTO = new ResponseDTO(ApiCode.INVALID_REQUEST,
                                    String.format(ApplicationConstants.AWS_BUCKET_NOT_EXIST, bucket.getKey()));
                            break;
                        }
                    }
                    if (responseDTO == null) {
                        responseDTO = new ResponseDTO(ApiCode.SUCCESS, ApplicationConstants.SUCCESS_MSG);
                    }
                }
            }
        } else if (storageDetailDto.getKeyType().equals(KeyType.FTP)) {
            FTP ftp = gson.fromJson(storageDetailDto.getStorageDetailJson().toString(), FTP.class);
            FtpFileExchange fileExchange = new FtpFileExchange().setHost(ftp.getHost()).setUser(ftp.getUser())
                .setPassword(ftp.getPassword()).setPort(ftp.getPort());
            if (StringUtils.isEmpty(ftp.getHost()) || StringUtils.isEmpty(ftp.getUser()) || StringUtils.isEmpty(ftp.getPassword())) {
                responseDTO = new ResponseDTO(ApiCode.INVALID_REQUEST, ApplicationConstants.FTP_DETAIL_MISSING);
            } else if (fileExchange.connectionOpen()) {
                fileExchange.close();
                responseDTO = new ResponseDTO(ApiCode.SUCCESS, ApplicationConstants.SUCCESS_MSG);
            } else {
                responseDTO = new ResponseDTO(ApiCode.INVALID_REQUEST, ApplicationConstants.FTP_NOT_CONNECT);
            }
        }
        return responseDTO;
    }

}
