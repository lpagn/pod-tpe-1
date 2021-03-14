package ar.edu.itba.pod;

import ar.edu.itba.pod.server.Election;
import ar.edu.itba.pod.server.servant.ElectionService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class FiscalServiceImplTest {

    private final ElectionService fsi = new ElectionService();

    @Before
    public void init(){
        Election.getInstance().restart();
    }

    @Test
    public void testAddFiscal(){
        fsi.addFiscal(1001,"TIGER", (party,pollingPlaceNumber) -> System.out.printf("New vote for %s on polling place %d%n\n",party,pollingPlaceNumber));
        Assert.assertEquals(1, Election.getInstance().getPollingPlacesWithoutProvince().size());
    }

    @Test(expected = IllegalStateException.class)
    public void testAddFiscalWhenElectionStarted(){
        Election.getInstance().startElection();
        fsi.addFiscal(1001,"TIGER",null);
    }

    @Test(expected = IllegalStateException.class)
    public void testAddFiscalWhenElectionFinished(){
        Election.getInstance().startElection();
        Election.getInstance().closeElection();
        fsi.addFiscal(1001,"TIGER",null);
    }

    @Test
    public void testReceivingMessage(){
        fsi.addFiscal(1000, "TIGER", (party,pollingPlaceNumber) -> System.out.printf("New vote for %s on polling place %d%n\n",party,pollingPlaceNumber));
        Election.getInstance().startElection();
        fsi.vote("1000;JUNGLE;TIGER|3;TIGER");
    }
}
