package ar.edu.itba.pod;

import ar.edu.itba.pod.server.Election;
import ar.edu.itba.pod.server.servant.ElectionService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ManagementServiceTest {
    private final ElectionService voteService = new ElectionService();

    @Before
    public void init(){
        Election.getInstance().restart();
    }

    @Test
    public void testStart(){
        voteService.open();
        Assert.assertTrue(Election.getInstance().getStarted());
    }

    @Test
    public void testClose(){
        Election.getInstance().startElection();
        voteService.close();
        Assert.assertFalse(Election.getInstance().getStarted());
    }

    @Test(expected = IllegalStateException.class)
    public void testStartWhenClosed(){
        voteService.open();
        voteService.close();
        voteService.open();
    }
}
