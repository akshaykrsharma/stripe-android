## Migration Guides

### Migration from versions < 10.0.0
- The signature of `Stripe#retrievePaymentIntentSynchronous()` has [changed](https://github.com/stripe/stripe-android/commit/5e56663c739ec694a6a393e96b651bd3d3c7a3e7#diff-26062503dd732750d15be3173b992de6R618). It now takes a [client_secret](https://stripe.com/docs/api/payment_intents/object#payment_intent_object-client_secret) `String` instead of a `PaymentIntentParams` instance.
  See [#1172](https://github.com/stripe/stripe-android/pull/1172).
    ```java
    // before
    stripe.retrievePaymentIntentSynchronous(
          PaymentIntentParams.createRetrievePaymentIntentParams(clientSecret));

    // after
    stripe.retrievePaymentIntentSynchronous(clientSecret);
    ```

- `PaymentIntentParams` is now [`ConfirmPaymentIntentParams`](https://github.com/stripe/stripe-android/blob/master/stripe/src/main/java/com/stripe/android/model/ConfirmPaymentIntentParams.java) and its method names have been simplified.
  See [#1172](https://github.com/stripe/stripe-android/pull/1172).
    ```java
    // before
    PaymentIntentParams.createConfirmPaymentIntentWithPaymentMethodId(
          paymentMethodId, clientSecret, returnUrl);

    // after
    ConfirmPaymentIntentParams.createWithPaymentMethodId(paymentMethodId, clientSecret, returnUrl);
    ```

- All `@StringDef` constants have been inlined in their respective `@interface`. Below is an example from `Card.FundingType`.
  See [#1173](https://github.com/stripe/stripe-android/pull/1173).
    ```java
    // before
    @Retention(RetentionPolicy.SOURCE)
    @StringDef({
            FUNDING_CREDIT,
            FUNDING_DEBIT,
            FUNDING_PREPAID,
            FUNDING_UNKNOWN
    })
    public @interface FundingType { }
    public static final String FUNDING_CREDIT = "credit";
    public static final String FUNDING_DEBIT = "debit";
    public static final String FUNDING_PREPAID = "prepaid";
    public static final String FUNDING_UNKNOWN = "unknown";

    // after
    @Retention(RetentionPolicy.SOURCE)
    @StringDef({
            FundingType.CREDIT,
            FundingType.DEBIT,
            FundingType.PREPAID,
            FundingType.UNKNOWN
    })
    public @interface FundingType {
        String CREDIT = "credit";
        String DEBIT = "debit";
        String PREPAID = "prepaid";
        String UNKNOWN = "unknown";
    }
    ```

### Migration from versions < 9.3.3
- The enum `PaymentIntent.Status` is now `StripeIntent.Status`
- The enum `PaymentIntent.NextActionType` is now `StripeIntent.NextActionType`

These changes should not impact Java integrations. For Kotlin integrations, please change your reference as shown in the example below.

```kotlin
// before
PaymentIntent.Status.Succeeded

// after
StripeIntent.Status.Succeeded
```

### Migration from versions < 9.3.0
- `CustomerSession`'s Listener interfaces's `onError()` method now have a `@NonNull` `errorMessage` argument
    ```java
    // before
    new CustomerSession.CustomerRetrievalListener() {
        @Override
        public void onError(int errorCode, @Nullable String errorMessage,
                            @Nullable StripeError stripeError) {

        }
    }

    // after
    new CustomerSession.CustomerRetrievalListener() {
        @Override
        public void onError(int errorCode, @NonNull String errorMessage,
                            @Nullable StripeError stripeError) {

        }
    }
    ```

- `PaymentResultListener` has been removed
- `PaymentSession#completePayment()` has been replaced with `PaymentSession#onCompleted()`
    ```java
    // before
    private void chargePaymentMethod() {
        mPaymentSession.completePayment(new PaymentCompletionProvider() {
            @Override
            public void completePayment(@NonNull PaymentSessionData data,
                                        @NonNull PaymentResultListener listener) {
                // Make async request to your backend to charge the payment method.
                // Upon success, call:
                listener.onPaymentResult(PaymentResultListener.SUCCESS);
            }
        });
    }

    // after
    private void chargePaymentMethod() {
        // Make async request to your backend to charge the payment method.
        // Upon success, call:
        mPaymentSession.onCompleted();
    }
    ```

### Migration from versions < 9.2.0
- `Card` model is now immutable
  - `Card#getType()` is now `Card#getBrand()`

### Migration from versions < 9.1.0
- [Standard UI components](https://stripe.com/docs/mobile/android/standard) now use `PaymentMethod` instead of `Source`
    - Setting a customer's default payment method is not available for PaymentMethod objects
    - `CustomerSession#getPaymentMethods()` and `CustomerSession#attachPaymentMethod()` have been added
    - `PaymentSessionData#getSelectedPaymentMethodId()` is now `PaymentSessionData#getPaymentMethod()` and returns a `PaymentMethod`. See usage in the samplestore app's [PaymentActivity.java](https://github.com/stripe/stripe-android/blob/194fef4c1c1981b8423125f26abf4b23f12631f8/samplestore/src/main/java/com/stripe/samplestore/PaymentActivity.java).
- Remove the following unused methods from `PaymentConfiguration`
  - `getRequiredBillingAddressFields()`
  - `setRequiredBillingAddressFields()`
  - `getShouldUseSourcesForCards()`
  - `setShouldUseSourcesForCards()`

### Migration from versions < 9.0.0
- `minSdkVersion` is now 19

- `AccountParams.createAccountParams()` requires a `AccountParams#BusinessType` parameter
    ```java
    // before
    AccountParams.createAccountParams(
      true,
      createBusinessData()
    );
    
    // after, AccountParams.BusinessType is required
    
    // for Individual entities
    AccountParams.createAccountParams(
      true,
      BusinessType.Individual,
      createBusinessData()
    );
    
    // for Company entities
    AccountParams.createAccountParams(
      true,
      BusinessType.Company,
      createBusinessData()
    );
    ```

- `CustomerSession.initCustomerSession()` now has a `Context` parameter.
   Related, `CustomerSession` public instance methods no longer have a `Context` parameter.
   ```java
   // before
   CustomerSession.initCustomerSession(ephemeralKeyProvider);
   CustomerSession.getInstance().setCustomerShippingInformation(this, listener);

   // after
   CustomerSession.initCustomerSession(this, ephemeralKeyProvider);
   CustomerSession.getInstance().setCustomerShippingInformation(listener);
   ```

- [`PaymentIntent`](https://github.com/stripe/stripe-android/blob/master/stripe/src/main/java/com/stripe/android/model/PaymentIntent.java) has been updated to reflect [API version 2019-02-11](https://stripe.com/docs/upgrades#2019-02-11)
    - `PaymentIntent.Status.RequiresSource` is now `PaymentIntent.Status.RequiresPaymentMethod`
    - `PaymentIntent.Status.RequiresSourceAction` is now `PaymentIntent.Status.RequiresAction`
    - `PaymentIntent.NextActionType.AuthorizeWithUrl` has been removed
    - `PaymentIntent#getNextSourceAction()` is now `PaymentIntent#getNextAction()`
    - `PaymentIntent#getAuthorizationUrl()` is now `PaymentIntent#getRedirectUrl()`
    - `PaymentIntent#requiresAction()` has been added as a convenience
    - `PaymentIntent#getStatus()` now returns a `PaymentIntent.Status` enum value instead of a `String`

- [`Address`](https://github.com/stripe/stripe-android/blob/master/stripe/src/main/java/com/stripe/android/model/Address.java) is now immutable and its setters have been removed.
  Use `Address.Builder` to create a new `Address` object.

### Migrating from versions < 7.0.0
- Remove Bitcoin source support because Stripe [no longer processes Bitcoin payments](https://stripe.com/blog/ending-bitcoin-support)
    - Sources can no longer have a "BITCOIN" source type. These sources will now be interpreted as "UNKNOWN".
    - You can no longer `createBitcoinParams`. Please use a different payment method.

### Migrating from versions < 5.0.0
- `StripeApiHandler` methods can no longer be called directly.
- `PaymentConfiguration` now stores your public key and is depended upon for `CustomerSession`.
- Many *Utils* classes have been migrated to package-private access.

### Migrating from versions < 4.0.0

- Instantiation of a Stripe object can no longer throw an `AuthenticationException`.
    - Any time you were instantiating a Stripe object in a try/catch block will be simplified.
    ```java
    Stripe stripe;
    try {
       stripe = new Stripe(mContext, MY_PUBLISHABLE_KEY);
    } catch (AuthenticationException authEx) {
       // This never happens because you check your key.
    }
    ```
    now becomes
    ```java
    Stripe stripe = new Stripe(mContext, MY_PUBLISHABLE_KEY);
    ```
- `Stripe#setDefaultPublishableKey(String key)` has similarly been changed, and no longer needs to be wrapped.
- Both methods can still throw an `IllegalArgumentException` if an invalid key is used, but as a
runtime exception, that does not need to be wrapped.
- `AuthenticationException` will now only be thrown if you attempt to create a `Token` or `Source`
with an invalid key.
