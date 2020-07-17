package com.fullcontact.apilib.enrich;

import com.fullcontact.apilib.FullContactException;
import com.fullcontact.apilib.auth.CredentialsProvider;
import com.fullcontact.apilib.auth.StaticApiKeyCredentialProvider;
import com.fullcontact.apilib.models.Response.EmailVerificationResponse;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.concurrent.ExecutionException;

public class EmailVerificationTest {
  private static HashMap<String, String> customHeader = new HashMap<>();

  @Before
  public void init() {
    System.setProperty("FC_TEST_ENV", "FC_TEST");
  }

  @After
  public void reset() {
    System.clearProperty("FC_TEST_ENV");
  }

  @Test
  public void emailVerificationModelDeserializationTest()
      throws FullContactException, ExecutionException, InterruptedException {
    CredentialsProvider staticCredentialsProvider = new StaticApiKeyCredentialProvider("fc_test");
    customHeader.put("testCode", "tc_201");
    FullContact fcTest =
        FullContact.builder()
            .credentialsProvider(staticCredentialsProvider)
            .headers(customHeader)
            .build();
    EmailVerificationResponse response = fcTest.emailVerification("bart@fullcontact.com").get();
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
