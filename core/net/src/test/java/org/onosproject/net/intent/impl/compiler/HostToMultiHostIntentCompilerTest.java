package org.onosproject.net.intent.impl.compiler;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.onosproject.core.ApplicationId;
import org.onosproject.TestApplicationId;
import org.onosproject.net.Host;
import org.onosproject.net.HostId;
import org.onosproject.net.flow.TrafficSelector;
import org.onosproject.net.flow.TrafficTreatment;
import org.onosproject.net.host.HostService;
import org.onosproject.net.intent.*;
import org.onosproject.net.resource.MockResourceService;
import org.onlab.packet.MacAddress;
import org.onlab.packet.VlanId;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.easymock.EasyMock.*;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.onosproject.net.NetTestTools.hid;
import static org.onosproject.net.intent.LinksHaveEntryWithSourceDestinationPairMatcher.linksHasPath;

/**
 * Created by alaurito on 25.08.16.
 */
public class HostToMultiHostIntentCompilerTest extends AbstractIntentTest {
    private static final String HOST_ONE_MAC = "00:00:00:00:00:01";
    private static final String HOST_TWO_MAC = "00:00:00:00:00:02";
    private static final String HOST_THREE_MAC = "00:00:00:00:00:03";
    private static final String HOST_FOUR_MAC = "00:00:00:00:00:04";
    private static final String HOST_ONE_VLAN = "None";
    private static final String HOST_TWO_VLAN = "None";
    private static final String HOST_THREE_VLAN = "None";
    private static final String HOST_FOUR_VLAN = "None";
    private static final String HOST_ONE = HOST_ONE_MAC + "/" + HOST_ONE_VLAN;
    private static final String HOST_TWO = HOST_TWO_MAC + "/" + HOST_TWO_VLAN;
    private static final String HOST_THREE = HOST_THREE_MAC + "/" + HOST_THREE_VLAN;
    private static final String HOST_FOUR = HOST_FOUR_MAC + "/" + HOST_FOUR_VLAN;

    private static final ApplicationId APPID = new TestApplicationId("testingHostToMultiHostIntentCompiler");

    private TrafficSelector selector = new IntentTestsMocks.MockSelector();
    private TrafficTreatment treatment = new IntentTestsMocks.MockTreatment();

    private HostId hostOneId = HostId.hostId(HOST_ONE);
    private HostId hostTwoId = HostId.hostId(HOST_TWO);
    private HostId hostThreeId = HostId.hostId(HOST_THREE);
    private HostId hostFourId = HostId.hostId(HOST_FOUR);
    private HostService mockHostService;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        Host hostOne = createMock(Host.class);
        expect(hostOne.mac()).andReturn(new MacAddress(HOST_ONE_MAC.getBytes())).anyTimes();
        expect(hostOne.vlan()).andReturn(VlanId.vlanId()).anyTimes();
        replay(hostOne);

        Host hostTwo = createMock(Host.class);
        expect(hostTwo.mac()).andReturn(new MacAddress(HOST_TWO_MAC.getBytes())).anyTimes();
        expect(hostTwo.vlan()).andReturn(VlanId.vlanId()).anyTimes();
        replay(hostTwo);

        Host hostThree = createMock(Host.class);
        expect(hostThree.mac()).andReturn(new MacAddress(HOST_THREE_MAC.getBytes())).anyTimes();
        expect(hostThree.vlan()).andReturn(VlanId.vlanId()).anyTimes();
        replay(hostThree);

        Host hostFour = createMock(Host.class);
        expect(hostFour.mac()).andReturn(new MacAddress(HOST_FOUR_MAC.getBytes())).anyTimes();
        expect(hostFour.vlan()).andReturn(VlanId.vlanId()).anyTimes();
        replay(hostFour);

        mockHostService = createMock(HostService.class);
        expect(mockHostService.getHost(eq(hostOneId))).andReturn(hostOne).anyTimes();
        expect(mockHostService.getHost(eq(hostTwoId))).andReturn(hostTwo).anyTimes();
        expect(mockHostService.getHost(eq(hostThreeId))).andReturn(hostThree).anyTimes();
        expect(mockHostService.getHost(eq(hostFourId))).andReturn(hostFour).anyTimes();
        replay(mockHostService);
    }

    /**
     * Creates a HostToMultiHost intent based on one source Id and three destinations Id.
     *
     * @param sourceids source host id
     * @param destinationsids Set of host destinations id
     * @return HostToHostIntent for the two hosts
     */
    private HostToMultiHostIntent makeIntent(HostId sourceids, Set<HostId> destinationsids) {
        return HostToMultiHostIntent.builder()
                .appId(APPID)
                .source(sourceids)
                .destinations(destinationsids)
                .selector(selector)
                .treatment(treatment)
                .build();
    }

    /**
     * Creates a compiler for HostToMultiHost intents.
     *
     * @param hops string array describing the path hops to use when compiling
     * @return HostToMultiHost intent compiler
     */
    private HostToMultiHostCompiler makeCompiler(String[] hops) {
        HostToMultiHostCompiler compiler =
                new HostToMultiHostCompiler();
        compiler.pathService = new IntentTestsMocks.MockPathService(hops);
        compiler.hostService = mockHostService;
        compiler.resourceService = new MockResourceService();
        return compiler;
    }


    /**
     * Tests a pair of hosts with 8 hops between them.
     */
    @Test
    public void testSingleLongPathCompilation() {

        HostToMultiHostIntent intent = makeIntent(hid(HOST_ONE),
                new HashSet<HostId>(Arrays.asList(hid(HOST_TWO),hid(HOST_THREE),hid(HOST_FOUR))));
        assertThat(intent, is(notNullValue()));

        String[] hops = {HOST_ONE, "h1", "h2", "h3", "h4", "h5", "h6", "h7", "h8", HOST_TWO};
        HostToMultiHostCompiler compiler = makeCompiler(hops);
        assertThat(compiler, is(notNullValue()));

        List<Intent> result = compiler.compile(intent, null);
        assertThat(result, is(Matchers.notNullValue()));
        assertThat(result, hasSize(3));
        Intent pathToHostTwo = result.get(0);
        assertThat(pathToHostTwo instanceof PathIntent, is(true));
        Intent pathToHostThree = result.get(1);
        assertThat(pathToHostThree instanceof PathIntent, is(true));
        Intent pathToHostFour = result.get(2);
        assertThat(pathToHostFour instanceof PathIntent, is(true));

        if (pathToHostTwo instanceof PathIntent) {
            PathIntent forwardPathIntent = (PathIntent) pathToHostTwo;
            assertThat(forwardPathIntent.path().links(), hasSize(9));
            assertThat(forwardPathIntent.path().links(), linksHasPath(HOST_ONE, "h1"));
            assertThat(forwardPathIntent.path().links(), linksHasPath("h1", "h2"));
            assertThat(forwardPathIntent.path().links(), linksHasPath("h2", "h3"));
            assertThat(forwardPathIntent.path().links(), linksHasPath("h3", "h4"));
            assertThat(forwardPathIntent.path().links(), linksHasPath("h4", "h5"));
            assertThat(forwardPathIntent.path().links(), linksHasPath("h5", "h6"));
            assertThat(forwardPathIntent.path().links(), linksHasPath("h6", "h7"));
            assertThat(forwardPathIntent.path().links(), linksHasPath("h7", "h8"));
            assertThat(forwardPathIntent.path().links(), linksHasPath("h8", HOST_TWO));
        }

        if (pathToHostThree instanceof PathIntent) {
            PathIntent forwardPathIntent = (PathIntent) pathToHostThree;
            assertThat(forwardPathIntent.path().links(), hasSize(9));
            assertThat(forwardPathIntent.path().links(), linksHasPath(HOST_ONE, "h1"));
            assertThat(forwardPathIntent.path().links(), linksHasPath("h1", "h2"));
            assertThat(forwardPathIntent.path().links(), linksHasPath("h2", "h3"));
            assertThat(forwardPathIntent.path().links(), linksHasPath("h3", "h4"));
            assertThat(forwardPathIntent.path().links(), linksHasPath("h4", "h5"));
            assertThat(forwardPathIntent.path().links(), linksHasPath("h5", "h6"));
            assertThat(forwardPathIntent.path().links(), linksHasPath("h6", "h7"));
            assertThat(forwardPathIntent.path().links(), linksHasPath("h7", "h8"));
            assertThat(forwardPathIntent.path().links(), linksHasPath("h8", HOST_THREE));
        }

        if (pathToHostFour instanceof PathIntent) {
            PathIntent forwardPathIntent = (PathIntent) pathToHostFour;
            assertThat(forwardPathIntent.path().links(), hasSize(9));
            assertThat(forwardPathIntent.path().links(), linksHasPath(HOST_ONE, "h1"));
            assertThat(forwardPathIntent.path().links(), linksHasPath("h1", "h2"));
            assertThat(forwardPathIntent.path().links(), linksHasPath("h2", "h3"));
            assertThat(forwardPathIntent.path().links(), linksHasPath("h3", "h4"));
            assertThat(forwardPathIntent.path().links(), linksHasPath("h4", "h5"));
            assertThat(forwardPathIntent.path().links(), linksHasPath("h5", "h6"));
            assertThat(forwardPathIntent.path().links(), linksHasPath("h6", "h7"));
            assertThat(forwardPathIntent.path().links(), linksHasPath("h7", "h8"));
            assertThat(forwardPathIntent.path().links(), linksHasPath("h8", HOST_FOUR));
        }

    }
}
