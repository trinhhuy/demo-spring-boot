#

### add dependency
```xml
<project>
    <dependencies>
        <dependency>
            <groupId>io.grpc</groupId>
            <artifactId>grpc-stub</artifactId>
            <version>1.71.0</version>
        </dependency>
        <dependency>
            <groupId>io.grpc</groupId>
            <artifactId>grpc-protobuf</artifactId>
            <version>1.71.0</version>
        </dependency>
        <dependency>
            <!-- Java 9+ compatibility - Do NOT update to 2.0.0 -->
            <groupId>jakarta.annotation</groupId>
            <artifactId>jakarta.annotation-api</artifactId>
            <version>1.3.5</version>
            <optional>true</optional>
        </dependency>
    </dependencies>

    <build>
        <extensions>
            <extension>
                <groupId>kr.motd.maven</groupId>
                <artifactId>os-maven-plugin</artifactId>
                <version>1.6.2</version>
            </extension>
        </extensions>

        <plugins>
            <plugin>
                <groupId>org.xolstice.maven.plugins</groupId>
                <artifactId>protobuf-maven-plugin</artifactId>
                <version>0.6.1</version>
                <configuration>
                    <protocArtifact>com.google.protobuf:protoc:3.25.6:exe:osx-x86_64</protocArtifact>
                    <pluginId>grpc-java</pluginId>
                    <pluginArtifact>io.grpc:protoc-gen-grpc-java:1.71.0:exe:osx-x86_64</pluginArtifact>
                </configuration>
                <executions>
                    <execution>
                        <goals>
                            <goal>compile</goal>
                            <goal>compile-custom</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
</project>
```

### write proto file
src>main>proto>name.proto
```protobuf
syntax = "proto3";

package com.example.demo;

option java_multiple_files = true;
option java_package = "com.example.demo";

service ProductService {
  rpc getAProduct(GetAProductRequest) returns (GetAProductResponse){}
}

message GetAProductRequest {
  uint32 id = 1;
}

message GetAProductResponse {
  uint32 id = 1;
  string name = 2;
  string description = 3;
  uint32 quantity = 4;
}
```

### package and install artifact to local repository 
lifecycle>package
```shell
mvn clean package
```
lifecycle>install
```shell
mvn install
```

### Mount thư mục Maven local repository từ host vào container
```yaml
volumes:
  - ~/.m2:/root/.m2
```
      