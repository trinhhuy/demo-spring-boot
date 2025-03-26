package com.example.demo.services;

import com.example.demo.ProductServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class GrpcClientApp4ServiceImpl {
    LoadBalancerClient loadBalancerClient;

    private ManagedChannel createChannel(String serviceName) {
        ServiceInstance instance = loadBalancerClient.choose(serviceName);
        if (instance == null) {
            throw new IllegalArgumentException("Not found instance " + serviceName);
        }

        String host = instance.getHost();
        int grpcPort = Integer.parseInt(instance.getMetadata().getOrDefault("grpc.port", "9090"));
        log.info("\n\nAAA grpc port {} \n\n", grpcPort);
        return ManagedChannelBuilder.forAddress(host, grpcPort).usePlaintext().build();
    }

    public ProductServiceGrpc.ProductServiceBlockingStub getProductServiceStub(String serviceName) {
        ManagedChannel channel = createChannel(serviceName);
        return ProductServiceGrpc.newBlockingStub(channel);
    }
}
