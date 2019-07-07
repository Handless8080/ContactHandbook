package dto;

import java.lang.reflect.Field;
import java.util.List;

public class Contact {
    private String id;
    private String name;
    private String surname;
    private String patronymic;
    private String year;
    private String month;
    private String day;
    private String gender;
    private String citizenship;
    private String familyStatus;
    private String site;
    private String email;
    private String jobPlace;
    private String country;
    private String town;
    private String street;
    private String house;
    private String flat;
    private String index;

    private List<Phone> phones;
    private List<AttachmentFile> attachmentFiles;

    private AvatarFile avatarFile;

    public AvatarFile getAvatarFile() {
        return avatarFile;
    }

    public void setAvatarFile(AvatarFile avatarFile) {
        this.avatarFile = avatarFile;
    }

    public List<AttachmentFile> getAttachmentFiles() {
        return attachmentFiles;
    }

    public void setAttachmentFiles(List<AttachmentFile> attachmentFiles) {
        this.attachmentFiles = attachmentFiles;
    }

    public List<Phone> getPhones() {
        return phones;
    }

    public void setPhones(List<Phone> phones) {
        this.phones = phones;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getPatronymic() {
        return patronymic;
    }

    public void setPatronymic(String patronymic) {
        this.patronymic = patronymic;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getFamilyStatus() {
        return familyStatus;
    }

    public void setFamilyStatus(String familyStatus) {
        this.familyStatus = familyStatus;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getJobPlace() {
        return jobPlace;
    }

    public void setJobPlace(String jobPlace) {
        this.jobPlace = jobPlace;
    }

    public String getCitizenship() {
        return citizenship;
    }

    public void setCitizenship(String citizenship) {
        this.citizenship = citizenship;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getTown() {
        return town;
    }

    public void setTown(String town) {
        this.town = town;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getHouse() {
        return house;
    }

    public void setHouse(String house) {
        this.house = house;
    }

    public String getFlat() {
        return flat;
    }

    public void setFlat(String flat) {
        this.flat = flat;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getDateInFormat() {
        return getYear() + '-' + getMonth() + '-' + getDay();
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();

        for (Field field : this.getClass().getDeclaredFields()) {
            if (!field.getName().equals("phones") && !field.getName().equals("attachmentFiles") && !field.getName().equals("avatarFile")) {
                try {
                    field.setAccessible(true);
                    result.append(field.getName())
                            .append(": ")
                            .append((String) field.get(this))
                            .append("; ");
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }

        return result.toString();
    }
}