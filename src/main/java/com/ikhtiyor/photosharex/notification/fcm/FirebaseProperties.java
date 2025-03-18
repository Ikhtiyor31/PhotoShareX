package com.ikhtiyor.photosharex.notification.fcm;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "gcp")
public class FirebaseProperties {

    private Resource firebaseFile;

    public Resource getFirebaseFile() {
        return firebaseFile;
    }

    public void setFirebaseFile(Resource firebaseFile) {
        this.firebaseFile = firebaseFile;
    }
}
