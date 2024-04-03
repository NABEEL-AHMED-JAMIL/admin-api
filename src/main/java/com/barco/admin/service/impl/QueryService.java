package com.barco.admin.service.impl;

import com.barco.common.utility.BarcoUtil;
import com.barco.model.dto.response.QueryResponse;
import com.google.gson.Gson;
import org.hibernate.query.internal.NativeQueryImpl;
import org.hibernate.transform.AliasToEntityMapResultTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import javax.persistence.*;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Map;

/**
 * @author Nabeel Ahmed
 */
@Service
@Transactional
public class QueryService {

    private Logger logger = LoggerFactory.getLogger(QueryService.class);

    @PersistenceContext
    private EntityManager _em;

    // Filed
    public static String ID = "id";
    public static String STATUS = "status";
    public static String PROFILE_NAME = "profile_name";
    public static String ROLE_NAME = "role_name";
    public static String PERMISSION_NAME = "permission_name";
    public static String DESCRIPTION = "description";
    public static String LINK_PP = "link_pp";
    // user detail
    public static String EMAIL = "email";
    public static String FULL_NAME = "full_name";
    public static String PROFILE_IMG = "profile_img";
    public static String LINK_DATA = "link_data";
    public static String LINKED = "linked";
    public static String LINK_STATUS = "link_status";
    public static String PROFILE_ID = "profile_id";

    // Query
    public static String DELETE_APP_USER_ROLES = "DELETE FROM APP_USER_ROLES aur WHERE aur.ROLE_ID = %d ";
    public static String DELETE_APP_USER_ROLE_ACCESS_BY_ROLE_ID = "DELETE FROM APP_USER_ROLE_ACCESS aura WHERE aura.ROLE_ID = %d ";
    public static String DELETE_PROFILE_PERMISSION_BY_PROFILE_ID = "DELETE FROM PROFILE_PERMISSION pp WHERE pp.PROFILE_ID = %d ";
    public static String DELETE_APP_USER_PROFILE_ACCESS_BY_PROFILE_ID = "DELETE FROM APP_USER_PROFILE_ACCESS aupp WHERE aupp.PROFILE_ID = %d ";
    public static String DELETE_PROFILE_PERMISSION_BY_PERMISSION_ID = "DELETE FROM PROFILE_PERMISSION pp WHERE pp.PERMISSION_ID = %d ";
    public static String DELETE_APP_USER_ENV_BY_ENV_KEY_ID = "DELETE FROM APP_USER_ENV aue WHERE aue.ENV_KEY_ID = %d ";
    public static String FETCH_PROFILE = "SELECT PRO.ID, PRO.PROFILE_NAME, PRO.STATUS, PRO.DESCRIPTION FROM PROFILE PRO WHERE PRO.CREATED_BY_ID = %d ";
    public static String FETCH_PERMISSION = "SELECT PER.ID, PER.PERMISSION_NAME, PER.STATUS, PER.DESCRIPTION FROM PERMISSION PER WHERE PER.CREATED_BY_ID = %d";
    public static String FETCH_PROFILE_PERMISSION = "SELECT PP.PROFILE_ID || 'X' || PP.PERMISSION_ID AS LINK_PP, PP.STATUS AS STATUS FROM PROFILE_PERMISSION PP WHERE PP.CREATED_BY_ID = %d";
    public static String DELETE_PROFILE_PERMISSION_BY_PROFILE_ID_AND_PERMISSION_ID = "DELETE FROM PROFILE_PERMISSION pp WHERE pp.PROFILE_ID = %d AND pp.PERMISSION_ID = %d ";
    public static String DELETE_APP_USER_ROLE_ACCESS_BY_ROLE_ID_AND_APP_USER_ID = "DELETE FROM APP_USER_ROLE_ACCESS aura WHERE aura.ROLE_ID = %d AND aura.APP_USER_ID = %d ";
    public static String DELETE_APP_USER_ENV_BY_ENV_KEY_ID_AND_APP_USER_ID = "DELETE FROM APP_USER_ENV aue WHERE aue.ENV_KEY_ID = %d AND aue.APP_USER_ID = %d  ";
    public static String DELETE_APP_USER_PROFILE_ACCESS_BY_ROLE_ID_AND_APP_USER_ID = "DELETE FROM APP_USER_PROFILE_ACCESS aupa WHERE aupa.PROFILE_ID = %d AND aupa.APP_USER_ID = %d ";
    public static String FETCH_LINK_ROLE_WITH_ROOT_USER = "SELECT DISTINCT AU.ID, AU.EMAIL, AU.FIRST_NAME || ' ' || AU.LAST_NAME AS FULL_NAME, " +
        "AU.IMG AS PROFIlE_IMG, PRO.ID AS PROFILE_ID, PRO.PROFILE_NAME, AURA.DATE_CREATED AS LINK_DATA, " +
        "CASE WHEN AURA.DATE_CREATED IS NULL THEN FALSE ELSE TRUE END LINKED " +
        "FROM APP_USER AU " +
        "INNER JOIN PROFILE PRO ON PRO.ID = AU.PROFILE_ID " +
        "LEFT JOIN APP_USER_ROLE_ACCESS AURA ON AURA.APP_USER_ID = AU.ID AND AURA.ROLE_ID = %d " +
        "WHERE AU.ID = %d OR AU.CREATED_BY_ID = %d " +
        "ORDER BY AU.ID ASC";
    public static String FETCH_LINK_PROFILE_WITH_ROOT_USER = "SELECT DISTINCT AU.ID, AU.EMAIL, AU.FIRST_NAME || ' ' || AU.LAST_NAME AS FULL_NAME, " +
        "AU.IMG AS PROFIlE_IMG, PRO.ID AS PROFILE_ID, PRO.PROFILE_NAME, AUPA.DATE_CREATED AS LINK_DATA, " +
        "CASE WHEN AUPA.DATE_CREATED IS NULL THEN FALSE ELSE TRUE END LINKED " +
        "FROM APP_USER AU " +
        "INNER JOIN PROFILE PRO ON PRO.ID = AU.PROFILE_ID " +
        "LEFT JOIN APP_USER_PROFILE_ACCESS AUPA ON AUPA.APP_USER_ID = AU.ID AND AUPA.PROFILE_ID = %d " +
        "WHERE AU.ID = %d OR AU.CREATED_BY_ID = %d " +
        "ORDER BY AU.ID ASC";
    public static String FETCH_LINK_ENVIRONMENT_VARIABLE_WITH_USER = "SELECT DISTINCT AU.ID, AU.EMAIL, AU.FIRST_NAME || ' ' || AU.LAST_NAME AS FULL_NAME, " +
        "AU.IMG AS PROFILE_IMG, PRO.ID AS PROFILE_ID, PRO.PROFILE_NAME, AUE.DATE_CREATED AS LINK_DATA, " +
        "CASE WHEN AUE.DATE_CREATED IS NULL THEN FALSE ELSE TRUE END LINKED, AUE.ENV_VALUE " +
        "FROM APP_USER AU " +
        "INNER JOIN PROFILE PRO ON PRO.ID = AU.PROFILE_ID " +
        "LEFT JOIN APP_USER_ENV AUE ON AUE.APP_USER_ID = AU.ID AND AUE.ENV_KEY_ID = %d " +
        "WHERE AU.STATUS != %d " +
        "ORDER by AU.ID ASC";


    // fetchProfileWithUser => FETCH_ROLE_WITH_USER
    public static String FETCH_ROLE_WITH_USER = "SELECT DISTINCT ROLE.NAME AS ROLE_NAME " +
        "FROM ROLE " +
        "INNER JOIN APP_USER_ROLE_ACCESS AURA ON AURA.ROLE_ID = ROLE.ID AND AURA.APP_USER_ID = %d AND AURA.STATUS = %d AND ROLE.STATUS = %d ";

    // fetchRoleWithUser => FETCH_PROFILE_WITH_USER
    public static String FETCH_PROFILE_WITH_USER = "SELECT DISTINCT PRO.ID, PRO.PROFILE_NAME " +
        "FROM PROFILE PRO " +
        "INNER JOIN APP_USER_PROFILE_ACCESS AUPA ON AUPA.PROFILE_ID = PRO.ID AND AUPA.APP_USER_ID = %d AND PRO.STATUS = %d AND AUPA.STATUS = %d ";

    public QueryService() {}

    /**
     * Method use to perform the delete query
     * @param queryStr
     * @return Object
     * */
    public Object deleteQuery(String queryStr) {
        logger.info("Execute Query :- " + queryStr);
        Query query = this._em.createNativeQuery(queryStr);
        int rowsDeleted = query.executeUpdate();
        logger.info("Execute deleted :- " + rowsDeleted);
        return rowsDeleted;
    }

    /**
     * Method use to perform the single result query
     * @param queryStr
     * @return Object
     * */
    public Object executeQueryForSingleResult(String queryStr) {
        logger.info("Execute Query :- " + queryStr);
        Query query = this._em.createNativeQuery(queryStr);
        return query.getSingleResult();
    }

    /**
     * Method use to execute query for fetch the result
     * @param queryStr
     * @return List<Object[]>
     * */
    public List<Object[]> executeQuery(String queryStr) {
        logger.info("Execute Query :- " + queryStr);
        Query query = this._em.createNativeQuery(queryStr);
        return query.getResultList();
    }

    /**
     * Method use to execute query for paging
     * @param queryStr
     * @param paging
     * @return List<Object[]>
     * **/
    public List<Object[]> executeQuery(String queryStr, Pageable paging) {
        logger.info("Execute Query :- " + queryStr);
        Query query = this._em.createNativeQuery(queryStr);
        if (!BarcoUtil.isNull(paging)) {
            query.setFirstResult(paging.getPageNumber() * paging.getPageSize());
            query.setMaxResults(paging.getPageSize());
        }
        return query.getResultList();
    }

    /**
     * Method use to execute query for dynamic result
     * @param queryString
     * @return QueryResponse
     * */
    public QueryResponse executeQueryResponse(String queryString) {
        logger.info("Execute Query :- " + queryString);
        Query query = this._em.createNativeQuery(queryString);
        NativeQueryImpl nativeQuery = (NativeQueryImpl) query;
        nativeQuery.setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE);
        List<Map<String,Object>> result = nativeQuery.getResultList();
        QueryResponse itemResponse=new QueryResponse();
        if (result != null && result.size() > 0) {
            itemResponse.setQuery(queryString);
            itemResponse.setData(result);
            itemResponse.setColumn(result.get(0).keySet());
        }
        return itemResponse;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

}