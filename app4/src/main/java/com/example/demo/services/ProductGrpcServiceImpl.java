package com.example.demo.services;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.example.demo.*;
import com.example.demo.mapper.ProductMapper;
import com.example.demo.models.Product;
import com.example.demo.repositories.ProductRepository;
import com.google.protobuf.Empty;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductGrpcServiceImpl extends ProductServiceGrpc.ProductServiceImplBase {
    ProductRepository repository;
    ProductMapper productMapper;

    @Override
    public void getAProduct(GetAProductRequest request, StreamObserver<GetAProductResponse> responseObserver) {
        long id = request.getId();
        Optional<Product> product = repository.findById(id);
        if (product.isEmpty()) {
            throw Status.NOT_FOUND
                    .withDescription("Book not found with ID: " + request.getId())
                    .asRuntimeException();
        }

        responseObserver.onNext(productMapper.toGetAProductResponse(product.get()));
        responseObserver.onCompleted();
    }

    @Override
    public void getListProducts(GetListProductRequest request, StreamObserver<ListProductResponse> responseObserver) {
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());
        Page<Product> products = repository.findAll(pageable);
        List<GetAProductResponse> list =
                products.stream().map(productMapper::toGetAProductResponse).toList();
        ListProductResponse response =
                ListProductResponse.newBuilder().addAllList(list).build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void getAllProducts(Empty request, StreamObserver<ListProductResponse> responseObserver) {
        boolean tableEof = false;
        int batchSize = 100;
        long lastId = 1;
        while (!tableEof) {
            List<Product> products = repository.findNextPage(lastId, batchSize);
            List<GetAProductResponse> list =
                    products.stream().map(productMapper::toGetAProductResponse).toList();

            ListProductResponse response =
                    ListProductResponse.newBuilder().addAllList(list).build();

            responseObserver.onNext(response);

            if (list.size() < batchSize) {
                tableEof = true;
            } else {
                lastId = list.getLast().getId();
            }
        }

        responseObserver.onCompleted(); // Kết thúc stream
    }
}
