package com.pm.patientservice;

import com.pm.patientservice.grpc.BillingServiceGrpcClient;
import com.pm.patientservice.kafka.KafkaProducer;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest(properties = {
        "grpc.server.port=-1",
        "spring.kafka.bootstrap-servers=localhost:9092"
})
class PatientServiceApplicationTests {

    @MockBean
    private BillingServiceGrpcClient billingServiceGrpcClient;

    @MockBean
    private KafkaProducer kafkaProducer;

    @Test
    void contextLoads() {
    }

}
