package com.urlshortener.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class QrCodeService {

    private final String appBaseUrl;

    public QrCodeService(@Value("${APP_BASE_URL:http://localhost:8080}") String appBaseUrl) {
        this.appBaseUrl = appBaseUrl;
    }

    public byte[] generateQrCodeImage(String shortCode) {
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