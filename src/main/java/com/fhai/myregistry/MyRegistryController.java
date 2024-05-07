package com.fhai.myregistry;

import com.flhai.iregistry.service.RegistryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class MyRegistryController {
    @Autowired
    RegistryService iRegistryService;
}
