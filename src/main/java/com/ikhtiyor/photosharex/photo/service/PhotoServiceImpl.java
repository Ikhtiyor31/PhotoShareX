package com.ikhtiyor.photosharex.photo.service;


import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.ikhtiyor.photosharex.exception.GCPStorageException;
import com.ikhtiyor.photosharex.exception.InvalidImageException;
import com.ikhtiyor.photosharex.exception.ResourceNotFoundException;
import com.ikhtiyor.photosharex.photo.dto.PhotoDTO;
import com.ikhtiyor.photosharex.photo.dto.PhotoRequest;
import com.ikhtiyor.photosharex.photo.dto.PhotoUpdateRequest;
import com.ikhtiyor.photosharex.photo.dto.UploadPhotoDTO;
import com.ikhtiyor.photosharex.photo.enums.VisibilityType;
import com.ikhtiyor.photosharex.photo.model.Photo;
import com.ikhtiyor.photosharex.photo.repository.PhotoRepository;
import com.ikhtiyor.photosharex.user.model.User;
import com.ikhtiyor.photosharex.utils.ImageUtil;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional
public class PhotoServiceImpl implements PhotoService {

    @Value("${gcp.cloud.bucket}")
    private String bucket;

    @Value("${gcp.cloud.bucket-directory}")
    private String bucketDirectory;

    private final PhotoRepository photoRepository;
    private final Storage storage;

    public PhotoServiceImpl(PhotoRepository photoRepository, Storage storage) {
        this.photoRepository = photoRepository;
        this.storage = storage;
    }

    @Transactional(readOnly = true)
    @Override
    public Page<PhotoDTO> getPhotoList(Pageable pageable, User user) {
        PageRequest createdAtPageable = PageRequest.of(
            pageable.getPageNumber(),
            pageable.getPageSize(),
            Direction.DESC, "createdAt");

        return photoRepository.findByUser(user, createdAtPageable).map(PhotoDTO::from);
    }

    @Override
    public void createPhoto(PhotoRequest request, User user) {

        Photo photo = Photo.createOf(request, user);
        photoRepository.save(photo);

    }

    @Transactional(readOnly = true)
    @Override
    public PhotoDTO getPhoto(Long photoId, User user) {
        Photo photo = photoRepository.findById(photoId)
            .orElseThrow(() -> new ResourceNotFoundException("Photo not found wih Id: " + photoId));

        if (!photo.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("No permission to access this photo");
        }

        return PhotoDTO.from(photo);
    }

    @Override
    public void updatePhotoDetail(PhotoUpdateRequest request, Long photoId, User user) {
        Photo photo = photoRepository.findById(photoId)
            .orElseThrow(() -> new ResourceNotFoundException("Photo not found wih Id: " + photoId));

        if (!photo.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("No permission to update photo");
        }

        photo.setTitle(request.title());
        photo.setDescription(request.location());
        photo.setLocation(request.location());
    }

    @Override
    public void changePhotoVisibility(Long photoId, VisibilityType visibilityType, User user) {
        Photo photo = photoRepository.findById(photoId)
            .orElseThrow(() -> new ResourceNotFoundException("Photo not found wih Id: " + photoId));

        if (!photo.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("No permission to update photo");
        }

        photo.setVisibilityType(visibilityType);

    }


    @Override
    public UploadPhotoDTO uploadImage(MultipartFile image) {
        if (!ImageUtil.isImage(image)) {
            throw new InvalidImageException("File type is not image");
        }
        try {
            String generatedImageName = ImageUtil.generateImageName(image.getOriginalFilename());
            String imageName = bucketDirectory + generatedImageName;

            BlobId blobId = BlobId.of(bucket, imageName);
            BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                .setContentType(image.getContentType())
                .build();

            ByteArrayOutputStream outputStream = ImageUtil.resizeImage(image.getInputStream());
            Blob blob = storage.create(blobInfo, outputStream.toByteArray());
            URL signedShortImageUrl = blob.signUrl(5, TimeUnit.MINUTES);
            return new UploadPhotoDTO(imageName, signedShortImageUrl.toString());
        } catch (IOException e) {
            throw new GCPStorageException("failed to upload image into GCP storage " + e);
        }
    }

    @Override
    public Resource downloadImage(String imageName) {
        Blob blob = storage.get(bucket, imageName);
        if (blob == null) {
            throw new InvalidImageException("Image not found: " + imageName);
        }
        try {
            URL signedUrl = blob.signUrl(5, TimeUnit.SECONDS);
            return new UrlResource(signedUrl);
        } catch (Exception e) {
            throw new GCPStorageException("Failed to download image: " + e.getMessage());
        }
    }
}
