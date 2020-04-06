package com.fullcontact.apilib;

/** Defines the custom FullContactException */
public class FullContactException extends Exception {

  private static final long serialVersionUID = -7672993955585002277L;

  public FullContactException(String message) {
    super(message);
  }
}
