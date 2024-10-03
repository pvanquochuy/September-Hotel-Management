package com.example.SeptemberHotel.service;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.example.SeptemberHotel.exception.OurException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

// Bucket: Là một đơn vị lưu trữ chính trong Amazon S3.
// Nó tương tự như một thư mục chứa các tệp tin (objects) và có thể chứa vô số tệp tin.

/**
 *  tải hình ảnh lên một S3 Bucket của AWS.
 */
@Service
public class AwsS3Service {
    private final String bucketName = "september-hotel-images"; //  Tên của S3 Bucket nơi các hình ảnh sẽ được lưu trữ.

    @Value("${aws.s3.access.key}")
    private String awsS3AccessKey;

    @Value(("${aws.s3.secret.key}"))
    private String awsS3SecretKey;

    public String saveImageToS3(MultipartFile photo){
        String s3LocationImage = null;
        try{
            String s3FileName = photo.getOriginalFilename(); // Lấy tên file gốc từ đối tượng MultipartFile.
            BasicAWSCredentials awsCredentials = new BasicAWSCredentials(awsS3AccessKey, awsS3SecretKey); // Tạo đối tượng BasicAWSCredentials bằng cách sử dụng khóa truy cập và khóa bí mật từ cấu hình.
            AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                    .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                    .withRegion(Regions.US_EAST_2)
                    .build();
            InputStream inputStream = photo.getInputStream(); // Lấy Dữ Liệu File:

            ObjectMetadata metadata = new ObjectMetadata(); // Thiết Lập Metadata cho File:
            metadata.setContentType("image/jpeg");

            // Tạo Yêu Cầu Tải Lên (PutObjectRequest):
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, s3FileName, inputStream, metadata);
            s3Client.putObject(putObjectRequest); // hực Hiện Tải Lên File:
             return  "https://" + bucketName + ".s3.amazonaws.com/"+s3FileName; // Trả Về URL của File Đã Tải Lên:

        }catch (Exception e){
            e.printStackTrace();
            throw new OurException("Unable to upload image s3 bucket " + e.getMessage());
        }
    }
}
