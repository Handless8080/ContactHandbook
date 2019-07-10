package services;

import dao.DataConnection;
import dto.*;
import org.apache.log4j.Logger;
import utils.JSONSerializer;
import utils.StringUtils;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.List;
import java.util.Set;

public class ContactService {
    private final static Logger logger = Logger.getLogger(ContactService.class);

    public static String uploadAvatarDir;
    public static String uploadAttachmentsDir;

    public static String getContacts(String pageNumber) throws IOException {
        int p;
        try {
            p = Integer.parseInt(pageNumber);
            if (p < 1) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            logger.warn("Page number parse has been handled (method: ContactService.getContacts); page = " + pageNumber);

            return null;
        }

        Page page = new Page();
        page.setPagesCount(DataConnection.getPagesCount());
        page.setContacts(DataConnection.getContactList(p));

        return JSONSerializer.toJSON(page);
    }

    public static String getContactsWithNotNullEmail() throws IOException {
        return JSONSerializer.toJSON(DataConnection.getContactsByNotNullEmail());
    }

    public static String getContactsByFilter(Contact filter, Set<String> genders, Set<String> familyStatuses, String dateType, String pageNumber) throws IOException {
        int p;
        try {
            p = Integer.parseInt(pageNumber);
            if (!StringUtils.isEmpty(filter.getFlat())) {
                Integer.parseInt(filter.getFlat());
            }
            if (!StringUtils.isEmpty(filter.getIndex())) {
                Integer.parseInt(filter.getIndex());
            }
            if (p < 1) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            logger.warn("Numeric values parse has been handled (method: ContactService.getContactsByFilter); page = " + pageNumber + " flat = " + filter.getFlat() + " index = " + filter.getIndex());

            return null;
        }

        if (!(StringUtils.isEmpty(filter.getYear()) && StringUtils.isEmpty(filter.getMonth()) && StringUtils.isEmpty(filter.getDay()))) {
            if (StringUtils.validDate(filter.getYear() + "-" + filter.getMonth() + "-" + filter.getDay())) {
                logger.warn("Filters date parse has been handled; date = " + filter.getYear() + '-' + filter.getMonth() + '-' + filter.getDay());

                return null;
            }
        }

        Page page = new Page();
        page.setPagesCount(DataConnection.getPagesCountByFilter(filter, genders, familyStatuses, dateType));
        page.setContacts(DataConnection.getContactsByFilter(filter, genders, familyStatuses, dateType, p));

        return JSONSerializer.toJSON(page);
    }

    public static String getContactAvatar(String id) {
        try {
            Integer.parseInt(id);
        } catch (NumberFormatException e) {
            logger.warn("Contacts id parse has been handled (method: ContactService.getContactAvatar); id = " + id);

            return null;
        }

        Contact contact = DataConnection.getContactById(id);

        return contact != null ? contact.getAvatarFile().getFileName() : null;
    }

    public static String getContactById(String id) throws IOException {
        try {
            Integer.parseInt(id);
        } catch (NumberFormatException e) {
            logger.warn("Contacts id parse has been handled (method: ContactService.getContactById); id = " + id);

            return null;
        }

        Contact contact = DataConnection.getContactById(id);

        if (contact != null) {
            logger.info("Check contact avatar");

            String fileName = "default_avatar.png";
            if (!StringUtils.isEmpty(contact.getAvatarFile().getFileName())) {
                fileName = contact.getAvatarFile().getFileName();
            }
            logger.info("Avatar filename = " + fileName);

            File file = new File(uploadAvatarDir + fileName);
            DataInputStream inputStream;
            try {
                inputStream = new DataInputStream(new FileInputStream(file));
                logger.info("Avatar file was found and read");
            } catch (FileNotFoundException e) {
                fileName = "default_avatar.png";
                file = new File(uploadAvatarDir + fileName);
                inputStream = new DataInputStream(new FileInputStream(file));
                logger.info("Avatar file not found. Read default_avatar.png");
            }

            byte[] bytes = new byte[(int) file.length()];

            inputStream.readFully(bytes);

            String encodedFile = new String(Base64.getEncoder().encode(bytes));
            AvatarFile avatar = new AvatarFile();
            avatar.setBytes(encodedFile);

            contact.setPhones(DataConnection.getContactPhones(id));
            contact.setAttachmentFiles(DataConnection.getContactAttachments(id));
            contact.setAvatarFile(avatar);

            inputStream.close();
            return JSONSerializer.contactToJSON(contact);
        }

        return null;
    }

    public static String getContactsByBornDate() {
        List<Contact> contacts = DataConnection.getContactsByNotNullBornDate();
        LocalDate currentDate = LocalDate.now();

        String strCurrentDate = DateTimeFormatter.ofPattern("yyyy-MM-dd").format(currentDate).substring(5);

        StringBuilder result = new StringBuilder();
        for (Contact contact : contacts) {
            String bornDate = contact.getMonth() + '-' + contact.getDay();

            if (strCurrentDate.equals(bornDate)) {
                result.append(contact.getSurname()).append(" ")
                        .append(contact.getName()).append(" ")
                        .append(contact.getPatronymic()).append("\n");
            }
        }

        return result.toString();
    }

    public static void disableContact(String[] ids) {
        if (ids == null || ids.length == 0) {
            return;
        }

        for (String id : ids) {
            try {
                Integer.parseInt(id);
            } catch (NumberFormatException e) {
                logger.warn("Contacts id parse has been handled (method: ContactService.disableContact); id = " + id);

                continue;
            }

            DataConnection.disableContact(id);
        }
    }

    public static void saveContact(Contact contact, List<Phone> phones, List<AttachmentFile> files, int[] fileStates) {
        try {
            Integer.parseInt(contact.getId());
        } catch (NumberFormatException e) {
            logger.warn("Contacts id parse has been handled (method: ContactService.disableContact); id = " + contact.getId());

            return;
        }

        if (validContact(contact)) {
            return;
        }

        if (!DataConnection.existContact(contact.getId())) {
            return;
        }

        if (StringUtils.isEmpty(contact.getFlat())) {
            contact.setFlat(null);
        }

        if (StringUtils.isEmpty(contact.getIndex())) {
            contact.setIndex(null);
        }

        if (contact.getAvatarFile() == null) {
            contact.setAvatarFile(new AvatarFile());
        }
        DataConnection.saveContact(contact);

        for (Phone phone : phones) {
            if (validPhone(phone)) {
                if (StringUtils.isEmpty(phone.getCountryCode())) {
                    phone.setCountryCode(null);
                }
                if (StringUtils.isEmpty(phone.getOperatorCode())) {
                    phone.setOperatorCode(null);
                }

                if (phone.getEnabled() == null) {
                    DataConnection.savePhone(phone);
                } else if (phone.getEnabled()) {
                    DataConnection.addPhone(phone, contact.getId());
                } else {
                    DataConnection.disablePhone(phone.getId());
                }
            }
        }

        int i = 0;
        for (AttachmentFile file : files) {
            if (fileStates[i] == 0 || fileStates[i] == 2) {
                DataConnection.saveAttachment(file);
            } else if (fileStates[i] == 1) {
                DataConnection.addAttachment(file, contact.getId());
            } else {
                DataConnection.disableAttachment(file.getId());
            }
            i++;
        }
    }

    public static void addContact(Contact contact) {
        if (validContact(contact)) {
            return;
        }

        if (StringUtils.isEmpty(contact.getFlat())) {
            contact.setFlat(null);
        }

        if (StringUtils.isEmpty(contact.getIndex())) {
            contact.setIndex(null);
        }

        DataConnection.addContact(contact);
    }

    public static void addPhones(List<Phone> phones) {
        String contactId = DataConnection.getLastContact();

        for (Phone phone : phones) {
            if (validPhone(phone)) {
                if (StringUtils.isEmpty(phone.getCountryCode())) {
                    phone.setCountryCode(null);
                }
                if (StringUtils.isEmpty(phone.getOperatorCode())) {
                    phone.setOperatorCode(null);
                }

                DataConnection.addPhone(phone, contactId);
            }
        }
    }

    public static void addAttachments(List<AttachmentFile> files) {
        String contactId = DataConnection.getLastContact();

        for (AttachmentFile file : files) {
            if (!StringUtils.isEmpty(file.getFileName())) {
                DataConnection.addAttachment(file, contactId);
            }
        }
    }

    public static String getAttachmentFileNameById(String id) {
        return DataConnection.getAttachmentFileNameById(id);
    }

    public static String getOriginalAttachmentFileNameById(String id) {
        return DataConnection.getOriginalAttachmentFileNameById(id);
    }

    private static boolean validContact(Contact contact) {
        if (StringUtils.isEmpty(contact.getName()) || StringUtils.isEmpty(contact.getSurname())) {
            logger.warn("Contacts name or surname was empty. Contacts name = " + contact.getName() + "; surname = " + contact.getSurname());
            return true;
        }

        if (contact.getName().length() > 20 || contact.getSurname().length() > 20 || contact.getPatronymic().length() > 20 ||
                contact.getCitizenship().length() > 30 || contact.getSite().length() > 50 || contact.getEmail().length() > 50 ||
                contact.getJobPlace().length() > 50 || contact.getCountry().length() > 30 || contact.getTown().length() > 30 ||
                contact.getStreet().length() > 30 || contact.getHouse().length() > 5) {
            logger.warn("Contacts fields length too long. Contact = " + contact.toString());
            return true;
        }

        if (StringUtils.validEmail(contact.getEmail())) {
            logger.warn("Contacts email was incorrect. Contacts email = " + contact.getEmail());
            return true;
        }

        int genderId, familyStatusId, flat = 1, index = 1;
        try {
            genderId = Integer.parseInt(contact.getGender());
            familyStatusId = Integer.parseInt(contact.getFamilyStatus());

            if (!StringUtils.isEmpty(contact.getFlat())) {
                flat = Integer.parseInt(contact.getFlat());
            }
            if (!StringUtils.isEmpty(contact.getIndex())) {
                index = Integer.parseInt(contact.getIndex());
            }
        } catch (NumberFormatException e) {
            logger.warn("Numeric values has been handled (method: ContactService.validContact); genderId = " + contact.getGender() +
                " familyStatus = " + contact.getFamilyStatus() +
                " flat = " + contact.getFlat() +
                " index = " + contact.getIndex());

            return true;
        }

        if (flat < 1) {
            logger.warn("Contacts flat was incorrect. Contacts flat = " + flat);
            return true;
        }

        if (index < 1 || index > 999999) {
            logger.warn("Contacts index was incorrect. Contacts index = " + index);
            return true;
        }

        if (genderId > 2 || familyStatusId > 4 || genderId < 1 || familyStatusId < 1) {
            logger.warn("Contacts gender or family status was incorrect. Contacts gender = " + genderId + "; family status = " + familyStatusId);
            return true;
        }

        if (!(StringUtils.isEmpty(contact.getYear()) && StringUtils.isEmpty(contact.getMonth()) && StringUtils.isEmpty(contact.getDay()))) {
            if (StringUtils.validDate(contact.getYear() + "-" + contact.getMonth() + "-" + contact.getDay())) {
                logger.warn("Contacts born date was incorrect. Year = " + contact.getYear() + "; month = " + contact.getMonth() + "; day = " + contact.getDay());
                return true;
            }
        }

        return !StringUtils.isEmpty(contact.getHouse()) && !StringUtils.validHouseNumber(contact.getHouse());

    }

    private static boolean validPhone(Phone phone) {
        int phoneType;
        try {
            Integer.parseInt(phone.getPhoneNumber());
            if (!StringUtils.isEmpty(phone.getOperatorCode())) {
                Integer.parseInt(phone.getOperatorCode());
            }
            if (!StringUtils.isEmpty(phone.getCountryCode())) {
                Integer.parseInt(phone.getCountryCode());
            }
            phoneType = Integer.parseInt(phone.getPhoneType());
        } catch (NumberFormatException e) {
            logger.warn("Numeric values has been handled (method: ContactService.validPhone); operatorCode = " + phone.getOperatorCode() +
                    " countryCode = " + phone.getCountryCode() +
                    " phoneType = " + phone.getPhoneType());

            return false;
        }

        return phoneType <= 2 && phoneType >= 1;
    }
}