package software.daveturner.np2transformer;

import java.util.*;

public class NotificationRequest {

    private String eventCode;
    private String templateCode;
    private String languageCode;
    private String notificationTypeCode;
    private String notificationChannel;
    private String notificationDeviceId;
    private String customerIdType;
    private String customerId;

    public String getLanguageCode() {
        return languageCode;
    }

    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }

    public String getEventCode() {
        return eventCode;
    }

    public void setEventCode(String eventCode) {
        this.eventCode = eventCode;
    }

    public String getTemplateCode() {
        return templateCode;
    }

    public void setTemplateCode(String templateCode) {
        this.templateCode = templateCode;
    }

    public String getNotificationTypeCode() {
        return notificationTypeCode;
    }

    public void setNotificationTypeCode(String notificationTypeCode) {
        this.notificationTypeCode = notificationTypeCode;
    }

    public String getNotificationChannel() {
        return notificationChannel;
    }

    public void setNotificationChannel(String notificationChannel) {
        this.notificationChannel = notificationChannel;
    }

    public String getNotificationDeviceId() {
        return notificationDeviceId;
    }

    public void setNotificationDeviceId(String notificationDeviceId) {
        this.notificationDeviceId = notificationDeviceId;
    }

    public String getCustomerIdType() {
        return customerIdType;
    }

    public void setCustomerIdType(String customerIdType) {
        this.customerIdType = customerIdType;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    private Map<String, String> keyValues = new HashMap<>();

    public void addKeyValue(Map<String, String> map) {
        keyValues.putAll(map);
    }

    public static NotificationRequest init(
            String newEventCode,
            String newTemplateCode,
            String newNotificationTypeCode,
            String newLanguageCode,
            String newNotificationChannel,
            String newNotificationDeviceId,
            String newCustomerIdType,
            String newCustomerId
    ) {
        NotificationRequest request = new NotificationRequest();
        request.setEventCode(newEventCode);
        request.setTemplateCode(newTemplateCode);
        request.setNotificationTypeCode(newNotificationTypeCode);
        request.setLanguageCode(newLanguageCode);
        request.setNotificationChannel(newNotificationChannel);
        request.setNotificationDeviceId(newNotificationDeviceId);
        request.setCustomerIdType(newCustomerIdType);
        request.setCustomerId(newCustomerId);
        return request;
    }


    public String toJson() {
        String s = "{\n";
        s += "\"eventCode\": \"" + this.eventCode + "\",\n";
        s += "\"templateId\": \"" + this.templateCode + "\",\n";
        s += "\"LANGUAGE_CODE\": \"" + this.languageCode + "\",\n";
        s += "\"notificationTypeCode\": \"" + this.notificationTypeCode + "\",\n";
        s += "\"notificationChannel\": \"" + this.notificationChannel + "\",\n";
        s += "\"notificationDeviceId\": \"" + this.notificationDeviceId + "\",\n";
        s += "\"customerIdType\": \"" + this.customerIdType + "\",\n";
        s += "\"customerId\": \"" + this.customerId + "\",\n";
        s += "\"keyvalues\": [\n";
        Set keys = keyValues.keySet();
        int counter =0;
        for( String k : keyValues.keySet()) {
            counter++;
            s += "{ \"key\": \"" + k + "\", \"value\": \"" + keyValues.get(k) + "\" }";
            if (counter < keyValues.keySet().size()) {
                s += ",\n";
            }
        }
        s += "\n]\n";
        s += "}\n";
        return s;

    }
}
