package com.flt.liblookupprovider;

import com.flt.liblookupprovider.extraction.ObbFilenameUnderstander;

import junit.framework.Assert;

import org.junit.Test;

import static org.junit.Assert.*;

public class ObbRecognitionTests {
  @Test
  public void can_recognise_obb_number() {

    String obb = "main.12345.some.package.name.obb";

    int value = ObbFilenameUnderstander.retrieveMainObbVersion(obb);
    Assert.assertEquals(12345, value);


  }
}