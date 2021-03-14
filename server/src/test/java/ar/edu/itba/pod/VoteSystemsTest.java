package ar.edu.itba.pod;

import ar.edu.itba.pod.server.Election;
import ar.edu.itba.pod.server.servant.ElectionService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.*;

public class VoteSystemsTest {
    private final ElectionService voteService = new ElectionService();

    @Before
    public void init(){
        Election.getInstance().restart();
        Election.getInstance().startElection();
        voteService.vote("1000;JUNGLE;TIGER|3,OWL|1;OWL");
        voteService.vote("1000;JUNGLE;TIGER|3,OWL|3;BUFFALO");
        voteService.vote("1000;JUNGLE;TIGER|4,LYNX|3,OWL|2,BUFFALO|1;TIGER");
        voteService.vote("1000;JUNGLE;TIGER|4,LYNX|3,OWL|2,BUFFALO|1;OWL");
        Election.getInstance().closeElection();
    }

    @Test
    public void testFPTP() {
        String str = voteService.getPollingPlace(1000);
        Assert.assertTrue(str.contains("Winner\nOWL"));
    }

    @Test
    public void testSPAV() {
        String str = voteService.getProvince("JUNGLE");
        Assert.assertTrue(str.contains("OWL,TIGER,BUFFALO"));
    }

    @Test
    public void testSTAR() {
        String str = voteService.getNation();
        Assert.assertTrue(str.contains("Winner\nTIGER"));
    }
}
