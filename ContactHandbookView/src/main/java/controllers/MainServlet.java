package controllers;

import dto.AttachmentFile;
import dto.AvatarFile;
import dto.Contact;
import dto.Phone;
import org.apache.log4j.Logger;
import services.ContactService;
import services.MailSender;
import shedulers.MailScheduler;
import utils.JSONSerializer;
import utils.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.*;
import java.net.URLEncoder;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@MultipartConfig
public class MainServlet extends HttpServlet {
    private final static Logger logger = Logger.getLogger(MainServlet.class);

    private final static String BASE_URL = "http://localhost:8080/contact-handbook-view-1.0-SNAPSHOT/";

    @Override
    public void init() throws ServletException {
        new MailScheduler();

        ContactService.uploadAvatarDir = getInitParameter("upload-avatar-dir");
        ContactService.uploadAttachmentsDir = getInitParameter("upload-attachment-dir");

        logger.info("MainServlet has been started");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String url = req.getRequestURL().toString();

        boolean flag = true;
        String JSON = null;

        String page = req.getParameter("page");

        switch (url) {
            case BASE_URL + "add":
            case BASE_URL + "filters":
            case BASE_URL + "save":
                Contact contact = new Contact();

                contact.setName(req.getParameter("name"));
                contact.setSurname(req.getParameter("surname"));
                contact.setPatronymic(req.getParameter("patronymic"));

                contact.setYear(req.getParameter("year"));
                contact.setMonth(req.getParameter("month"));
                contact.setDay(req.getParameter("day"));

                contact.setGender(req.getParameter("gender"));

                contact.setFamilyStatus(req.getParameter("familyStatus"));

                contact.setCountry(req.getParameter("country"));
                contact.setTown(req.getParameter("town"));
                contact.setStreet(req.getParameter("street"));
                contact.setHouse(req.getParameter("house"));
                contact.setFlat(req.getParameter("flat"));
                contact.setIndex(req.getParameter("index"));

                contact.setSite(req.getParameter("site"));
                contact.setEmail(req.getParameter("email"));
                contact.setJobPlace(req.getParameter("job"));
                contact.setCitizenship(req.getParameter("citizenship"));

                switch (url) {
                    case BASE_URL + "add":
                        logger.info("Request action = add");

                        List<Phone> phones = JSONSerializer.phonesFromJSON(req.getParameter("phones"));
                        List<AttachmentFile> files = JSONSerializer.filesInfoFromJSON(req.getParameter("files"));

                        Part part = req.getPart("avatar");
                        AvatarFile avatar = new AvatarFile();

                        if (part.getContentType() != null && part.getContentType().contains("image")) {
                            avatar.setFileName(UUID.randomUUID().toString());
                            logger.info("Avatar file has been founded; filename = " + avatar.getFileName() + " upload path = " + ContactService.uploadAvatarDir);

                            part.write(ContactService.uploadAvatarDir + avatar.getFileName());
                            logger.info("Avatar file written on the disk");
                        }

                        contact.setAvatarFile(avatar);
                        ContactService.addContact(contact);
                        logger.info("Contact has been added; contact = " + contact.toString());

                        assert phones != null;
                        ContactService.addPhones(phones);

                        assert files != null;
                        int i = 0;
                        for (AttachmentFile attachmentFile : files) {
                            attachmentFile.setGeneratedFileName(UUID.randomUUID().toString());

                            Part file;
                            file = req.getPart("file" + i);
                            if (file.getContentType() != null) {
                                logger.info("Attachment file has been founded; filename = " + attachmentFile.getGeneratedFileName() + " upload path = " + ContactService.uploadAttachmentsDir);

                                file.write(ContactService.uploadAttachmentsDir + attachmentFile.getGeneratedFileName());
                                logger.info("Attachment file written on the disk");
                            }
                            i++;
                        }
                        ContactService.addAttachments(files);

                        break;
                    case BASE_URL + "filters":
                        logger.info("Request action = filters");
                        String dateType = req.getParameter("date-type");

                        Set<String> genders = new HashSet<>();
                        for (int j = 1; j < 3; j++) {
                            String g = req.getParameter("g" + j);
                            if (!StringUtils.isEmpty(g)) {
                                genders.add(g);
                            }
                        }

                        Set<String> familyStatuses = new HashSet<>();
                        for (int j = 1; j < 5; j++) {
                            String fs = req.getParameter("fs" + j);
                            if (!StringUtils.isEmpty(fs)) {
                                familyStatuses.add(fs);
                            }
                        }

                        JSON = ContactService.getContactsByFilter(contact, genders, familyStatuses, dateType, page);

                        flag = false;
                        break;
                    case BASE_URL + "save":
                        logger.info("Request action = save");
                        contact.setId(req.getParameter("id"));

                        Part avatarPart = req.getPart("avatar");

                        String contactAvatar = ContactService.getContactAvatar(req.getParameter("id"));
                        avatar = new AvatarFile();
                        if (avatarPart.getContentType() != null && avatarPart.getContentType().contains("image")) {
                            if (StringUtils.isEmpty(contactAvatar)) {
                                contactAvatar = UUID.randomUUID().toString();
                            }

                            logger.info("Avatar file has been founded; filename = " + contactAvatar + "; uploadPath = " + ContactService.uploadAvatarDir);
                            avatarPart.write(ContactService.uploadAvatarDir + contactAvatar);
                            logger.info("Avatar file written on the disk");
                        }
                        avatar.setFileName(contactAvatar);
                        contact.setAvatarFile(avatar);

                        phones = JSONSerializer.phonesFromJSON(req.getParameter("phones"));
                        files = JSONSerializer.filesInfoFromJSON(req.getParameter("files"));

                        i = 0;
                        logger.info("Updating contacts attachments files");

                        int[] states = new int[0];
                        if (files != null) {
                            states = new int[files.size()];
                            for (AttachmentFile attachmentFile : files) {
                                String fileName;

                                if (attachmentFile.getEnabled() == null) {
                                    states[i] = 0;
                                    i++;
                                    logger.info("File with filename = " + attachmentFile.getGeneratedFileName() + " has been skipped");
                                    continue;
                                } else if (attachmentFile.getEnabled()) {
                                    if (StringUtils.isEmpty(attachmentFile.getGeneratedFileName())) {
                                        states[i] = 1;

                                        fileName = UUID.randomUUID().toString();
                                        attachmentFile.setGeneratedFileName(fileName);

                                        logger.info("File with filename = " + attachmentFile.getGeneratedFileName() + " ready to be written to disk");
                                    } else {
                                        states[i] = 2;
                                        fileName = attachmentFile.getGeneratedFileName();

                                        logger.info("File with filename = " + attachmentFile.getGeneratedFileName() + " ready to be rewritten to disk");
                                    }
                                } else {
                                    states[i] = 3;

                                    fileName = ContactService.getAttachmentFileNameById(attachmentFile.getId());
                                    File deleteFile = new File(ContactService.uploadAttachmentsDir + fileName);
                                    deleteFile.delete();

                                    logger.info("File with filename = " + attachmentFile.getGeneratedFileName() + " has been deleted");
                                    i++;
                                    continue;
                                }

                                Part file;
                                file = req.getPart("file" + i);
                                if (file.getContentType() != null) {
                                    file.write(ContactService.uploadAttachmentsDir + fileName);

                                    logger.info("File with filename = " + attachmentFile.getGeneratedFileName() + " has been written to disk");
                                }
                                i++;
                            }
                        }

                        ContactService.saveContact(contact, phones, files, states);
                        break;
                }
                break;
            case BASE_URL + "edit":
                logger.info("Request action = edit");
                JSON = ContactService.getContactById(req.getParameter("id"));

                flag = false;
                break;
            case BASE_URL + "delete":
                logger.info("Request action = delete");

                String[] ids = req.getParameterValues("id");
                ContactService.disableContact(ids);

                StringBuilder stringBuilder = new StringBuilder();
                for (String id : ids) {
                    stringBuilder.append(id).append(' ');
                }
                logger.info("Contacts with id = " + stringBuilder.toString() + "has been deleted");

                break;
            case BASE_URL + "download":
                logger.info("Request action = download");

                String fileId = req.getParameter("file-id");
                if (!StringUtils.isEmpty(fileId)) {
                    String fileName = ContactService.getAttachmentFileNameById(fileId);
                    String originalFileName = ContactService.getOriginalAttachmentFileNameById(fileId);

                    resp.setHeader("Content-disposition", "attachment; filename=" + URLEncoder.encode(originalFileName, "UTF-8"));

                    logger.info("Downloaded file: filename = " + fileName + ", original filename = " + originalFileName);
                    try (InputStream inputStream = new FileInputStream(ContactService.uploadAttachmentsDir + fileName); OutputStream outputStream = resp.getOutputStream()) {
                        byte[] buffer = new byte[1048];

                        int bytes;
                        while ((bytes = inputStream.read(buffer)) > 0) {
                            outputStream.write(buffer, 0, bytes);
                        }
                    }
                    logger.info("File ready to be downloaded");
                }
                return;
            case BASE_URL + "emails":
                logger.info("Request action = emails");
                JSON = ContactService.getContactsWithNotNullEmail();
                logger.info("Contacts with not null emails = " + JSON);

                flag = false;
                break;
            case BASE_URL + "send-messages":
                logger.info("Request action = send-messages");

                String subject = req.getParameter("subject");
                String text = req.getParameter("text");

                logger.info("Subject = " + subject + ", text = " + text);
                if (!StringUtils.isEmpty(subject) && !StringUtils.isEmpty(text)) {
                    int i = 0;
                    String contactId = req.getParameter("email-id" + i);
                    String email = req.getParameter("email" + i++);
                    while (!StringUtils.isEmpty(email) && !StringUtils.validEmail(email)) {
                        MailSender.send(email, subject, text, contactId);

                        contactId = req.getParameter("email-id" + i);
                        email = req.getParameter("email" + i++);
                    }
                }

                logger.info("Mail has been sent");
                return;
        }

        PrintWriter writer = resp.getWriter();

        if (flag) {
            JSON = ContactService.getContacts(page);
        }

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        writer.print(JSON);
    }
}