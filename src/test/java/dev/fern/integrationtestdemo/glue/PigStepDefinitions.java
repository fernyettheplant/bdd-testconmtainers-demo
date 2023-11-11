package dev.fern.integrationtestdemo.glue;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class PigStepDefinitions {
    @When("I ping the endpoint")
    public void i_ping_the_endpoint() {}

    @Then("I should received a {int} Http Status")
    public void i_should_received_a_http_status(int httpStatus) {}
}
