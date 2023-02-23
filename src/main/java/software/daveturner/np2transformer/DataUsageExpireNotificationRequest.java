package software.daveturner.np2transformer;

public class DataUsageExpireNotificationRequest  extends NotificationRequest {
    public DataUsageExpireNotificationRequest
            (String newLanguageCode, String newEmail,String newCustomerIdType,String newCustomerId) {
        super( "PLAN_EXPIRY",
                "DU-" + newLanguageCode.toUpperCase() + "-" + "DATA_USAGE_NOTFN" + "-EM",
                "DATA_USAGE_NOTFN",
                newLanguageCode,
                "EMAIL",
                newEmail,
                newCustomerIdType,
                newCustomerId
        );
    }
}
