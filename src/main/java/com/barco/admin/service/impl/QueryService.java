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
    public static String FIELD_TYPE = "field_type";
    public static String CONTROL_NAME = "control_name";
    public static String PROFILE_NAME = "profile_name";
    public static String FORM_NAME = "form_name";
    public static String FORM_TYPE = "form_type";
    public static String ROLE_NAME = "role_name";
    public static String PERMISSION_NAME = "permission_name";
    public static String DESCRIPTION = "description";
    public static String LINK_PP = "link_pp";
    public static String SECTION_NAME = "section_name";
    public static String LINK_SECTION_ID = "link_section_id";
    public static String LINK_CONTROL_ID = "link_control_id";
    public static String LINK_FORM_ID = "link_form_id";
    public static String CONTROL_ORDER = "control_order";
    public static String FILED_WIDTH = "field_width";
    public static String DISABLED_PATTERN = "disabled_pattern";
    public static String VISIBLE_PATTERN = "visible_pattern";
    public static String SECTION_ORDER = "section_order";
    public static String EMAIL = "email";
    public static String USERNAME = "username";
    public static String FULL_NAME = "full_name";
    public static String PROFILE_IMG = "profile_img";
    public static String LINK_DATA = "link_data";
    public static String LINKED = "linked";
    public static String SERVICE_ID = "service_id";
    public static String LINK_STATUS = "link_status";
    public static String PROFILE_ID = "profile_id";
    public static String ENV_VALUE = "env_value";
    public static String ACCESS_TOKEN = "access_token";
    public static String EXPIRE_TIME = "expire_time";
    public static String TOKEN_ID = "token_id";
    public static String SERVICE_NAME = "service_name";
    public static String LINK_STT_ID = "link_stt_id";
    public static String TASK_TYPE = "task_type";

    // Query
    public static String FETCH_PROFILE = "SELECT PRO.ID, PRO.PROFILE_NAME, PRO.STATUS, PRO.DESCRIPTION FROM PROFILE PRO WHERE PRO.CREATED_BY_ID = %d ";
    public static String FETCH_PERMISSION = "SELECT PER.ID, PER.PERMISSION_NAME, PER.STATUS, PER.DESCRIPTION FROM PERMISSION PER WHERE PER.CREATED_BY_ID = %d";
    public static String FETCH_PROFILE_PERMISSION = "SELECT PP.PROFILE_ID || 'X' || PP.PERMISSION_ID AS LINK_PP, PP.STATUS AS STATUS FROM PROFILE_PERMISSION PP WHERE PP.CREATED_BY_ID = %d";
    public static String DELETE_PROFILE_PERMISSION_BY_PROFILE_ID_AND_PERMISSION_ID = "DELETE FROM PROFILE_PERMISSION pp WHERE pp.PROFILE_ID = %d AND pp.PERMISSION_ID = %d ";
    public static String DELETE_APP_USER_ROLE_ACCESS_BY_ROLE_ID_AND_APP_USER_ID = "DELETE FROM APP_USER_ROLE_ACCESS aura WHERE aura.ROLE_ID = %d AND aura.APP_USER_ID = %d ";
    public static String DELETE_APP_USER_ENV_BY_ENV_KEY_ID_AND_APP_USER_ID = "DELETE FROM APP_USER_ENV aue WHERE aue.ENV_KEY_ID = %d AND aue.APP_USER_ID = %d  ";
    public static String DELETE_APP_USER_EVENT_BRIDGE_BY_EVENT_BRIDGE_ID_AND_APP_USER_ID = "DELETE FROM APP_USER_EVENT_BRIDGE AUEB WHERE EVENT_BRIDGE_ID = %d AND APP_USER_ID = %d  ";
    public static String DELETE_APP_USER_PROFILE_ACCESS_BY_ROLE_ID_AND_APP_USER_ID = "DELETE FROM APP_USER_PROFILE_ACCESS aupa WHERE aupa.PROFILE_ID = %d AND aupa.APP_USER_ID = %d ";
    public static String FETCH_LINK_ROLE_WITH_ROOT_USER = "SELECT DISTINCT AU.ID, AU.EMAIL, AU.USERNAME, AU.FIRST_NAME || ' ' || AU.LAST_NAME AS FULL_NAME, " +
        "AU.IMG AS PROFIlE_IMG, PRO.ID AS PROFILE_ID, PRO.PROFILE_NAME, PRO.DESCRIPTION, AURA.DATE_CREATED AS LINK_DATA, " +
        "CASE WHEN AURA.DATE_CREATED IS NULL THEN FALSE ELSE TRUE END LINKED, " +
        "CASE WHEN AURA.STATUS IS NOT NULL THEN AURA.STATUS WHEN AU.STATUS = 0 OR RL.STATUS = 0 THEN 0 ELSE 1 END AS LINK_STATUS " +
        "FROM APP_USER AU " +
        "INNER JOIN PROFILE PRO ON PRO.ID = AU.PROFILE_ID " +
        "LEFT JOIN APP_USER_ROLE_ACCESS AURA ON AURA.APP_USER_ID = AU.ID AND AURA.ROLE_ID = %d " +
        "LEFT JOIN ROLE RL ON RL.ID = AURA.ROLE_ID " +
        "WHERE AU.STATUS != 2 AND (AU.ID = %d OR AU.CREATED_BY_ID = %d) " +
        "ORDER BY AU.ID DESC";
    public static String FETCH_LINK_PROFILE_WITH_ROOT_USER = "SELECT DISTINCT AU.ID, AU.EMAIL, AU.USERNAME, AU.FIRST_NAME || ' ' || AU.LAST_NAME AS FULL_NAME, " +
        "AU.IMG AS PROFIlE_IMG, PRO.ID AS PROFILE_ID, PRO.PROFILE_NAME, PRO.DESCRIPTION, AUPA.DATE_CREATED AS LINK_DATA, " +
        "CASE WHEN AUPA.DATE_CREATED IS NULL THEN FALSE ELSE TRUE END LINKED, " +
        "CASE WHEN AUPA.STATUS IS NOT NULL THEN AUPA.STATUS WHEN AU.STATUS = 0 OR PRO.STATUS = 0 THEN 0 ELSE 1 END AS LINK_STATUS " +
        "FROM APP_USER AU " +
        "INNER JOIN PROFILE PRO ON PRO.ID = AU.PROFILE_ID " +
        "LEFT JOIN APP_USER_PROFILE_ACCESS AUPA ON AUPA.APP_USER_ID = AU.ID AND AUPA.PROFILE_ID = %d " +
        "WHERE AU.STATUS != 2 AND (AU.ID = %d OR AU.CREATED_BY_ID = %d) " +
        "ORDER BY AU.ID DESC";
    public static String FETCH_LINK_ENVIRONMENT_VARIABLE_WITH_USER = "SELECT DISTINCT AU.ID, AU.EMAIL, AU.USERNAME, AU.FIRST_NAME || ' ' || AU.LAST_NAME AS FULL_NAME, " +
        "AU.IMG AS PROFILE_IMG, PRO.ID AS PROFILE_ID, PRO.PROFILE_NAME, PRO.DESCRIPTION, AUE.DATE_CREATED AS LINK_DATA, " +
        "CASE WHEN AUE.DATE_CREATED IS NULL THEN FALSE ELSE TRUE END LINKED, AUE.ENV_VALUE, " +
        "CASE WHEN AUE.STATUS IS NOT NULL THEN AUE.STATUS WHEN AU.STATUS = 0 OR AUE.STATUS = 0 THEN 0 ELSE 1 END AS LINK_STATUS " +
        "FROM APP_USER AU " +
        "INNER JOIN PROFILE PRO ON PRO.ID = AU.PROFILE_ID " +
        "LEFT JOIN APP_USER_ENV AUE ON AUE.APP_USER_ID = AU.ID AND AUE.ENV_KEY_ID = %d " +
        "WHERE AU.STATUS != %d " +
        "ORDER by AU.ID DESC";
    public static String FETCH_LINK_EVENT_BRIDGE_WITH_USER = "SELECT DISTINCT AU.ID, AU.EMAIL, AU.USERNAME, AU.FIRST_NAME || ' ' || AU.LAST_NAME AS FULL_NAME, " +
        "AU.IMG AS PROFILE_IMG, PRO.ID AS PROFILE_ID, PRO.PROFILE_NAME, PRO.DESCRIPTION, AUEB.DATE_CREATED AS LINK_DATA, " +
        "CASE WHEN AUEB.DATE_CREATED IS NULL THEN FALSE ELSE TRUE END LINKED, AUEB.ACCESS_TOKEN,  AUEB.EXPIRE_TIME,  AUEB.TOKEN_ID, " +
        "CASE WHEN AUEB.STATUS IS NOT NULL THEN AUEB.STATUS WHEN AU.STATUS = 0 OR AUEB.STATUS = 0 THEN 0 ELSE 1 END AS LINK_STATUS " +
        "FROM APP_USER AU " +
        "INNER JOIN PROFILE PRO ON PRO.ID = AU.PROFILE_ID " +
        "LEFT JOIN APP_USER_EVENT_BRIDGE AUEB ON AUEB.APP_USER_ID = AU.ID AND AUEB.EVENT_BRIDGE_ID = %d " +
        "WHERE AU.STATUS != %d " +
        "ORDER BY AU.ID DESC";
    public static String FETCH_ALL_CONTROLS_LINK_SECTION= "SELECT GS.ID, GS.SECTION_NAME, GS.DESCRIPTION, GS.STATUS, " +
        "GLG.CONTROL_ORDER, GLG.FIELD_WIDTH, " +
        "CASE WHEN GLG.SECTION_ID IS NOT NULL THEN 'true' ELSE 'false' END AS LINK_STATUS, GLG.ID AS LINK_SECTION_ID " +
        "FROM GEN_SECTION GS " +
        "LEFT JOIN GC_LINK_GS GLG ON GLG.SECTION_ID = GS.ID AND GLG.CONTROL_ID = %d AND GLG.STATUS != %2$d " +
        "WHERE GS.STATUS != %2$d AND GS.CREATED_BY_ID = %3$d " +
        "ORDER BY GS.DATE_CREATED DESC";
    public static String FETCH_ALL_SECTION_LINK_CONTROLS = "SELECT GC.ID, GC.FIELD_TYPE, GC.CONTROL_NAME, GC.STATUS, " +
        "GLG.CONTROL_ORDER, GLG.FIELD_WIDTH, " +
        "CASE WHEN GLG.SECTION_ID IS NOT NULL THEN 'TRUE' ELSE 'FALSE' END AS LINK_STATUS, GLG.ID AS LINK_CONTROL_ID " +
        "FROM GEN_CONTROL GC " +
        "LEFT JOIN GC_LINK_GS GLG ON GLG.CONTROL_ID = GC.ID AND GLG.SECTION_ID = %d AND GLG.STATUS != %2$d " +
        "WHERE GC.STATUS != %2$d AND GC.CREATED_BY_ID = %3$d " +
        "ORDER BY GC.DATE_CREATED DESC";
    public static String FETCH_ALL_FORM_LINK_SECTION = "SELECT GF.ID, GF.FORM_NAME, GF.FORM_TYPE, GF.STATUS, " +
        "CASE WHEN GLG.FORM_ID IS NOT NULL THEN 'TRUE' ELSE 'FALSE' END AS LINK_STATUS, " +
        "GLG.SECTION_ORDER, GLG.ID AS LINK_FORM_ID " +
        "FROM GEN_FORM GF " +
        "LEFT JOIN GS_LINK_GF GLG ON GLG.FORM_ID = GF.ID AND GLG.SECTION_ID = %d AND GLG.STATUS != %2$d " +
        "WHERE GF.STATUS != %2$d AND GF.CREATED_BY_ID = %3$d " +
        "ORDER BY GF.DATE_CREATED DESC";
    public static String FETCH_ALL_SECTION_LINK_FORM = "SELECT GS.ID, GS.SECTION_NAME, GS.DESCRIPTION, GS.STATUS, " +
        "CASE WHEN GLG.FORM_ID IS NOT NULL THEN 'TRUE' ELSE 'FALSE' END AS LINK_STATUS, " +
        "GLG.SECTION_ORDER, GLG.ID AS LINK_SECTION_ID " +
        "FROM GEN_SECTION GS " +
        "LEFT JOIN GS_LINK_GF GLG ON GLG.SECTION_ID = GS.ID AND GLG.FORM_ID = %d AND GLG.STATUS != %2$d " +
        "WHERE GS.STATUS != %2$d AND GS.CREATED_BY_ID = %3$d " +
        "ORDER BY GS.DATE_CREATED DESC";
    public static String FETCH_ALL_STT_LINK_FORM = "SELECT STT.ID, STT.SERVICE_NAME, STT.TASK_TYPE, " +
        "CASE WHEN SLS.FORM_ID IS NOT NULL THEN 'TRUE' ELSE 'FALSE' END AS LINK_STATUS, " +
        "SLS.ID AS LINK_STT_ID " +
        "FROM SOURCE_TASK_TYPE STT " +
        "LEFT JOIN STTF_LINK_STT SLS ON SLS.STT_ID = STT.ID AND SLS.FORM_ID = %d  AND SLS.STATUS != %2$d " +
        "WHERE STT.STATUS != %2$d AND STT.CREATED_BY_ID = %3$d " +
        "ORDER BY STT.DATE_CREATED DESC";
    public static String FETCH_ALL_FORM_LINK_STT = "SELECT GF.ID, GF.FORM_NAME, GF.SERVICE_ID, GF.FORM_TYPE, GF.STATUS, " +
        "CASE WHEN SLS.FORM_ID IS NOT NULL THEN 'TRUE' ELSE 'FALSE' END AS LINK_STATUS, " +
        "SLS.ID AS LINK_FORM_ID " +
        "FROM GEN_FORM GF " +
        "LEFT JOIN STTF_LINK_STT SLS ON SLS.FORM_ID = GF.ID AND SLS.STT_ID = %d  AND SLS.STATUS != %2$d " +
        "WHERE GF.STATUS != %2$d AND GF.FORM_TYPE = %3$d AND GF.CREATED_BY_ID = %4$d " +
        "ORDER BY GF.DATE_CREATED DESC";
    public static String FETCH_ROLE_WITH_USER = "SELECT DISTINCT ROLE.ID, ROLE.NAME AS ROLE_NAME, ROLE.DESCRIPTION AS DESCRIPTION " +
        "FROM ROLE " +
        "INNER JOIN APP_USER_ROLE_ACCESS AURA ON AURA.ROLE_ID = ROLE.ID AND AURA.APP_USER_ID = %d AND AURA.STATUS = %d AND ROLE.STATUS = %d ";
    public static String FETCH_PROFILE_WITH_USER = "SELECT DISTINCT PRO.ID, PRO.PROFILE_NAME, PRO.DESCRIPTION AS DESCRIPTION " +
        "FROM PROFILE PRO " +
        "INNER JOIN APP_USER_PROFILE_ACCESS AUPA ON AUPA.PROFILE_ID = PRO.ID AND AUPA.APP_USER_ID = %d AND AUPA.STATUS = %d AND PRO.STATUS = %d ";
    public static String APP_SETTING_STATISTICS = "SELECT CASE WHEN LD.STATUS = 0 THEN 'INACTIVE-LOOKUP' WHEN LD.STATUS = 1 THEN 'ACTIVE-LOOKUP' END AS NAME, COUNT(*) AS VALUE " +
        "FROM LOOKUP_DATA LD WHERE LD.PARENT_LOOKUP_ID IS NULL AND LD.CREATED_BY_ID = %1$d AND LD.STATUS != 2 GROUP BY LD.STATUS \n" +
        "UNION \n" +
        "SELECT CASE WHEN EV.STATUS = 0 THEN 'INACTIVE-E-VARIABLE' WHEN EV.STATUS = 1 THEN 'ACTIVE-E-VARIABLE' END AS NAME, COUNT(*) AS VALUE " +
        "FROM ENV_VARIABLES EV WHERE EV.CREATED_BY_ID = %1$d AND EV.STATUS != 2 GROUP BY EV.STATUS \n" +
        "UNION \n" +
        "SELECT CASE WHEN EB.STATUS = 0 THEN 'INACTIVE-EVENT-BRIDGE' WHEN EB.STATUS = 1 THEN 'ACTIVE-EVENT-BRIDGE' END AS NAME, COUNT(*) AS VALUE " +
        "FROM EVENT_BRIDGE EB WHERE EB.CREATED_BY_ID = %1$d AND EB.STATUS != 2 GROUP BY EB.STATUS \n" +
        "UNION \n" +
        "SELECT CASE WHEN TR.STATUS = 0 THEN 'INACTIVE-TEMPLATE' WHEN TR.STATUS = 1 THEN 'ACTIVE-TEMPLATE' END AS NAME, COUNT(*) AS VALUE " +
        "FROM TEMPLATE_REG TR WHERE TR.CREATED_BY_ID = %1$d AND TR.STATUS != 2 GROUP BY TR.STATUS \n" +
        "UNION \n" +
        "SELECT CASE WHEN CRD.STATUS = 0 THEN 'INACTIVE-CREDENTIAL' WHEN CRD.STATUS = 1 THEN 'ACTIVE-CREDENTIAL' END AS NAME, COUNT(*) AS VALUE " +
        "FROM CREDENTIAL CRD WHERE CRD.CREATED_BY_ID = %1$d AND CRD.STATUS != 2 GROUP BY CRD.STATUS ";
    public static String PROFILE_SETTING_STATISTICS = "SELECT CASE WHEN ROL.STATUS = 0 THEN 'INACTIVE-ROLE' WHEN ROL.STATUS = 1 THEN 'ACTIVE-ROLE' END AS NAME, COUNT(*) AS VALUE " +
        "FROM ROLE ROL WHERE ROL.CREATED_BY_ID = %1$d AND ROL.STATUS != 2 GROUP BY ROL.STATUS \n" +
        "UNION \n" +
        "SELECT CASE WHEN PRO.STATUS = 0 THEN 'INACTIVE-PROFILE' WHEN PRO.STATUS = 1 THEN 'ACTIVE-PROFILE' END AS NAME, COUNT(*) AS VALUE " +
        "FROM PROFILE PRO WHERE PRO.CREATED_BY_ID = %1$d AND PRO.STATUS != 2 GROUP BY PRO.STATUS \n" +
        "UNION \n" +
        "SELECT CASE WHEN PER.STATUS = 0 THEN 'INACTIVE-PERMISSION' WHEN PER.STATUS = 1 THEN 'ACTIVE-PERMISSION' END AS NAME, COUNT(*) AS VALUE " +
        "FROM PERMISSION PER WHERE PER.CREATED_BY_ID = %1$d AND PER.STATUS != 2 GROUP BY PER.STATUS \n" +
        "UNION \n" +
        "SELECT CASE WHEN AU.STATUS = 0 THEN 'INACTIVE-USER' WHEN AU.STATUS = 1 THEN 'ACTIVE-USER' END AS NAME, COUNT(*) AS VALUE " +
        "FROM APP_USER AU WHERE AU.CREATED_BY_ID = %1$d AND AU.STATUS != 2 GROUP BY AU.STATUS";
    public static String FORM_SETTING_STATISTICS = "SELECT CASE WHEN GF.STATUS = 0 THEN 'INACTIVE-FORM' WHEN GF.STATUS = 1 THEN 'ACTIVE-FORM' END AS NAME, COUNT(*) AS VALUE " +
        "FROM GEN_FORM GF WHERE GF.CREATED_BY_ID = %1$d AND GF.STATUS != 2 GROUP BY GF.STATUS \n" +
        "UNION \n" +
        "SELECT CASE WHEN GC.STATUS = 0 THEN 'INACTIVE-CONTROL' WHEN GC.STATUS = 1 THEN 'ACTIVE-CONTROL' END AS NAME, COUNT(*) AS VALUE " +
        "FROM GEN_CONTROL GC WHERE GC.CREATED_BY_ID = %1$d AND GC.STATUS != 2 GROUP BY GC.STATUS\n" +
        "UNION \n" +
        "SELECT CASE WHEN GS.STATUS = 0 THEN 'INACTIVE-SECTION' WHEN GS.STATUS = 1 THEN 'ACTIVE-SECTION' END AS NAME, COUNT(*) AS VALUE " +
        "FROM GEN_SECTION GS WHERE GS.CREATED_BY_ID = %1$d AND GS.STATUS != 2 GROUP BY GS.STATUS";
    public static String REPORT_SETTING_STATISTICS = "SELECT CASE WHEN RS.STATUS = 0 THEN 'INACTIVE-REPORT' WHEN RS.STATUS = 1 THEN 'ACTIVE-REPORT' END AS NAME, COUNT(*) AS VALUE " +
        "FROM REPORT_SETTING RS WHERE RS.CREATED_BY_ID = %1$d AND RS.STATUS != 2 \n" +
        "GROUP BY RS.STATUS";
    public static String DASHBOARD_SETTING_STATISTICS = "SELECT CASE WHEN DS.STATUS = 0 THEN 'INACTIVE-DASHBOARD' WHEN DS.STATUS = 1 THEN 'ACTIVE-DASHBOARD' END AS NAME, COUNT(*) AS VALUE " +
        "FROM DASHBOARD_SETTING DS WHERE DS.CREATED_BY_ID = %1$d AND DS.STATUS != 2 \n" +
        "GROUP BY DS.STATUS " +
        "UNION \n" +
        "SELECT CASE WHEN RS.STATUS = 0 THEN 'INACTIVE-REPORT' WHEN RS.STATUS = 1 THEN 'ACTIVE-REPORT' END AS NAME, COUNT(*) AS VALUE " +
        "FROM REPORT_SETTING RS WHERE RS.CREATED_BY_ID = %1$d AND RS.STATUS != 2 \n" +
        "GROUP BY RS.STATUS";
    public static String DASHBOARD_AND_REPORT_SETTING_STATISTICS = "SELECT CASE WHEN DS.STATUS = 0 THEN 'INACTIVE-DASHBOARD' WHEN DS.STATUS = 1 THEN 'ACTIVE-DASHBOARD' END AS NAME, COUNT(*) AS VALUE " +
        "FROM DASHBOARD_SETTING DS WHERE DS.CREATED_BY_ID = %1$d AND DS.STATUS != 2 \n" +
        "GROUP BY DS.STATUS " +
        "UNION \n" +
        "SELECT CASE WHEN RS.STATUS = 0 THEN 'INACTIVE-REPORT' WHEN RS.STATUS = 1 THEN 'ACTIVE-REPORT' END AS NAME, COUNT(*) AS VALUE " +
        "FROM REPORT_SETTING RS WHERE RS.CREATED_BY_ID = %1$d AND RS.STATUS != 2 \n" +
        "GROUP BY RS.STATUS";
    public static String SERVICE_SETTING_STATISTICS = "SELECT CASE WHEN STT.STATUS = 0 THEN 'INACTIVE-STT' WHEN STT.STATUS = 1 THEN 'ACTIVE-STT' END AS NAME, COUNT(*) AS VALUE " +
        "FROM SOURCE_TASK_TYPE STT WHERE STT.CREATED_BY_ID = %1$d AND STT.STATUS != 2 " +
        "GROUP BY STT.STATUS " +
        "UNION \n" +
        "SELECT CASE WHEN ST.STATUS = 0 THEN 'INACTIVE-ST' WHEN ST.STATUS = 1 THEN 'ACTIVE-ST' END AS NAME, COUNT(*) AS VALUE " +
        "FROM SOURCE_TASK ST WHERE ST.CREATED_BY_ID = %1$d " +
        "GROUP BY ST.STATUS";
    public static String SESSION_COUNT_STATISTICS = "SELECT DATE(DATE_CREATED) AS KEY, COUNT(*) AS VALUE\n" +
        "FROM REFRESH_TOKEN RT WHERE RT.CREATED_BY_ID = %1$d GROUP BY DATE(DATE_CREATED) ORDER BY DATE(DATE_CREATED) ASC";
    public static String SESSION_STATISTICS = "SELECT * FROM (\n" +
        "SELECT '1' AS ORDER, 'DAILY' AS NAME, COUNT(1) AS TOTALCOUNT, COALESCE(SUM(CASE WHEN RT.STATUS = 1 THEN 1 ELSE 0 END), 0) AS ACTIVECOUNT, COALESCE(SUM(CASE WHEN RT.STATUS = 2 THEN 1 ELSE 0 END), 0) AS OFFCOUNT\n" +
        "FROM REFRESH_TOKEN RT\n" +
        "WHERE CAST(DATE_CREATED AS DATE) = CURRENT_DATE\n" +
        "UNION\n" +
        "SELECT '2' AS ORDER, 'WEEK' AS NAME, COUNT(1) AS TOTALCOUNT, COALESCE(SUM(CASE WHEN RT.STATUS = 1 THEN 1 ELSE 0 END), 0) AS ACTIVECOUNT, COALESCE(SUM(CASE WHEN RT.STATUS = 2 THEN 1 ELSE 0 END), 0) AS OFFCOUNT\n" +
        "FROM REFRESH_TOKEN RT\n" +
        "WHERE DATE_CREATED >= DATE_TRUNC('WEEK', CURRENT_DATE) AND DATE_CREATED < DATE_TRUNC('WEEK', CURRENT_DATE) + INTERVAL '1 WEEK'\n" +
        "UNION\n" +
        "SELECT '3' AS ORDER, 'MONTH' AS NAME, COUNT(1) AS TOTALCOUNT, COALESCE(SUM(CASE WHEN RT.STATUS = 1 THEN 1 ELSE 0 END), 0) AS ACTIVECOUNT, COALESCE(SUM(CASE WHEN RT.STATUS = 2 THEN 1 ELSE 0 END), 0) AS OFFCOUNT\n" +
        "FROM REFRESH_TOKEN RT\n" +
        "WHERE DATE_CREATED >= DATE_TRUNC('MONTH', CURRENT_DATE) AND DATE_CREATED < DATE_TRUNC('MONTH', CURRENT_DATE) + INTERVAL '1 MONTH'\n" +
        "UNION\n" +
        "SELECT '4' AS ORDER, 'YEAR' AS NAME, COUNT(1) AS TOTALCOUNT, COALESCE(SUM(CASE WHEN RT.STATUS = 1 THEN 1 ELSE 0 END), 0) AS ACTIVECOUNT, COALESCE(SUM(CASE WHEN RT.STATUS = 2 THEN 1 ELSE 0 END), 0) AS OFFCOUNT\n" +
        "FROM REFRESH_TOKEN RT\n" +
        "WHERE DATE_CREATED >= CURRENT_DATE - INTERVAL '1 YEAR')" +
        "\n TOKEN_DATA ORDER BY TOKEN_DATA.ORDER ASC;\n";

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