package dto;

import java.lang.reflect.Field;

public class Phone {
    private String id;
    private String countryCode;
    private String operatorCode;
    private String phoneNumber;
    private String phoneType;
    private String comment;
    private Boolean enabled;

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getOperatorCode() {
        return operatorCode;
    }

    public void setOperatorCode(String operatorCode) {
        this.operatorCode = operatorCode;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPhoneType() {
        return phoneType;
    }

    public void setPhoneType(String phoneType) {
        this.phoneType = phoneType;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
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