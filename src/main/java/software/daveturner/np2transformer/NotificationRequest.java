package software.daveturner.np2transformer;

import java.util.*;

public class NotificationRequest {

    private final String eventCode;
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
    public String getEventCode() {
        return eventCode;
    }

    public String getTemplateCode() {
        return templateCode;
    }

    public String getNotificationTypeCode() {
        return notificationTypeCode;
    }

    public String getNotificationChannel() {
        return notificationChannel;
    }

    public String getNotificationDeviceId() {
        return notificationDeviceId;
    }
    public String getCustomerIdType() {
        return customerIdType;
    }
    public String getCustomerId() {
        return customerId;
    }
    private Map<String, String> keyValues = new HashMap<>();

    public void addKeyValue(Map<String, String> map) {
        keyValues.putAll(map);
    }

    public NotificationRequest(
            String newEventCode,
            String newTemplateCode,
            String newNotificationTypeCode,
            String newLanguageCode,
            String newNotificationChannel,
            String newNotificationDeviceId,
            String newCustomerIdType,
            String newCustomerId
    ) {
        this.eventCode = newEventCode;
        this.templateCode = newTemplateCode;
        this.notificationTypeCode = newNotificationTypeCode;
        this.languageCode = newLanguageCode;
        this.notificationChannel = newNotificationChannel;
        this.notificationDeviceId = newNotificationDeviceId;
        this.customerIdType = newCustomerIdType;
        this.customerId = newCustomerId;
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
        s += "\n]}";
        return s;

    }
}
