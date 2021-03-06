package org.onosproject.net.intent;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableList;
import org.apache.commons.collections.ListUtils;
import org.onosproject.core.ApplicationId;
import org.onosproject.net.HostId;
import org.onosproject.net.Link;
import org.onosproject.net.flow.TrafficSelector;
import org.onosproject.net.flow.TrafficTreatment;
import org.onosproject.net.intent.constraint.LinkTypeConstraint;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by alaurito on 25.08.16.
 */
public final class HostToMultiHostIntent extends ConnectivityIntent {

    static final LinkTypeConstraint NOT_OPTICAL = new LinkTypeConstraint(false, Link.Type.OPTICAL);

    private final HostId source;
    private final Set<HostId> destinations;

    /**
     * Returns a new host to multihost intent builder.
     *
     * @return host to multihost intent builder
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder of a host to multihost intent.
     */
    public static final class Builder extends ConnectivityIntent.Builder {
        HostId source;
        Set<HostId> destinations;

        private Builder() {
            // Hide constructor
        }

        @Override
        public Builder appId(ApplicationId appId) {
            return (Builder) super.appId(appId);
        }

        @Override
        public Builder key(Key key) {
            return (Builder) super.key(key);
        }

        @Override
        public Builder selector(TrafficSelector selector) {
            return (Builder) super.selector(selector);
        }

        @Override
        public Builder treatment(TrafficTreatment treatment) {
            return (Builder) super.treatment(treatment);
        }

        @Override
        public Builder constraints(List<Constraint> constraints) {
            return (Builder) super.constraints(constraints);
        }

        @Override
        public Builder priority(int priority) {
            return (Builder) super.priority(priority);
        }

        /**
         * Sets the first host of the intent that will be built.
         *
         * @param one first host
         * @return this builder
         */
        public Builder source(HostId one) {
            this.source = one;
            return this;
        }

        /**
         * Sets the second host of the intent that will be built.
         *
         * @param destinations second host
         * @return this builder
         */
        public Builder destinations(Set<HostId> destinations) {
            this.destinations = destinations;
            return this;
        }



        /**
         * Builds a host to host intent from the accumulated parameters.
         *
         * @return point to point intent
         */
        public HostToMultiHostIntent build() {

            List<Constraint> theConstraints = constraints;
            // If not-OPTICAL constraint hasn't been specified, add them
            if (!constraints.contains(NOT_OPTICAL)) {
                theConstraints = ImmutableList.<Constraint>builder()
                        .add(NOT_OPTICAL)
                        .addAll(constraints)
                        .build();
            }

            return new HostToMultiHostIntent(
                    appId,
                    key,
                    source,
                    destinations,
                    selector,
                    treatment,
                    theConstraints,
                    priority
            );
        }
    }


    /**
     * Creates a new host-to-host intent with the supplied host pair.
     *
     * @param appId             application identifier
     * @param key               intent key
     * @param source            first host
     * @param destinations      destination hosts
     * @param selector          action
     * @param treatment         ingress port
     * @param constraints       optional prioritized list of path selection constraints
     * @param priority          priority to use for flows generated by this intent
     * @throws NullPointerException if {@code one} or {@code two} is null.
     */
    private HostToMultiHostIntent(ApplicationId appId, Key key,
                             HostId source, Set<HostId> destinations,
                             TrafficSelector selector,
                             TrafficTreatment treatment,
                             List<Constraint> constraints,
                             int priority) {
        super(appId, key, ListUtils.union(Arrays.asList(source), new ArrayList<HostId>(destinations)),
                selector, treatment, constraints, priority);

        this.source = checkNotNull(source);
        this.destinations = checkNotNull(destinations);
        checkArgument(!destinations.isEmpty(), "Destination should have at least one host");
        checkArgument(!destinations.contains(source),
                "Destinations host should not contain source (source: %s)", source);

    }

    /**
     * Returns identifier of the first host.
     *
     * @return first host identifier
     */
    public HostId source() {
        return source;
    }

    /**
     * Returns identifier of the destinations host.
     *
     * @return destinations set identifiers
     */
    public Set<HostId> destinations() {
        return destinations;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(getClass())
                .add("id", id())
                .add("key", key())
                .add("appId", appId())
                .add("priority", priority())
                .add("resources", resources())
                .add("selector", selector())
                .add("treatment", treatment())
                .add("constraints", constraints())
                .add("source", source)
                .add("destinations", destinations)
                .toString();
    }

}
