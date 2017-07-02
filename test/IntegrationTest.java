import org.junit.*;

import play.mvc.*;
import play.test.*;

import static play.test.Helpers.*;
import static org.junit.Assert.*;

import static org.fluentlenium.core.filter.FilterConstructor.*;

public class IntegrationTest {

    /**
     * Integration test for single users
     */
    @Test
    public void testSingleUser1() {
        running(testServer(3333, fakeApplication()), HTMLUNIT, browser -> {
            browser.goTo("http://localhost:3333/arrondis/user1@mail.com");
            assertTrue(browser.pageSource().contains("\"transaction_id\":38000003650888"));
        });
    }

    @Test
    public void testSingleUser2() {
        running(testServer(3333, fakeApplication()), HTMLUNIT, browser -> {
            browser.goTo("http://localhost:3333/arrondis/user2@mail.com");
            assertTrue(browser.pageSource().contains("\"transaction_id\":38000003649284"));
        });
    }

    @Test
    public void testSingleUser3() {
        running(testServer(3333, fakeApplication()), HTMLUNIT, browser -> {
            browser.goTo("http://localhost:3333/arrondis/user3@mail.com");
            assertTrue(browser.pageSource().contains("\"transaction_id\":38000003647189"));
        });
    }


    /**
     * Integration test for aggregation
     */

    @Test
    public void testAggregation() {
        running(testServer(3333, fakeApplication()), HTMLUNIT, browser -> {
            browser.goTo("http://localhost:3333/aggregateArrondis/2010-01-01/2017-06-01");
            assertTrue(browser.pageSource().contains("\"totalArrondis\":18519.12"));
        });
    }

    @Test
    public void testAggregationNone() {
        running(testServer(3333, fakeApplication()), HTMLUNIT, browser -> {
            browser.goTo("http://localhost:3333/aggregateArrondis/2010-01-01/2010-01-02");
            assertTrue(browser.pageSource().contains("\"totalArrondis\":0.0"));
        });
    }

    @Test
    public void testAggregationOneMonth() {
        running(testServer(3333, fakeApplication()), HTMLUNIT, browser -> {
            browser.goTo("http://localhost:3333/aggregateArrondis/2016-05-01/2016-06-01");
            assertTrue(browser.pageSource().contains("\"totalArrondis\":306.0"));
        });
    }

}
