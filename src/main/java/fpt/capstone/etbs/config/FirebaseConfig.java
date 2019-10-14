package fpt.capstone.etbs.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreOptions;
import com.google.cloud.storage.Bucket;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import com.google.firebase.cloud.StorageClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import javax.annotation.PostConstruct;
import java.io.*;
import java.nio.charset.StandardCharsets;

@Configuration
public class FirebaseConfig {

    private static final String BUCKET_URL = "etbs-441a1.appspot.com";
    private static final String SDK_FILE = "firebasesdk.json";

    @Bean
    public Firestore firestore() {
        return FirestoreClient.getFirestore();
    }

    @Bean
    public Bucket bucket() {
        return StorageClient.getInstance().bucket();
    }

    @PostConstruct
    public void init() throws IOException {
        File file = new ClassPathResource(SDK_FILE).getFile();
        FileInputStream serviceAccount =
                new FileInputStream(file);
        GoogleCredentials credentials = GoogleCredentials.fromStream(serviceAccount);
        FirestoreOptions firestoreOptions = FirestoreOptions.newBuilder()
                .setCredentials(credentials)
                .setTimestampsInSnapshotsEnabled(true).build();
        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(credentials)
                .setFirestoreOptions(firestoreOptions)
                .setStorageBucket(BUCKET_URL)
                .build();
        FirebaseApp.initializeApp(options);
    }

}
