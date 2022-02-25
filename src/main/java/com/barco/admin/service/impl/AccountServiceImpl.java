package com.barco.admin.service.impl;

import com.barco.admin.service.IAccountService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Nabeel Ahmed
 */
@Service
@Transactional
public class AccountServiceImpl implements IAccountService {

    public Logger logger = LogManager.getLogger(AccountServiceImpl.class);
}
