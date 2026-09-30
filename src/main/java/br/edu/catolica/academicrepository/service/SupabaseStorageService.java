package br.edu.catolica.academicrepository.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;


import java.io.IOException;

@Service
public class SupabaseStorageService {

    private final WebClient webClient;
    private final String bucket;
    private final String baseUrl;

    public SupabaseStorageService(
            @Value("${supabase.url}") String baseUrl,
            @Value("${supabase.key}") String serviceKey,
            @Value("${supabase.bucket}") String bucket
    ) {
        this.baseUrl = baseUrl;
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl + "/storage/v1/object")
                .defaultHeader("apikey", serviceKey)
                .defaultHeader("Authorization", "Bearer " + serviceKey)
                .build();
        this.bucket = bucket;
    }

    public void uploadFile(MultipartFile file, String pathInBucket) throws IOException {
        webClient.put()
                .uri("/{bucket}/{path}", bucket, pathInBucket)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .bodyValue(file.getBytes())
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    public byte[] downloadFile(String pathInBucket) {
        return webClient.get()
                .uri("/{bucket}/{path}", bucket, pathInBucket)
                .retrieve()
                .bodyToMono(byte[].class)
                .block();
    }

    public void deleteFile(String pathInBucket) {
        webClient.delete()
                .uri("/{bucket}/{path}", bucket, pathInBucket)
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }

    public String getPublicUrl(String pathInBucket) {
        return String.format(
                "%s/storage/v1/object/public/%s/%s",
                baseUrl, bucket, pathInBucket
        );
    }

    public byte[] downloadUsingPublicUrl(String pathInBucket) {
        String publicUrl = getPublicUrl(pathInBucket);

        return WebClient.create()
                .get()
                .uri(publicUrl)
                .retrieve()
                .bodyToMono(byte[].class)
                .block();
    }
}
