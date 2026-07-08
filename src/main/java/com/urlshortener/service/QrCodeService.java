package com.urlshortener.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.urlshortener.entity.UrlMapping;
import com.urlshortener.exception.UrlNotFoundException;
import com.urlshortener.repository.UrlMappingRepository;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class QrCodeService {

    private final String appBaseUrl;
    private final UrlMappingRepository urlMappingRepository;
    private final UrlService urlService;

    public QrCodeService(@Value("${APP_BASE_URL:http://localhost:8080}") String appBaseUrl,
                          UrlMappingRepository urlMappingRepository,
                          UrlService urlService) {
        this.appBaseUrl = appBaseUrl;
        this.urlMappingRepository = urlMappingRepository;
        this.urlService = urlService;
    }

    public byte[] generateQrCodeImage(String shortCode) {
        UrlMapping mapping = urlMappingRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new UrlNotFoundException("Short URL not found: " + shortCode));
        urlService.assertOwnership(mapping);

        String fullShortUrl = appBaseUrl + "/" + shortCode;

        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(fullShortUrl, BarcodeFormat.QR_CODE, 300, 300);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);
            return outputStream.toByteArray();
        } catch (WriterException | IOException e) {
            throw new RuntimeException("QR code generation failed for: " + shortCode, e);
        }
    }
}