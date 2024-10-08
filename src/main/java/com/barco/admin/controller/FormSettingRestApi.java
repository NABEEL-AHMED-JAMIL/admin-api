package com.barco.admin.controller;

import com.barco.admin.service.FormSettingService;
import com.barco.common.utility.BarcoUtil;
import com.barco.common.utility.ExceptionUtil;
import com.barco.model.dto.request.*;
import com.barco.model.dto.response.AppResponse;
import com.barco.model.util.MessageUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

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
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('ADMIN') or hasRole('USER')")
    @RequestMapping(value = "/addForm", method = RequestMethod.POST)
    public ResponseEntity<?> addForm(@RequestBody FormRequest payload) {
        try {
            return new ResponseEntity<>(this.formSettingService.addForm(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while addForm ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ex.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- updateForm
     * @apiNote :- Api use to update form(source form)
     * @param payload
     * @return ResponseEntity<?>
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('ADMIN') or hasRole('USER')")
    @RequestMapping(value = "/updateForm", method = RequestMethod.POST)
    public ResponseEntity<?> updateForm(@RequestBody FormRequest payload) {
        try {
            return new ResponseEntity<>(this.formSettingService.updateForm(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while updateForm ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ex.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- deleteFormById
     * @apiNote :- Api use to delete form(source form)
     * @param payload
     * @return ResponseEntity<?>
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('ADMIN') or hasRole('USER')")
    @RequestMapping(value = "/deleteFormById", method = RequestMethod.POST)
    public ResponseEntity<?> deleteFormById(@RequestBody FormRequest payload) {
        try {
            return new ResponseEntity<>(this.formSettingService.deleteFormById(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while deleteFormById ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ex.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- fetchFormByFormId
     * @apiNote :- Api use to fetch form by form id
     * @param payload
     * @return ResponseEntity<?>
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('ADMIN') or hasRole('USER')")
    @RequestMapping(value = "/fetchFormByFormId", method = RequestMethod.POST)
    public ResponseEntity<?> fetchFormByFormId(@RequestBody FormRequest payload) {
        try {
            return new ResponseEntity<>(this.formSettingService.fetchFormByFormId(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while fetchFormByFormId ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ex.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- fetchForms
     * @apiNote :- Api use to fetch form
     * @param payload
     * @return ResponseEntity<?>
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('ADMIN') or hasRole('USER')")
    @RequestMapping(value = "/fetchForms", method = RequestMethod.POST)
    public ResponseEntity<?> fetchForms(@RequestBody FormRequest payload) {
        try {
            return new ResponseEntity<>(this.formSettingService.fetchForms(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while fetchForms ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ex.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- fetchFormsByFormType
     * @apiNote :- Api use to fetch form by form type
     * @param payload
     * @return ResponseEntity<?>
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('ADMIN') or hasRole('USER')")
    @RequestMapping(value = "/fetchFormsByFormType", method = RequestMethod.POST)
    public ResponseEntity<?> fetchFormsByFormType(@RequestBody FormRequest payload) {
        try {
            return new ResponseEntity<>(this.formSettingService.fetchFormsByFormType(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while fetchFormsByFormType ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ex.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- deleteAllForms
     * @apiNote :- Api use to fetch form
     * @param payload
     * @return ResponseEntity<?>
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('ADMIN') or hasRole('USER')")
    @RequestMapping(value = "/deleteAllForms", method = RequestMethod.POST)
    public ResponseEntity<?> deleteAllForms(@RequestBody FormRequest payload) {
        try {
            return new ResponseEntity<>(this.formSettingService.deleteAllForms(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while deleteAllForms ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ex.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- fetchAllFormLinkSTT
     * @apiNote :- Api use to fetch all form link stt
     * @param payload
     * @return ResponseEntity<?>
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('ADMIN') or hasRole('USER')")
    @RequestMapping(value = "/fetchAllFormLinkSTT", method = RequestMethod.POST)
    public ResponseEntity<?> fetchAllFormLinkSTT(@RequestBody FormRequest payload) {
        try {
            return new ResponseEntity<>(this.formSettingService.fetchAllFormLinkSTT(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while fetchAllFormLinkSTT ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ex.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- linkFormSTT
     * @apiNote :- Api use link form with stt
     * @param payload
     * @return ResponseEntity<?>
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('ADMIN') or hasRole('USER')")
    @RequestMapping(value = "/linkFormSTT", method = RequestMethod.POST)
    public ResponseEntity<?> linkFormSTT(@RequestBody FormRequest payload) {
        try {
            return new ResponseEntity<>(this.formSettingService.linkFormSTT(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while linkFormSTT ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ex.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- fetchAllFormLinkSection
     * @apiNote :- Api use to fetch all form link section
     * @param payload
     * @return ResponseEntity<?>
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('ADMIN') or hasRole('USER')")
    @RequestMapping(value = "/fetchAllFormLinkSection", method = RequestMethod.POST)
    public ResponseEntity<?> fetchAllFormLinkSection(@RequestBody FormRequest payload) {
        try {
            return new ResponseEntity<>(this.formSettingService.fetchAllFormLinkSection(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while fetchAllFormLinkSection ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ex.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- linkFormSection
     * @apiNote :- Api use linkFormSection form link section
     * @param payload
     * @return ResponseEntity<?>
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('ADMIN') or hasRole('USER')")
    @RequestMapping(value = "/linkFormSection", method = RequestMethod.POST)
    public ResponseEntity<?> linkFormSection(@RequestBody FormRequest payload) {
        try {
            return new ResponseEntity<>(this.formSettingService.linkFormSection(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while linkFormSection ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ex.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- linkFormSectionOrder
     * @apiNote :- Api use to set form section order
     * @param payload
     * @return ResponseEntity<?>
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('ADMIN') or hasRole('USER')")
    @RequestMapping(value = "/linkFormSectionOrder", method = RequestMethod.POST)
    public ResponseEntity<?> linkFormSectionOrder(@RequestBody FormRequest payload) {
        try {
            return new ResponseEntity<>(this.formSettingService.linkFormSectionOrder(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while linkFormSectionOrder ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ex.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- addSection
     * @apiNote :- Api use to add section(form section)
     * @param payload
     * @return ResponseEntity<?>
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('ADMIN') or hasRole('USER')")
    @RequestMapping(value = "/addSection", method = RequestMethod.POST)
    public ResponseEntity<?> addSection(@RequestBody SectionRequest payload) {
        try {
            return new ResponseEntity<>(this.formSettingService.addSection(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while addSection ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ex.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- updateSection
     * @apiNote :- Api use to update section(form section)
     * @param payload
     * @return ResponseEntity<?>
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('ADMIN') or hasRole('USER')")
    @RequestMapping(value = "/updateSection", method = RequestMethod.POST)
    public ResponseEntity<?> updateSection(@RequestBody SectionRequest payload) {
        try {
            return new ResponseEntity<>(this.formSettingService.updateSection(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while updateSection ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ex.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- deleteSectionById
     * @apiNote :- Api use to delete section by id (form section)
     * @param payload
     * @return ResponseEntity<?>
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('ADMIN') or hasRole('USER')")
    @RequestMapping(value = "/deleteSectionById", method = RequestMethod.POST)
    public ResponseEntity<?> deleteSectionById(@RequestBody SectionRequest payload) {
        try {
            return new ResponseEntity<>(this.formSettingService.deleteSectionById(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while deleteSectionById ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ex.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- fetchSectionBySectionId
     * @apiNote :- Api use to fetch section by id(form section)
     * @param payload
     * @return ResponseEntity<?>
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('ADMIN') or hasRole('USER')")
    @RequestMapping(value = "/fetchSectionBySectionId", method = RequestMethod.POST)
    public ResponseEntity<?> fetchSectionBySectionId(@RequestBody SectionRequest payload) {
        try {
            return new ResponseEntity<>(this.formSettingService.fetchSectionBySectionId(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while fetchSectionBySectionId ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ex.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- fetchSections
     * @apiNote :- Api use to fetch sections(form section)
     * @param payload
     * @return ResponseEntity<?>
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('ADMIN') or hasRole('USER')")
    @RequestMapping(value = "/fetchSections", method = RequestMethod.POST)
    public ResponseEntity<?> fetchSections(@RequestBody SectionRequest payload) {
        try {
            return new ResponseEntity<>(this.formSettingService.fetchSections(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while fetchSections ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ex.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- deleteAllSections
     * @apiNote :- Api use to delete all sections(form section)
     * @param payload
     * @return ResponseEntity<?>
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('ADMIN') or hasRole('USER')")
    @RequestMapping(value = "/deleteAllSections", method = RequestMethod.POST)
    public ResponseEntity<?> deleteAllSections(@RequestBody SectionRequest payload) {
        try {
            return new ResponseEntity<>(this.formSettingService.deleteAllSections(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while deleteAllSections ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ex.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- fetchAllSectionLinkControl
     * @apiNote :- Api use to fetch all section link controls
     * @param payload
     * @return ResponseEntity<?>
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('ADMIN') or hasRole('USER')")
    @RequestMapping(value = "/fetchAllSectionLinkControl", method = RequestMethod.POST)
    public ResponseEntity<?> fetchAllSectionLinkControl(@RequestBody SectionRequest payload) {
        try {
            return new ResponseEntity<>(this.formSettingService.fetchAllSectionLinkControl(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while fetchAllSectionLinkControl ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ex.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- linkSectionControl
     * @apiNote :- Api use linkControlSection link section link controls
     * @param payload
     * @return ResponseEntity<?>
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('ADMIN') or hasRole('USER')")
    @RequestMapping(value = "/linkSectionControl", method = RequestMethod.POST)
    public ResponseEntity<?> linkSectionControl(@RequestBody SectionRequest payload) {
        try {
            return new ResponseEntity<>(this.formSettingService.linkSectionControl(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while linkSectionControl ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ex.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- linkSectionControlOrder
     * @apiNote :- Api use to set section link controls order
     * @param payload
     * @return ResponseEntity<?>
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('ADMIN') or hasRole('USER')")
    @RequestMapping(value = "/linkSectionControlOrder", method = RequestMethod.POST)
    public ResponseEntity<?> linkSectionControlOrder(@RequestBody SectionRequest payload) {
        try {
            return new ResponseEntity<>(this.formSettingService.linkSectionControlOrder(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while linkSectionControlOrder ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ex.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- fetchAllSectionLinkForm
     * @apiNote :- Api use to fetch all section link form
     * @param payload
     * @return ResponseEntity<?>
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('ADMIN') or hasRole('USER')")
    @RequestMapping(value = "/fetchAllSectionLinkForm", method = RequestMethod.POST)
    public ResponseEntity<?> fetchAllSectionLinkForm(@RequestBody SectionRequest payload) {
        try {
            return new ResponseEntity<>(this.formSettingService.fetchAllSectionLinkForm(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while fetchAllSectionLinkForm ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ex.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- linkSectionForm
     * @apiNote :- Api use linkControlSection link section link form
     * @param payload
     * @return ResponseEntity<?>
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('ADMIN') or hasRole('USER')")
    @RequestMapping(value = "/linkSectionForm", method = RequestMethod.POST)
    public ResponseEntity<?> linkSectionForm(@RequestBody SectionRequest payload) {
        try {
            return new ResponseEntity<>(this.formSettingService.linkSectionForm(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while linkSectionForm ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ex.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- linkSectionControlOrder
     * @apiNote :- Api use to set section link controls order
     * @param payload
     * @return ResponseEntity<?>
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('ADMIN') or hasRole('USER')")
    @RequestMapping(value = "/linkSectionFormOrder", method = RequestMethod.POST)
    public ResponseEntity<?> linkSectionFormOrder(@RequestBody SectionRequest payload) {
        try {
            return new ResponseEntity<>(this.formSettingService.linkSectionFormOrder(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while linkSectionFormOrder ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ex.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- addControl
     * @apiNote :- Api use to add control (form control)
     * @param payload
     * @return ResponseEntity<?>
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('ADMIN') or hasRole('USER')")
    @RequestMapping(value = "/addControl", method = RequestMethod.POST)
    public ResponseEntity<?> addControl(@RequestBody ControlRequest payload) {
        try {
            return new ResponseEntity<>(this.formSettingService.addControl(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while addControl ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ex.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- updateControl
     * @apiNote :- Api use to update
     * @param payload
     * @return ResponseEntity<?>
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('ADMIN') or hasRole('USER')")
    @RequestMapping(value = "/updateControl", method = RequestMethod.POST)
    public ResponseEntity<?> updateControl(@RequestBody ControlRequest payload) {
        try {
            return new ResponseEntity<>(this.formSettingService.updateControl(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while updateControl ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ex.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- deleteControlById
     * @apiNote :- Api use to delete control by id(form control)
     * @param payload
     * @return ResponseEntity<?>
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('ADMIN') or hasRole('USER')")
    @RequestMapping(value = "/deleteControlById", method = RequestMethod.POST)
    public ResponseEntity<?> deleteControlById(@RequestBody ControlRequest payload) {
        try {
            return new ResponseEntity<>(this.formSettingService.deleteControlById(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while deleteControlById ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ex.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- fetchControlByControlId
     * @apiNote :- Api use to fetch control by control id
     * @param payload
     * @return ResponseEntity<?>
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('ADMIN') or hasRole('USER')")
    @RequestMapping(value = "/fetchControlByControlId", method = RequestMethod.POST)
    public ResponseEntity<?> fetchControlByControlId(@RequestBody ControlRequest payload) {
        try {
            return new ResponseEntity<>(this.formSettingService.fetchControlByControlId(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while fetchControlByControlId ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ex.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- fetchControls
     * @apiNote :- Api use to fetch controls (form control)
     * @param payload
     * @return ResponseEntity<?>
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('ADMIN') or hasRole('USER')")
    @RequestMapping(value = "/fetchControls", method = RequestMethod.POST)
    public ResponseEntity<?> fetchControls(@RequestBody ControlRequest payload) {
        try {
            return new ResponseEntity<>(this.formSettingService.fetchControls(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while fetchControls ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ex.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- deleteAllSections
     * @apiNote :- Api use to delete all sections(form section)
     * @param payload
     * @return ResponseEntity<?>
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('ADMIN') or hasRole('USER')")
    @RequestMapping(value = "/deleteAllControls", method = RequestMethod.POST)
    public ResponseEntity<?> deleteAllControls(@RequestBody ControlRequest payload) {
        try {
            return new ResponseEntity<>(this.formSettingService.deleteAllControls(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while deleteAllControls ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ex.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- fetchAllControlsLinkSection
     * @apiNote :- Api use to fetch all controls link section
     * @param payload
     * @return ResponseEntity<?>
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('ADMIN') or hasRole('USER')")
    @RequestMapping(value = "/fetchAllControlLinkSection", method = RequestMethod.POST)
    public ResponseEntity<?> fetchAllControlLinkSection(@RequestBody ControlRequest payload) {
        try {
            return new ResponseEntity<>(this.formSettingService.fetchAllControlLinkSection(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while fetchAllControlLinkSection ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ex.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- linkControlsSection
     * @apiNote :- Api use linkControlSection link controls link section
     * @param payload
     * @return ResponseEntity<?>
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('ADMIN') or hasRole('USER')")
    @RequestMapping(value = "/linkControlSection", method = RequestMethod.POST)
    public ResponseEntity<?> linkControlSection(@RequestBody ControlRequest payload) {
        try {
            return new ResponseEntity<>(this.formSettingService.linkControlSection(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while linkControlSection ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ex.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- linkControlSectionOrder
     * @apiNote :- Api use to set controls link section order
     * @param payload
     * @return ResponseEntity<?>
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('ADMIN') or hasRole('USER')")
    @RequestMapping(value = "/linkControlSectionOrder", method = RequestMethod.POST)
    public ResponseEntity<?> linkControlSectionOrder(@RequestBody ControlRequest payload) {
        try {
            return new ResponseEntity<>(this.formSettingService.linkControlSectionOrder(payload), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while linkControlSectionOrder ", ExceptionUtil.getRootCause(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ex.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- downloadSTTCommonTemplateFile
     * @apiNote :- Api use to download sttc template file
     * @param payload
     * @return ResponseEntity<?>
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('ADMIN') or hasRole('USER')")
    @RequestMapping(value = "/downloadSTTCommonTemplateFile", method = RequestMethod.POST)
    public ResponseEntity<?> downloadSTTCommonTemplateFile(@RequestBody STTFileUploadRequest payload) {
        try {
            HttpHeaders headers = new HttpHeaders();
            DateFormat dateFormat = new SimpleDateFormat(BarcoUtil.SIMPLE_DATE_PATTERN);
            String fileName = "BatchSTTDownload-"+dateFormat.format(new Date())+"-"+ UUID.randomUUID() + ".xlsx";
            headers.add(BarcoUtil.CONTENT_DISPOSITION,BarcoUtil.FILE_NAME_HEADER + fileName);
            return ResponseEntity.ok().headers(headers).body(this.formSettingService.downloadSTTCommonTemplateFile(payload).toByteArray());
        } catch (Exception ex) {
            logger.error("An error occurred while downloadSTTCommonTemplateFile xlsx file", ExceptionUtil.getRootCauseMessage(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ex.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- downloadSTTCommon
     * @apiNote :- Api use to download stt* all file
     * @param payload
     * @return ResponseEntity<?>
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('ADMIN') or hasRole('USER')")
    @RequestMapping(value = "/downloadSTTCommon", method = RequestMethod.POST)
    public ResponseEntity<?> downloadSTTCommon(@RequestBody STTFileUploadRequest payload) {
        try {
            HttpHeaders headers = new HttpHeaders();
            DateFormat dateFormat = new SimpleDateFormat(BarcoUtil.SIMPLE_DATE_PATTERN);
            String fileName = "BatchLookupDownload-"+dateFormat.format(new Date())+"-"+ UUID.randomUUID() + ".xlsx";
            headers.add(BarcoUtil.CONTENT_DISPOSITION,BarcoUtil.FILE_NAME_HEADER + fileName);
            return ResponseEntity.ok().headers(headers).body(this.formSettingService.downloadSTTCommon(payload).toByteArray());
        } catch (Exception ex) {
            logger.error("An error occurred while downloadSTTCommon ", ExceptionUtil.getRootCauseMessage(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ExceptionUtil.getRootCauseMessage(ex)), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @apiName :- uploadSTTCommon
     * @apiNote :- Api use to upload
     * @param payload
     * @return ResponseEntity<?>
     * */
    @PreAuthorize("hasRole('MASTER_ADMIN') or hasRole('ADMIN') or hasRole('USER')")
    @RequestMapping(value = "/uploadSTTCommon", method = RequestMethod.POST)
    public ResponseEntity<?> uploadSTTCommon(FileUploadRequest payload) {
        try {
            if (!BarcoUtil.isNull(payload.getFile())) {
                return new ResponseEntity<>(this.formSettingService.uploadSTTCommon(payload), HttpStatus.OK);
            }
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, MessageUtil.DATA_NOT_FOUND), HttpStatus.BAD_REQUEST);
        } catch (Exception ex) {
            logger.error("An error occurred while uploadSTTCommon ", ExceptionUtil.getRootCauseMessage(ex));
            return new ResponseEntity<>(new AppResponse(BarcoUtil.ERROR, ExceptionUtil.getRootCauseMessage(ex)), HttpStatus.BAD_REQUEST);
        }
    }

}
