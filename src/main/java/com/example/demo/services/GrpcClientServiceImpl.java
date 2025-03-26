package com.example.demo.services;

import com.example.demo.OrderServiceGrpc;
import com.example.demo.ProductServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class GrpcClientServiceImpl {
    String orderServiceName;
    String productServiceName;
    private final LoadBalancerClient loadBalancerClient;
    ManagedChannel managedChannel;

    public GrpcClientServiceImpl(
            LoadBalancerClient loadBalancerClient,
            @Value("${grpc.client.mcs_order_grpc_server.address}") String orderServiceName,
            @Value("${grpc.client.mcs_product_grpc_server.address}") String productServiceName
    ) {
        this.loadBalancerClient = loadBalancerClient;
        this.orderServiceName = orderServiceName;
        this.productServiceName = productServiceName;
    }

    @PreDestroy
    public void shutdownChannel() {
        if (managedChannel != null) {
            managedChannel.shutdownNow();
        }
    }

    private ManagedChannel createChannel(String serviceName) {
        ServiceInstance instance = loadBalancerClient.choose(serviceName);
        if (instance == null) {
            throw new IllegalArgumentException("Not found instance " + serviceName);
        }

        String host = instance.getHost();
        int grpcPort = Integer.parseInt(instance.getMetadata().getOrDefault("grpc.port", "9090"));
        log.info("\n\nAAA grpc port {} \n\n", grpcPort);

        if (managedChannel == null || managedChannel.isShutdown()) {
            managedChannel = ManagedChannelBuilder.forAddress(host, grpcPort).usePlaintext().build();
        }
        return managedChannel;
    }

    public ProductServiceGrpc.ProductServiceBlockingStub getProductServiceStub() {
        ManagedChannel channel = createChannel(productServiceName);
        return ProductServiceGrpc.newBlockingStub(channel);
    }

    public OrderServiceGrpc.OrderServiceBlockingStub getOrderServiceStub() {
        ManagedChannel channel = createChannel(orderServiceName);
        return OrderServiceGrpc.newBlockingStub(channel);
    }
}
