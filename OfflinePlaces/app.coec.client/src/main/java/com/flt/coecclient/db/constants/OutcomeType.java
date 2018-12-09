package com.flt.coecclient.db.constants;

public enum OutcomeType {
  NotAttempted(false),
  AttemptedInterrupted(false),
  AttemptedAbandoned(false),
  Succeeded(true);

  public boolean successful;

  OutcomeType(boolean isSuccessful) {
    this.successful = isSuccessful;
  }

}
