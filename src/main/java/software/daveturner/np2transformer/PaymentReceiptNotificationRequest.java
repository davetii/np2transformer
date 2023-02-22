package software.daveturner.np2transformer;

public class PaymentReceiptNotificationRequest extends NotificationRequest {
    public static NotificationRequest init(
            String newLanguageCode,
            String newEmail,
            String newCustomerIdType,
            String newCustomerId
    ) {
        String newEventCode = "PAYMNTERCEIPTNOTFN";
        String newTemplate = newLanguageCode.toUpperCase() + "-" + newEventCode + "-EM";
        return NotificationRequest.init(
                newEventCode,
                newTemplate,
                newEventCode,
                newLanguageCode,
                "EMAIL",
                newEmail,
                newCustomerIdType,
                newCustomerId
        );
    }


}
