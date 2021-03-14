package ar.edu.itba.pod;

import ar.edu.itba.pod.server.Election;
import ar.edu.itba.pod.server.models.PollingPlace;
import ar.edu.itba.pod.server.servant.ElectionService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

public class VoteServiceImplTest {
    private final ElectionService voteService = new ElectionService();

    @Before
    public void init(){
        Election.getInstance().restart();
        Election.getInstance().startElection();
    }

    @Test
    public void testVote(){
        voteService.vote("1002;SAVANNAH;TIGER|3,LYNX|3,OWL|3,BUFFALO|5;BUFFALO");
        Map<Integer, PollingPlace> map = Election.getInstance().getProvinces().get("SAVANNAH").getPollingPlaces();

        Assert.assertEquals(map.size(), 1);
        Assert.assertEquals(map.get(1002).getVotes().get(0).getSPAV().size(), 4);
        Assert.assertEquals(map.get(1002).getVotes().get(0).getFPTP(), "BUFFALO");
    }
}
