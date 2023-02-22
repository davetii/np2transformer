package software.daveturner.np2transformer;

public class PaymentReceiptNotificationRequest extends NotificationRequest{

    public PaymentReceiptNotificationRequest(String newLanguageCode,
                                             String newEmail,
                                             String newCustomerIdType,
                                             String newCustomerId) {
        super( "PAYMNTRECEIPTNOTFN",
                newLanguageCode.toUpperCase() + "-" + "PAYMNTRECEIPTNOTFN" + "-EM",
                "PAYMNTRECEIPTNOTFN",
                newLanguageCode,
                "EMAIL",
                newEmail,
                newCustomerIdType,
                newCustomerId
                );
    }

}
