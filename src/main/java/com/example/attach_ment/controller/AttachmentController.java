package com.example.attach_ment.controller;

import com.example.attach_ment.model.Attachment;
import com.example.attach_ment.model.AttachmentContent;
import com.example.attach_ment.repositariy.AttachmentContentRepository;
import com.example.attach_ment.repositariy.AttachmentRepository;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@RestController
@RequestMapping("/attachment")
public class AttachmentController {

    private String uploadFolder = "E:\\springproektPdp\\attach_ment\\src\\main\\resources\\upload";
    private final AttachmentRepository attachmentRepository;
    private final AttachmentContentRepository attachmentContentRepository;
    @Autowired
    public AttachmentController( AttachmentRepository attachmentRepository, AttachmentContentRepository attachmentContentRepository) {
        this.attachmentRepository = attachmentRepository;
        this.attachmentContentRepository = attachmentContentRepository;
    }
    @PostMapping("/upload")
    public String saveFile(MultipartHttpServletRequest multipartFile) throws IOException {

        Iterator<String> fileNames = multipartFile.getFileNames();
        MultipartFile file = multipartFile.getFile(fileNames.next());
        Attachment attachment =new Attachment();
        assert file != null;
        attachment.setSize(file.getSize());
        attachment.setOrginalName(file.getOriginalFilename());
        attachment.setContentType(file.getContentType());
        Attachment attachment1 = attachmentRepository.save(attachment);
        AttachmentContent attachmentContent =new AttachmentContent();
        attachmentContent.setAttachment(attachment1);
        attachmentContent.setContent(file.getBytes());
        attachmentContentRepository.save(attachmentContent);
        return "fayil saqlandi";
    }

    @PostMapping("/uploadSystem")
    public void uploadSystem(MultipartHttpServletRequest request) throws IOException {
        Iterator<String> fileNames = request.getFileNames();
        while (fileNames.hasNext()) {
            MultipartFile file = request.getFile(fileNames.next());
            Attachment attachment = new Attachment();
            String s = UUID.randomUUID().toString();
            String originalFilename = file.getOriginalFilename();
            if (file != null) {
                attachment.setOrginalName(originalFilename);
                attachment.setSize(file.getSize());
                String[] split = originalFilename.split("\\.");
                attachment.setName(s + "." + split[split.length - 1]);
                attachment.setContentType(file.getContentType());
                Attachment save = attachmentRepository.save(attachment);
                Path path = Paths.get(uploadFolder + "\\" + save.getName());
                Files.copy(file.getInputStream(), path);
            }
        }


    }

    @GetMapping("/info")
    public List<Attachment> getInfoAttachment(){
        return attachmentRepository.findAll();
    }
    @GetMapping("/download/{id}")
    public void getById(@PathVariable Integer id, HttpServletResponse response) throws IOException {
        Optional<Attachment> optionalAttachment = attachmentRepository.findById(id);

        if (optionalAttachment.isPresent()){
            Attachment attachment = optionalAttachment.get();
            Optional<AttachmentContent> optionalAttachmentContent = attachmentContentRepository.findByAttachmentId(id);
            if (optionalAttachmentContent.isPresent()){
                AttachmentContent attachmentContent = optionalAttachmentContent.get();
                response.setHeader("Content-Disposition","attachment; filename=\"" +attachment.getOrginalName() + "\"");
                response.setContentType(attachment.getContentType());
                FileCopyUtils.copy(attachmentContent.getContent(),response.getOutputStream());
            }

        }
    }
    @GetMapping("/info/{id}")
    public Attachment getById(@PathVariable Integer id){
        Optional<Attachment> byId = attachmentRepository.findById(id);
        return byId.orElseGet(Attachment::new);
    }

    @SneakyThrows
    @GetMapping("/downloads/{id}")
    public void  getFileSystem(@PathVariable Integer id,HttpServletResponse response){
        Optional<Attachment> optionalAttechmat = attachmentRepository.findById(id);
        if (optionalAttechmat.isPresent()){
            Attachment attachment = optionalAttechmat.get();
            response.setHeader("Content-Disposition","attachment; filename=\"" +attachment.getOrginalName() + "\"");
            response.setContentType(attachment.getContentType());
            FileInputStream fileInputStream = new FileInputStream(uploadFolder+"\\"+attachment.getName());
            FileCopyUtils.copy(fileInputStream,response.getOutputStream());
        }
    }

    @GetMapping("/attachment")
    public List<Attachment> getFileSystem(){
        List<Attachment> attachmentList = attachmentRepository.findAll();
        List<Attachment> byNameList = new ArrayList<>();
        boolean ishas = false;
        for (Attachment attachment : attachmentList) {
            if (attachment.getName() != null){
                byNameList.add(attachment);
            }
        }
        return byNameList;

    }
}
