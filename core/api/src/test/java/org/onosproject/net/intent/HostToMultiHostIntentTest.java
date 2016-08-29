package org.onosproject.net.intent;

import com.google.common.collect.ImmutableList;
import com.google.common.testing.EqualsTester;
import org.junit.Test;
import org.onlab.util.DataRateUnit;
import org.onosproject.TestApplicationId;
import org.onosproject.core.ApplicationId;
import org.onosproject.net.HostId;
import org.onosproject.net.flow.TrafficSelector;
import org.onosproject.net.intent.constraint.BandwidthConstraint;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.onlab.junit.ImmutableClassChecker.assertThatClassIsImmutable;
import static org.onosproject.net.NetTestTools.hid;

/**
 * Created by alaurito on 25.08.16.
 */
public class HostToMultiHostIntentTest extends IntentTest {
    private final TrafficSelector selector = new IntentTestsMocks.MockSelector();
    private final IntentTestsMocks.MockTreatment treatment = new IntentTestsMocks.MockTreatment();
    private final HostId id1 = hid("12:34:56:78:91:ab/1");
    private final HostId id2 = hid("12:34:56:78:92:ab/2");
    private final HostId id3 = hid("12:34:56:78:93:ab/3");
    private final HostId id4 = hid("12:34:56:78:94:ab/4");

    private static final ApplicationId APPID = new TestApplicationId("testingHostToMultiHost");

    private HostToMultiHostIntent makeHostToMultiHostIntent(HostId source, Set<HostId> destinations) {
        return HostToMultiHostIntent.builder()
                .appId(APPID)
                .source(source)
                .destinations(destinations)
                .selector(selector)
                .treatment(treatment)
                .build();
    }

    /**
     * Tests the equals() method where two HostToHostIntents have references
     * to the same hosts. These should compare equal.
     */
    @Test
    public void testSameEquals() {

        HostId source = hid("00:00:00:00:00:01/-1");
        HostId destination1 = hid("00:00:00:00:00:02/-1");
        HostId destination2 = hid("00:00:00:00:00:03/-1");
        HostId destination3 = hid("00:00:00:00:00:04/-1");
        Set<HostId> destinations = new HashSet<HostId>(Arrays.asList(destination1, destination2, destination3));
        HostToMultiHostIntent i1 = makeHostToMultiHostIntent(source, destinations);
        HostToMultiHostIntent i2 = makeHostToMultiHostIntent(source, destinations);

        assertThat(i1.source(), is(equalTo(i2.source())));
        assertThat(i1.destinations(), is(equalTo(i2.destinations())));
    }

    /**
     * Checks that the HostToHostIntent class is immutable.
     */
    @Test
    public void testImmutability() {
        assertThatClassIsImmutable(HostToMultiHostIntent.class);
    }

    /**
     * Tests equals(), hashCode() and toString() methods.
     */
    @Test
    public void testEquals() {
        final HostToMultiHostIntent intent1 = HostToMultiHostIntent.builder()
                .appId(APPID)
                .source(id1)
                .destinations(new HashSet<HostId>(Arrays.asList(id2, id3, id4)))
                .selector(selector)
                .treatment(treatment)
                .build();

        final HostToMultiHostIntent intent2 = HostToMultiHostIntent.builder()
                .appId(APPID)
                .source(id1)
                .destinations(new HashSet<HostId>(Arrays.asList(id2, id3, id4)))
                .selector(selector)
                .treatment(treatment)
                .build();

        new EqualsTester()
                .addEqualityGroup(intent1)
                .addEqualityGroup(intent2)
                .testEquals();
    }

    @Test
    public void testImplicitConstraintsAreAdded() {
        final Constraint other = BandwidthConstraint.of(1, DataRateUnit.GBPS);
        final HostToMultiHostIntent intent = HostToMultiHostIntent.builder()
                .appId(APPID)
                .source(id1)
                .destinations(new HashSet<HostId>(Arrays.asList(id2, id3, id4)))
                .selector(selector)
                .treatment(treatment)
                .constraints(ImmutableList.of(other))
                .build();

        assertThat(intent.constraints(), hasItem(HostToMultiHostIntent.NOT_OPTICAL));
    }

    @Test
    public void testImplicitConstraints() {
        final HostToMultiHostIntent implicit = HostToMultiHostIntent.builder()
                .appId(APPID)
                .source(id1)
                .destinations(new HashSet<HostId>(Arrays.asList(id2, id3, id4)))
                .selector(selector)
                .treatment(treatment)
                .build();
        final HostToMultiHostIntent empty = HostToMultiHostIntent.builder()
                .appId(APPID)
                .source(id1)
                .destinations(new HashSet<HostId>(Arrays.asList(id2, id3, id4)))
                .selector(selector)
                .treatment(treatment)
                .constraints(ImmutableList.of())
                .build();
        final HostToMultiHostIntent exact = HostToMultiHostIntent.builder()
                .appId(APPID)
                .source(id1)
                .destinations(new HashSet<HostId>(Arrays.asList(id2, id3, id4)))
                .selector(selector)
                .treatment(treatment)
                .constraints(ImmutableList.of(HostToHostIntent.NOT_OPTICAL))
                .build();

        new EqualsTester()
                .addEqualityGroup(implicit.constraints(),
                        empty.constraints(),
                        exact.constraints())
                .testEquals();

    }

    @Override
    protected Intent createOne() {
        return HostToMultiHostIntent.builder()
                .appId(APPID)
                .source(id1)
                .destinations(new HashSet<HostId>(Arrays.asList(id2, id3, id4)))
                .selector(selector)
                .treatment(treatment)
                .build();
    }

    @Override
    protected Intent createAnother() {
        return HostToMultiHostIntent.builder()
                .appId(APPID)
                .source(id1)
                .destinations(new HashSet<HostId>(Arrays.asList(id2, id3, id4)))
                .selector(selector)
                .treatment(treatment)
                .build();
    }
}

