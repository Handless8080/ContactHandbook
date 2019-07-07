package dto;

import java.lang.reflect.Field;

public class AttachmentFile {
    private String id;
    private String fileName;
    private String generatedFileName;
    private String year;
    private String month;
    private String day;
    private String comment;
    private Boolean enabled;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
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

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public String getGeneratedFileName() {
        return generatedFileName;
    }

    public void setGeneratedFileName(String generatedFileName) {
        this.generatedFileName = generatedFileName;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();

        for (Field field : this.getClass().getDeclaredFields()) {
            try {
                field.setAccessible(true);
                result.append(field.getName()).append(": ").append((String) field.get(this)).append(" ");
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        return result.toString();
    }
}