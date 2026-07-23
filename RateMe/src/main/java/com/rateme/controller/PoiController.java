package com.rateme.controller;

import com.rateme.dto.PoiDTO;
import com.rateme.service.PoiService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(originPatterns = "*", allowCredentials = "true")
public class PoiController {

    private final PoiService poiService;

    public PoiController(PoiService poiService) {
        this.poiService = poiService;
    }

    @GetMapping("/pois")
    public List<PoiDTO> getAllPois() {
        return poiService.getAllPois();
    }

    @GetMapping("/pois/{id}")
    public PoiDTO getPoi(@PathVariable Long id) {
        return poiService.getPoiById(id);
    }
}
