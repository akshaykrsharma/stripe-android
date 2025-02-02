package com.stripe.samplestore.service;

import java.util.Map;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * The {@link retrofit2.Retrofit} interface that creates our API service.
 */
public interface StripeService {

    /**
     * Returns the PaymentIntent client secret in the format shown below.
     *
     * {"secret": "pi_1Eu5SqCRMb_secret_O2Avhk5V0Pjeo"}
     */
    @POST("capture_payment")
    Observable<ResponseBody> capturePayment(@Body Map<String, Object> params);

    /**
     * Used for Payment Intent Manual confirmation
     *
     * @see <a
     * href="https://stripe.com/docs/payments/payment-intents/quickstart#manual-confirmation-flow">
     * Manual Confirmation Flow</a>
     *
     * Returns the PaymentIntent client secret in the format shown below.
     *
     * {"secret": "pi_1Eu5SqCRMb_secret_O2Avhk5V0Pjeo"}
     */
    @POST("confirm_payment")
    Observable<ResponseBody> confirmPayment(@Body Map<String, Object> params);

    @FormUrlEncoded
    @POST("ephemeral_keys")
    Observable<ResponseBody> createEphemeralKey(@FieldMap Map<String, String> apiVersionMap);
}
