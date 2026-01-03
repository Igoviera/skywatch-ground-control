package com.skywatch.groundcontrol.services;

import com.skywatch.groundcontrol.model.AircraftData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class GroundControlService {
    private static final Logger LOGGER = LoggerFactory.getLogger(GroundControlService.class);

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final String TOPIC = "skywatch-commands";

    private final Map<String, AircraftData> fleetMap = new ConcurrentHashMap<>();

    private final Set<String> aircraftsInCriticalFuel = ConcurrentHashMap.newKeySet();
    private final Set<String> aircraftsLandingInitiated = ConcurrentHashMap.newKeySet();

    public GroundControlService(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @KafkaListener(topics = "skywatch-telemetry", groupId = "embraer-monitoring-group")
    public void monitorAircraftTraffic(AircraftData data){

        AircraftData last = fleetMap.get(data.getAircraftId());

        if (last == null || data.getTimestamp() > last.getTimestamp()){
            fleetMap.put(data.getAircraftId(), data);
        } else {
            LOGGER.debug(
                    "Telemetria ignorada (antiga) | Aircraft={} | ts={} < {}",
                    data.getAircraftId(),
                    data.getTimestamp(),
                    last.getTimestamp()
            );
            return;
        }

        LOGGER.info("Aeronave: {} monitorada", data.getAircraftId());

        if (data.getFuelLevel() < 10.0 && aircraftsInCriticalFuel.add(data.getAircraftId())){
            LOGGER.error("🚨 ALERTA: COMBUSTÍVEL CRÍTICO!  🚨");
            LOGGER.error("Aeronave: {}", data.getAircraftId());
            LOGGER.error("Combustével: {}%", String.format("%.2f", data.getFuelLevel()));
        }

        if (data.getFuelLevel() < 8.0 && aircraftsLandingInitiated.add(data.getAircraftId())){
            enviarComando(data.getAircraftId(), "LAND");
            LOGGER.warn("Comando LAND enviado automaticamente para {}", data.getAircraftId());
        }
    }

    public void enviarComando(String id, String comando){
        kafkaTemplate.send(TOPIC, id, comando);
        LOGGER.info("Comando '{}' enviado para {}", comando, id);
    }

    public Collection<AircraftData> getLastData(){
        return fleetMap.values();
    }
}
