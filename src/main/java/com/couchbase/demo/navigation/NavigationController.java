package com.couchbase.demo.navigation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("navigation")
public class NavigationController {
    private final NavigationRulesEngineService navigationService;

    @Autowired
    public NavigationController(NavigationRulesEngineService navigationService) {
        this.navigationService = navigationService;
    }

    @GetMapping("/buckets")
    public Flux<String> getBuckets(@RequestParam(value = "only_active", defaultValue = "false") boolean active) {
        return navigationService.getBuckets(active);
    }


}
