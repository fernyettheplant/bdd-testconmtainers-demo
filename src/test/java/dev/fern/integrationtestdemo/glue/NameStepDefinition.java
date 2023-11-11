package dev.fern.integrationtestdemo.glue;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class NameStepDefinition {
    @Given("a name {string}")
    public void a_name(String string) {}

    @When("I submit a new name")
    public void i_submit_a_new_name() {}

    @Then("Should be saved in database")
    public void should_be_saved_in_database() {}
}
