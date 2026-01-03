package com.skywatch.groundcontrol.Controller;

import com.skywatch.groundcontrol.model.AircraftData;
import com.skywatch.groundcontrol.services.GroundControlService;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping("api/status")
public class GroundControlController {

    private final GroundControlService services;

    public GroundControlController(GroundControlService services) {
        this.services = services;
    }

    @GetMapping
    public Collection<AircraftData> getLatestStatus(){
        return services.getLastData();
    }

    @PostMapping("/comando/{id}")
    public void enviarComando(@PathVariable String id, @RequestBody String comando){
        services.enviarComando(id, comando);
    }

    @GetMapping("/teste")
    public String teste() {
        return "O Centro de Controle está online!";
    }
}
