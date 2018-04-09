/*
 * Copyright (C) 2018 Henrik Bærbak Christensen, baerbak.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package telemed.domain;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

import org.junit.*;

import telemed.helper.HelperMethods;

import com.google.gson.*;

/**
 * Learning tests for the TeleMed system. Unit testing of TeleObservation.
 * 
 * @author Henrik Baerbak Christensen, Aarhus University
 *
 */
public class TestTeleObservation {

  private TeleObservation to; 

  @Before public void setup() {
    to = new TeleObservation(HelperMethods.NANCY_ID, 120.0, 70.0);
  } 

  @Test public void shouldCreateTeleObservation() {
    assertThat( to.getPatientId(), is(HelperMethods.NANCY_ID));
    assertThat( to.getSystolic().getValue(), is(120.0));
    assertThat( to.getDiastolic().getValue(), is(70.0));
    assertThat( to.getSystolic().getUnit(), is("mm(Hg)") );
  }

  @Test public void shouldMarshallTeleObservation() {
    // This is a learning test, showing Gson marshalling
    Gson gson = new Gson();
    String json = gson.toJson(to);

    assertThat(json, containsString("\"patientId\":\"251248-0000\""));

    TeleObservation copy = gson.fromJson(json, TeleObservation.class);
    assertThat( copy.getPatientId(), is(HelperMethods.NANCY_ID));
    assertThat( copy.getSystolic().getValue(), is(120.0));
    assertThat( copy.getDiastolic().getValue(), is(70.0));
    assertThat( copy.getSystolic().getUnit(), is("mm(Hg)") );
  }
}
