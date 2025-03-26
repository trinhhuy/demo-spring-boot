package com.example.demo.services;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.example.demo.dto.request.PageableProductRequest;
import com.example.demo.dto.response.ProductResponse;
import io.grpc.StatusRuntimeException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.google.protobuf.Empty;
import com.example.demo.*;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class App4ServiceImpl {
    String serviceName;

    GrpcClientApp4ServiceImpl grpcClientService;

    public App4ServiceImpl(GrpcClientApp4ServiceImpl grpcClientService, @Value("${grpc.client.mcs_name_grpc_server.address}") String serviceName) {
        this.grpcClientService = grpcClientService;
        this.serviceName = serviceName;
    }

    public ProductResponse getAProduct(int id) {
        try {
        ProductServiceGrpc.ProductServiceBlockingStub stub = grpcClientService.getProductServiceStub(serviceName);
        // Gọi các method từ stub
        GetAProductRequest productRequest =
                GetAProductRequest.newBuilder().setId(id).build();
        GetAProductResponse productResponse = stub.getAProduct(productRequest);
        return ProductResponse.builder()
                .id(productResponse.getId())
                .name(productResponse.getName())
                .description(productResponse.getDescription())
                .quantity(productResponse.getQuantity())
                .build();
        } catch (StatusRuntimeException e) {
            log.info("StatusRuntimeException: {}", e.getMessage());
            return null;
        } catch (Exception e) {
            log.info("Exception: {}", e.getMessage());
            return null;
        }
    }

    public List<ProductResponse> getListProducts(PageableProductRequest request) {
        ProductServiceGrpc.ProductServiceBlockingStub stub = grpcClientService.getProductServiceStub(serviceName);
        // Gọi các method từ stub
        GetListProductRequest productRequest = GetListProductRequest.newBuilder()
                .setPage(request.getPage())
                .setSize(request.getSize())
                .setOrderDirection(request.getOrderDirection())
                .build();

        log.info("\n\n -- start call grpc -- \n\n");
        ListProductResponse productResponse = stub.getListProducts(productRequest);

        log.info("\n\n -- end call grpc -- \n\n");
        return productResponse.getListList().stream()
                .map(this::mapperProductResponse)
                .toList();
    }

    public List<ProductResponse> getAllProducts() {
        ProductServiceGrpc.ProductServiceBlockingStub stub = grpcClientService.getProductServiceStub(serviceName);
        // Gọi các method từ stub
        List<ProductResponse> productList = new ArrayList<>();

        // Gọi phương thức getAllProducts với Empty request và nhận Iterator
        Iterator<ListProductResponse> iterator =
                stub.getAllProducts(Empty.newBuilder().build());

        // Lặp qua từng phần tử trong Iterator
        while (iterator.hasNext()) {
            ListProductResponse response = iterator.next();

            // use sublist addAll
            List<ProductResponse> subList = response.getListList().stream()
                    .map(this::mapperProductResponse)
                    .toList();
            productList.addAll(subList);
        }

        return productList;
    }

    public SseEmitter streamProducts() {
        ProductServiceGrpc.ProductServiceBlockingStub stub = grpcClientService.getProductServiceStub(serviceName);
        // Gọi các method từ stub
        SseEmitter emitter = new SseEmitter();

        // Gọi phương thức getAllProducts với Empty request và nhận Iterator
        Iterator<ListProductResponse> iterator =
                stub.getAllProducts(Empty.newBuilder().build());

        // Lặp qua từng phần tử trong Iterator và gửi mỗi phần tử qua SSE
        try {
            while (iterator.hasNext()) {
                ListProductResponse response = iterator.next();

                List<ProductResponse> list = response.getListList().stream()
                        .map(this::mapperProductResponse)
                        .toList();
                // send batch sse
                emitter.send(list);
            }
            emitter.complete(); // Kết thúc SSE khi hết dữ liệu
        } catch (Exception e) {
            emitter.completeWithError(e); // Nếu có lỗi xảy ra, kết thúc SSE với lỗi
        }

        return emitter;
    }

    private ProductResponse mapperProductResponse(GetAProductResponse productResponse) {
        return ProductResponse.builder()
                .id(productResponse.getId())
                .name(productResponse.getName())
                .description(productResponse.getDescription())
                .quantity(productResponse.getQuantity())
                .build();
    }

}

