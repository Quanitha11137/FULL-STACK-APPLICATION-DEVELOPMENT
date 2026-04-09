package flavius.com.controller;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.imageio.ImageIO;

import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.TextAlignment;

import flavius.com.entity.AppUser;
import flavius.com.entity.Course;
import flavius.com.entity.Enrollment;
import flavius.com.repository.AppUserRepository;
import flavius.com.repository.CourseRepository;
import flavius.com.repository.EnrollmentRepository;

@Controller
public class EnrollmentController {

    private final EnrollmentRepository enrollmentRepository;
    private final AppUserRepository appUserRepository;
    private final CourseRepository courseRepository;

    public EnrollmentController(EnrollmentRepository enrollmentRepository,
                                AppUserRepository appUserRepository,
                                CourseRepository courseRepository) {
        this.enrollmentRepository = enrollmentRepository;
        this.appUserRepository = appUserRepository;
        this.courseRepository = courseRepository;
    }
    
    @PostMapping("/enroll")
    public String enroll(Authentication authentication,
                         @RequestParam Long courseId) {

        AppUser user = appUserRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        // check existing enrollment safely
        Enrollment saved = enrollmentRepository
                .findByUserAndCourse(user, course)
                .orElse(null);

        // create only if not exists
        if (saved == null) {
            saved = enrollmentRepository.save(new Enrollment(user, course));
        }

        return "redirect:/pay/" + saved.getId();
    }

    @GetMapping("/my-courses")
    public String myCourses(Authentication authentication, Model model) {

        AppUser user = appUserRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Enrollment> enrollments = enrollmentRepository.findByUser(user);
        model.addAttribute("enrollments", enrollments);

        return "my-courses";
    }
    @PostMapping("/complete-course")
    public String completeCourse(@RequestParam Long enrollmentId,
                                 Authentication authentication) {

        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new RuntimeException("Enrollment not found"));

        if (!enrollment.getUser().getUsername().equals(authentication.getName())) {
            throw new RuntimeException("Unauthorized action");
        }

        enrollment.setStatus(Enrollment.Status.COMPLETED);
        enrollment.setCompletedDate(LocalDate.now());
        enrollment.setProgressPercent(100);

        enrollmentRepository.save(enrollment);

        return "redirect:/my-courses";
    }
    @PostMapping("/remove-enrollment")
    public String removeEnrollment(@RequestParam Long enrollmentId,
                                   Authentication authentication) {

        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new RuntimeException("Enrollment not found"));

        if (!enrollment.getUser().getUsername().equals(authentication.getName())) {
            throw new RuntimeException("Unauthorized action");
        }

        enrollmentRepository.delete(enrollment);
        return "redirect:/my-courses";
    }
    @PostMapping("/update-progress")
    public String updateProgress(@RequestParam Long enrollmentId,
                                 @RequestParam Integer progressPercent,
                                 Authentication authentication) {

        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new RuntimeException("Enrollment not found"));

        if (!enrollment.getUser().getUsername().equals(authentication.getName())) {
            throw new RuntimeException("Unauthorized action");
        }
        if (progressPercent == null) progressPercent = 0;
        if (progressPercent < 0) progressPercent = 0;
        if (progressPercent > 100) progressPercent = 100;

        enrollment.setProgressPercent(progressPercent);
        if (progressPercent == 100) {
            enrollment.setStatus(Enrollment.Status.COMPLETED);
            if (enrollment.getCompletedDate() == null) {
                enrollment.setCompletedDate(LocalDate.now());
            }
        } else {
            enrollment.setStatus(Enrollment.Status.IN_PROGRESS);
            enrollment.setCompletedDate(null);
        }

        enrollmentRepository.save(enrollment);

        return "redirect:/my-courses";
    }
    @GetMapping("/certificate/{enrollmentId}")
    public ResponseEntity<byte[]> downloadCertificate(
            @PathVariable Long enrollmentId,
            Authentication authentication) throws Exception {

        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new RuntimeException("Enrollment not found"));

        if (!enrollment.getUser().getUsername().equals(authentication.getName())) {
            throw new RuntimeException("Unauthorized");
        }

        if (enrollment.getStatus() != Enrollment.Status.COMPLETED) {
            throw new RuntimeException("Course not completed");
        }

        String certificateNumber = "FLAV-" + enrollment.getId() + "-" + enrollment.getCourse().getId();
        LocalDateTime issuedAt = LocalDateTime.now();
        String issuedAtText = issuedAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(out);
        PdfDocument pdf = new PdfDocument(writer);

        com.itextpdf.kernel.pdf.PdfPage page = pdf.addNewPage();
        Document document = new Document(pdf);

        float pageW = pdf.getDefaultPageSize().getWidth();
        float pageH = pdf.getDefaultPageSize().getHeight();

        Rectangle rect = new Rectangle(20, 20, pageW - 40, pageH - 40);
        PdfCanvas canvas = new PdfCanvas(page);
        canvas.setLineWidth(3);
        canvas.rectangle(rect);
        canvas.stroke();

        float left = 60;
        float right = 60;
        float contentW = pageW - left - right;

        document.add(new Paragraph("CERTIFICATE OF COMPLETION")
                .setBold().setFontSize(24)
                .setTextAlignment(TextAlignment.CENTER)
                .setFixedPosition(left, pageH - 160, contentW));

        document.add(new Paragraph("This certificate is proudly presented to")
                .setFontSize(12)
                .setTextAlignment(TextAlignment.CENTER)
                .setFixedPosition(left, pageH - 200, contentW));

        document.add(new Paragraph(enrollment.getUser().getUsername())
                .setBold().setFontSize(18)
                .setTextAlignment(TextAlignment.CENTER)
                .setFixedPosition(left, pageH - 235, contentW));

        document.add(new Paragraph("For successfully completing the course")
                .setFontSize(12)
                .setTextAlignment(TextAlignment.CENTER)
                .setFixedPosition(left, pageH - 270, contentW));

        document.add(new Paragraph(enrollment.getCourse().getName())
                .setBold().setFontSize(16)
                .setTextAlignment(TextAlignment.CENTER)
                .setFixedPosition(left, pageH - 305, contentW));

        document.add(new Paragraph("Completed Date: " + enrollment.getCompletedDate())
                .setFontSize(11)
                .setTextAlignment(TextAlignment.CENTER)
                .setFixedPosition(left, pageH - 345, contentW));

        document.add(new Paragraph("Certificate No: " + certificateNumber)
                .setFontSize(11)
                .setTextAlignment(TextAlignment.CENTER)
                .setFixedPosition(left, pageH - 365, contentW));

        document.add(new Paragraph("Issued At: " + issuedAtText)
                .setFontSize(11)
                .setTextAlignment(TextAlignment.CENTER)
                .setFixedPosition(left, pageH - 385, contentW));
        float sigWidth = 140f;
        float sigX = pageW - 60 - sigWidth;
        float sigY = 100f;

        try {
            ClassPathResource signatureResource =
                    new ClassPathResource("static/images/signature.png");
            try (InputStream is = signatureResource.getInputStream()) {
                byte[] signatureBytes = is.readAllBytes();
                ImageData sigData = ImageDataFactory.create(signatureBytes);
                Image sigImage = new Image(sigData);
                sigImage.setWidth(sigWidth);
                sigImage.setFixedPosition(sigX, sigY + 30);
                document.add(sigImage);
            }
        } catch (Exception ignored) {}
        float textWidth = 260f;
        float textX = sigX - 120f;
        float baseY = sigY + 20f;
        float gap = 16f;

        document.add(new Paragraph("Digitally signed by Flavius Academy")
                .setFontSize(10)
                .setTextAlignment(TextAlignment.RIGHT)
                .setFixedPosition(textX, baseY, textWidth));

        document.add(new Paragraph("Signature Timestamp: " + issuedAtText)
                .setFontSize(10)
                .setTextAlignment(TextAlignment.RIGHT)
                .setFixedPosition(textX, baseY - gap, textWidth));

        document.add(new Paragraph("Authorized Signature")
                .setFontSize(10)
                .setTextAlignment(TextAlignment.RIGHT)
                .setFixedPosition(textX, baseY - (gap * 2), textWidth));

        String verificationUrl = "http://172.17.195.129:8080/verify/" + enrollment.getId();

        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(verificationUrl, BarcodeFormat.QR_CODE, 140, 140);

        BufferedImage qrImage = MatrixToImageWriter.toBufferedImage(bitMatrix);
        ByteArrayOutputStream qrBaos = new ByteArrayOutputStream();
        ImageIO.write(qrImage, "png", qrBaos);

        ImageData qrData = ImageDataFactory.create(qrBaos.toByteArray());
        Image qr = new Image(qrData);

        float qrX = 60f;
        float qrY = 75f;

        qr.setFixedPosition(qrX, qrY);
        qr.setWidth(120f);
        document.add(qr);

        document.add(new Paragraph("Scan to verify")
                .setFontSize(10)
                .setFixedPosition(qrX, qrY - 15, 200));

        document.close();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=certificate.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(out.toByteArray());
    }

    @GetMapping("/verify/{id}")
    public String verifyCertificate(@PathVariable Long id, Model model) {

        Enrollment enrollment = enrollmentRepository.findById(id).orElse(null);

        if (enrollment == null || enrollment.getStatus() != Enrollment.Status.COMPLETED) {
            model.addAttribute("valid", false);
            return "verify";
        }

        String certificateNumber = "FLAV-" + enrollment.getId() + "-" + enrollment.getCourse().getId();

        model.addAttribute("valid", true);
        model.addAttribute("student", enrollment.getUser().getUsername());
        model.addAttribute("course", enrollment.getCourse().getName());
        model.addAttribute("date", enrollment.getCompletedDate());
        model.addAttribute("certNo", certificateNumber);

        return "verify";
    }
}