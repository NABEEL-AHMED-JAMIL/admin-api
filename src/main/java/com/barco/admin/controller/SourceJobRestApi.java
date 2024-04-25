package com.barco.admin.controller;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author Nabeel Ahmed
 */
@Entity
@Table(name = "source_job.json")
@JsonIgnoreProperties(ignoreUnknown=true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SourceJobRestApi {

    private Logger logger = LoggerFactory.getLogger(SourceJobRestApi.class);
}
