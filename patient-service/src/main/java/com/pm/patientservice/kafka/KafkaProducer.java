package com.pm.patientservice.kafka;

import com.pm.patientservice.model.Patient;
import com.pm.patientservice.kafka.events.PatientEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class KafkaProducer {
    private static final Logger log = LoggerFactory.getLogger(KafkaProducer.class);
    private final KafkaTemplate<String, byte[]> kafkaTemplate;
    private static final String TOPIC = "patient-events";

    public KafkaProducer(KafkaTemplate<String, byte[]> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendEvent(Patient patient) {
        if (patient == null || patient.getId() == null) {
            log.error("Cannot send event: patient or patient ID is null");
            return;
        }

        try {
            PatientEvent event = PatientEvent.newBuilder()
                    .setPatientId(patient.getId().toString())
                    .setName(patient.getName() != null ? patient.getName() : "")
                    .setEmail(patient.getEmail() != null ? patient.getEmail() : "")
                    .setEventType("PATIENT_CREATED")
                    .build();

            // Use patient ID as the message key for proper partitioning
            String messageKey = patient.getId().toString();

            CompletableFuture<SendResult<String, byte[]>> future =
                    kafkaTemplate.send(TOPIC, messageKey, event.toByteArray());

            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    log.info("PatientCreated event sent successfully for patient: {} to partition: {}",
                            patient.getId(), result.getRecordMetadata().partition());
                } else {
                    log.error("Failed to send PatientCreated event for patient: {}", patient.getId(), ex);
                }
            });

        } catch (Exception e) {
            log.error("Error creating PatientCreated event for patient: {}", patient.getId(), e);
        }
    }
}