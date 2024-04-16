package com.barco.admin.controller;

import com.barco.admin.service.FormSettingService;
import com.barco.common.utility.BarcoUtil;
import com.barco.common.utility.ExceptionUtil;
import com.barco.model.dto.request.ControlRequest;
import com.barco.model.dto.request.FormRequest;
import com.barco.model.dto.request.SectionRequest;
import com.barco.model.dto.response.AppResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Api use form setting rest api
 * @author Nabeel Ahmed
 */
@RestController
@CrossOrigin(origins = "*")
@RequestMapping(value = "/formSetting.json")
public class FormSettingRestApi {

    private Logger logger = LoggerFactory.getLogger(FormSettingRestApi.class);

    @Autowired
    private FormSettingService formSettingService;

    /**
     * @apiName :- addForm
     * @apiNote :- Api use to add form(source form)
     * @param payload
     * @return ResponseEntity<?>
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('ADMIN')")
    @RequestMapping(value = "/addForm", method = RequestMethod.POST)
    public ResponseEntity<?> addForm(@RequestBody FormRequest payload) {
        try {
            return new ResponseEntity<>(this.formSettingService.addForm(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while addForm ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ExceptionUtil.getRootCauseMessage(ex)), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- editForm
     * @apiNote :- Api use to edit form(source form)
     * @param payload
     * @return ResponseEntity<?>
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('ADMIN')")
    @RequestMapping(value = "/editForm", method = RequestMethod.POST)
    public ResponseEntity<?> editForm(@RequestBody FormRequest payload) {
        try {
            return new ResponseEntity<>(this.formSettingService.editForm(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while editForm ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ExceptionUtil.getRootCauseMessage(ex)), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- deleteFormById
     * @apiNote :- Api use to delete form(source form)
     * @param payload
     * @return ResponseEntity<?>
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('ADMIN')")
    @RequestMapping(value = "/deleteFormById", method = RequestMethod.POST)
    public ResponseEntity<?> deleteFormById(@RequestBody FormRequest payload) {
        try {
            return new ResponseEntity<>(this.formSettingService.deleteFormById(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while deleteFormById ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ExceptionUtil.getRootCauseMessage(ex)), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- fetchFormByFormId
     * @apiNote :- Api use to fetch form by form id
     * @param payload
     * @return ResponseEntity<?>
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('ADMIN')")
    @RequestMapping(value = "/fetchFormByFormId", method = RequestMethod.POST)
    public ResponseEntity<?> fetchFormByFormId(@RequestBody FormRequest payload) {
        try {
            return new ResponseEntity<>(this.formSettingService.fetchFormByFormId(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while fetchFormByFormId ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ExceptionUtil.getRootCauseMessage(ex)), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- fetchSTTF
     * @apiNote :- Api use to fetch form
     * @param payload
     * @return ResponseEntity<?>
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('ADMIN')")
    @RequestMapping(value = "/fetchForms", method = RequestMethod.POST)
    public ResponseEntity<?> fetchForms(@RequestBody FormRequest payload) {
        try {
            return new ResponseEntity<>(this.formSettingService.fetchForms(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while fetchForms ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ExceptionUtil.getRootCauseMessage(ex)), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- deleteAllForms
     * @apiNote :- Api use to fetch form
     * @param payload
     * @return ResponseEntity<?>
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('ADMIN')")
    @RequestMapping(value = "/deleteAllForms", method = RequestMethod.POST)
    public ResponseEntity<?> deleteAllForms(@RequestBody FormRequest payload) {
        try {
            return new ResponseEntity<>(this.formSettingService.deleteAllForms(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while deleteAllForms ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ExceptionUtil.getRootCauseMessage(ex)), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- addSection
     * @apiNote :- Api use to add section(form section)
     * @param payload
     * @return ResponseEntity<?>
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('ADMIN')")
    @RequestMapping(value = "/addSection", method = RequestMethod.POST)
    public ResponseEntity<?> addSection(@RequestBody SectionRequest payload) {
        try {
            return new ResponseEntity<>(this.formSettingService.addSection(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while addSection ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ExceptionUtil.getRootCauseMessage(ex)), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- editSection
     * @apiNote :- Api use to edit section(form section)
     * @param payload
     * @return ResponseEntity<?>
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('ADMIN')")
    @RequestMapping(value = "/editSection", method = RequestMethod.POST)
    public ResponseEntity<?> editSection(@RequestBody SectionRequest payload) {
        try {
            return new ResponseEntity<>(this.formSettingService.editSection(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while editSection ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ExceptionUtil.getRootCauseMessage(ex)), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- deleteSectionById
     * @apiNote :- Api use to delete section by id (form section)
     * @param payload
     * @return ResponseEntity<?>
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('ADMIN')")
    @RequestMapping(value = "/deleteSectionById", method = RequestMethod.POST)
    public ResponseEntity<?> deleteSectionById(@RequestBody SectionRequest payload) {
        try {
            return new ResponseEntity<>(this.formSettingService.deleteSectionById(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while deleteSectionById ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ExceptionUtil.getRootCauseMessage(ex)), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- fetchSectionBySectionId
     * @apiNote :- Api use to fetch section by id(form section)
     * @param payload
     * @return ResponseEntity<?>
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('ADMIN')")
    @RequestMapping(value = "/fetchSectionBySectionId", method = RequestMethod.POST)
    public ResponseEntity<?> fetchSectionBySectionId(@RequestBody SectionRequest payload) {
        try {
            return new ResponseEntity<>(this.formSettingService.fetchSectionBySectionId(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while fetchSectionBySectionId ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ExceptionUtil.getRootCauseMessage(ex)), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- fetchSections
     * @apiNote :- Api use to fetch sections(form section)
     * @param payload
     * @return ResponseEntity<?>
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('ADMIN')")
    @RequestMapping(value = "/fetchSections", method = RequestMethod.POST)
    public ResponseEntity<?> fetchSections(@RequestBody SectionRequest payload) {
        try {
            return new ResponseEntity<>(this.formSettingService.fetchSections(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while fetchSections ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ExceptionUtil.getRootCauseMessage(ex)), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- deleteAllSections
     * @apiNote :- Api use to delete all sections(form section)
     * @param payload
     * @return ResponseEntity<?>
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('ADMIN')")
    @RequestMapping(value = "/deleteAllSections", method = RequestMethod.POST)
    public ResponseEntity<?> deleteAllSections(@RequestBody SectionRequest payload) {
        try {
            return new ResponseEntity<>(this.formSettingService.deleteAllSections(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while deleteAllSections ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ExceptionUtil.getRootCauseMessage(ex)), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- addControl
     * @apiNote :- Api use to add control (form control)
     * @param payload
     * @return ResponseEntity<?>
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('ADMIN')")
    @RequestMapping(value = "/addControl", method = RequestMethod.POST)
    public ResponseEntity<?> addControl(@RequestBody ControlRequest payload) {
        try {
            return new ResponseEntity<>(this.formSettingService.addControl(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while addControl ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ExceptionUtil.getRootCauseMessage(ex)), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- editControl
     * @apiNote :- Api use to edit
     * @param payload
     * @return ResponseEntity<?>
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('ADMIN')")
    @RequestMapping(value = "/editControl", method = RequestMethod.POST)
    public ResponseEntity<?> editControl(@RequestBody ControlRequest payload) {
        try {
            return new ResponseEntity<>(this.formSettingService.editControl(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while editControl ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ExceptionUtil.getRootCauseMessage(ex)), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- deleteControlById
     * @apiNote :- Api use to delete control by id(form control)
     * @param payload
     * @return ResponseEntity<?>
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('ADMIN')")
    @RequestMapping(value = "/deleteControlById", method = RequestMethod.POST)
    public ResponseEntity<?> deleteControlById(@RequestBody ControlRequest payload) {
        try {
            return new ResponseEntity<>(this.formSettingService.deleteControlById(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while deleteControlById ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ExceptionUtil.getRootCauseMessage(ex)), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- fetchControlByControlId
     * @apiNote :- Api use to fetch control by control id
     * @param payload
     * @return ResponseEntity<?>
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('ADMIN')")
    @RequestMapping(value = "/fetchControlByControlId", method = RequestMethod.POST)
    public ResponseEntity<?> fetchControlByControlId(@RequestBody ControlRequest payload) {
        try {
            return new ResponseEntity<>(this.formSettingService.fetchControlByControlId(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while fetchControlByControlId ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ExceptionUtil.getRootCauseMessage(ex)), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- fetchControls
     * @apiNote :- Api use to fetch controls (form control)
     * @param payload
     * @return ResponseEntity<?>
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('ADMIN')")
    @RequestMapping(value = "/fetchControls", method = RequestMethod.POST)
    public ResponseEntity<?> fetchControls(@RequestBody ControlRequest payload) {
        try {
            return new ResponseEntity<>(this.formSettingService.fetchControls(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while fetchControls ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ExceptionUtil.getRootCauseMessage(ex)), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- deleteAllSections
     * @apiNote :- Api use to delete all sections(form section)
     * @param payload
     * @return ResponseEntity<?>
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('ADMIN')")
    @RequestMapping(value = "/deleteAllControls", method = RequestMethod.POST)
    public ResponseEntity<?> deleteAllControls(@RequestBody ControlRequest payload) {
        try {
            return new ResponseEntity<>(this.formSettingService.deleteAllControls(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while deleteAllControls ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ExceptionUtil.getRootCauseMessage(ex)), HttpStatus.BAD_REQUEST);
        }
    }

}
