package com.fullcontact.apilib.enrich;

import com.fullcontact.apilib.models.Response.EmailVerificationResponse;
import org.junit.Assert;
import org.junit.Test;

public class EmailVerificationTest {
  @Test
  public void emailVerificationDeserializationTest() {
    EmailVerificationResponse response =
        FullContact.getEmailVerificationResponse(
            HttpResponseTestObjects.httpResponseTestObjectProvider("tc_201"));
    Assert.assertTrue(response.isSuccessful);
    Assert.assertEquals(200, response.getStatus());
    Assert.assertEquals(
        "High Risk (Complainer, Fraudulent)",
        response.getEmails().get("bart@fullcontact.com").getMessage());
    Assert.assertEquals(
        "bart@fullcontact.com", response.getEmails().get("bart@fullcontact.com").getAddress());
    Assert.assertEquals("bart", response.getEmails().get("bart@fullcontact.com").getUsername());
    Assert.assertEquals(
        "fullcontact.com", response.getEmails().get("bart@fullcontact.com").getDomain());
    Assert.assertFalse(response.getEmails().get("bart@fullcontact.com").isCorrected());
    Assert.assertFalse(response.getEmails().get("bart@fullcontact.com").isSendSafely());
    Assert.assertTrue(
        response.getEmails().get("bart@fullcontact.com").getAttributes().isValidSyntax());
    Assert.assertTrue(response.getEmails().get("bart@fullcontact.com").getAttributes().isRisky());
    Assert.assertFalse(
        response.getEmails().get("bart@fullcontact.com").getAttributes().isDeliverable());
    Assert.assertFalse(
        response.getEmails().get("bart@fullcontact.com").getAttributes().isCatchall());
    Assert.assertFalse(
        response.getEmails().get("bart@fullcontact.com").getAttributes().isDisposable());
  }
}
