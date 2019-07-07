package dao;

import dto.AttachmentFile;
import dto.AvatarFile;
import dto.Contact;
import dto.Phone;
import org.apache.log4j.Logger;
import utils.StringUtils;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Set;

public class DataConnection {
    private static DataSource dataSource;

    private final static Logger logger = Logger.getLogger(DataConnection.class);

    static {
        try {
            InitialContext initialContext = new InitialContext();
            dataSource = (DataSource) initialContext.lookup("java:/comp/env/jdbc/contactDB");
            logger.info("Database connection was successful");
        } catch (NamingException e) {
            logger.error(e.getMessage(), e);
            e.printStackTrace();
        }
    }

    public static int getPagesCount() {
        int count = 0;

        try {
            try (Connection connection = dataSource.getConnection()) {
                try (PreparedStatement statement = connection.prepareStatement("select count(id) from contact where enabled = 1")) {
                    ResultSet resultSet = statement.executeQuery();
                    if (resultSet.next()) {
                        count = resultSet.getInt(1);
                        count = (count % 10 != 0 ? count / 10 + 1 : count / 10);
                    }

                    logger.info("The result of query when choosing the number of pages: " + count);
                }
            }
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            e.printStackTrace();
        }

        return count;
    }

    public static ArrayList<Contact> getContactList(int page) {
        ArrayList<Contact> result = new ArrayList<>();

        try {
            try (Connection connection = dataSource.getConnection()) {
                try (PreparedStatement statement = connection.prepareStatement(
                        "select id, name, surname, patronymic, born_date, " +
                                "country, town, street, house_number, flat_number, " +
                                "job from contact where enabled = 1 order by id limit ?, ?"
                )) {
                    statement.setInt(1, --page * 10);
                    statement.setInt(2, 10);

                    ResultSet resultSet = statement.executeQuery();

                    logger.info("Printing result of contacts selecting by page = " + (page + 1) + " :");
                    while (resultSet.next()) {
                        Contact contact = new Contact();

                        contact.setId(resultSet.getString(1));
                        contact.setName(resultSet.getString(2));
                        contact.setSurname(resultSet.getString(3));
                        contact.setPatronymic(resultSet.getString(4));
                        String bornDate = resultSet.getString(5);
                        if (bornDate != null) {
                            String[] bornDateSplitted = bornDate.split("-");
                            if (bornDateSplitted.length != 0) {
                                contact.setYear(bornDateSplitted[0]);
                                contact.setMonth(bornDateSplitted[1]);
                                contact.setDay(bornDateSplitted[2]);
                            }
                        }
                        contact.setCountry(resultSet.getString(6));
                        contact.setTown(resultSet.getString(7));
                        contact.setStreet((resultSet.getString(8)));
                        contact.setHouse(resultSet.getString(9));
                        contact.setFlat(resultSet.getString(10));
                        contact.setJobPlace(resultSet.getString(11));

                        logger.info(contact.toString());
                        result.add(contact);
                    }
                }
            }
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            e.printStackTrace();
        }

        return result;
    }

    public static ArrayList<Contact> getContactsByNotNullBornDate() {
        ArrayList<Contact> result = new ArrayList<>();

        try {
            try (Connection connection = dataSource.getConnection()) {
                try (PreparedStatement statement = connection.prepareStatement("select name, surname, patronymic, born_date from contact where enabled = 1 and born_date is not null")) {
                    ResultSet resultSet = statement.executeQuery();

                    logger.info("Printing result of contacts selecting by not null born date:");
                    while (resultSet.next()) {
                        Contact contact = new Contact();

                        contact.setName(resultSet.getString(1));
                        contact.setSurname(resultSet.getString(2));
                        contact.setPatronymic(resultSet.getString(3));
                        String bornDate = resultSet.getString(4);
                        if (bornDate != null) {
                            String[] bornDateSplitted = bornDate.split("-");
                            if (bornDateSplitted.length != 0) {
                                contact.setYear(bornDateSplitted[0]);
                                contact.setMonth(bornDateSplitted[1]);
                                contact.setDay(bornDateSplitted[2]);
                            }
                        }

                        logger.info(contact.toString());
                        result.add(contact);
                    }
                }
            }
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            e.printStackTrace();
        }

        return result;
    }

    public static ArrayList<Contact> getContactsByNotNullEmail() {
        ArrayList<Contact> result = new ArrayList<>();

        try {
            try (Connection connection = dataSource.getConnection()) {
                try (PreparedStatement statement = connection.prepareStatement("select id, name, surname, patronymic, email from contact where enabled = 1 and email != '' and email is not null")) {
                    ResultSet resultSet = statement.executeQuery();

                    logger.info("Printing result of contacts selecting by not null email:");
                    while (resultSet.next()) {
                        Contact contact = new Contact();

                        contact.setId(resultSet.getString(1));
                        contact.setName(resultSet.getString(2));
                        contact.setSurname(resultSet.getString(3));
                        contact.setPatronymic(resultSet.getString(4));
                        contact.setEmail(resultSet.getString(5));

                        logger.info(contact.toString());
                        result.add(contact);
                    }
                }
            }
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            e.printStackTrace();
        }

        return result;
    }

    public static ArrayList<Contact> getContactsByFilter(Contact filter, Set<String> genders, Set<String> familyStatuses, String dateType, int page) {
        ArrayList<Contact> result = new ArrayList<>();

        StringBuilder query = new StringBuilder("select id, name, surname, patronymic, born_date, " +
                "country, town, street, house_number, flat_number, " +
                "job from contact where enabled = 1 ");

        likeQuery(query, "name", filter.getName());
        likeQuery(query, "surname", filter.getSurname());
        likeQuery(query, "patronymic", filter.getPatronymic());
        likeQuery(query, "citizenship", filter.getCitizenship());
        likeQuery(query, "country", filter.getCountry());
        likeQuery(query, "street", filter.getStreet());
        likeQuery(query, "town", filter.getTown());

        equalEnumQuery(query, "gender", genders);
        equalEnumQuery(query, "martial_status", familyStatuses);

        equalQuery(query, "house_number", filter.getHouse(), true);
        equalQuery(query, "flat_number", filter.getFlat(), false);
        equalQuery(query, "zip_code", filter.getIndex(), false);

        boolean isLesser = false;
        if (dateType.equals("1")) {
            isLesser = true;
        }

        if (!StringUtils.validDate(filter.getDateInFormat())) {
            compareQuery(query, "born_date", filter.getDateInFormat(), isLesser);
        }

        query.append(" order by id limit ?, ?");

        logger.info("Final query by filters: " + query.toString());

        try {
            try (Connection connection = dataSource.getConnection()) {
                try (PreparedStatement statement = connection.prepareStatement(query.toString())) {
                    statement.setInt(1, --page * 10);
                    statement.setInt(2, 10);

                    ResultSet resultSet = statement.executeQuery();

                    logger.info("Printing result of contacts selecting by filters on page = " + page + " :");
                    while (resultSet.next()) {
                        Contact contact = new Contact();

                        contact.setId(resultSet.getString(1));
                        contact.setName(resultSet.getString(2));
                        contact.setSurname(resultSet.getString(3));
                        contact.setPatronymic(resultSet.getString(4));
                        String bornDate = resultSet.getString(5);
                        if (bornDate != null) {
                            String[] bornDateSplitted = bornDate.split("-");
                            if (bornDateSplitted.length != 0) {
                                contact.setYear(bornDateSplitted[0]);
                                contact.setMonth(bornDateSplitted[1]);
                                contact.setDay(bornDateSplitted[2]);
                            }
                        }
                        contact.setCountry(resultSet.getString(6));
                        contact.setTown(resultSet.getString(7));
                        contact.setStreet((resultSet.getString(8)));
                        contact.setHouse(resultSet.getString(9));
                        contact.setFlat(resultSet.getString(10));
                        contact.setJobPlace(resultSet.getString(11));

                        logger.info(contact.toString());
                        result.add(contact);
                    }
                }
            }
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            e.printStackTrace();
        }

        return result;
    }

    public static int getPagesCountByFilter(Contact filter, Set<String> genders, Set<String> familyStatuses, String dateType) {
        int count = 0;

        StringBuilder query = new StringBuilder("select count(id) from contact where enabled = 1 ");

        likeQuery(query, "name", filter.getName());
        likeQuery(query, "surname", filter.getSurname());
        likeQuery(query, "patronymic", filter.getPatronymic());
        likeQuery(query, "citizenship", filter.getCitizenship());
        likeQuery(query, "country", filter.getCountry());
        likeQuery(query, "street", filter.getStreet());
        likeQuery(query, "town", filter.getTown());

        equalEnumQuery(query, "gender", genders);
        equalEnumQuery(query, "martial_status", familyStatuses);

        equalQuery(query, "house_number", filter.getHouse(), true);
        equalQuery(query, "flat_number", filter.getFlat(), false);
        equalQuery(query, "zip_code", filter.getIndex(), false);

        boolean isLesser = false;
        if (dateType.equals("1")) {
            isLesser = true;
        }

        if (!StringUtils.validDate(filter.getDateInFormat())) {
            compareQuery(query, "born_date", filter.getDateInFormat(), isLesser);
        }

        logger.info("Final query of pages count selecting by filters: " + query.toString());

        try {
            try (Connection connection = dataSource.getConnection()) {
                try (PreparedStatement statement = connection.prepareStatement(query.toString())) {
                    ResultSet resultSet = statement.executeQuery();
                    if (resultSet.next()) {
                        count = resultSet.getInt(1);
                        count = (count % 10 != 0 ? count / 10 + 1 : count / 10);
                    }

                    logger.info("The result of query when choosing the number of pages by filter: " + count);
                }
            }
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            e.printStackTrace();
        }

        return count;
    }

    public static Contact getContactById(String id) {
        Contact contact = new Contact();

        try {
            try (Connection connection = dataSource.getConnection()) {
                try (PreparedStatement statement = connection.prepareStatement("select * from contact where id = ? and enabled = 1")) {
                    statement.setString(1, id);

                    ResultSet resultSet = statement.executeQuery();
                    if (resultSet.next()) {
                        contact.setName(resultSet.getString(2));
                        contact.setSurname(resultSet.getString(3));
                        contact.setPatronymic(resultSet.getString(4));
                        String bornDate = resultSet.getString(5);
                        if (bornDate != null) {
                            String[] bornDateSplitted = bornDate.split("-");
                            if (bornDateSplitted.length != 0) {
                                contact.setYear(bornDateSplitted[0]);
                                contact.setMonth(bornDateSplitted[1]);
                                contact.setDay(bornDateSplitted[2]);
                            }
                        }
                        contact.setGender(resultSet.getString(6));
                        contact.setCitizenship(resultSet.getString(7));
                        contact.setFamilyStatus(resultSet.getString(8));
                        contact.setSite(resultSet.getString(9));
                        contact.setEmail(resultSet.getString(10));
                        contact.setJobPlace(resultSet.getString(11));
                        contact.setCountry(resultSet.getString(12));
                        contact.setTown(resultSet.getString(13));
                        contact.setStreet(resultSet.getString(14));
                        contact.setHouse(resultSet.getString(15));
                        contact.setFlat(resultSet.getString(16));
                        contact.setIndex(resultSet.getString(17));

                        AvatarFile avatar = new AvatarFile();
                        avatar.setFileName(resultSet.getString(18));
                        contact.setAvatarFile(avatar);
                    } else {
                        return null;
                    }

                    logger.info("Selected contact by id: " + contact.toString());
                }
            }
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            e.printStackTrace();
        }

        return contact;
    }

    public static void disableContact(String id) {
        try {
            try (Connection connection = dataSource.getConnection()) {
                try (PreparedStatement statement = connection.prepareStatement("update contact set enabled = 0 where id = ? and enabled = 1")) {
                    statement.setString(1, id);

                    statement.executeUpdate();

                    logger.info("Contact with id = " + id + " has been deleted");
                }
            }
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            e.printStackTrace();
        }
    }

    public static void saveContact(Contact contact) {
        try {
            try (Connection connection = dataSource.getConnection()) {
                try (PreparedStatement statement = connection.prepareStatement(
                        "update contact set " +
                                "name = ?, surname = ?, patronymic = ?, born_date = ?, gender = ?, martial_status = ?," +
                                "citizenship = ?, country = ?, town = ?, street = ?, house_number = ?," +
                                "flat_number = ?, zip_code = ?, web_site = ?, email = ?, job = ?, avatar_filename = ? " +
                                "where id = ? and enabled = 1"
                )) {
                    statement.setString(1, contact.getName());
                    statement.setString(2, contact.getSurname());
                    statement.setString(3, contact.getPatronymic());
                    if (!StringUtils.isEmpty(contact.getYear()) && !StringUtils.isEmpty(contact.getMonth()) && !StringUtils.isEmpty(contact.getDay())) {
                        statement.setString(4, contact.getYear() + "-" + contact.getMonth() + "-" + contact.getDay() + "-");
                    } else {
                        statement.setString(4, null);
                    }
                    statement.setString(5, contact.getGender());
                    statement.setString(6, contact.getFamilyStatus());
                    statement.setString(7, contact.getCitizenship());
                    statement.setString(8, contact.getCountry());
                    statement.setString(9, contact.getTown());
                    statement.setString(10, contact.getStreet());
                    statement.setString(11, contact.getHouse());
                    statement.setString(12, contact.getFlat());
                    statement.setString(13, contact.getIndex());
                    statement.setString(14, contact.getSite());
                    statement.setString(15, contact.getEmail());
                    statement.setString(16, contact.getJobPlace());
                    statement.setString(17, contact.getAvatarFile().getFileName());
                    statement.setString(18, contact.getId());

                    statement.executeUpdate();

                    logger.info("Contact with id = " + contact.getId() + " has been saved");
                }
            }
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            e.printStackTrace();
        }
    }

    public static void addContact(Contact contact) {
        try {
            try (Connection connection = dataSource.getConnection()) {
                try (PreparedStatement statement = connection.prepareStatement(
                        "insert into contact (name, surname, patronymic, born_date, gender, martial_status, citizenship, country, town, street, house_number, flat_number, zip_code, web_site, email, job, avatar_filename)" +
                                "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"
                )) {
                    statement.setString(1, contact.getName());
                    statement.setString(2, contact.getSurname());
                    statement.setString(3, contact.getPatronymic());
                    if (!StringUtils.isEmpty(contact.getYear())) {
                        statement.setString(4, contact.getYear() + "-" + contact.getMonth() + "-" + contact.getDay() + "-");
                    } else {
                        statement.setString(4, null);
                    }
                    statement.setString(5, contact.getGender());
                    statement.setString(6, contact.getFamilyStatus());
                    statement.setString(7, contact.getCitizenship());
                    statement.setString(8, contact.getCountry());
                    statement.setString(9, contact.getTown());
                    statement.setString(10, contact.getStreet());
                    statement.setString(11, contact.getHouse());
                    statement.setString(12, contact.getFlat());
                    statement.setString(13, contact.getIndex());
                    statement.setString(14, contact.getSite());
                    statement.setString(15, contact.getEmail());
                    statement.setString(16, contact.getJobPlace());
                    statement.setString(17, contact.getAvatarFile().getFileName());

                    statement.executeUpdate();

                    logger.info("New contact has been successfully added");
                }
            }
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            e.printStackTrace();
        }
    }

    public static void addPhone(Phone phone, String contactId) {
        try {
            try (Connection connection = dataSource.getConnection()) {
                try (PreparedStatement statement = connection.prepareStatement(
                        "insert into phone (country_code, operator_code, number, phone_type, comment, contact_id)" +
                                "values (?, ?, ?, ?, ?, ?)"
                )) {
                    statement.setString(1, phone.getCountryCode());
                    statement.setString(2, phone.getOperatorCode());
                    statement.setString(3, phone.getPhoneNumber());
                    statement.setString(4, phone.getPhoneType());
                    statement.setString(5, phone.getComment());
                    statement.setString(6, contactId);

                    statement.executeUpdate();

                    logger.info("New phone has been successfully added for contact with id = " + contactId);
                }
            }
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            e.printStackTrace();
        }
    }

    public static void savePhone(Phone phone) {
        try {
            try (Connection connection = dataSource.getConnection()) {
                try (PreparedStatement statement = connection.prepareStatement(
                        "update phone set country_code = ?, operator_code = ?, number = ?, phone_type = ?, comment = ? where id = ?"
                )) {
                    statement.setString(1, phone.getCountryCode());
                    statement.setString(2, phone.getOperatorCode());
                    statement.setString(3, phone.getPhoneNumber());
                    statement.setString(4, phone.getPhoneType());
                    statement.setString(5, phone.getComment());
                    statement.setString(6, phone.getId());

                    statement.executeUpdate();

                    logger.info("Phone with id = " + phone.getId() + " has been saved");
                }
            }
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            e.printStackTrace();
        }
    }

    public static void disablePhone(String id) {
        try {
            try (Connection connection = dataSource.getConnection()) {
                try (PreparedStatement statement = connection.prepareStatement(
                        "update phone set enabled = 0 where id = ?"
                )) {
                    statement.setString(1, id);

                    statement.executeUpdate();

                    logger.info("Phone with id = " + id + " has been deleted");
                }
            }
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            e.printStackTrace();
        }
    }

    public static void addAttachment(AttachmentFile file, String contactId) {
        try {
            try (Connection connection = dataSource.getConnection()) {
                try (PreparedStatement statement = connection.prepareStatement(
                        "insert into attachment (file_name, original_file_name, comment, contact_id)" +
                                "values (?, ?, ?, ?)"
                )) {
                    statement.setString(1, file.getGeneratedFileName());
                    statement.setString(2, file.getFileName());
                    statement.setString(3, file.getComment());
                    statement.setString(4, contactId);

                    statement.executeUpdate();

                    logger.info("New attachment has been successfully added for contact with id = " + contactId);
                }
            }
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            e.printStackTrace();
        }
    }

    public static void saveAttachment(AttachmentFile file) {
        try {
            try (Connection connection = dataSource.getConnection()) {
                try (PreparedStatement statement = connection.prepareStatement(
                        "update attachment set original_file_name = ?, up_date = ?, comment = ? where id = ? and enabled = 1"
                )) {
                    statement.setString(1, file.getFileName());
                    if (!StringUtils.isEmpty(file.getYear()) && !StringUtils.isEmpty(file.getMonth()) && !StringUtils.isEmpty(file.getDay())) {
                        statement.setString(2, file.getYear() + '-' + file.getMonth() + '-' + file.getDay() + " 00:00:00");
                    } else {
                        statement.setString(2, null);
                    }
                    statement.setString(3, file.getComment());
                    statement.setString(4, file.getId());

                    statement.executeUpdate();

                    logger.info("Attachment with id = " + file.getId() + " has been saved");
                }
            }
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            e.printStackTrace();
        }
    }

    public static void disableAttachment(String id) {
        try {
            try (Connection connection = dataSource.getConnection()) {
                try (PreparedStatement statement = connection.prepareStatement(
                        "update attachment set enabled = 0 where id = ?"
                )) {
                    statement.setString(1, id);

                    statement.executeUpdate();

                    logger.info("Attachment with id = " + id + " has been deleted");
                }
            }
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            e.printStackTrace();
        }
    }

    public static boolean existContact(String id) {
        try {
            try (Connection connection = dataSource.getConnection()) {
                try (PreparedStatement statement = connection.prepareStatement(
                        "select * from contact where id = ? and enabled = 1"
                )) {
                    statement.setString(1, id);

                    ResultSet resultSet = statement.executeQuery();
                    if (resultSet.next()) {
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            e.printStackTrace();
        }

        return false;
    }

    public static ArrayList<Phone> getContactPhones(String contactId) {
        ArrayList<Phone> phones = new ArrayList<>();

        try {
            try (Connection connection = dataSource.getConnection()) {
                try (PreparedStatement statement = connection.prepareStatement(
                        "select * from phone where contact_id = ? and enabled = 1"
                )) {
                    statement.setString(1, contactId);

                    ResultSet resultSet = statement.executeQuery();

                    logger.info("Printing result of contact phones selecting:");
                    while (resultSet.next()) {
                        Phone phone = new Phone();
                        phone.setId(resultSet.getString(1));
                        phone.setCountryCode(resultSet.getString(2));
                        phone.setOperatorCode(resultSet.getString(3));
                        phone.setPhoneNumber(resultSet.getString(4));
                        phone.setPhoneType(resultSet.getString(5));
                        phone.setComment(resultSet.getString(6));

                        logger.info(phone.toString());
                        phones.add(phone);
                    }
                }
            }
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            e.printStackTrace();
        }

        return phones;
    }

    public static ArrayList<AttachmentFile> getContactAttachments(String contactId) {
        ArrayList<AttachmentFile> files = new ArrayList<>();

        try {
            try (Connection connection = dataSource.getConnection()) {
                try (PreparedStatement statement = connection.prepareStatement(
                        "select id, file_name, original_file_name, upload_date, comment from attachment where contact_id = ? and enabled = 1"
                )) {
                    statement.setString(1, contactId);

                    ResultSet resultSet = statement.executeQuery();

                    logger.info("Printing result of contact attachments selecting:");
                    while (resultSet.next()) {
                        AttachmentFile file = new AttachmentFile();
                        file.setId(resultSet.getString(1));
                        file.setGeneratedFileName(resultSet.getString(2));
                        file.setFileName(resultSet.getString(3));
                        String uploadDate = resultSet.getString(4);
                        if (!StringUtils.isEmpty(uploadDate)) {
                            String[] uploadDateSplitted = uploadDate.split("-");
                            if (uploadDateSplitted.length != 0) {
                                file.setYear(uploadDateSplitted[0]);
                                file.setMonth(uploadDateSplitted[1]);
                                file.setDay(uploadDateSplitted[2]);
                            }
                        }
                        file.setComment(resultSet.getString(5));

                        logger.info(file.toString());
                        files.add(file);
                    }
                }
            }
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            e.printStackTrace();
        }

        return files;
    }

    public static String getAttachmentFileNameById(String id) {
        String fileName = null;

        try {
            try (Connection connection = dataSource.getConnection()) {
                try (PreparedStatement statement = connection.prepareStatement(
                        "select file_name from attachment where id = ? and enabled = 1"
                )) {
                    statement.setString(1, id);

                    ResultSet resultSet = statement.executeQuery();
                    if (resultSet.next()) {
                        fileName = resultSet.getString(1);
                    }

                    logger.info("Selected attachment filename by id = " + id + " is: " + fileName);
                }
            }
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            e.printStackTrace();
        }

        return fileName;
    }

    public static String getOriginalAttachmentFileNameById(String id) {
        String fileName = null;

        try {
            try (Connection connection = dataSource.getConnection()) {
                try (PreparedStatement statement = connection.prepareStatement(
                        "select original_file_name from attachment where id = ? and enabled = 1"
                )) {
                    statement.setString(1, id);

                    ResultSet resultSet = statement.executeQuery();
                    if (resultSet.next()) {
                        fileName = resultSet.getString(1);
                    }

                    logger.info("Selected attachment original filename by id = " + id + " is: " + fileName);
                }
            }
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            e.printStackTrace();
        }

        return fileName;
    }

    public static String getLastContact() {
        String result = null;

        try {
            try (Connection connection = dataSource.getConnection()) {
                try (PreparedStatement statement = connection.prepareStatement(
                        "select id from contact order by id desc limit 1"
                )) {
                    ResultSet resultSet = statement.executeQuery();

                    if (resultSet.next()) {
                        result = resultSet.getString(1);
                    }

                    logger.info("Selected last contact id: " + result);
                }
            }
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            e.printStackTrace();
        }

        return result;
    }

    private static void likeQuery(StringBuilder query, String field, String fieldValue) {
        if (!StringUtils.isEmpty(fieldValue)) {
            query.append(" and ")
                    .append(field)
                    .append(" like '%")
                    .append(fieldValue)
                    .append("%'");
        }
    }

    private static void equalQuery(StringBuilder query, String field, String fieldValue, boolean isString) {
        if (!StringUtils.isEmpty(fieldValue)) {
            query.append(" and ")
                    .append(field)
                    .append(" = ");
            if (isString) {
                query.append('\'')
                        .append(fieldValue)
                        .append('\'');
            } else {
                query.append(fieldValue);
            }
        }
    }

    private static void equalEnumQuery(StringBuilder query, String field, Set<String> fieldValues) {
        boolean notEmpty = false;
        boolean notFirst = false;

        if (fieldValues.size() != 0) {
            query.append(" and (");
            notEmpty = true;
        }

        for (String value : fieldValues) {
            if (value != null) {
                if (notFirst) {
                    query.append(" or ");
                }

                notFirst = true;

                query.append(field)
                        .append(" = ")
                        .append(value);
            }
        }

        if (notEmpty) {
            query.append(")");
        }
    }

    private static void compareQuery(StringBuilder query, String field, String fieldValue, boolean lesser) {
        char operator = '>';
        if (lesser) {
            operator = '<';
        }

        query.append(" and ")
                .append(field)
                .append(" ")
                .append(operator)
                .append('\'')
                .append(fieldValue)
                .append('\'');
    }
}