package com.flt.coecclient.receivers;

import com.flt.coecclient.service.CoecService;
import com.flt.servicelib.AbstractBootReceiver;

public class StartupReceiver extends AbstractBootReceiver<CoecService> {
  @Override
  protected Class<CoecService> getServiceClass() {
    return CoecService.class;
  }
}
