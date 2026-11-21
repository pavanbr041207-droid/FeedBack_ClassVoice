package com.classvoice.service;

import com.classvoice.domain.CollegeLogo;
import com.classvoice.repo.CollegeLogoRepository;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Locale;
import java.util.Optional;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 * Stores one college logo. The file is trusted only after the server decodes its header:
 * the client-supplied file name and content type are ignored.
 */
@Service
public class LogoService {

    public static final int MAX_BYTES = 512 * 1024;
    public static final int MAX_DIMENSION = 2000;

    private record Detected(String contentType, int width, int height) {
    }

    private final CollegeLogoRepository repository;

    public LogoService(CollegeLogoRepository repository) {
        this.repository = repository;
    }

    /** Replaces the current logo. Returns the detected content type, for the audit entry. */
    @Transactional
    public String replace(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new InvalidLogoException("empty");
        }
        if (file.getSize() > MAX_BYTES) {
            throw new InvalidLogoException("size");
        }
        byte[] bytes;
        try {
            bytes = file.getBytes();
        } catch (IOException e) {
            throw new InvalidLogoException("type");
        }
        if (bytes.length > MAX_BYTES) {
            throw new InvalidLogoException("size");
        }
        Detected detected = inspect(bytes);
        if (detected.width() > MAX_DIMENSION || detected.height() > MAX_DIMENSION
                || detected.width() < 1 || detected.height() < 1) {
            throw new InvalidLogoException("dimensions");
        }
        repository.deleteAll();
        repository.flush();
        repository.save(new CollegeLogo(detected.contentType(), bytes));
        return detected.contentType();
    }

    @Transactional(readOnly = true)
    public Optional<CollegeLogo> current() {
        return repository.findFirstByOrderByIdDesc();
    }

    private static Detected inspect(byte[] bytes) {
        try (ImageInputStream in = ImageIO.createImageInputStream(new ByteArrayInputStream(bytes))) {
            if (in == null) {
                throw new InvalidLogoException("type");
            }
            Iterator<ImageReader> readers = ImageIO.getImageReaders(in);
            if (!readers.hasNext()) {
                throw new InvalidLogoException("type");
            }
            ImageReader reader = readers.next();
            try {
                reader.setInput(in, true, true);
                String format = reader.getFormatName().toLowerCase(Locale.ROOT);
                String type;
                if (format.equals("png")) {
                    type = "image/png";
                } else if (format.equals("jpeg")) {
                    type = "image/jpeg";
                } else {
                    throw new InvalidLogoException("type");
                }
                return new Detected(type, reader.getWidth(0), reader.getHeight(0));
            } finally {
                reader.dispose();
            }
        } catch (IOException | RuntimeException e) {
            if (e instanceof InvalidLogoException invalid) {
                throw invalid;
            }
            throw new InvalidLogoException("type");
        }
    }
}
