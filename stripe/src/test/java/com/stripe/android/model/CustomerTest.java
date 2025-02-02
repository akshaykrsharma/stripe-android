package com.stripe.android.model;

import android.support.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import static com.stripe.android.model.CardTest.JSON_CARD_USD;
import static com.stripe.android.model.CustomerSourceTest.JSON_APPLE_PAY_CARD;
import static com.stripe.android.model.SourceTest.EXAMPLE_ALIPAY_SOURCE;
import static com.stripe.android.model.SourceTest.EXAMPLE_JSON_SOURCE_WITHOUT_NULLS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Test class for {@link Customer} model object.
 */
public class CustomerTest {

    private static final String NON_CUSTOMER_OBJECT =
            "{\n" +
                    "    \"object\": \"not_a_customer\",\n" +
                    "    \"has_more\": false,\n" +
                    "    \"total_count\": 22,\n" +
                    "    \"url\": \"http://google.com\"\n" +
                    "}";

    private static final String TEST_CUSTOMER_OBJECT =
            "{\n" +
            "  \"id\": \"cus_AQsHpvKfKwJDrF\",\n" +
            "  \"object\": \"customer\",\n" +
            "  \"default_source\": \"abc123\",\n" +
            "  \"sources\": {\n" +
            "    \"object\": \"list\",\n" +
            "    \"data\": [\n" +
            "\n" +
            "    ],\n" +
            "    \"has_more\": false,\n" +
            "    \"total_count\": 0,\n" +
            "    \"url\": \"/v1/customers/cus_AQsHpvKfKwJDrF/sources\"\n" +
            "  }\n" +
            "}";

    @Test
    public void fromString_whenStringIsNull_returnsNull() {
        assertNull(Customer.fromString(null));
    }

    @Test
    public void fromJson_whenNotACustomer_returnsNull() {
        assertNull(Customer.fromString(NON_CUSTOMER_OBJECT));
    }

    @Test
    public void fromJson_whenCustomer_returnsExpectedCustomer() {
        final Customer customer = Customer.fromString(TEST_CUSTOMER_OBJECT);
        assertNotNull(customer);
        assertEquals("cus_AQsHpvKfKwJDrF", customer.getId());
        assertEquals("abc123", customer.getDefaultSource());
        assertNull(customer.getShippingInformation());
        assertNotNull(customer.getSources());
        assertEquals("/v1/customers/cus_AQsHpvKfKwJDrF/sources", customer.getUrl());
        assertEquals(Boolean.FALSE, customer.getHasMore());
        assertEquals(Integer.valueOf(0), customer.getTotalCount());
    }

    @Test
    public void fromJson_whenCustomerHasApplePay_returnsCustomerWithoutApplePaySources()
            throws JSONException {
        final Customer customer = Customer.fromString(createTestCustomerObjectWithApplePaySource());
        assertNotNull(customer);
        assertEquals(2, customer.getSources().size());
        // Note that filtering the apple_pay sources intentionally does not change the total
        // count value.
        assertEquals(Integer.valueOf(5), customer.getTotalCount());
    }

    @Test
    public void fromJson_createsSameObject() {
        Customer customer = Customer.fromString(TEST_CUSTOMER_OBJECT);
        assertNotNull(customer);
        assertEquals(Customer.fromString(TEST_CUSTOMER_OBJECT),
                Customer.fromString(TEST_CUSTOMER_OBJECT));
    }

    @NonNull
    private String createTestCustomerObjectWithApplePaySource() throws JSONException {
        final JSONObject rawJsonCustomer = new JSONObject(TEST_CUSTOMER_OBJECT);
        final JSONObject sourcesObject = rawJsonCustomer.getJSONObject("sources");
        final JSONArray sourcesArray = sourcesObject.getJSONArray("data");

        sourcesObject.put("total_count", 5);
        final CustomerSource applePayCard = CustomerSource.fromString(JSON_APPLE_PAY_CARD);
        assertNotNull(applePayCard);
        sourcesArray.put(new JSONObject(applePayCard.toMap()));

        final Card testCard = Card.fromString(JSON_CARD_USD);
        assertNotNull(testCard);

        final JSONObject manipulatedCard = new JSONObject(JSON_CARD_USD);
        manipulatedCard.put("id", "card_id55555");
        manipulatedCard.put("tokenization_method", "apple_pay");
        final Card manipulatedApplePayCard = Card.fromJson(manipulatedCard);
        assertNotNull(manipulatedApplePayCard);

        Source sourceCardWithApplePay = Source.fromString(EXAMPLE_JSON_SOURCE_WITHOUT_NULLS);
        // Note that we don't yet explicitly support bitcoin sources, but this data is
        // convenient for the test because it is not an apple pay source.
        final Source alipaySource = Source.fromString(EXAMPLE_ALIPAY_SOURCE);
        assertNotNull(sourceCardWithApplePay);
        assertNotNull(alipaySource);
        sourcesArray.put(new JSONObject(sourceCardWithApplePay.toMap()));
        sourcesArray.put(new JSONObject(alipaySource.toMap()));
        sourcesArray.put(new JSONObject(testCard.toMap()));
        sourcesArray.put(new JSONObject(manipulatedApplePayCard.toMap()));
        sourcesObject.put("data", sourcesArray);

        rawJsonCustomer.put("sources", sourcesObject);

        // Verify JSON manipulation
        assertTrue(rawJsonCustomer.has("sources"));
        assertTrue(rawJsonCustomer.getJSONObject("sources").has("data"));
        assertEquals(5,
                rawJsonCustomer.getJSONObject("sources").getJSONArray("data").length());
        return rawJsonCustomer.toString();
    }
}
