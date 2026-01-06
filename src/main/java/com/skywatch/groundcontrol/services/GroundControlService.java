package com.skywatch.groundcontrol.services;

import com.skywatch.groundcontrol.enums.CommandType;
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

    private final Map<String, AircraftData> aircraftFleet = new ConcurrentHashMap<>();

    private final Set<String> aircraftsInCriticalFuel = ConcurrentHashMap.newKeySet();
    private final Set<String> aircraftsLandingInitiated = ConcurrentHashMap.newKeySet();

    private static final double CRITICAL_FUEL_THRESHOLD = 10.0;
    private static final double EMERGENCY_LANDING_THRESHOLD = 8.0;

    public GroundControlService(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @KafkaListener(topics = "skywatch-telemetry", groupId = "embraer-monitoring-group")
    public void monitorAircraftTraffic(AircraftData aircraftData){

        aircraftFleet.put(aircraftData.getAircraftId(), aircraftData);
        LOGGER.info("Aeronave: {} monitorada", aircraftData.getAircraftId());

        if (aircraftData.getFuelLevel() < CRITICAL_FUEL_THRESHOLD && aircraftsInCriticalFuel.add(aircraftData.getAircraftId())){
            LOGGER.error("🚨 ALERTA: COMBUSTÍVEL CRÍTICO!  🚨");
            LOGGER.error("Aeronave: {}", aircraftData.getAircraftId());
            LOGGER.error("Combustével: {}%", String.format("%.2f", aircraftData.getFuelLevel()));
        }

        if (aircraftData.getFuelLevel() < EMERGENCY_LANDING_THRESHOLD && aircraftsLandingInitiated.add(aircraftData.getAircraftId())){
            enviarComando(aircraftData.getAircraftId(), CommandType.POUSO.name());
            LOGGER.warn("Comando POUSO enviado automaticamente para {}", aircraftData.getAircraftId());
        }
    }

    public void enviarComando(String id, String comando){
        kafkaTemplate.send(TOPIC, id, comando);
        LOGGER.info("Comando '{}' enviado para {}", comando, id);
    }

    public Collection<AircraftData> getLastData(){
        return aircraftFleet.values();
    }
}
